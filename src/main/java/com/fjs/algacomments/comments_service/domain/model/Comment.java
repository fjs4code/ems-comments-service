package com.fjs.algacomments.comments_service.domain.model;

import com.fjs.algacomments.comments_service.api.model.CommentInput;
import com.fjs.algacomments.comments_service.domain.model.identifier.TSIDEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment extends TSIDEntity {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    @Column(nullable = false)
    private String author;
    private OffsetDateTime createdAt;

    public Comment(CommentInput input){
        generateId();
        this.text = input.text();
        this.author = input.author();
        this.createdAt = OffsetDateTime.now();
    }
}
