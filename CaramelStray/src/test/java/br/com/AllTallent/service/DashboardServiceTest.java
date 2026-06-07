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
}
