package br.com.alura.screenmatch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.dto.EpisodioDto;
import br.com.alura.screenmatch.dto.SerieDto;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;

    public List<SerieDto> obterTodasAsSeries() {
        return converteDados(repositorio.findAll());
    }

    public List<SerieDto> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDto> obterTop5Lancamentos() {
        return converteDados(repositorio.lancamentosMaisRecentes());
    }

    public SerieDto buscarPorId(long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if (!serie.isPresent())
            return null;

        Serie s = serie.get();

        return new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
    }

    public List<EpisodioDto> obterTodasAsTemporadas(long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if (!serie.isPresent())
            return null;
        
        Serie s = serie.get();
        return s.getEpisodios().stream()
                .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<EpisodioDto> obterTemporadasPorNumero(long id, long numero) {
        return repositorio.obterEpisodiosPorTemporada(id, numero).stream()
                .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<SerieDto> obterSereiesCategoria(String nomeCategoria) {
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        return converteDados(repositorio.findByGenero(categoria));
    }

    public List<EpisodioDto> obterTop5Episodios(long id) {
        return repositorio.obterTop5MelhoresEpisodios(id).stream()
                .map(e -> new EpisodioDto(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    private List<SerieDto> converteDados(List<Serie> series) {
        return series.stream()
            .map(s -> new SerieDto(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
            .collect(Collectors.toList());
    }
}
