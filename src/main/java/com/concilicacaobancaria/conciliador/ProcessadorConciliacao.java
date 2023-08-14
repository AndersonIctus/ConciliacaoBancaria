package com.concilicacaobancaria.conciliador;

import com.concilicacaobancaria.conciliador.impl.cora.ConciliadorCora;
import com.concilicacaobancaria.conciliador.impl.itau.ConciliadorItau;
import com.concilicacaobancaria.conciliador.model.DadoComum;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessadorConciliacao {
    private final String diretorioConciliados = "conciliados";
    private final String diretorioNaoConciliados = "nao_conciliados";
    private IConciliador conciliador;

    public ProcessadorConciliacao() {
        conciliador = new ConciliadorItau();
    }

    public int executarConciliacao(String pathArquivoBruto) throws IOException {
        if(pathArquivoBruto.endsWith(".csv")) {
            conciliador = new ConciliadorCora();
        }

        System.out.println("** Processando para o CONCILIADOR: " + conciliador.getNomeConciliador());

        // 1 - Ler o Arquivo Bruto para formar uma lista de Dados Conhecidos
        List<DadoComum> dadosArquivoComum = conciliador.gerarDadosArquivoComum(pathArquivoBruto);

        // 2 - Ler o Arquivo de Conciliados
        List<DadoComum> dadosConciliados = conciliador.carregarDadosConciliados(diretorioConciliados);

        // 3 - fazer a conciliacao
        List<DadoComum> dadosNaoConciliados;
        if (dadosConciliados.size() == 0) {
            // A propria lista de dados é o arquivo de conciliacao
            dadosNaoConciliados = dadosArquivoComum;
        } else {
            dadosNaoConciliados = fazerConciliacao(dadosArquivoComum, dadosConciliados);
        }

        // 4 - Escrever no arquivo de nao conciliados
        conciliador.gerarArquivoNaoConciliado(dadosNaoConciliados, diretorioNaoConciliados);
        return dadosNaoConciliados.size();
    }

    /**
     * Gera o Arquivo com o formato que será incluído direto no Excell
     *
     * @throws IOException
     */
    public void gerarArquivoFormatado() throws IOException {
        conciliador.gerarArquivoFormatado(diretorioNaoConciliados);
    }

    /**
     * Se tudo estiver OK, faz o processamento do que falta ser conciliado.
     * Nessa lógica os arquivos conciliados são gerados/atualizados POR MÊS e o arquivo não conciliado é excluído!
     *
     * @throws IOException
     */
    public void processarArquivoNaoConciliado() throws IOException {
        conciliador.processarArquivoNaoConciliado(diretorioConciliados, diretorioNaoConciliados);
    }

    private List<DadoComum> fazerConciliacao(List<DadoComum> dadosArquivoComum, List<DadoComum> dadosConciliados) {
        // Deve fazer a comparação item a item para verificar se deve remover ou não
        return dadosArquivoComum.stream()
                .filter(dado -> {
                    final boolean ret = !dadosConciliados.contains(dado);

                    return ret;
                })
                .collect(Collectors.toList());
    }
}
