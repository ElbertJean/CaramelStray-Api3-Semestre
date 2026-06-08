package br.com.alltallent.controller;

import br.com.alltallent.dto.PerguntaRequestDTO;
import br.com.alltallent.dto.PerguntaResponseDTO;
import br.com.alltallent.service.PerguntaService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class PerguntaControllerTest {

    @Mock
    private PerguntaService perguntaService;

    @InjectMocks
    private PerguntaController perguntaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testCriarPergunta_Success() {
        PerguntaRequestDTO reqDto = new PerguntaRequestDTO("Pergunta?", 10, "dissertativa", List.of());
        PerguntaResponseDTO respDto = new PerguntaResponseDTO(1L, "Pergunta?", 10, "TI");

        when(perguntaService.criarPergunta(any(PerguntaRequestDTO.class))).thenReturn(respDto);

        ResponseEntity<PerguntaResponseDTO> response = perguntaController.criarPergunta(reqDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(respDto, response.getBody());
    }

    @Test
    void testCriarPergunta_NotFound() {
        PerguntaRequestDTO reqDto = new PerguntaRequestDTO("Pergunta?", 10, "dissertativa", List.of());
        when(perguntaService.criarPergunta(any(PerguntaRequestDTO.class))).thenThrow(new EntityNotFoundException());

        ResponseEntity<PerguntaResponseDTO> response = perguntaController.criarPergunta(reqDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testListarTodasPerguntas() {
        PerguntaResponseDTO respDto = new PerguntaResponseDTO(1L, "Pergunta?", 10, "TI");
        when(perguntaService.listarTodas()).thenReturn(List.of(respDto));

        ResponseEntity<List<PerguntaResponseDTO>> response = perguntaController.listarTodasPerguntas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(respDto, response.getBody().get(0));
    }

    @Test
    void testBuscarPerguntaPorId_Success() {
        PerguntaResponseDTO respDto = new PerguntaResponseDTO(1L, "Pergunta?", 10, "TI");
        when(perguntaService.buscarPorId(1L)).thenReturn(respDto);

        ResponseEntity<PerguntaResponseDTO> response = perguntaController.buscarPerguntaPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respDto, response.getBody());
    }

    @Test
    void testBuscarPerguntaPorId_NotFound() {
        when(perguntaService.buscarPorId(1L)).thenThrow(new EntityNotFoundException());

        ResponseEntity<PerguntaResponseDTO> response = perguntaController.buscarPerguntaPorId(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeletarPergunta_Success() {
        ResponseEntity<Void> response = perguntaController.deletarPergunta(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeletarPergunta_NotFound() {
        doThrow(new EntityNotFoundException()).when(perguntaService).deletarPergunta(1L);

        ResponseEntity<Void> response = perguntaController.deletarPergunta(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
