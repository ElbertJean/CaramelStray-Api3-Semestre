package br.com.alltallent.config;

import br.com.alltallent.model.Funcionario;
import br.com.alltallent.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApplicationConfigTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationConfig = new ApplicationConfig(funcionarioRepository);
    }

    @Test
    void testUserDetailsService_Success() {
        Funcionario f = new Funcionario();
        f.setEmail("test@test.com");
        f.setSenhaHash("hash");
        when(funcionarioRepository.findByEmailForSecurity("test@test.com")).thenReturn(Optional.of(f));

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        assertNotNull(userDetailsService);

        var userDetails = userDetailsService.loadUserByUsername("test@test.com");
        assertEquals("test@test.com", userDetails.getUsername());
    }

    @Test
    void testUserDetailsService_UserNotFound() {
        when(funcionarioRepository.findByEmailForSecurity("test@test.com")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@test.com"));
    }

    @Test
    void testAuthenticationProvider() {
        AuthenticationProvider provider = applicationConfig.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(config.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = applicationConfig.authenticationManager(config);
        assertEquals(manager, result);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = applicationConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("anyValueToMatch123", encoder.encode("anyValueToMatch123")));
    }
}
