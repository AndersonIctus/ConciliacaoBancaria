package main;

import com.concilicacaobancaria.conciliador.impl.itau.ConciliadorItau;
import com.concilicacaobancaria.conciliador.model.DadoComum;
import com.concilicacaobancaria.conciliador.model.Tipo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class ProcessadorConciliacaoTest {

    public static void main(String[] args) throws IOException {
        testarGerarArquivoConciliado();
        // testarGerarArquivoNaoConciliado();
    }

    private static void testarGerarArquivoConciliado() throws IOException {
        String diretorioConciliados = "conciliados";
        ConciliadorItau conciliador = new ConciliadorItau();

        final List<DadoComum> dadoComums = conciliador.carregarDadosConciliados(diretorioConciliados);
        if(dadoComums.size() == 0) {
            System.out.println("** Não há dados conciliados!!");

        } else {
            for (DadoComum dado : dadoComums) {
                System.out.println(dado);
            }
        }
    }

    private static void testarGerarArquivoNaoConciliado() throws IOException {
        String diretorioNaoConciliados = "nao_conciliados";
        ConciliadorItau conciliador = new ConciliadorItau();

        List<DadoComum> dadosNaoConciliados = Arrays.asList(
            new DadoComum("Bank", "Anderson", Tipo.PIX, "22/03/2023", "22/03", new BigDecimal("70.00")),
            new DadoComum("Bank", "Ana Paul", Tipo.PIX, "20/03/2023", "20/03", new BigDecimal("35.00")),
            new DadoComum("Bank", "Roberto S", Tipo.PIX, "20/03/2023", "20/03", new BigDecimal("89.90"))
        );

        conciliador.gerarArquivoNaoConciliado(dadosNaoConciliados, diretorioNaoConciliados);
    }
}
