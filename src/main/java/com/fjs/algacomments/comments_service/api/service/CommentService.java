package com.fjs.algacomments.comments_service.api.service;

import com.fjs.algacomments.comments_service.api.client.model.ModerationInput;
import com.fjs.algacomments.comments_service.api.client.model.ModerationOutput;
import com.fjs.algacomments.comments_service.api.client.ModerationServiceClient;
import com.fjs.algacomments.comments_service.api.commons.CommentFilter;
import com.fjs.algacomments.comments_service.api.commons.CommentSpecifications;
import com.fjs.algacomments.comments_service.api.model.CommentInput;
import com.fjs.algacomments.comments_service.api.model.CommentOutput;
import com.fjs.algacomments.comments_service.domain.model.Comment;
import java.util.UUID;
import com.fjs.algacomments.comments_service.domain.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final ModerationServiceClient client;

    @Transactional(rollbackOn = Exception.class)
    public CommentOutput create(CommentInput input){
        var comment = new Comment(input);
        validateWithCommentService(comment);

        Comment commentSaved = repository.saveAndFlush(comment);
        return CommentOutput.from(commentSaved);
    }

    private void validateWithCommentService(Comment comment) {
        ModerationOutput moderationOutput = client.moderate(new ModerationInput(comment.getText(), comment.getId().toString()));
        if(!moderationOutput.approved())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, moderationOutput.reason());
    }

    public CommentOutput findById(String uuid){
        UUID id;
        try {
            id = UUID.fromString(uuid);
            if (!id.toString().equalsIgnoreCase(uuid)) {
                throw new IllegalArgumentException("Non-canonical UUID");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID: " + uuid, e);
        }
        return repository.findById(id)
                .map(CommentOutput::from)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found: "+uuid)
                );
    }

    public Page<CommentOutput> search(CommentFilter filter, Pageable pageable){
        return repository.findAll(CommentSpecifications.filter(filter), pageable).map(this::convertToModel);
    }

    private CommentOutput convertToModel(Comment comment) {
        return new CommentOutput(comment.getId().toString(), comment.getText(), comment.getAuthor(), comment.getCreatedAt());
    }
}
