package com.concilicacaobancaria.conciliador;

import com.concilicacaobancaria.conciliador.model.DadoComum;

import java.io.IOException;
import java.util.List;

public interface IConciliador {
    List<DadoComum> gerarDadosArquivoComum(String pathArquivo) throws IOException;

    List<DadoComum> carregarDadosConciliados(String diretorio) throws IOException;

    void gerarArquivoNaoConciliado(List<DadoComum> dadosNaoConciliados, String diretorio) throws IOException;

    void gerarArquivoFormatado(String diretorio) throws IOException;

    void processarArquivoNaoConciliado(String diretorioConciliados, String diretorioNaoConciliados) throws IOException;

    String getNomeConciliador();
}
