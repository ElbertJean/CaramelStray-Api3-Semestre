package br.com.alltallent.service;

import br.com.alltallent.dto.DashboardResponseDTO;
import br.com.alltallent.repository.AvaliacaoFuncionarioRepository;
import br.com.alltallent.repository.AvaliacaoRepository;
import br.com.alltallent.repository.FuncionarioRepository;
import br.com.alltallent.repository.RespostaColaboradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepo;

    @Mock
    private AvaliacaoRepository avaliacaoRepo;

    @Mock
    private AvaliacaoFuncionarioRepository avaliacaoFuncionarioRepo;

    @Mock
    private RespostaColaboradorRepository respostaColaboradorRepo;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData() {
        when(funcionarioRepo.count()).thenReturn(10L);
        when(avaliacaoFuncionarioRepo.countTotalPendentes()).thenReturn(5);
        when(avaliacaoFuncionarioRepo.countConcluidasNoMes(any(), any())).thenReturn(10);
        when(avaliacaoFuncionarioRepo.countAprovadasNoMes(any(), any())).thenReturn(8);
        when(funcionarioRepo.findEvolucaoMensal()).thenReturn(new ArrayList<>());
        
        when(funcionarioRepo.countFuncionariosPorCompetencia()).thenReturn(new ArrayList<>());
        when(avaliacaoFuncionarioRepo.findTopCompetenciasMaisAvaliadas(any())).thenReturn(new ArrayList<>());
        when(funcionarioRepo.countFuncionariosPorArea()).thenReturn(new ArrayList<>());

        DashboardResponseDTO result = dashboardService.getDashboardData(null);

        assertNotNull(result);
        assertNotNull(result.getEvolucaoMensal());
    }

    @Test
    void testGetDashboardData_ComFiltroArea() {
        when(funcionarioRepo.countByAreaCodigo(any())).thenReturn(5L);
        when(avaliacaoFuncionarioRepo.countTotalPendentesByArea(any())).thenReturn(2);
        when(avaliacaoFuncionarioRepo.countConcluidasNoMesByArea(any(), any(), any())).thenReturn(5);
        when(avaliacaoFuncionarioRepo.countAprovadasNoMesByArea(any(), any(), any())).thenReturn(4);
        when(funcionarioRepo.findEvolucaoMensalByArea(any())).thenReturn(new ArrayList<>());
        
        DashboardResponseDTO result = dashboardService.getDashboardData(1);
        assertNotNull(result);
    }

    @Test
    void testGerarResumo() {
        when(funcionarioRepo.findAll()).thenReturn(new ArrayList<>());
        when(avaliacaoRepo.findAll()).thenReturn(new ArrayList<>());
        when(avaliacaoFuncionarioRepo.findAll()).thenReturn(new ArrayList<>());
        
        java.util.Map<String, Object> resumo = dashboardService.gerarResumo();
        assertNotNull(resumo);
    }

    @Test
    void testGetDistribuicaoPorArea() {
        when(funcionarioRepo.findAll()).thenReturn(new ArrayList<>());
        java.util.Map<String, Long> dist = dashboardService.getDistribuicaoPorArea();
        assertNotNull(dist);
    }

    @Test
    void testGetDistribuicaoPorCompetencias() {
        when(funcionarioRepo.findAll()).thenReturn(new ArrayList<>());
        java.util.Map<String, Long> dist = dashboardService.getDistribuicaoPorCompetencias();
        assertNotNull(dist);
    }
}
