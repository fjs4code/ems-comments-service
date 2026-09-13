package com.fjs.algacomments.comments_service.api.client.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record ModerationOutput(boolean approved, String reason) {}
