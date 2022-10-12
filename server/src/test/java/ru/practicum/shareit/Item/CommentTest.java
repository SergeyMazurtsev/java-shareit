package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CommentTest {
    @Test
    void commentEqual() {
        LocalDateTime time = LocalDateTime.now();
        Item item = Item.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test")
                .author(user)
                .item(item)
                .created(time)
                .build();
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Test")
                .author(user)
                .item(item)
                .created(time)
                .build();
        assertThat(comment.getId(), is(comment1.getId()));
        assertThat(comment.getText(), is(comment1.getText()));
        assertThat(comment.getAuthor(), is(comment1.getAuthor()));
        assertThat(comment.getItem(), is(comment1.getItem()));
        assertThat(comment.getCreated(), is(comment1.getCreated()));
        assertThat(comment.hashCode(), is(comment1.hashCode()));

        Comment comment2 = new Comment();
        Comment comment3 = new Comment();
        assertThat(comment2.getItem(), equalTo(comment3.getItem()));
    }
}
