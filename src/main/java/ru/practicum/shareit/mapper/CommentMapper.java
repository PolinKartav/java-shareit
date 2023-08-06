package ru.practicum.shareit.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateCommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .id((long) comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();

    }

    public Comment toCommentFromCreateUpdateCommentDto(CreateUpdateCommentDto createUpdateCommentDto) {
        return Comment.builder()
                .text(createUpdateCommentDto.getText())
                .build();

    }
}
