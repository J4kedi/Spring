package br.com.alura.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer numeroTemporada;
    private String titulo;
    private Integer numeroEpisodio;
    private double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
            this.numeroTemporada = numeroTemporada;
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch(NumberFormatException e) {
            this.avaliacao = 0.0;
        } catch(NullPointerException e) {
            this.numeroTemporada = 0;
        } catch(DateTimeParseException e) {
            this.dataLancamento = null;
        }

    }

    public Integer getNumeroTemporada() {
        return numeroTemporada;
    }

    public void setNumeroTemporada(Integer numeroTemporada) {
        this.numeroTemporada = numeroTemporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        return "[Titulo: " + titulo + 
               ",\n Temporada: " + numeroTemporada + 
               ", Episodio: " + numeroEpisodio +
               ", Avaliacao: " + avaliacao +
               ", Data de Lançamento: " + dataLancamento + "]";	
    }
}
