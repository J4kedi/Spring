package br.com.alura.screenmatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=2af0c999");
		System.out.println(json);

		ConverteDados converte = new ConverteDados();
		DadosSerie dados = converte.obterDados(json, DadosSerie.class);

		System.out.println("\n" + dados);

		json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=2af0c999");

		DadosEpisodio dadosEpisodio = converte.obterDados(json, DadosEpisodio.class);
		System.out.println("\n" + dadosEpisodio + "\n");

		List<DadosTemporada> temporadas = new ArrayList<>(); 
		
		for(int i = 1; i <= dados.totalTemoradas(); i++) {
			json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&season="+ i + "&apikey=2af0c999");

			DadosTemporada dadosTemporada = converte.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);
	}
}
