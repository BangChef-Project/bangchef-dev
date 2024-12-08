package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.user.dto.JoinDto;
import com.bangchef.recipe_platform.common.enums.Role;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.util.UUID;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void joinProcess(JoinDto joinDto) throws MessagingException {
        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        String email = joinDto.getEmail();

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 사항입니다.");
        }

        boolean isUsernameExist = userRepository.existsByUsername(username);
        boolean isEmailExist = userRepository.existsByEmail(email);

        if (isUsernameExist) {
            throw new RuntimeException("이미 등록된 닉네임입니다.");
        }
        if (isEmailExist) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 유저 데이터 생성
        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setEnabled(false); // 초기 상태는 비활성화

        // 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        // 유저 저장
        userRepository.save(user);

        // 이메일 발송
        emailService.sendVerificationEmail(email, token);
    }
}

