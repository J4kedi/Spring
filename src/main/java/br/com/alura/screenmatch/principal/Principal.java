package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    private Scanner input = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados converte = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String SEASON = "&season=";
    private final String API_KEY = "&apikey=2af0c999";


    public void exibeMenu() {
        System.out.println("Digite o nome da serie para busca: ");
        var busca = input.nextLine();

        var json = consumo.obterDados(ENDERECO + busca.replace(" ", "+") + API_KEY);

        DadosSerie dados = converte.obterDados(json, DadosSerie.class);
        // System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + busca.replace(" ", "+") + SEASON + i + API_KEY);
            DadosTemporada dadosTemporada = converte.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        // temporadas.forEach(System.out::println);

        // for(int i = 0; i < dados.totalTemoradas(); i++) {
        //     List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        //     for(int j = 0; j < episodiosTemporada.size(); j++) {
        //         System.out.println(episodiosTemporada.get(j).titulo());
        //     }
        // }

        // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

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

        // episodios.forEach(System.out::println); 

        System.out.println("Apartir de que ano, quer ver os episódios? ");
        var ano = input.nextInt();
        input.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        episodios.stream()
            .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
            .forEach(e -> System.out.println(
                "Temporada: " + e.getNumeroTemporada() + 
                    ", Episódio: " + e.getTitulo() +
                    ", Data Lancamento: " + e.getDataLancamento().format(formatador)
            ));

        input.close();
    }
}