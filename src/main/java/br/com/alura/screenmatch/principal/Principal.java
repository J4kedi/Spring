package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String SEASON = "&season=";
    private final String API_KEY = "&apikey=2af0c999";
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1. Buscar Séries
                    2. Buscar episódios
                    3. Listar séries buscadas
                    4. Buscar série por título
                    5. Buscar séries por Ator
                    6. Top 5 séries
                    7. Buscar séries por categoria
                    8. Filtrar séries
                    9. Buscar episódio por trecho
                    10. Top 5 episódios por série
                    11. Buscar episódios a partir de uma data
                    ------------------------------------------
                    0. Sair
                """;
            System.out.println("\n    ** MENU **");
            System.out.println(menu);
            System.out.print("    Digite sua opção: ");
            opcao = input.nextInt();
            input.nextLine();
            
            switch(opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTopCincoSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosProSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.print("    Digite o nome da série para busca: ");
        var nomeSerie = input.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.print("    Escolha uma série pelo nome: ");
        var nomeSerie = input.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie).get(0);

        if (!serie.isPresent()) {
            System.err.println("    Série não encontrada");
            return;
        }

        var serieEncontrada = serie.get();
        List<DadosTemporada> temporadas = new ArrayList<>();
        
        for(int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + SEASON + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                    .map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());

        serieEncontrada.setEpisodios(episodios);
        repositorio.save(serieEncontrada);
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo(){
        System.out.print("\n    Escolha uma série pelo nome: ");
        var nomeSerie = input.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie).get(0);

        if(!serieBusca.isPresent()) {
            System.err.println("    Série não encontrada");
            return;
        }

        System.out.println("    Dados série: " + serieBusca.get());
    }

    private void buscarSeriesPorAtor() {
        System.out.print("\n    Escolha um ator: ");
        var nomeAtor = input.nextLine();
        System.out.print("\n    Avaliação a partir de qual valor? ");
        var avaliacao = input.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("    Séries em que " + nomeAtor + " trabalhou:");
        seriesEncontradas.forEach(s -> System.out.println("\n    " + s.getTitulo() + " avaliação da série: " + s.getAvaliacao()));
    }

    private void buscarTopCincoSeries() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println("\n    **** Top Cinco Series **** ");
        topSeries.forEach(s -> System.out.println("\n    Titulo: " + s.getTitulo() + "\n    Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.print("\n    Deseja buscar séries de qual categoria/gênero? ");
        var nomeCategoria = input.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("     Séries da categoria " + nomeCategoria + ": ");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.print("\n    Filtrar séries até quantas temporadas? ");
        var totalTemporadas = input.nextInt();
        input.nextLine();
        System.out.print("\n    Com avaliação a partir de que valor? ");
        var avaliacao = input.nextDouble();
        input.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("\n    *** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println("    " + s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.print("\n    Qual o nome do episódio? ");
        var trechoEpisodio = input.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosProSerie() {
        buscarSeriePorTitulo();

        if(!serieBusca.isPresent())
            return;

        Serie serie = serieBusca.get();
        List<Episodio> topEpisodios = repositorio.topCincoEpisodiosPorSerie(serie);
        topEpisodios.forEach(e ->
        System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
            e.getSerie().getTitulo(), e.getTemporada(),
            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();

        if(!serieBusca.isPresent())
            return;

        Serie serie = serieBusca.get();

        System.out.print("\n    Digite o ano limite de lançamento: ");
        var anoLancamento = input.nextInt();
        input.nextLine();

        List<Episodio> episodiosAno = repositorio.episodioPorSerieAno(serie, anoLancamento);
        episodiosAno.forEach(System.out::println);
    }
}
