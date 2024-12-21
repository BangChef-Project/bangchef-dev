package com.bangchef.recipe_platform.recipe.controller;

import com.bangchef.recipe_platform.recipe.dto.RequestCommentDto;
import com.bangchef.recipe_platform.recipe.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Validated @RequestBody RequestCommentDto.Create createCommentDto,
                                           @RequestParam Long userId) {

        return ResponseEntity.ok(commentService.createComment(createCommentDto, userId));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateComment(@RequestBody @Validated RequestCommentDto.Update update) {
        return ResponseEntity.ok(commentService.updateComment(update));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
