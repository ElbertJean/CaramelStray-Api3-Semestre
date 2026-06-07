package br.com.AllTallent.controller;

import br.com.AllTallent.dto.CompetenciaDTO;
import br.com.AllTallent.model.Competencia;
import br.com.AllTallent.repository.CompetenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CompetenciaControllerTest {

    @Mock
    private CompetenciaRepository competenciaRepository;

    @InjectMocks
    private CompetenciaController competenciaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListar() {
        Competencia comp = new Competencia();
        comp.setCodigo(1);
        comp.setNome("Java");
        comp.setCategoria("Tecnologia");

        when(competenciaRepository.findAll()).thenReturn(List.of(comp));

        ResponseEntity<List<CompetenciaDTO>> response = competenciaController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Java", response.getBody().get(0).nome());
    }

    @Test
    void testBuscarPorIdEncontrado() {
        Competencia comp = new Competencia();
        comp.setCodigo(1);
        comp.setNome("Java");
        comp.setCategoria("Tecnologia");

        when(competenciaRepository.findById(1)).thenReturn(Optional.of(comp));

        ResponseEntity<CompetenciaDTO> response = competenciaController.buscarPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java", response.getBody().nome());
    }

    @Test
    void testBuscarPorIdNaoEncontrado() {
        when(competenciaRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<CompetenciaDTO> response = competenciaController.buscarPorId(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCriar() {
        CompetenciaDTO dto = new CompetenciaDTO(null, "Java", "Tecnologia");
        Competencia compSalva = new Competencia();
        compSalva.setCodigo(1);
        compSalva.setNome("Java");
        compSalva.setCategoria("Tecnologia");

        when(competenciaRepository.existsByNomeIgnoreCase("Java")).thenReturn(false);
        when(competenciaRepository.save(any(Competencia.class))).thenReturn(compSalva);

        ResponseEntity<CompetenciaDTO> response = competenciaController.criar(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().id());
    }

    @Test
    void testAtualizar() {
        CompetenciaDTO dto = new CompetenciaDTO(null, "Java Atualizado", "Tecnologia");
        Competencia compExistente = new Competencia();
        compExistente.setCodigo(1);
        compExistente.setNome("Java");
        compExistente.setCategoria("Tecnologia");

        when(competenciaRepository.findById(1)).thenReturn(Optional.of(compExistente));
        when(competenciaRepository.save(any(Competencia.class))).thenReturn(compExistente);

        ResponseEntity<CompetenciaDTO> response = competenciaController.atualizar(1, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java Atualizado", response.getBody().nome()); // Comp mudou internamente antes do save mockado retornar
    }

    @Test
    void testDeletar() {
        when(competenciaRepository.existsById(1)).thenReturn(true);
        ResponseEntity<Void> response = competenciaController.deletar(1);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
