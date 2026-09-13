package com.fjs.algacomments.comments_service.api.client.impl;

import com.fjs.algacomments.comments_service.api.client.model.ModerationInput;
import com.fjs.algacomments.comments_service.api.client.model.ModerationOutput;
import com.fjs.algacomments.comments_service.api.client.ModerationServiceClient;
import org.springframework.web.client.RestClient;

public class ModerationServiceClientImpl implements ModerationServiceClient {

    private final RestClient restClient;

    public ModerationServiceClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ModerationOutput moderate(ModerationInput input) {
        return restClient.post()
                .uri("/api/moderate",input)
                .retrieve()
                .body(ModerationOutput.class);
    }
}
