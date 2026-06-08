package br.com.alltallent.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alltallent.dto.PerfilDTO;
import br.com.alltallent.model.Perfil;
import br.com.alltallent.repository.PerfilRepository;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final PerfilRepository perfilRepository;

    public PerfilController(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }


    @PostMapping
    public ResponseEntity<PerfilDTO> createPerfil(@RequestBody PerfilDTO perfilDto) {
        Perfil perfil = new Perfil();
        perfil.setNome(perfilDto.nome());
        perfil.setDescricao(perfilDto.descricao());
        Perfil novoPerfil = perfilRepository.save(perfil);
        return new ResponseEntity<>(new PerfilDTO(novoPerfil), HttpStatus.CREATED);
    }


    @GetMapping
    public List<PerfilDTO> getAllPerfis() {
        return perfilRepository.findAll().stream()
                .map(PerfilDTO::new)
                .toList();
    }
}