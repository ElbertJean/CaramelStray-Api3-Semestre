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
import java.util.List;
import br.com.alltallent.config.CustomUserDetails;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private br.com.alltallent.repository.AreaRepository areaRepository;

    @Mock
    private br.com.alltallent.repository.PerfilRepository perfilRepository;

    @Mock
    private br.com.alltallent.repository.CompetenciaRepository competenciaRepository;

    @Mock
    private br.com.alltallent.repository.ExperienciaRepository experienciaRepository;

    @Mock
    private br.com.alltallent.repository.CertificadoRepository certificadoRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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

    private CustomUserDetails mockUsuarioLogado(String role, Integer id, Integer areaId) {
        org.springframework.security.core.Authentication auth = org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        CustomUserDetails userDetails = org.mockito.Mockito.mock(CustomUserDetails.class);
        org.mockito.Mockito.when(userDetails.getCodigo()).thenReturn(id);
        org.mockito.Mockito.when(userDetails.getAreaId()).thenReturn(areaId);
        org.mockito.Mockito.doReturn(java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))).when(userDetails).getAuthorities();
        org.mockito.Mockito.when(auth.getPrincipal()).thenReturn(userDetails);
        
        org.springframework.security.core.context.SecurityContext context = org.mockito.Mockito.mock(org.springframework.security.core.context.SecurityContext.class);
        org.mockito.Mockito.when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
        return userDetails;
    }

    @Test
    void testCriar_Success() {
        br.com.alltallent.dto.FuncionarioRequestDTO dto = new br.com.alltallent.dto.FuncionarioRequestDTO(
            "Maria Silva", "maria@test.com", "12345678901", "1199999999", "mockSecurePassPhrase123", 10, 2, null, "Dev", "SP", "Resumo"
        );
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area(10, "TI", "TI desc");
        br.com.alltallent.model.Perfil perfil = new br.com.alltallent.model.Perfil(2, "Gestor", "Gestor desc");

        when(areaRepository.findById(10)).thenReturn(Optional.of(area));
        when(perfilRepository.findById(2)).thenReturn(Optional.of(perfil));
        when(passwordEncoder.encode("mockSecurePassPhrase123")).thenReturn("encoded_senha");

        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria Silva");
        f.setArea(area);
        f.setPerfil(perfil);
        when(funcionarioRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.Funcionario.class))).thenReturn(f);

        br.com.alltallent.dto.FuncionarioResponseDTO result = funcionarioService.criar(dto);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals("Maria Silva", result.nomeCompleto());
    }

    @Test
    void testAtualizar_Success() {
        br.com.alltallent.dto.FuncionarioRequestDTO dto = new br.com.alltallent.dto.FuncionarioRequestDTO(
            "Maria Silva Atualizada", "maria@test.com", "12345678901", "1199999999", "mockSecurePassPhrase123", 10, 2, null, "Dev", "SP", "Resumo"
        );
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area(10, "TI", "TI desc");
        br.com.alltallent.model.Perfil perfil = new br.com.alltallent.model.Perfil(2, "Gestor", "Gestor desc");
        
        br.com.alltallent.model.Funcionario fExistente = new br.com.alltallent.model.Funcionario();
        fExistente.setCodigo(1);
        fExistente.setNomeCompleto("Maria Silva");

        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(fExistente));
        when(areaRepository.findById(10)).thenReturn(Optional.of(area));
        when(perfilRepository.findById(2)).thenReturn(Optional.of(perfil));
        when(passwordEncoder.encode("mockSecurePassPhrase123")).thenReturn("encoded_senha");
        when(funcionarioRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.Funcionario.class))).thenReturn(fExistente);

        br.com.alltallent.dto.FuncionarioResponseDTO result = funcionarioService.atualizar(1, dto);
        org.junit.jupiter.api.Assertions.assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertEquals("Maria Silva Atualizada", result.nomeCompleto());
    }

    @Test
    void testDeletar_Success() {
        when(funcionarioRepository.existsById(1)).thenReturn(true);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> funcionarioService.deletar(1));
    }

    @Test
    void testAssociarCompetencias_Success() {
        mockUsuarioLogado("ROLE_GESTOR", 100, 10);

        br.com.alltallent.model.Funcionario alvo = new br.com.alltallent.model.Funcionario();
        alvo.setCodigo(200);
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        alvo.setArea(area);
        br.com.alltallent.model.Perfil perfil = new br.com.alltallent.model.Perfil();
        perfil.setCodigo(3); // Colaborador
        alvo.setPerfil(perfil);

        when(funcionarioRepository.findByIdCompleto(200)).thenReturn(Optional.of(alvo));

        br.com.alltallent.model.Competencia comp = new br.com.alltallent.model.Competencia();
        comp.setCodigo(5);
        when(competenciaRepository.findAllById(List.of(5))).thenReturn(List.of(comp));

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> funcionarioService.associarCompetencias(200, List.of(5)));
    }

    @Test
    void testAssociarCompetencias_Unauthorized() {
        mockUsuarioLogado("ROLE_USER", 100, 10); // Simple user trying to edit someone else

        br.com.alltallent.model.Funcionario alvo = new br.com.alltallent.model.Funcionario();
        alvo.setCodigo(200);
        when(funcionarioRepository.findByIdCompleto(200)).thenReturn(Optional.of(alvo));

        assertThrows(br.com.alltallent.exception.UnauthorizedActionException.class, () -> funcionarioService.associarCompetencias(200, List.of(5)));
    }

    @Test
    void testAssociarCompetencias_CompetenciaNotFound() {
        mockUsuarioLogado("ROLE_ADMIN", 100, 10);

        br.com.alltallent.model.Funcionario alvo = new br.com.alltallent.model.Funcionario();
        alvo.setCodigo(200);
        br.com.alltallent.model.Area area = new br.com.alltallent.model.Area();
        area.setCodigo(10);
        alvo.setArea(area);
        br.com.alltallent.model.Perfil perfil = new br.com.alltallent.model.Perfil();
        perfil.setCodigo(3);
        alvo.setPerfil(perfil);

        when(funcionarioRepository.findByIdCompleto(200)).thenReturn(Optional.of(alvo));
        when(competenciaRepository.findAllById(List.of(5))).thenReturn(List.of()); // Expected 1, got 0

        assertThrows(br.com.alltallent.exception.ResourceNotFoundException.class, () -> funcionarioService.associarCompetencias(200, List.of(5)));
    }

    @Test
    void testAdicionarCertificado_Success() {
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        f.setCertificados(new java.util.HashSet<>());
        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(f));

        br.com.alltallent.dto.CertificadoRequestDTO req = new br.com.alltallent.dto.CertificadoRequestDTO("Java Cert");
        br.com.alltallent.dto.CertificadoDTO res = funcionarioService.adicionarCertificado(1, req);

        org.junit.jupiter.api.Assertions.assertNotNull(res);
        org.junit.jupiter.api.Assertions.assertEquals("Java Cert", res.nome());
    }

    @Test
    void testRemoverCertificado_Success() {
        when(certificadoRepository.existsById(5)).thenReturn(true);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> funcionarioService.removerCertificado(5));
    }

    @Test
    void testRemoverCertificado_NotFound() {
        when(certificadoRepository.existsById(5)).thenReturn(false);
        assertThrows(br.com.alltallent.exception.ResourceNotFoundException.class, () -> funcionarioService.removerCertificado(5));
    }

    @Test
    void testAdicionarExperiencia_Success() {
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(1);
        f.setExperiencias(new java.util.HashSet<>());
        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(f));

        br.com.alltallent.dto.ExperienciaRequestDTO req = new br.com.alltallent.dto.ExperienciaRequestDTO(
            "Cargo", "Empresa", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"), "Desc"
        );
        br.com.alltallent.dto.ExperienciaDTO res = funcionarioService.adicionarExperiencia(1, req);

        org.junit.jupiter.api.Assertions.assertNotNull(res);
        org.junit.jupiter.api.Assertions.assertEquals("Cargo", res.cargo());
    }

    @Test
    void testAtualizarExperiencia_Success() {
        br.com.alltallent.model.Experiencia exp = new br.com.alltallent.model.Experiencia();
        exp.setCodigo(10);
        when(experienciaRepository.findById(10)).thenReturn(Optional.of(exp));
        when(experienciaRepository.save(org.mockito.ArgumentMatchers.any(br.com.alltallent.model.Experiencia.class))).thenReturn(exp);

        br.com.alltallent.dto.ExperienciaRequestDTO req = new br.com.alltallent.dto.ExperienciaRequestDTO(
            "Cargo Novo", "Empresa", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"), "Desc"
        );
        br.com.alltallent.dto.ExperienciaDTO res = funcionarioService.atualizarExperiencia(10, req);

        org.junit.jupiter.api.Assertions.assertNotNull(res);
        org.junit.jupiter.api.Assertions.assertEquals("Cargo Novo", res.cargo());
    }

    @Test
    void testAtualizarExperiencia_NotFound() {
        when(experienciaRepository.findById(10)).thenReturn(Optional.empty());
        br.com.alltallent.dto.ExperienciaRequestDTO req = new br.com.alltallent.dto.ExperienciaRequestDTO(
            "Cargo Novo", "Empresa", java.time.LocalDate.parse("2020-01-01"), java.time.LocalDate.parse("2021-01-01"), "Desc"
        );
        assertThrows(br.com.alltallent.exception.ResourceNotFoundException.class, () -> funcionarioService.atualizarExperiencia(10, req));
    }

    @Test
    void testUsuarioPodeEditarExperiencia() {
        br.com.alltallent.model.Experiencia exp = new br.com.alltallent.model.Experiencia();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(100);
        exp.setFuncionario(f);

        when(experienciaRepository.findById(10)).thenReturn(Optional.of(exp));

        org.junit.jupiter.api.Assertions.assertTrue(funcionarioService.usuarioPodeEditarExperiencia(10, 100));
        org.junit.jupiter.api.Assertions.assertFalse(funcionarioService.usuarioPodeEditarExperiencia(10, 200));
    }

    @Test
    void testUsuarioPodeRemoverCertificado() {
        br.com.alltallent.model.FuncionarioCertificado cert = new br.com.alltallent.model.FuncionarioCertificado();
        br.com.alltallent.model.Funcionario f = new br.com.alltallent.model.Funcionario();
        f.setCodigo(100);
        cert.setFuncionario(f);

        when(certificadoRepository.findById(10)).thenReturn(Optional.of(cert));

        org.junit.jupiter.api.Assertions.assertTrue(funcionarioService.usuarioPodeRemoverCertificado(10, 100));
        org.junit.jupiter.api.Assertions.assertFalse(funcionarioService.usuarioPodeRemoverCertificado(10, 200));
    }
}

