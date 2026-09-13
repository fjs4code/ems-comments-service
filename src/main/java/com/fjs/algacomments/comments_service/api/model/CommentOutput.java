package com.fjs.algacomments.comments_service.api.model;

import com.fjs.algacomments.comments_service.domain.model.Comment;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder(toBuilder = true)
public record CommentOutput(String id, String text, String author, OffsetDateTime createdAt) {

    public static CommentOutput from(Comment comment){
        return new CommentOutput(
                comment.getTSID(),
                comment.getText(),
                comment.getAuthor(),
                comment.getCreatedAt()
        );
    }

}
