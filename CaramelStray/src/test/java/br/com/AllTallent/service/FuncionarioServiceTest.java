package br.com.alltallent.service;

import br.com.alltallent.exception.ResourceNotFoundException;
import br.com.alltallent.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuscarPorId_NotFound() {
        when(funcionarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.buscarPorId(1));
    }

    @Test
    void testAtualizar_NotFound() {
        when(funcionarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.atualizar(1, null));
    }

    @Test
    void testDeletar_NotFound() {
        when(funcionarioRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.deletar(1));
    }

    @Test
    void testBuscarPerfilPorId_NotFound() {
        when(funcionarioRepository.findByIdCompleto(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.buscarPerfilPorId(1));
    }

    @Test
    void testAdicionarCertificado_NotFound() {
        when(funcionarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> funcionarioService.adicionarCertificado(1, null));
    }

    @Test
    void testBuscarFuncionarioCompleto_NotFound() {
        when(funcionarioRepository.findByIdCompleto(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.buscarFuncionarioCompleto(1));
    }

    @Test
    void testListarExperiencias_NotFound() {
        when(funcionarioRepository.findByIdCompleto(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.listarExperienciasPorFuncionario(1));
    }

    @Test
    void testAdicionarExperiencia_NotFound() {
        when(funcionarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> funcionarioService.adicionarExperiencia(1, null));
    }

    @Test
    void testBuscarPorId_Success() {
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        br.com.alltallent.model.Perfil p = new br.com.alltallent.model.Perfil();
        p.setNome("Admin");
        f.setPerfil(p);
        br.com.alltallent.model.Area a = new br.com.alltallent.model.Area();
        a.setNome("TI");
        f.setArea(a);

        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(f));
        br.com.alltallent.dto.FuncionarioResponseDTO result = funcionarioService.buscarPorId(1);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.codigo());
    }

    @Test
    void testListarTodos_SemTexto() {
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Perfil p = new br.com.alltallent.model.Perfil();
        p.setNome("Admin");
        f.setPerfil(p);
        br.com.alltallent.model.Area a = new br.com.alltallent.model.Area();
        a.setNome("TI");
        f.setArea(a);
        when(funcionarioRepository.findAll()).thenReturn(java.util.List.of(f));

        java.util.List<br.com.alltallent.dto.FuncionarioResponseDTO> list = funcionarioService.listarTodos(null);
        org.junit.jupiter.api.Assertions.assertEquals(1, list.size());
    }

    @Test
    void testListarTodos_ComTexto() {
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Perfil p = new br.com.alltallent.model.Perfil();
        p.setNome("Admin");
        f.setPerfil(p);
        br.com.alltallent.model.Area a = new br.com.alltallent.model.Area();
        a.setNome("TI");
        f.setArea(a);
        when(funcionarioRepository.buscarPorTexto("Maria")).thenReturn(java.util.List.of(f));

        java.util.List<br.com.alltallent.dto.FuncionarioResponseDTO> list = funcionarioService.listarTodos("Maria");
        org.junit.jupiter.api.Assertions.assertEquals(1, list.size());
    }
}
