package br.com.alltallent.controller;

import br.com.alltallent.config.CustomUserDetails;
import br.com.alltallent.dto.DashboardResponseDTO;
import br.com.alltallent.model.Funcionario;
import br.com.alltallent.repository.FuncionarioRepository;
import br.com.alltallent.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;
    private final FuncionarioRepository funcionarioRepository;

    /**
     * Retorna dados do Dashboard.
     * - Se for GESTOR: Retorna apenas dados da sua equipe/área.
     * - Se for ADMIN: Pode ver tudo ou filtrar por ?codigoArea=X
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getDashboardData(
            @RequestParam(required = false) Integer codigoArea,
            Authentication authentication) {

        logger.info(">>> 1. ENDPOINT ACIONADO - INICIANDO...");

        try {
            // --- LÓGICA DE PERMISSÃO ---
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Integer usuarioLogadoId = userDetails.getCodigo();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_DIRETORIA"));
            
            boolean isGestor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR") || a.getAuthority().equals("ROLE_SUPERVISAO"));

            Integer filtroAreaId = codigoArea;

            // Se for Gestor (e não Admin), FORÇA o filtro para a área dele.
            if (isGestor && !isAdmin) {
                Funcionario gestor = funcionarioRepository.findById(usuarioLogadoId)
                        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
                
                if (gestor.getArea() != null) {
                    filtroAreaId = gestor.getArea().getCodigo();
                    logger.info(">>> 2. FILTRO APLICADO (GESTOR): Área ID {}", filtroAreaId);
                }
            }

            // --- CHAMADA AO SERVICE ---
            logger.info(">>> TENTANDO CHAMAR O SERVICE...");
            DashboardResponseDTO data = dashboardService.getDashboardData(filtroAreaId);
            
            logger.info(">>> 3. SUCESSO! DADOS RECEBIDOS DO SERVICE: {}", data);

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            // --- CAPTURA DO ERRO ---
            logger.error(">>> ERRO CAPTURADO NO CONTROLLER ", e);
            return ResponseEntity.internalServerError().body("Erro interno no servidor: " + e.getMessage());
        }
    }
}