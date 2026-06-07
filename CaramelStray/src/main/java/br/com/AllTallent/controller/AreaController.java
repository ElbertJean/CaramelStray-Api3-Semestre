package br.com.alltallent.controller;

import br.com.alltallent.dto.AreaDTO;
import br.com.alltallent.model.Area;
import br.com.alltallent.repository.AreaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/area") 
public class AreaController {

    private final AreaRepository areaRepository;


    public AreaController(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }


    @PostMapping
    public ResponseEntity<AreaDTO> createArea(@RequestBody AreaDTO areaDto) {
        Area area = new Area();
        area.setNome(areaDto.nome());
        area.setDescricao(areaDto.descricao());
        Area novaArea = areaRepository.save(area);
        return new ResponseEntity<>(new AreaDTO(novaArea), HttpStatus.CREATED);
    }


    @GetMapping
    public List<AreaDTO> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(AreaDTO::new)
                .toList();
    }
}