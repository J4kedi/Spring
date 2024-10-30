package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal2 {
    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String SEASON = "&season=";
    private final String API_KEY = "&apikey=2af0c999";

    public void exibeMenu() {
        System.out.println("Digite o nome da serie para busca: ");
        var busca = input.nextLine();

        var link = ENDERECO + busca.replace(" ", "+") + API_KEY;

        var json = consumo.obterDados(link);

        System.out.println("link de consulta: " + link);
        DadosSerie dados = converte.obterDados(json, DadosSerie.class);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + busca.replace(" ", "+") + SEASON + i + API_KEY);
            DadosTemporada dadosTemporada = converte.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

        for(int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for(int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());

        System.out.println("\nTop 10 episódios com melhores avaliações ---");

        dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .peek(e -> System.out.println("Primeiro filtro(N/A) " + e))
            .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            .peek(e -> System.out.println("Ordenacão " + e))
            .limit(10)
            .peek(e -> System.out.println("Limite de espisódios " + e))
            .map(e -> e.titulo().toUpperCase())
            .peek(e -> System.out.println("mapeamento " + e))
            .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))    
            ).collect(Collectors.toList());

        episodios.forEach(System.out::println); 

        System.out.println("\nDigite o nome do episódio: ");
        var trechoTitulo = input.nextLine();

        Optional<Episodio> nomeBuscado = episodios.stream()
            .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
            .findFirst();

        if(nomeBuscado.isPresent()) {
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + nomeBuscado.get().getTemporada() + ", Titulo: " + nomeBuscado.get().getTitulo());
        } else {
            System.out.println("Episódio não encontrado!");
        }

        System.out.println("Apartir de que ano, quer ver os episódios? ");
        var ano = input.nextInt();
        input.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        episodios.stream()
            .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
            .forEach(e -> System.out.println(
                "Temporada: " + e.getTemporada() + 
                    ", Episódio: " + e.getTitulo() +
                    ", Data Lancamento: " + e.getDataLancamento().format(formatador)
            ));

        Map<Integer, Double> avaliacoesTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, 
                    Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesTemporada);

        DoubleSummaryStatistics est = episodios.stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getAvaliacao))
        ;

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor avaliacao: " + est.getMax());
        System.out.println("Pior avaliação: " + est.getMin());
        System.out.println("Total de avaliação: " + est.getCount());

        input.close();
    }
}