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

    @Mock
    private br.com.alltallent.repository.FuncionarioRepository funcionarioRepository;

    @Mock
    private br.com.alltallent.repository.PerguntaRepository perguntaRepository;

    @Mock
    private br.com.alltallent.repository.PerguntaOpcaoRepository perguntaOpcaoRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CustomUserDetails mockUsuarioLogado(String role) {
        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        org.mockito.Mockito.doReturn(List.of(new SimpleGrantedAuthority(role))).when(userDetails).getAuthorities();
        when(auth.getPrincipal()).thenReturn(userDetails);
        
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        return userDetails;
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

    @Test
    void testBuscarPendentesPorFuncionario() {
        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setResultadoStatus("PENDENTE");
        when(avaliacaoFuncionarioRepository.findByFuncionarioCodigo(1)).thenReturn(List.of(af));
        List<br.com.alltallent.dto.AvaliacaoFuncionarioResponseDTO> result = avaliacaoService.buscarPendentesPorFuncionario(1);
        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFinalizarPeloColaborador_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_USER");
        when(user.getCodigo()).thenReturn(1);
        
        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setResultadoStatus("PENDENTE");
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        af.setFuncionario(f);
        
        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));
        
        avaliacaoService.finalizarPeloColaborador(1L);
        org.junit.jupiter.api.Assertions.assertEquals("AGUARDANDO_REVISAO", af.getResultadoStatus());
    }

    @Test
    void testBuscarParaResponder_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_USER");
        when(user.getCodigo()).thenReturn(1);
        
        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        af.setFuncionario(f);
        
        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(1);
        a.setPerguntas(java.util.Collections.emptySet());
        af.setAvaliacao(a);
        
        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));
        
        br.com.alltallent.dto.AvaliacaoParaResponderDTO dto = avaliacaoService.buscarParaResponder(1L);
        org.junit.jupiter.api.Assertions.assertNotNull(dto);
    }

    @Test
    void testListarTodasAvaliacoes_Admin_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_ADMIN");
        when(user.getAreaId()).thenReturn(10);
        
        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(1);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        a.setCriador(criador);
        
        when(avaliacaoRepository.findAll()).thenReturn(List.of(a));
        
        List<br.com.alltallent.dto.AvaliacaoResponseDTO> result = avaliacaoService.listarTodasAvaliacoes();
        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
    }

    @Test
    void testListarTodasAvaliacoes_Gestor_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getAreaId()).thenReturn(10);
        when(user.getCodigo()).thenReturn(100);
        
        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(1);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        criador.setCodigo(100);
        a.setCriador(criador);
        
        when(avaliacaoRepository.findAll()).thenReturn(List.of(a));
        
        List<br.com.alltallent.dto.AvaliacaoResponseDTO> result = avaliacaoService.listarTodasAvaliacoes();
        org.junit.jupiter.api.Assertions.assertEquals(1, result.size());
    }

    @Test
    void testBuscarAvaliacaoDetalhada_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_ADMIN");
        when(user.getAreaId()).thenReturn(10);
        
        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(1);
        a.setPerguntas(java.util.Collections.emptySet());
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        a.setCriador(criador);
        
        when(avaliacaoRepository.findById(1)).thenReturn(Optional.of(a));
        
        br.com.alltallent.dto.AvaliacaoDetalhadaDTO dto = avaliacaoService.buscarAvaliacaoDetalhada(1);
        org.junit.jupiter.api.Assertions.assertNotNull(dto);
    }

    @Test
    void testCriarAvaliacaoCompleta_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        criador.setCodigo(100);
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        when(funcionarioRepository.getReferenceById(100)).thenReturn(criador);

        br.com.alltallent.model.Pergunta p1 = new br.com.alltallent.model.Pergunta();
        p1.setCodigo(1L);
        when(perguntaRepository.findAllById(List.of(1L))).thenReturn(List.of(p1));

        br.com.alltallent.model.Funcionario alvo = new br.com.alltallent.model.Funcionario();
        alvo.setCodigo(200);
        alvo.setArea(area);
        br.com.alltallent.model.Perfil perfilColab = new br.com.alltallent.model.Perfil();
        perfilColab.setCodigo(3); // Colaborador
        alvo.setPerfil(perfilColab);
        when(funcionarioRepository.findAllById(List.of(200))).thenReturn(List.of(alvo));

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(1);
        a.setCriador(criador);
        a.setPerguntas(java.util.Set.of(p1));
        when(avaliacaoRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.Avaliacao.class))).thenReturn(a);

        br.com.alltallent.dto.AvaliacaoRequestDTO req = new br.com.alltallent.dto.AvaliacaoRequestDTO("Aval", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        br.com.alltallent.dto.AvaliacaoResponseDTO res = avaliacaoService.criarAvaliacaoCompleta(req);
        org.junit.jupiter.api.Assertions.assertNotNull(res);
    }

    @Test
    void testCriarAvaliacaoCompleta_Unauthorized() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        criador.setCodigo(100);
        when(funcionarioRepository.getReferenceById(100)).thenReturn(criador);

        br.com.alltallent.model.Pergunta p1 = new br.com.alltallent.model.Pergunta();
        p1.setCodigo(1L);
        when(perguntaRepository.findAllById(List.of(1L))).thenReturn(List.of(p1));

        // Target employee in different department
        br.com.alltallent.model.Funcionario alvo = new br.com.alltallent.model.Funcionario();
        alvo.setCodigo(200);
        br.com.alltallent.model.Area areaDiff = new br.com.alltallent.model.Area();
        areaDiff.setCodigo(20);
        alvo.setArea(areaDiff);
        br.com.alltallent.model.Perfil perfilColab = new br.com.alltallent.model.Perfil();
        perfilColab.setCodigo(3);
        alvo.setPerfil(perfilColab);
        when(funcionarioRepository.findAllById(List.of(200))).thenReturn(List.of(alvo));

        br.com.alltallent.dto.AvaliacaoRequestDTO req = new br.com.alltallent.dto.AvaliacaoRequestDTO("Aval", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> avaliacaoService.criarAvaliacaoCompleta(req));
    }

    @Test
    void testCriarAvaliacaoCompleta_QuestionsNotFound() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);
        when(perguntaRepository.findAllById(List.of(1L))).thenReturn(List.of()); // Returns empty, expected 1

        br.com.alltallent.dto.AvaliacaoRequestDTO req = new br.com.alltallent.dto.AvaliacaoRequestDTO("Aval", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.criarAvaliacaoCompleta(req));
    }

    @Test
    void testCriarAvaliacaoCompleta_EmployeesNotFound() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);

        br.com.alltallent.model.Pergunta p1 = new br.com.alltallent.model.Pergunta();
        p1.setCodigo(1L);
        when(perguntaRepository.findAllById(List.of(1L))).thenReturn(List.of(p1));
        when(funcionarioRepository.findAllById(List.of(200))).thenReturn(List.of()); // Returns empty, expected 1

        br.com.alltallent.dto.AvaliacaoRequestDTO req = new br.com.alltallent.dto.AvaliacaoRequestDTO("Aval", java.time.LocalDate.of(2026, java.time.Month.JUNE, 7), List.of(200), List.of(1L));
        assertThrows(EntityNotFoundException.class, () -> avaliacaoService.criarAvaliacaoCompleta(req));
    }

    @Test
    void testSalvarOuAtualizarResposta_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_USER");
        when(user.getCodigo()).thenReturn(200);

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(200);
        af.setFuncionario(f);
        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));

        br.com.alltallent.model.Pergunta p = new br.com.alltallent.model.Pergunta();
        p.setCodigo(10L);
        when(perguntaRepository.findById(10L)).thenReturn(Optional.of(p));

        br.com.alltallent.model.PerguntaOpcao opt = new br.com.alltallent.model.PerguntaOpcao();
        opt.setCodigo(30L);
        opt.setPergunta(p);
        when(perguntaOpcaoRepository.findById(30L)).thenReturn(Optional.of(opt));

        br.com.alltallent.model.RespostaColaborador resp = new br.com.alltallent.model.RespostaColaborador();
        resp.setCodigo(500L);
        resp.setPergunta(p);
        resp.setRespostaTexto("Text Response");
        resp.setOpcaoSelecionada(opt);
        when(respostaColaboradorRepository.findByFuncionarioAvaliacaoCodigoAndPerguntaCodigo(1L, 10L)).thenReturn(Optional.empty());
        when(respostaColaboradorRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.RespostaColaborador.class))).thenReturn(resp);

        br.com.alltallent.dto.RespostaColaboradorRequestDTO dto = new br.com.alltallent.dto.RespostaColaboradorRequestDTO(1L, 10L, "Text Response", 30L);
        br.com.alltallent.dto.RespostaColaboradorResponseDTO response = avaliacaoService.salvarOuAtualizarResposta(dto);

        org.junit.jupiter.api.Assertions.assertNotNull(response);
    }

    @Test
    void testSalvarOuAtualizarResposta_InconsistentOption() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_USER");
        when(user.getCodigo()).thenReturn(200);

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(200);
        af.setFuncionario(f);
        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));

        br.com.alltallent.model.Pergunta p = new br.com.alltallent.model.Pergunta();
        p.setCodigo(10L);
        when(perguntaRepository.findById(10L)).thenReturn(Optional.of(p));

        br.com.alltallent.model.Pergunta pOther = new br.com.alltallent.model.Pergunta();
        pOther.setCodigo(99L);

        br.com.alltallent.model.PerguntaOpcao opt = new br.com.alltallent.model.PerguntaOpcao();
        opt.setCodigo(30L);
        opt.setPergunta(pOther); // Belongs to different question
        when(perguntaOpcaoRepository.findById(30L)).thenReturn(Optional.of(opt));

        br.com.alltallent.dto.RespostaColaboradorRequestDTO dto = new br.com.alltallent.dto.RespostaColaboradorRequestDTO(1L, 10L, "Text Response", 30L);
        assertThrows(IllegalArgumentException.class, () -> avaliacaoService.salvarOuAtualizarResposta(dto));
    }

    @Test
    void testSalvarOuAtualizarResposta_Unauthorized() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_USER");
        when(user.getCodigo()).thenReturn(200);

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(300); // User is 200, trying to save for 300
        af.setFuncionario(f);
        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));

        br.com.alltallent.dto.RespostaColaboradorRequestDTO dto = new br.com.alltallent.dto.RespostaColaboradorRequestDTO(1L, 10L, "Text Response", 30L);
        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> avaliacaoService.salvarOuAtualizarResposta(dto));
    }

    @Test
    void testSalvarRevisaoSupervisor_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setCodigo(1L);
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(200);
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        f.setArea(area);
        br.com.alltallent.model.Perfil perfilColab = new br.com.alltallent.model.Perfil();
        perfilColab.setCodigo(3);
        f.setPerfil(perfilColab);
        af.setFuncionario(f);

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        af.setAvaliacao(a);

        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));
        when(avaliacaoFuncionarioRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.AvaliacaoFuncionario.class))).thenReturn(af);

        br.com.alltallent.dto.RevisaoSupervisorRequestDTO dto = new br.com.alltallent.dto.RevisaoSupervisorRequestDTO("Revisado", "Muito bom", "APROVADO");
        br.com.alltallent.dto.AvaliacaoFuncionarioResponseDTO res = avaliacaoService.salvarRevisaoSupervisor(1L, dto);

        org.junit.jupiter.api.Assertions.assertNotNull(res);
    }

    @Test
    void testBuscarParaRevisao_Success() {
        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setCodigo(1L);
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(200);
        af.setFuncionario(f);
        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        a.setPerguntas(java.util.Collections.emptySet());
        af.setAvaliacao(a);
        af.setRespostas(java.util.Collections.emptySet());

        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));

        br.com.alltallent.dto.AvaliacaoRevisaoDTO res = avaliacaoService.buscarParaRevisao(1L);
        org.junit.jupiter.api.Assertions.assertNotNull(res);
    }

    @Test
    void testBuscarInstanciasPorAvaliacao_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_ADMIN");
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        a.setCriador(criador);

        when(avaliacaoRepository.findById(5)).thenReturn(Optional.of(a));

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setCodigo(1L);
        af.setFuncionario(criador);
        af.setAvaliacao(a);
        when(avaliacaoFuncionarioRepository.findByAvaliacaoCodigo(5)).thenReturn(List.of(af));

        List<br.com.alltallent.dto.AvaliacaoFuncionarioResponseDTO> list = avaliacaoService.buscarInstanciasPorAvaliacao(5);
        org.junit.jupiter.api.Assertions.assertEquals(1, list.size());
    }

    @Test
    void testBuscarRespostasPorInstancia_Success() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_ADMIN");
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        a.setCriador(criador);

        br.com.alltallent.model.AvaliacaoFuncionario af = new br.com.alltallent.model.AvaliacaoFuncionario();
        af.setCodigo(1L);
        af.setFuncionario(criador);
        af.setAvaliacao(a);

        when(avaliacaoFuncionarioRepository.findById(1L)).thenReturn(Optional.of(af));

        br.com.alltallent.model.RespostaColaborador r = new br.com.alltallent.model.RespostaColaborador();
        r.setCodigo(100L);
        br.com.alltallent.model.Pergunta p = new br.com.alltallent.model.Pergunta();
        p.setCodigo(10L);
        r.setPergunta(p);
        when(respostaColaboradorRepository.findByAvaliacaoFuncionarioCodigo(1L)).thenReturn(List.of(r));

        List<br.com.alltallent.dto.RespostaColaboradorResponseDTO> list = avaliacaoService.buscarRespostasPorInstancia(1L);
        org.junit.jupiter.api.Assertions.assertEquals(1, list.size());
    }

    @Test
    void testValidarPermissaoDeAcesso_Unauthorized_DifferentArea() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_ADMIN");
        when(user.getAreaId()).thenReturn(10); // User is area 10

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(20); // Assessment is area 20
        criador.setArea(area);
        a.setCriador(criador);

        when(avaliacaoRepository.findById(5)).thenReturn(Optional.of(a));

        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> avaliacaoService.buscarAvaliacaoDetalhada(5));
    }

    @Test
    void testValidarPermissaoDeAcesso_Unauthorized_SupervisorNotCreator() {
        CustomUserDetails user = mockUsuarioLogado("ROLE_GESTOR");
        when(user.getCodigo()).thenReturn(100);
        when(user.getAreaId()).thenReturn(10);

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        br.com.alltallent.model.Funcionario criador = new br.com.alltallent.model.Funcionario();
        criador.setCodigo(200); // Created by someone else
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        criador.setArea(area);
        a.setCriador(criador);

        when(avaliacaoRepository.findById(5)).thenReturn(Optional.of(a));

        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> avaliacaoService.buscarAvaliacaoDetalhada(5));
    }

    @Test
    void testValidarPermissaoDeAcesso_MissingCriadorOrArea() {
        mockUsuarioLogado("ROLE_ADMIN");

        br.com.alltallent.model.Avaliacao a = new br.com.alltallent.model.Avaliacao();
        a.setCodigo(5);
        a.setCriador(null); // No creator

        when(avaliacaoRepository.findById(5)).thenReturn(Optional.of(a));

        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> avaliacaoService.buscarAvaliacaoDetalhada(5));
    }
}

