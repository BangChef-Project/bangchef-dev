package com.bangchef.recipe_platform.recipe.service;

import com.bangchef.recipe_platform.recipe.dto.RequestCommentDto;
import com.bangchef.recipe_platform.recipe.dto.ResponseCommentDto;
import com.bangchef.recipe_platform.recipe.entity.Comment;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import com.bangchef.recipe_platform.recipe.repository.CommentRepository;
import com.bangchef.recipe_platform.recipe.repository.RecipeRepository;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseCommentDto.Detail createComment(RequestCommentDto.Create requestDto, Long userId) {
        Recipe recipe = recipeRepository.findById(requestDto.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment parent = null;
        if (requestDto.getParentId() != null) {
            parent = commentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        Comment savedComment = commentRepository.save(Comment.builder()
                .recipe(recipe)
                .user(user)
                .parent(parent)
                .content(requestDto.getContent())
                .build());

        return ResponseCommentDto.Detail.builder()
                .commentId(savedComment.getId())
                .parentId(savedComment.getParentId())
                .recipeId(savedComment.getRecipe().getId())
                .userId(savedComment.getUser().getId())
                .username(savedComment.getUser().getUsername())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    @Transactional
    public ResponseCommentDto.Detail updateComment(RequestCommentDto.Update requestDto) {
        Comment comment = commentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setContent(requestDto.getContent());

        return ResponseCommentDto.Detail.builder()
                .commentId(comment.getId())
                .parentId(comment.getParentId())
                .recipeId(comment.getRecipe().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
