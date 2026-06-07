package br.com.AllTallent.controller;

import br.com.AllTallent.dto.PerfilDTO;
import br.com.AllTallent.model.Perfil;
import br.com.AllTallent.repository.PerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PerfilControllerTest {

    @Mock
    private PerfilRepository perfilRepository;

    @InjectMocks
    private PerfilController perfilController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePerfil() {
        PerfilDTO inputDto = new PerfilDTO(null, "Test Nome", "Test Desc");
        Perfil mockSavedPerfil = new Perfil(1, "Test Nome", "Test Desc");
        
        when(perfilRepository.save(any(Perfil.class))).thenReturn(mockSavedPerfil);

        ResponseEntity<PerfilDTO> response = perfilController.createPerfil(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().codigo());
        assertEquals("Test Nome", response.getBody().nome());
    }

    @Test
    void testGetAllPerfis() {
        Perfil mockPerfil = new Perfil(1, "Test Nome", "Test Desc");
        when(perfilRepository.findAll()).thenReturn(List.of(mockPerfil));

        List<PerfilDTO> response = perfilController.getAllPerfis();

        assertEquals(1, response.size());
        assertEquals("Test Nome", response.get(0).nome());
    }
}
