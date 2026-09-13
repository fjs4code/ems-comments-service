package com.fjs.algacomments.comments_service.api.client;

import com.fjs.algacomments.comments_service.api.client.model.ModerationInput;
import com.fjs.algacomments.comments_service.api.client.model.ModerationOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/moderate")
public interface ModerationServiceClient {

    @PostExchange
    ModerationOutput moderate(@RequestBody ModerationInput input);

}
