package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CommentMapperTest {
    private Comment comment;
    private CommentDto commentDto;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(User.builder().name("Test user").build())
                .created(time)
                .build();
        commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    @Test
    void toComment() {
        Comment testComment = CommentMapper.toComment(commentDto);

        assertThat(testComment.getText(), equalTo(comment.getText()));
        assertThat(testComment.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void toCommentDto() {
        CommentDto testCommentDto = CommentMapper.toCommentDto(comment);

        assertThat(testCommentDto.getId(), equalTo(commentDto.getId()));
        assertThat(testCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(testCommentDto.getAuthor(), equalTo(commentDto.getAuthor()));
        assertThat(testCommentDto.getCreated(), equalTo(commentDto.getCreated()));
    }
}
