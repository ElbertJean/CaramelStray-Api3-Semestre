package br.com.alltallent.service;

import br.com.alltallent.dto.CadastroRequestDTO;
import br.com.alltallent.model.Area;
import br.com.alltallent.model.Funcionario;
import br.com.alltallent.model.Perfil;
import br.com.alltallent.repository.AreaRepository;
import br.com.alltallent.repository.FuncionarioRepository;
import br.com.alltallent.repository.PerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        CadastroRequestDTO request = new CadastroRequestDTO();
        request.setEmail("test@test.com");
        request.setCodigoArea(1);
        request.setCodigoPerfil(1);
        request.setSenha("123");
        request.setNomeCompleto("Test Name");
        request.setDataAdmissao(java.time.LocalDate.of(2023, java.time.Month.JANUARY, 1));

        when(funcionarioRepository.findByEmail(any())).thenReturn(Optional.empty());

        Area area = new Area();
        area.setCodigo(1);
        when(areaRepository.findById(1)).thenReturn(Optional.of(area));

        Perfil perfil = new Perfil();
        perfil.setCodigo(1);
        when(perfilRepository.findById(1)).thenReturn(Optional.of(perfil));

        when(passwordEncoder.encode(any())).thenReturn("hashedpass");

        Funcionario savedFuncionario = new Funcionario();
        savedFuncionario.setCodigo(1);
        when(funcionarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Funcionario result = authService.register(request);

        assertNotNull(result);
        assertNotNull(result.getDataCadastro());
    }

    @Test
    void testRegister_EmailAlreadyInUse() {
        CadastroRequestDTO request = new CadastroRequestDTO();
        request.setEmail("test@test.com");
        
        when(funcionarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new Funcionario()));

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class, 
            () -> authService.register(request)
        );
        org.junit.jupiter.api.Assertions.assertEquals("Erro: Email já está em uso!", ex.getMessage());
    }

    @Test
    void testRegister_AreaNotFound() {
        CadastroRequestDTO request = new CadastroRequestDTO();
        request.setEmail("new@test.com");
        request.setCodigoArea(999);

        when(funcionarioRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(areaRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, 
            () -> authService.register(request)
        );
        org.junit.jupiter.api.Assertions.assertEquals("Erro: Área (Departamento) não encontrada.", ex.getMessage());
    }

    @Test
    void testRegister_PerfilNotFound() {
        CadastroRequestDTO request = new CadastroRequestDTO();
        request.setEmail("new@test.com");
        request.setCodigoArea(1);
        request.setCodigoPerfil(999);

        when(funcionarioRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(areaRepository.findById(1)).thenReturn(Optional.of(new Area()));
        when(perfilRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, 
            () -> authService.register(request)
        );
        org.junit.jupiter.api.Assertions.assertEquals("Erro: Perfil (Cargo) não encontrado.", ex.getMessage());
    }
}
