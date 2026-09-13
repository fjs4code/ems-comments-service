package com.fjs.algacomments.comments_service.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            SocketTimeoutException.class,
            ConnectException.class,
            ClosedChannelException.class
    })
    public ProblemDetail handle(IOException e){
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);
        detail.setTitle("Gateway timeout");
        detail.setDetail(e.getMessage());
        detail.setType(URI.create("errors/gateway-timeout"));
        return detail;
    }

    @ExceptionHandler(ModerationServiceClientBadGatewayException.class)
    public ProblemDetail handle(ModerationServiceClientBadGatewayException e){
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle("Bad gateway");
        detail.setDetail(e.getMessage());
        detail.setType(URI.create("errors/bad-gateway"));
        return detail;
    }

}
