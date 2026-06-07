package br.com.AllTallent.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.AllTallent.dto.CompetenciaDTO;
import br.com.AllTallent.model.Competencia;
import br.com.AllTallent.repository.CompetenciaRepository;

@RestController
@RequestMapping("/api/competencia")
public class CompetenciaController {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaController(CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    
     @GetMapping
    public ResponseEntity<List<CompetenciaDTO>> listar() {
        List<CompetenciaDTO> dtos = competenciaRepository.findAll().stream()
                .map(CompetenciaDTO::new) 
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetenciaDTO> buscarPorId(@PathVariable Integer id) {
        return competenciaRepository.findById(id)
                .map(c -> ResponseEntity.ok(new CompetenciaDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompetenciaDTO> criar(@RequestBody CompetenciaDTO novaDto) {
        if (competenciaRepository.existsByNomeIgnoreCase(novaDto.nome())) {
            return ResponseEntity.badRequest().build();
        }
        Competencia nova = new Competencia();
        nova.setNome(novaDto.nome());
        nova.setCategoria(novaDto.categoria());
        Competencia salva = competenciaRepository.save(nova);
        return ResponseEntity.created(URI.create("/api/competencia/" + salva.getCodigo())).body(new CompetenciaDTO(salva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompetenciaDTO> atualizar(@PathVariable Integer id, @RequestBody CompetenciaDTO atualizada) {
        return competenciaRepository.findById(id)
                .map(c -> {
                    c.setNome(atualizada.nome());
                    Competencia salva = competenciaRepository.save(c);
                    return ResponseEntity.ok(new CompetenciaDTO(salva));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (!competenciaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        competenciaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
