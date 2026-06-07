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
}
