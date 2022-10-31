package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(), commentDto.getText(), null, null, LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                comment.getAuthor() != null ? comment.getAuthor().getName() : null, comment.getCreated());
    }
}
