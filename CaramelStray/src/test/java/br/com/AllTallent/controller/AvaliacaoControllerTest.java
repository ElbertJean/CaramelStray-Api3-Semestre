package br.com.alltallent.controller;

import br.com.alltallent.dto.AvaliacaoRequestDTO;
import br.com.alltallent.dto.RespostaColaboradorRequestDTO;
import br.com.alltallent.dto.RevisaoSupervisorRequestDTO;
import br.com.alltallent.service.AvaliacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarAvaliacao_Exception() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO(null, null, null, null);
        when(avaliacaoService.criarAvaliacaoCompleta(any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<?> response = avaliacaoController.criarAvaliacao(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testSalvarResposta_Exception() {
        RespostaColaboradorRequestDTO request = new RespostaColaboradorRequestDTO(null, null, null, null);
        when(avaliacaoService.salvarOuAtualizarResposta(any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<?> response = avaliacaoController.salvarResposta(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno.", response.getBody());
    }

    @Test
    void testSalvarRevisaoSupervisor_Exception() {
        RevisaoSupervisorRequestDTO request = new RevisaoSupervisorRequestDTO(null, null, null);
        when(avaliacaoService.salvarRevisaoSupervisor(eq(1L), any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<?> response = avaliacaoController.salvarRevisaoSupervisor(1L, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno ao salvar revisão.", response.getBody());
    }
}
