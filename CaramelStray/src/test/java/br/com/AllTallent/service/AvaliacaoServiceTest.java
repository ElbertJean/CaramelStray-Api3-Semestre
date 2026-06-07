package br.com.alltallent.service;

import br.com.alltallent.config.CustomUserDetails;
import br.com.alltallent.repository.AvaliacaoFuncionarioRepository;
import br.com.alltallent.repository.AvaliacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AvaliacaoFuncionarioRepository avaliacaoFuncionarioRepository;

    @Mock
    private br.com.alltallent.repository.RespostaColaboradorRepository respostaColaboradorRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockUsuarioLogado(String role) {
        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        org.mockito.Mockito.doReturn(List.of(new SimpleGrantedAuthority(role))).when(userDetails).getAuthorities();
        when(auth.getPrincipal()).thenReturn(userDetails);
        
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testListarTodasAvaliacoes_Admin() {
        mockUsuarioLogado("ROLE_ADMIN");
        List<br.com.alltallent.dto.AvaliacaoResponseDTO> result = avaliacaoService.listarTodasAvaliacoes();
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    void testListarTodasAvaliacoes_Gestor() {
        mockUsuarioLogado("ROLE_GESTOR");
        List<br.com.alltallent.dto.AvaliacaoResponseDTO> result = avaliacaoService.listarTodasAvaliacoes();
        org.junit.jupiter.api.Assertions.assertNotNull(result);
    }

    @Test
    void testBuscarRespostasPorInstancia_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.buscarRespostasPorInstancia(1L));
    }

    @Test
    void testSalvarOuAtualizarResposta_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        br.com.alltallent.dto.RespostaColaboradorRequestDTO request = new br.com.alltallent.dto.RespostaColaboradorRequestDTO(1L, 1L, "", 1L);
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.salvarOuAtualizarResposta(request));
    }

    @Test
    void testSalvarRevisaoSupervisor_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.salvarRevisaoSupervisor(1L, null));
    }

    @Test
    void testBuscarParaResponder_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.buscarParaResponder(1L));
    }

    @Test
    void testFinalizarPeloColaborador_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.finalizarPeloColaborador(1L));
    }

    @Test
    void testBuscarParaRevisao_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.buscarParaRevisao(1L));
    }

    @Test
    void testBuscarDadosRevisao_NotFound() {
        mockUsuarioLogado("ROLE_USER");
        when(avaliacaoFuncionarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.buscarDadosRevisao(1L));
    }

    @Test
    void testBuscarDadosRevisao_Success() {
        when(avaliacaoFuncionarioRepository.existsById(1L)).thenReturn(true);
        
        br.com.alltallent.model.RespostaColaborador resp = new br.com.alltallent.model.RespostaColaborador();
        br.com.alltallent.model.Pergunta p = new br.com.alltallent.model.Pergunta();
        p.setCodigo(1L);
        p.setTextoPergunta("Q1");
        resp.setPergunta(p);
        resp.setRespostaTexto("Resposta");
        
        when(respostaColaboradorRepository.findByAvaliacaoFuncionarioCodigo(1L)).thenReturn(List.of(resp));
        
        List<br.com.alltallent.dto.RevisaoDetalhadaDTO> result = avaliacaoService.buscarDadosRevisao(1L);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
        org.junit.jupiter.api.Assertions.assertEquals("Q1", result.get(0).getPerguntaTexto());
    }
}

