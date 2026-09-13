package com.fjs.algacomments.comments_service.api.service;

import com.fjs.algacomments.comments_service.api.client.model.ModerationInput;
import com.fjs.algacomments.comments_service.api.client.model.ModerationOutput;
import com.fjs.algacomments.comments_service.api.client.ModerationServiceClient;
import com.fjs.algacomments.comments_service.api.commons.CommentFilter;
import com.fjs.algacomments.comments_service.api.commons.CommentSpecifications;
import com.fjs.algacomments.comments_service.api.model.CommentInput;
import com.fjs.algacomments.comments_service.api.model.CommentOutput;
import com.fjs.algacomments.comments_service.domain.model.Comment;
import com.fjs.algacomments.comments_service.domain.model.identifier.TSIDCodec;
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
        ModerationOutput moderationOutput = client.moderate(new ModerationInput(comment.getText(), comment.getTSID()));
        if(!moderationOutput.approved())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, moderationOutput.reason());
    }

    public CommentOutput findById(String tsid){
        long id = TSIDCodec.decode(tsid);
        return repository.findById(id)
                .map(CommentOutput::from)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found: "+tsid)
                );
    }

    public Page<CommentOutput> search(CommentFilter filter, Pageable pageable){
        return repository.findAll(CommentSpecifications.filter(filter), pageable).map(this::convertToModel);
    }

    private CommentOutput convertToModel(Comment comment) {
        return new CommentOutput(comment.getTSID(), comment.getText(), comment.getAuthor(), comment.getCreatedAt());
    }
}
