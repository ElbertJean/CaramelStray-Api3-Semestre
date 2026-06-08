package br.com.alltallent.controller;

import br.com.alltallent.dto.AreaDTO;
import br.com.alltallent.model.Area;
import br.com.alltallent.repository.AreaRepository;
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

class AreaControllerTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaController areaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateArea() {
        AreaDTO inputDto = new AreaDTO(null, "Test Nome", "Test Desc");
        Area mockSavedArea = new Area(1, "Test Nome", "Test Desc");
        
        when(areaRepository.save(any(Area.class))).thenReturn(mockSavedArea);

        ResponseEntity<AreaDTO> response = areaController.createArea(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, response.getBody().codigo());
        assertEquals("Test Nome", response.getBody().nome());
    }

    @Test
    void testGetAllAreas() {
        Area mockArea = new Area(1, "Test Nome", "Test Desc");
        when(areaRepository.findAll()).thenReturn(List.of(mockArea));

        List<AreaDTO> response = areaController.getAllAreas();

        assertEquals(1, response.size());
        assertEquals("Test Nome", response.get(0).nome());
    }
}
