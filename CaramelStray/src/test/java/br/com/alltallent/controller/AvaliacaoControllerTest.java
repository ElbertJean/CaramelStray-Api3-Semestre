package br.com.alltallent.controller;

import br.com.alltallent.dto.AvaliacaoRequestDTO;
import br.com.alltallent.dto.AvaliacaoResponseDTO;
import br.com.alltallent.dto.AvaliacaoDetalhadaDTO;
import br.com.alltallent.dto.AvaliacaoFuncionarioResponseDTO;
import br.com.alltallent.dto.RespostaColaboradorRequestDTO;
import br.com.alltallent.dto.RespostaColaboradorResponseDTO;
import br.com.alltallent.dto.RevisaoDetalhadaDTO;
import br.com.alltallent.dto.RevisaoSupervisorRequestDTO;
import br.com.alltallent.dto.AvaliacaoParaResponderDTO;
import br.com.alltallent.exception.ResourceNotFoundException;
import br.com.alltallent.model.Avaliacao;
import br.com.alltallent.model.AvaliacaoFuncionario;
import br.com.alltallent.service.AvaliacaoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private Avaliacao makeDummyAvaliacao() {
        Avaliacao a = new Avaliacao();
        a.setCodigo(1);
        a.setTitulo("Title");
        a.setStatus("ATIVA");
        a.setDataCriacao(java.time.LocalDate.of(2026, java.time.Month.JUNE, 7));
        a.setDataPrazo(java.time.LocalDate.of(2026, java.time.Month.JUNE, 7));
        return a;
    }

    private AvaliacaoFuncionario makeDummyAvaliacaoFuncionario() {
        AvaliacaoFuncionario af = new AvaliacaoFuncionario();
        af.setCodigo(1L);
        af.setResultadoStatus("PENDENTE");
        af.setComentarioColaborador("Colab comment");
        af.setComentarioSupervisao("Supervisor comment");
        af.setNota(10);
        return af;
    }

    @Test
    void testCriarAvaliacao_Success() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO("Title", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        AvaliacaoResponseDTO responseDto = new AvaliacaoResponseDTO(makeDummyAvaliacao());

        when(avaliacaoService.criarAvaliacaoCompleta(any())).thenReturn(responseDto);

        ResponseEntity<AvaliacaoResponseDTO> response = avaliacaoController.criarAvaliacao(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testCriarAvaliacao_EntityNotFound() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO("Title", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        when(avaliacaoService.criarAvaliacaoCompleta(any())).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<AvaliacaoResponseDTO> response = avaliacaoController.criarAvaliacao(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCriarAvaliacao_Exception() {
        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO(null, null, null, null);
        when(avaliacaoService.criarAvaliacaoCompleta(any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<AvaliacaoResponseDTO> response = avaliacaoController.criarAvaliacao(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testListarTodasAvaliacoes() {
        AvaliacaoResponseDTO dto = new AvaliacaoResponseDTO(makeDummyAvaliacao());
        when(avaliacaoService.listarTodasAvaliacoes()).thenReturn(List.of(dto));

        ResponseEntity<List<AvaliacaoResponseDTO>> response = avaliacaoController.listarTodasAvaliacoes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void testBuscarAvaliacaoDetalhada_Success() {
        AvaliacaoDetalhadaDTO dto = new AvaliacaoDetalhadaDTO(makeDummyAvaliacao());
        when(avaliacaoService.buscarAvaliacaoDetalhada(1)).thenReturn(dto);

        ResponseEntity<AvaliacaoDetalhadaDTO> response = avaliacaoController.buscarAvaliacaoDetalhada(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testBuscarAvaliacaoDetalhada_NotFound() {
        when(avaliacaoService.buscarAvaliacaoDetalhada(1)).thenThrow(new ResourceNotFoundException("Not found"));

        ResponseEntity<AvaliacaoDetalhadaDTO> response = avaliacaoController.buscarAvaliacaoDetalhada(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testBuscarInstanciasPorAvaliacao_Success() {
        AvaliacaoFuncionarioResponseDTO dto = new AvaliacaoFuncionarioResponseDTO(makeDummyAvaliacaoFuncionario());
        when(avaliacaoService.buscarInstanciasPorAvaliacao(1)).thenReturn(List.of(dto));

        ResponseEntity<List<AvaliacaoFuncionarioResponseDTO>> response = avaliacaoController.buscarInstanciasPorAvaliacao(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void testBuscarInstanciasPorAvaliacao_NotFound() {
        when(avaliacaoService.buscarInstanciasPorAvaliacao(1)).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<List<AvaliacaoFuncionarioResponseDTO>> response = avaliacaoController.buscarInstanciasPorAvaliacao(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSalvarResposta_Success() {
        RespostaColaboradorRequestDTO request = new RespostaColaboradorRequestDTO(1L, 10L, "Answer", null);
        RespostaColaboradorResponseDTO responseDto = new RespostaColaboradorResponseDTO(100L, 1L, 10L, "Answer", null);
        when(avaliacaoService.salvarOuAtualizarResposta(any())).thenReturn(responseDto);

        ResponseEntity<Object> response = avaliacaoController.salvarResposta(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testSalvarResposta_BadRequest() {
        RespostaColaboradorRequestDTO request = new RespostaColaboradorRequestDTO(1L, 10L, "Answer", null);
        when(avaliacaoService.salvarOuAtualizarResposta(any())).thenThrow(new IllegalArgumentException("Inconsistent"));

        ResponseEntity<Object> response = avaliacaoController.salvarResposta(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSalvarResposta_Exception() {
        RespostaColaboradorRequestDTO request = new RespostaColaboradorRequestDTO(null, null, null, null);
        when(avaliacaoService.salvarOuAtualizarResposta(any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<Object> response = avaliacaoController.salvarResposta(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno.", response.getBody());
    }

    @Test
    void testBuscarRespostasPorInstancia_Success() {
        RespostaColaboradorResponseDTO dto = new RespostaColaboradorResponseDTO(100L, 1L, 10L, "Answer", null);
        when(avaliacaoService.buscarRespostasPorInstancia(1L)).thenReturn(List.of(dto));

        ResponseEntity<Object> response = avaliacaoController.buscarRespostasPorInstancia(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto), response.getBody());
    }

    @Test
    void testBuscarRespostasPorInstancia_NotFound() {
        when(avaliacaoService.buscarRespostasPorInstancia(1L)).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<Object> response = avaliacaoController.buscarRespostasPorInstancia(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetDadosParaRevisao_Success() {
        RevisaoDetalhadaDTO dto = RevisaoDetalhadaDTO.builder()
            .perguntaId(10L)
            .perguntaTexto("Question?")
            .respostaDada("Answer")
            .build();
        when(avaliacaoService.buscarDadosRevisao(1L)).thenReturn(List.of(dto));

        ResponseEntity<Object> response = avaliacaoController.getDadosParaRevisao(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(dto), response.getBody());
    }

    @Test
    void testGetDadosParaRevisao_NotFound() {
        when(avaliacaoService.buscarDadosRevisao(1L)).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<Object> response = avaliacaoController.getDadosParaRevisao(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSalvarRevisaoSupervisor_Success() {
        RevisaoSupervisorRequestDTO request = new RevisaoSupervisorRequestDTO("Supervisor note", "Colab note", "APROVADO");
        AvaliacaoFuncionarioResponseDTO responseDto = new AvaliacaoFuncionarioResponseDTO(makeDummyAvaliacaoFuncionario());
        when(avaliacaoService.salvarRevisaoSupervisor(eq(1L), any())).thenReturn(responseDto);

        ResponseEntity<Object> response = avaliacaoController.salvarRevisaoSupervisor(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testSalvarRevisaoSupervisor_NotFound() {
        RevisaoSupervisorRequestDTO request = new RevisaoSupervisorRequestDTO(null, null, null);
        when(avaliacaoService.salvarRevisaoSupervisor(eq(1L), any())).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<Object> response = avaliacaoController.salvarRevisaoSupervisor(1L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testSalvarRevisaoSupervisor_Exception() {
        RevisaoSupervisorRequestDTO request = new RevisaoSupervisorRequestDTO(null, null, null);
        when(avaliacaoService.salvarRevisaoSupervisor(eq(1L), any())).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<Object> response = avaliacaoController.salvarRevisaoSupervisor(1L, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno ao salvar revisão.", response.getBody());
    }

    @Test
    void testBuscarAvaliacoesPendentes() {
        AvaliacaoFuncionarioResponseDTO dto = new AvaliacaoFuncionarioResponseDTO(makeDummyAvaliacaoFuncionario());
        when(avaliacaoService.buscarPendentesPorFuncionario(100)).thenReturn(List.of(dto));

        ResponseEntity<List<AvaliacaoFuncionarioResponseDTO>> response = avaliacaoController.buscarAvaliacoesPendentes(100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(dto, response.getBody().get(0));
    }

    @Test
    void testBuscarAvaliacaoParaResponder_Success() {
        AvaliacaoParaResponderDTO dto = new AvaliacaoParaResponderDTO(1L, "Title", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of());
        when(avaliacaoService.buscarParaResponder(1L)).thenReturn(dto);

        ResponseEntity<Object> response = avaliacaoController.buscarAvaliacaoParaResponder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testBuscarAvaliacaoParaResponder_NotFound() {
        when(avaliacaoService.buscarParaResponder(1L)).thenThrow(new EntityNotFoundException("Not found"));

        ResponseEntity<Object> response = avaliacaoController.buscarAvaliacaoParaResponder(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFinalizarAvaliacaoColaborador_Success() {
        ResponseEntity<Void> response = avaliacaoController.finalizarAvaliacaoColaborador(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testFinalizarAvaliacaoColaborador_NotFound() {
        doThrow(new EntityNotFoundException("Not found")).when(avaliacaoService).finalizarPeloColaborador(1L);

        ResponseEntity<Void> response = avaliacaoController.finalizarAvaliacaoColaborador(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testFinalizarAvaliacaoColaborador_Conflict() {
        doThrow(new IllegalStateException("Already finalized")).when(avaliacaoService).finalizarPeloColaborador(1L);

        ResponseEntity<Void> response = avaliacaoController.finalizarAvaliacaoColaborador(1L);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
