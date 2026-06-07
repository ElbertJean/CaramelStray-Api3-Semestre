package br.com.alltallent.controller;

import br.com.alltallent.dto.CertificadoDTO;
import br.com.alltallent.dto.CertificadoRequestDTO;
import br.com.alltallent.dto.ExperienciaDTO;
import br.com.alltallent.dto.ExperienciaRequestDTO;
import br.com.alltallent.dto.FuncionarioCompetenciaUpdateDTO;
import br.com.alltallent.dto.FuncionarioCompetenciasResponseDTO;
import br.com.alltallent.dto.FuncionarioExperienciasResponseDTO;
import br.com.alltallent.dto.FuncionarioPerfilDTO;
import br.com.alltallent.dto.FuncionarioRequestDTO;
import br.com.alltallent.dto.FuncionarioResponseDTO;
import br.com.alltallent.exception.ResourceNotFoundException;
import br.com.alltallent.exception.UnauthorizedActionException;
import br.com.alltallent.model.Funcionario;
import br.com.alltallent.service.FuncionarioService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FuncionarioControllerTest {

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testListarTodos() {
        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria");
        FuncionarioResponseDTO fDto = new FuncionarioResponseDTO(f);
        when(funcionarioService.listarTodos("Maria")).thenReturn(List.of(fDto));

        ResponseEntity<List<FuncionarioResponseDTO>> response = funcionarioController.listarTodos("Maria");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testBuscarPorId() {
        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria");
        FuncionarioResponseDTO fDto = new FuncionarioResponseDTO(f);
        when(funcionarioService.buscarPorId(1)).thenReturn(fDto);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioController.buscarPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fDto, response.getBody());
    }

    @Test
    void testCriar() {
        FuncionarioRequestDTO req = new FuncionarioRequestDTO(
            "Maria", "maria@test.com", "12345678901", "119", "plainTextMockValue123", 10, 2, null, "Dev", "SP", "Resumo"
        );
        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria");
        FuncionarioResponseDTO fDto = new FuncionarioResponseDTO(f);
        when(funcionarioService.criar(any(FuncionarioRequestDTO.class))).thenReturn(fDto);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioController.criar(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fDto, response.getBody());
    }

    @Test
    void testAtualizar() {
        FuncionarioRequestDTO req = new FuncionarioRequestDTO(
            "Maria", "maria@test.com", "12345678901", "119", "plainTextMockValue123", 10, 2, null, "Dev", "SP", "Resumo"
        );
        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria");
        FuncionarioResponseDTO fDto = new FuncionarioResponseDTO(f);
        when(funcionarioService.atualizar(eq(1), any(FuncionarioRequestDTO.class))).thenReturn(fDto);

        ResponseEntity<FuncionarioResponseDTO> response = funcionarioController.atualizar(1, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fDto, response.getBody());
    }

    @Test
    void testDeletar() {
        ResponseEntity<Void> response = funcionarioController.deletar(1);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(funcionarioService, times(1)).deletar(1);
    }

    @Test
    void testBuscarPerfilPorId() {
        FuncionarioPerfilDTO pDto = mock(FuncionarioPerfilDTO.class);
        when(funcionarioService.buscarPerfilPorId(1)).thenReturn(pDto);

        ResponseEntity<FuncionarioPerfilDTO> response = funcionarioController.buscarPerfilPorId(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pDto, response.getBody());
    }

    @Test
    void testAdicionarCertificado() {
        CertificadoRequestDTO req = new CertificadoRequestDTO("Cert");
        CertificadoDTO res = new CertificadoDTO(1, "Cert");
        when(funcionarioService.adicionarCertificado(eq(1), any(CertificadoRequestDTO.class))).thenReturn(res);

        ResponseEntity<CertificadoDTO> response = funcionarioController.adicionarCertificado(1, req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(res, response.getBody());
    }

    @Test
    void testRemoverCertificado() {
        ResponseEntity<Void> response = funcionarioController.removerCertificado(10);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(funcionarioService, times(1)).removerCertificado(10);
    }

    @Test
    void testAtualizarCompetencias_Success() {
        FuncionarioCompetenciaUpdateDTO req = new FuncionarioCompetenciaUpdateDTO(List.of(1, 2));
        doNothing().when(funcionarioService).associarCompetencias(1, List.of(1, 2));

        ResponseEntity<Void> response = funcionarioController.atualizarCompetencias(1, req);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testAtualizarCompetencias_NotFound() {
        FuncionarioCompetenciaUpdateDTO req = new FuncionarioCompetenciaUpdateDTO(List.of(1, 2));
        doThrow(new ResourceNotFoundException("Not found")).when(funcionarioService).associarCompetencias(1, List.of(1, 2));

        ResponseEntity<Void> response = funcionarioController.atualizarCompetencias(1, req);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAtualizarCompetencias_Unauthorized() {
        FuncionarioCompetenciaUpdateDTO req = new FuncionarioCompetenciaUpdateDTO(List.of(1, 2));
        doThrow(new UnauthorizedActionException("Forbidden")).when(funcionarioService).associarCompetencias(1, List.of(1, 2));

        ResponseEntity<Void> response = funcionarioController.atualizarCompetencias(1, req);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testListarCompetenciasPorFuncionario() {
        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setCompetencias(java.util.Collections.emptySet());
        when(funcionarioService.buscarFuncionarioCompleto(1)).thenReturn(f);

        ResponseEntity<FuncionarioCompetenciasResponseDTO> response = funcionarioController.listarCompetenciasPorFuncionario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testListarExperienciasPorFuncionario() {
        FuncionarioExperienciasResponseDTO experiences = mock(FuncionarioExperienciasResponseDTO.class);
        when(funcionarioService.listarExperienciasPorFuncionario(1)).thenReturn(experiences);

        ResponseEntity<FuncionarioExperienciasResponseDTO> response = funcionarioController.listarExperienciasPorFuncionario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAdicionarExperiencia() {
        ExperienciaRequestDTO req = new ExperienciaRequestDTO("Cargo", "Empresa", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"), "Desc");
        ExperienciaDTO res = new ExperienciaDTO(1, "Cargo", "Empresa", "Desc", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"));
        when(funcionarioService.adicionarExperiencia(eq(1), any(ExperienciaRequestDTO.class))).thenReturn(res);

        ResponseEntity<ExperienciaDTO> response = funcionarioController.adicionarExperiencia(1, req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(res, response.getBody());
    }

    @Test
    void testAtualizarExperiencia() {
        ExperienciaRequestDTO req = new ExperienciaRequestDTO("Cargo", "Empresa", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"), "Desc");
        ExperienciaDTO res = new ExperienciaDTO(10, "Cargo", "Empresa", "Desc", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"));
        when(funcionarioService.atualizarExperiencia(eq(10), any(ExperienciaRequestDTO.class))).thenReturn(res);

        ResponseEntity<ExperienciaDTO> response = funcionarioController.atualizarExperiencia(10, req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(res, response.getBody());
    }
}
