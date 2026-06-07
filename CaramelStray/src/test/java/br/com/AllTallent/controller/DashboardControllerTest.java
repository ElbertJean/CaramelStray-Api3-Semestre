package br.com.alltallent.controller;

import br.com.alltallent.config.CustomUserDetails;
import br.com.alltallent.repository.FuncionarioRepository;
import br.com.alltallent.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData_Exception() {
        // Arrange
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getCodigo()).thenReturn(1);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        when(authentication.getAuthorities()).thenReturn((java.util.Collection) List.of(authority));

        when(dashboardService.getDashboardData(any())).thenThrow(new RuntimeException("Simulated error"));

        // Act
        ResponseEntity<?> response = dashboardController.getDashboardData(null, authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno no servidor: Simulated error", response.getBody());
    }
}
