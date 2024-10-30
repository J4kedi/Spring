package br.com.alura.screenmatch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screenmatch.dto.EpisodioDto;
import br.com.alura.screenmatch.dto.SerieDto;
import br.com.alura.screenmatch.service.SerieService;

@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService servico;

    @GetMapping
    public List<SerieDto> obterSeries() {
        return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDto> obterTop5Series() {
        return servico.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDto> obterLancamentos() {
        return servico.obterTop5Lancamentos();
    }

    @GetMapping("/{id}")
    public SerieDto buscarPorId(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDto> obterTodasTemporadas(@PathVariable Long id) {
        return servico.obterTodasAsTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDto> obterTemporadasPorNumero(@PathVariable long id, @PathVariable long numero) {
        return servico.obterTemporadasPorNumero(id, numero);
    }
    
    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDto> obterTop5Episodios(@PathVariable long id) {
        return servico.obterTop5Episodios(id);
    }

    @GetMapping("/categoria/{nomeCategoria}")
    public List<SerieDto> obterSeries(@PathVariable String nomeCategoria) {
        return servico.obterSereiesCategoria(nomeCategoria);
    }
} 