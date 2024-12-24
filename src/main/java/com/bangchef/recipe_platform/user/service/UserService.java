package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.common.enums.UserSortType;
import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.security.JWTUtil;
import com.bangchef.recipe_platform.security.token.repository.RefreshTokenRepository;
import com.bangchef.recipe_platform.user.dto.CustomUserDetails;
import com.bangchef.recipe_platform.user.dto.LoginDto;
import com.bangchef.recipe_platform.user.dto.UserResponseDto;
import com.bangchef.recipe_platform.user.dto.UserUpdateDto;
import com.bangchef.recipe_platform.user.entity.Subscription;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.SubscriptionRepository;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더 추가
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소 추가
    private final EmailService emailService;
    private final SubscriptionRepository subscriptionRepository;

    @Value("1")
    private Long jwtExpiration;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, EmailService emailService,
                       SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailService = emailService;
        this.subscriptionRepository = subscriptionRepository;

    }

    // 로그인 로직: DB에서 사용자 정보 조회 후 JWT 발급
    public String loginUser(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 최신 권한을 DB에서 조회하여 토큰 생성
        String role = user.getRole().toString();
        return jwtUtil.createJwt("access", user, role, jwtExpiration);
    }

    public String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // 8자리 임시 비밀번호 생성
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_EMAIL_NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPassword;
        try {
            tempPassword = generateTempPassword();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TEMP_PASSWORD_GENERATION_FAILED);
        }

        try {
            // 이메일 발송
            emailService.sendTemporaryPasswordEmail(email, tempPassword);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILURE);
        }

        // 비밀번호 암호화 후 저장
        user.setPassword(new BCryptPasswordEncoder().encode(tempPassword));
        userRepository.save(user);
    }

    // 현재 로그인한 사용자 정보 조회
    public UserResponseDto getLoggedInUser() {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponseDTO(user);
    }

    // 사용자 정보 업데이트
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto) {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdateDto.getUsername() != null) {
            user.setUsername(userUpdateDto.getUsername());
        }
        if (userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        if (userUpdateDto.getProfileImage() != null) {
            user.setProfileImage(userUpdateDto.getProfileImage());
        }
        if (userUpdateDto.getIntroduction() != null) {
            user.setIntroduction(userUpdateDto.getIntroduction());
        }

        user = userRepository.save(user);
        return convertToUserResponseDTO(user);
    }

    // 현재 로그인한 사용자 정보 가져오기 (SecurityContext 활용)
    private CustomUserDetails getLoggedInUserDetails() {
        return (CustomUserDetails) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // User 데이터를 UserResponseDTO로 변환
    private UserResponseDto convertToUserResponseDTO(User user) {
        UserResponseDto userResponse = new UserResponseDto();

        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setProfileImage(user.getProfileImage());
        userResponse.setIntroduction(user.getIntroduction());
        userResponse.setSubscribers(user.getSubscribers());
        userResponse.setAvgRating(user.getAvgRating());
        userResponse.setRole(user.getRole());

        return userResponse;

    }

    // 회원 탈퇴
    public void deleteUser() {
        CustomUserDetails userDetails = getLoggedInUserDetails();
        String email = userDetails.getUsername();

        // User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // RefreshToken 삭제
        refreshTokenRepository.deleteByEmail(email);

        // User 삭제
        userRepository.delete(user);
    }

    // 회원 닉네임 검색
    public List<UserResponseDto> findUserByName(String username, int page, UserSortType sortType){
        List<User> userList = userRepository.findByUserNameLike(username);

        if (userList.isEmpty()){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<UserResponseDto> userDtoList = new ArrayList<>();

        for (User user : userList) {
            UserResponseDto userDto = convertToUserResponseDTO(user);
            userDto.setPassword("****");
            userDtoList.add(userDto);
        }

        sortBySortType(userDtoList, sortType);

        return getPagedUser(userDtoList, page);
    }

    private static void sortBySortType(List<UserResponseDto> userDtoList, UserSortType sortType){
        if (sortType == UserSortType.ABC_ASC){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return o1.getUsername().compareTo(o2.getUsername());
                }
            });
        } else if (sortType == UserSortType.ABC_DES){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return o2.getUsername().compareTo(o1.getUsername());
                }
            });
        } else if (sortType == UserSortType.SUBSCRIBER_ASC){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return o1.getSubscribers() - o2.getSubscribers();
                }
            });
        } else if (sortType == UserSortType.SUBSCRIBER_DES){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return o2.getSubscribers() - o1.getSubscribers();
                }
            });
        } else if (sortType == UserSortType.RATING_ASC){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return (int)(o1.getAvgRating() - o2.getAvgRating());
                }
            });
        } else if (sortType == UserSortType.RATING_DES){
            userDtoList.sort(new Comparator<UserResponseDto>() {
                @Override
                public int compare(UserResponseDto o1, UserResponseDto o2) {
                    return (int)(o2.getAvgRating() - o1.getAvgRating());
                }
            });
        }
    }

    private static List<UserResponseDto> getPagedUser(List<UserResponseDto> userDtoList, int page){
        List<UserResponseDto> pagedUser = new ArrayList<>();
        final int PAGE_SIZE = 15;

        for (int i = PAGE_SIZE * page; i < PAGE_SIZE * (page + 1); i++){
            if (userDtoList.size() <= i){
                break;
            }

            pagedUser.add(userDtoList.get(i));
        }

        return pagedUser;
    }

    @Transactional
    public String updateSubscribe(String userEmail, String token){
        String ownEmail = jwtUtil.getEmail(token);

        User own = userRepository.findByEmail(ownEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userEmail.equals(ownEmail)){
            throw new CustomException(ErrorCode.SAME_USER);
        }

        user.setSubscribers(user.getSubscribers() + 1);

        Subscription subscription = Subscription.builder()
                        .subscriber(own)
                                .subscribedTo(user)
                                        .build();

        userRepository.save(user);
        subscriptionRepository.save(subscription);

        return user.getUsername() + "(" + user.getEmail() + ") 님을 구독하셨습니다!";
    }

    @Transactional
    public String cancelSubscribe(String userEmail, String token){
        String ownEmail = jwtUtil.getEmail(token);

        List<Subscription> subscriptionList = subscriptionRepository.findByUserEmail(ownEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (subscriptionList.isEmpty()){
            throw new CustomException(ErrorCode.SUBSCRIBE_NOT_FOUND);
        }

        for (Subscription subscription : subscriptionList){
            if (subscription.getSubscribedTo().getEmail().equals(user.getEmail())){
                if (!ownEmail.equals(subscription.getSubscriber().getEmail())){
                    throw new CustomException(ErrorCode.ACCESS_DENIED);
                }

                subscriptionRepository.delete(subscription);
                return user.getUsername() + "(" + user.getEmail() + ") 님을 구독 해지하셨습니다!";
            }
        }

        throw new CustomException(ErrorCode.SUBSCRIBE_NOT_FOUND);
    }
}
