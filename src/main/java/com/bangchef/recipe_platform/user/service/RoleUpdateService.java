package com.bangchef.recipe_platform.user.service;

import com.bangchef.recipe_platform.common.enums.RequestStatus;
import com.bangchef.recipe_platform.common.enums.Role;
import com.bangchef.recipe_platform.common.exception.CustomException;
import com.bangchef.recipe_platform.common.exception.ErrorCode;
import com.bangchef.recipe_platform.user.entity.RoleUpdate;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.RoleUpdateRepository;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleUpdateService {

    private final UserRepository userRepository;
    private final RoleUpdateRepository roleUpdateRepository;

    // 등업 요청 생성
    public void createRoleUpdateRequest(String email, Role requestedRole) {
        validateRole(requestedRole);
        User user = getUserByEmail(email);
        validateUserForRoleUpdate(user);
        checkDuplicatePendingRequest(email);

        RoleUpdate roleUpdate = new RoleUpdate();
        roleUpdate.setUser(user);
        roleUpdate.setRequestedRole(requestedRole);
        roleUpdate.setStatus(RequestStatus.PENDING);

        roleUpdateRepository.save(roleUpdate);
    }

    // 사용자의 모든 등업 요청 상태 조회 (모든 요청 반환)
    public List<RoleUpdate> getUserRoleUpdateRequests(String email) {
        return roleUpdateRepository.findByUser_Email(email);
    }

    // 특정 등업 요청 상태 조회 (최신 요청만 반환)
    public String getUserRoleUpdateStatus(String email) {
        RequestStatus status = roleUpdateRepository.findByUser_Email(email)
                .stream()
                .findFirst()
                .map(RoleUpdate::getStatus)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_UPDATE_REQUEST_NOT_FOUND));

        // 한국어로 변환
        return convertStatusToKorean(status);
    }

    // 등업 요청 상태 업데이트
    public void updateRoleUpdateStatus(Long requestId, RequestStatus status) {
        // RoleUpdateRequest 조회
        RoleUpdate roleUpdate = roleUpdateRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_UPDATE_REQUEST_NOT_FOUND));

        // 상태 검증
        if (status == null) {
            throw new CustomException(ErrorCode.INVALID_ROLE_UPDATE_STATUS);
        }

        // 상태 업데이트
        roleUpdate.setStatus(status);
        roleUpdateRepository.save(roleUpdate);

        // 만약 승인되었다면 사용자 Role 업데이트
        if (status == RequestStatus.APPROVED) {
            User user = roleUpdate.getUser();
            user.setRole(roleUpdate.getRequestedRole());
            userRepository.save(user);
        }
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 연관된 RoleUpdate 삭제
        roleUpdateRepository.deleteByUser(user);

        // 사용자 삭제
        userRepository.delete(user);
    }

    // Role 검증
    private void validateRole(Role requestedRole) {
        if (requestedRole != Role.CHEF) {
            throw new CustomException(ErrorCode.INVALID_ROLE_UPDATE_REQUEST);
        }
    }

    // 사용자 검증
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateUserForRoleUpdate(User user) {
        if (user.getRole() == Role.CHEF) {
            throw new CustomException(ErrorCode.ALREADY_CHEF);
        }
    }

    // 중복 PENDING 요청 확인
    private void checkDuplicatePendingRequest(String email) {
        if (roleUpdateRepository.findByUser_EmailAndStatus(email, RequestStatus.PENDING).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_ROLE_UPDATE_REQUEST);
        }
    }

    // 요청 상태를 한국어로 변환
    private String convertStatusToKorean(RequestStatus status) {
        switch (status) {
            case PENDING:
                return "등업 요청 승인을 대기 중입니다.";
            case APPROVED:
                return "등업이 승인되었습니다.";
            case REJECTED:
                return "승인이 거부되었습니다.";
            default:
                return "알 수 없음";
        }
    }
}
