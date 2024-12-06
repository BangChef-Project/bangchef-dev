package com.bangchef.recipe_platform.user.controller;


import com.bangchef.recipe_platform.user.dto.JoinDto;
import com.bangchef.recipe_platform.user.service.JoinService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;

    }

    @PostMapping("/users/join")
    public ResponseEntity<String> join(@RequestBody JoinDto joinDto) throws MessagingException {
        joinService.joinProcess(joinDto);
        return ResponseEntity.ok("회원가입되었습니다. 이메일 인증을 마쳐주세요.");
    }
}
