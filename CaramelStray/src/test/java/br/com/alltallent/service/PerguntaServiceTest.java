package br.com.alltallent.service;

import br.com.alltallent.dto.OpcaoRequest;
import br.com.alltallent.dto.PerguntaRequestDTO;
import br.com.alltallent.dto.PerguntaResponseDTO;
import br.com.alltallent.model.Competencia;
import br.com.alltallent.model.Pergunta;
import br.com.alltallent.repository.CompetenciaRepository;
import br.com.alltallent.repository.PerguntaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PerguntaServiceTest {

    @Mock
    private PerguntaRepository perguntaRepository;

    @Mock
    private CompetenciaRepository competenciaRepository;

    @InjectMocks
    private PerguntaService perguntaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCriarPergunta_Multipla_Sucesso() {
        PerguntaRequestDTO request = new PerguntaRequestDTO(
                "Qual a cor do céu?",
                1,
                "múltipla escolha",
                List.of(new OpcaoRequest("Azul", true), new OpcaoRequest("Verde", false)));

        Competencia comp = new Competencia();
        comp.setCodigo(1);
        when(competenciaRepository.findById(1)).thenReturn(Optional.of(comp));

        Pergunta saved = new Pergunta();
        saved.setCodigo(100L);
        saved.setTextoPergunta(request.pergunta());
        when(perguntaRepository.save(any(Pergunta.class))).thenReturn(saved);

        PerguntaResponseDTO response = perguntaService.criarPergunta(request);

        assertNotNull(response);
        assertEquals(100L, response.codigo());
        verify(perguntaRepository, times(1)).save(any(Pergunta.class));
    }

    @Test
    void testCriarPergunta_Dissertativa_Sucesso() {
        PerguntaRequestDTO request = new PerguntaRequestDTO(
                "Descreva-se.",
                1,
                "dissertativa",
                null);

        Competencia comp = new Competencia();
        comp.setCodigo(1);
        when(competenciaRepository.findById(1)).thenReturn(Optional.of(comp));

        Pergunta saved = new Pergunta();
        saved.setCodigo(101L);
        saved.setTextoPergunta(request.pergunta());
        when(perguntaRepository.save(any(Pergunta.class))).thenReturn(saved);

        PerguntaResponseDTO response = perguntaService.criarPergunta(request);

        assertNotNull(response);
        assertEquals(101L, response.codigo());
    }

    @Test
    void testCriarPergunta_CompetenciaNotFound() {
        PerguntaRequestDTO request = new PerguntaRequestDTO("T?", 99, "múltipla escolha", null);
        when(competenciaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> perguntaService.criarPergunta(request));
    }

    @Test
    void testListarTodas() {
        Pergunta p1 = new Pergunta();
        p1.setCodigo(1L);
        p1.setTextoPergunta("Q1");

        when(perguntaRepository.findAll()).thenReturn(List.of(p1));

        List<PerguntaResponseDTO> list = perguntaService.listarTodas();
        assertEquals(1, list.size());
    }

    @Test
    void testBuscarPorId_Sucesso() {
        Pergunta p = new Pergunta();
        p.setCodigo(1L);
        when(perguntaRepository.findById(1L)).thenReturn(Optional.of(p));

        PerguntaResponseDTO resp = perguntaService.buscarPorId(1L);
        assertNotNull(resp);
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(perguntaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> perguntaService.buscarPorId(99L));
    }

    @Test
    void testDeletarPergunta_Sucesso() {
        when(perguntaRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> perguntaService.deletarPergunta(1L));
        verify(perguntaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletarPergunta_NotFound() {
        when(perguntaRepository.existsById(99L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> perguntaService.deletarPergunta(99L));
    }
}
