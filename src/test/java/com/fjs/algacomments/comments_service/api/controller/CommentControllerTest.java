package com.fjs.algacomments.comments_service.api.controller;

import com.fjs.algacomments.comments_service.api.client.model.ModerationInput;
import com.fjs.algacomments.comments_service.api.client.model.ModerationOutput;
import com.fjs.algacomments.comments_service.api.client.ModerationServiceClient;
import com.fjs.algacomments.comments_service.api.exception.GlobalExceptionHandler;
import com.fjs.algacomments.comments_service.api.service.CommentService;
import com.fjs.algacomments.comments_service.domain.model.Comment;
import com.fjs.algacomments.comments_service.domain.model.identifier.TSIDCodec;
import com.fjs.algacomments.comments_service.domain.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentRepository repository;
    @Mock
    private ModerationServiceClient moderationClient;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new CommentController(new CommentService(repository, moderationClient)))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Comentário válido: retorna 201 e salva após aprovação")
    void shouldCreateApprovedComment() throws Exception {
        when(moderationClient.moderate(any())).thenReturn(new ModerationOutput(true, "Approved"));
        when(repository.saveAndFlush(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Excelente conteúdo!","author":"Ana"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.text").value("Excelente conteúdo!"))
                .andExpect(jsonPath("$.author").value("Ana"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn();

        var moderation = ArgumentCaptor.forClass(ModerationInput.class);
        var saved = ArgumentCaptor.forClass(Comment.class);
        var order = inOrder(moderationClient, repository);
        order.verify(moderationClient).moderate(moderation.capture());
        order.verify(repository).saveAndFlush(saved.capture());
        assertThat(moderation.getValue().text()).isEqualTo("Excelente conteúdo!");
        assertThat(moderation.getValue().commentId()).isEqualTo(saved.getValue().getTSID());
        assertThat(saved.getValue().getAuthor()).isEqualTo("Ana");
        assertThat(result.getResponse().getContentAsString()).contains(saved.getValue().getTSID());
    }

    @Test
    @DisplayName("Comentário com palavras proibidas: retorna 422 sem salvar")
    void shouldRejectCommentWithForbiddenWords() throws Exception {
        String reason = "Rejected: Comentário com xingamentos";
        when(moderationClient.moderate(any())).thenReturn(new ModerationOutput(false, reason));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Comentário com xingamentos","author":"Ana"}
                                """))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.detail").value(reason));

        verify(moderationClient).moderate(any());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Timeout na moderação: retorna 504 sem salvar")
    void shouldReturnGatewayTimeoutWhenModerationTimesOut() throws Exception {
        when(moderationClient.moderate(any())).thenThrow(new ResourceAccessException(
                "I/O error on POST request", new SocketTimeoutException("Read timed out")));

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"Excelente conteúdo!","author":"Ana"}
                                """))
                .andExpect(status().isGatewayTimeout())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(504))
                .andExpect(jsonPath("$.title").value("Gateway timeout"))
                .andExpect(jsonPath("$.type").value("errors/gateway-timeout"))
                .andExpect(jsonPath("$.detail").value("Read timed out"));

        verify(moderationClient).moderate(any());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Consulta de comentário inexistente: retorna 404")
    void shouldReturnNotFoundForMissingComment() throws Exception {
        String id = TSIDCodec.encode(123L);
        when(repository.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/comments/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Comment not found: " + id));

        verify(repository).findById(123L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(moderationClient);
    }
}
