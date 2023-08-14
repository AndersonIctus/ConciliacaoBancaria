package com.concilicacaobancaria.ConciliacaoBancaria;

import com.concilicacaobancaria.conciliador.ProcessadorConciliacao;

import java.io.IOException;

public class ConciliacaoBancariaApplication {
    public static ProcessadorConciliacao PROCESSADOR = new ProcessadorConciliacao();

    public static void main(String... args) {
        if (args == null || args.length != 1) {
            printOpcoes();

        } else {
            System.out.println("#######################################################");
            System.out.println("######### PROCESSANDO ARQUIVO DE CONCILIACAO ##########");
            System.out.println("** Processando o Arquivo => " + args[0]);
            try {
                int dadosGerados = PROCESSADOR.executarConciliacao((args[0]));
                System.out.printf("** %d DADOS NAO CONCILIADOS\r\n", dadosGerados);

            } catch (IOException e) {
                System.out.printf("**  ERRO AO TENTAR LER O ARQUIVO '%s': CAUSA: %s\r\n", args[0], e.getMessage());
                return;
            }

            System.out.println("** ARQUIVO de conciliação PROCESSADO!");
            System.out.println("#######################################################\r\n");

            printOpcoes();
        }

        System.out.println("**************** FINALIZADA *****************");
    }

    private static void printOpcoes() {
        try {
            mostrarOpcoes();

            while (true) {
                char opcao = (char) System.in.read();

                switch (opcao) {
                    case '\n':
                        continue;
                    case '0':
                        return;
                    case '1':
                        gerandoParaExcel();
                        break;
                    case '2':
                        processandoConciliacao();
                        break;
                    case '3':
                        gerandoParaExcel();
                        processandoConciliacao();
                        break;

                    default:
                        System.out.printf("** A opção '%c' escolhida não existe, por favor escolha uma opção válida!\r\n", opcao);
                }

                mostrarOpcoes();
            }

        } catch (IOException e) {
            System.out.println("** ERRO Na opção escolhida! CAUSE: " + e.getMessage());
        }
    }

    private static void mostrarOpcoes() {
        String opcoes = "Por favor escolha uma das Opções abaixo: \r\n" +
                "1 - Gerar arquivo Formatado para excell\r\n" +
                "2 - Conciliar o arquivo final pendente\r\n" +
                "3 - Gerar Excell e Conciliar o arquivo final!!\r\n" +
                "0 - FECHAR\r\n\r\n" +
                "Opção:";
        System.out.print(opcoes);
    }

    private static void gerandoParaExcel() throws IOException {
        System.out.println("** Gerando arquivo NAO CONCILIADO para excel ...");

        PROCESSADOR.gerarArquivoFormatado();

        System.out.println("** GERAÇÃO FINALIZADA **");
    }

    private static void processandoConciliacao() throws IOException {
        System.out.println("** Processando dados para conciliação ...");

        PROCESSADOR.processarArquivoNaoConciliado();

        System.out.println("** PROCESSO FINALIZADO **");
    }
}
