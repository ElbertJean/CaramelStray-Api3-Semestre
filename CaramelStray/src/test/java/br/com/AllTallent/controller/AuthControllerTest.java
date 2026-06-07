package br.com.alltallent.controller;

import br.com.alltallent.config.CustomUserDetails;
import br.com.alltallent.config.JwtService;
import br.com.alltallent.dto.CadastroRequestDTO;
import br.com.alltallent.dto.FuncionarioResponseDTO;
import br.com.alltallent.dto.LoginRequestDTO;
import br.com.alltallent.dto.LoginResponseDTO;
import br.com.alltallent.model.Funcionario;
import br.com.alltallent.repository.FuncionarioRepository;
import br.com.alltallent.service.AuthService;
import br.com.alltallent.service.FuncionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private FuncionarioService funcionarioService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO req = new LoginRequestDTO("test@test.com", "plainTextMockValue123");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria Silva");
        when(funcionarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(f));
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        ResponseEntity<LoginResponseDTO> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().token());
        assertEquals(1, response.getBody().userId());
        assertEquals("Maria Silva", response.getBody().nomeCompleto());
    }

    @Test
    void testLogin_UserNotFoundAfterAuthentication() {
        LoginRequestDTO req = new LoginRequestDTO("test@test.com", "plainTextMockValue123");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(funcionarioRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authController.login(req));
    }

    @Test
    void testRegister_Success() {
        CadastroRequestDTO req = new CadastroRequestDTO();
        req.setNomeCompleto("Maria Silva");
        req.setEmail("maria@test.com");
        req.setSenha("plainTextMockValue123");
        req.setTelefone("1199999999");
        req.setCodigoArea(10);
        req.setCodigoPerfil(2);
        req.setCpf("12345678901");
        
        Funcionario fSaved = new Funcionario();
        fSaved.setCodigo(1);

        when(authService.register(any(CadastroRequestDTO.class))).thenReturn(fSaved);

        ResponseEntity<Object> response = authController.register(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Colaborador cadastrado com sucesso!", response.getBody());
    }

    @Test
    void testRegister_Failure() {
        CadastroRequestDTO req = new CadastroRequestDTO();
        req.setNomeCompleto("Maria Silva");
        req.setEmail("maria@test.com");
        req.setSenha("plainTextMockValue123");
        req.setTelefone("1199999999");
        req.setCodigoArea(10);
        req.setCodigoPerfil(2);
        req.setCpf("12345678901");

        when(authService.register(any(CadastroRequestDTO.class))).thenThrow(new RuntimeException("Email already in use"));

        ResponseEntity<Object> response = authController.register(req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already in use", response.getBody());
    }

    @Test
    void testGetMeuPerfil() {
        Authentication auth = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getCodigo()).thenReturn(1);
        when(auth.getPrincipal()).thenReturn(userDetails);

        Funcionario f = new Funcionario();
        f.setCodigo(1);
        f.setNomeCompleto("Maria Silva");
        f.setTelefone("1199999999");
        f.setTituloProfissional("Dev");
        f.setLocalizacao("SP");
        f.setResumo("Resumo");
        f.setEmail("maria@test.com");
        FuncionarioResponseDTO fDto = new FuncionarioResponseDTO(f);
        when(funcionarioService.buscarPorId(1)).thenReturn(fDto);

        ResponseEntity<FuncionarioResponseDTO> response = authController.getMeuPerfil(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fDto, response.getBody());
    }
}
