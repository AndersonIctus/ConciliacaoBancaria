package com.concilicacaobancaria.conciliador.impl;

import com.concilicacaobancaria.conciliador.IConciliador;
import com.concilicacaobancaria.conciliador.model.DadoComum;
import com.concilicacaobancaria.conciliador.model.Tipo;
import lombok.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.concilicacaobancaria.conciliador.model.Tipo.*;
import static com.concilicacaobancaria.conciliador.model.Tipo.OUTRO;

public abstract class ConciliadorBase implements IConciliador {
    protected final String ARQUIVO_NAO_CONCILIADO = "NAO_CONCILIADO.CON";
    protected final String arquivoNaoConciliadoFormatado = "NAO_CONCILIADO_FORMATADO.TXT";

    protected abstract DadoComum formatarLinhaParaDadoComum(String linha);

    @Override
    public List<DadoComum> gerarDadosArquivoComum(String pathArquivo) throws IOException {
        return Files
                .lines(Paths.get(pathArquivo))
                .map(this::formatarLinhaParaDadoComum)
                .filter(Objects::nonNull)
                .sorted(
                        Comparator
                                .comparing(DadoComum::getLocalDateProcessado, Comparator.reverseOrder())
                                .thenComparing(DadoComum::getNome)
                )
                .collect(Collectors.toList());
    }

    @Override
    public void gerarArquivoNaoConciliado(List<DadoComum> dadosNaoConciliados, String diretorio) throws IOException {
        // Cria o diretório se não existir
        Files.createDirectories(Paths.get(diretorio));

        // CRIA/Sobrescreve ultimo arquivo conciliado na pasta
        Path pathArquivo = Paths.get(diretorio + "/" + ARQUIVO_NAO_CONCILIADO);
        if (Files.notExists(pathArquivo)) {
            Files.createFile(pathArquivo);
        }

        final StringBuilder dadosFormatados = new StringBuilder();
        dadosNaoConciliados
                .stream()
                .sorted(
                        Comparator
                                .comparing(DadoComum::getLocalDateProcessado)
                                .thenComparing(DadoComum::getNome)
                )
                .forEach(dado -> {
                    dadosFormatados.append(formatarDado(dado)).append("\r\n");
                });

        Files.writeString(pathArquivo, dadosFormatados.toString());
    }

    @Override
    public void gerarArquivoFormatado(String diretorio) throws IOException {
        // Cria o diretório se não existir
        Files.createDirectories(Paths.get(diretorio));

        // Pega o Arquivo não conciliado para Leitura
        final StringBuilder dadosFormatados = new StringBuilder();
        Path pathArquivo = Paths.get(diretorio + "/" + ARQUIVO_NAO_CONCILIADO);
        Files.readAllLines(pathArquivo)
                .forEach(line -> {
                    final DadoComum dado = linhaFormatadaParaDadoComum(line);
                    dadosFormatados.append(formatarDadoParaExcell(dado)).append("\r\n");
                });

        // Grava no Arquivo de Saida
        Files.writeString(Paths.get(arquivoNaoConciliadoFormatado), dadosFormatados.toString());
    }

    @Override
    public List<DadoComum> carregarDadosConciliados(String diretorio) throws IOException {
        // 1 - verificar se o diretorio existe, se não existir cria
        Path path = Paths.get(diretorio);

        // Cria o diretório se não existir
        if (Files.notExists(path)) {
            Files.createDirectories(path);
            return new ArrayList<>();
        }

        final Set<DadoComum> dados = new HashSet<>();
        try (Stream<Path> paths = Files.walk(path)) {
            paths
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".CON"))
                .forEach(file -> {
                    try {
                        dados.addAll(carregarDadosDoArquivo(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }

        return dados.stream()
                .sorted(
                        Comparator
                                .comparing(DadoComum::getLocalDateProcessado, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
    }

    @Override
    public void processarArquivoNaoConciliado(String diretorioConciliados, String diretorioNaoConciliados) throws IOException {
        // 1 - Pega os dados Comum do arquivo não conciliado
        List<DadoComum> dadosComum = carregarDadosDoArquivo(Paths.get(diretorioNaoConciliados + "/" + ARQUIVO_NAO_CONCILIADO));

        // 2 - Pega, num Map, os arquivos Conciliados TODOS
        final Map<String, List<DadoComum>> mpDadosConciliados = getDadosConciliadosDoDiretorio(Paths.get(diretorioConciliados));

        // 3 - Coloca os não conciliados no Mapa carregado. Se for necessário cria um novo
        dadosComum.forEach(dado -> {
            incluirParaConciliados(mpDadosConciliados, dado);
        });

        // 4 - Grava no diretório conciliado os arquivos sobre escrevendo eles
        for (String key : mpDadosConciliados.keySet()) {
            final StringBuilder dadosFormatados = new StringBuilder();
            mpDadosConciliados.get(key)
                    .stream()
                    .sorted(
                            Comparator
                                    .comparing(DadoComum::getLocalDateProcessado, Comparator.reverseOrder())
                                    .thenComparing(DadoComum::getNome)
                    )
                    .forEach(dado -> {
                        dadosFormatados.append(formatarDado(dado)).append("\r\n");
                    });

            Path pathArquivo = Paths.get(diretorioConciliados + "/Conciliados." + key + ".CON");
            Files.writeString(pathArquivo, dadosFormatados.toString());
        }
    }

    protected Tipo getTipo(String tipoFormatado) {
        switch (tipoFormatado) {
            case "PIX":
                return PIX;
            case "REND":
            case "RENDIMENTO":
                return RENDIMENTO;
            case "DESPESA":
                return DESPESA;
            default:
                return OUTRO;
        }
    }

    @NonNull
    private List<DadoComum> carregarDadosDoArquivo(Path path) throws IOException {
        final Set<DadoComum> setRetorno = new HashSet<>();
        Files.readAllLines(path)
                .forEach(line -> setRetorno.add(linhaFormatadaParaDadoComum(line)));
        return setRetorno.stream()
                .sorted(
                        Comparator
                                .comparing(DadoComum::getLocalDateProcessado, Comparator.reverseOrder())
                                .thenComparing(DadoComum::getNome)
                )
                .collect(Collectors.toList());
    }

    @NonNull
    private DadoComum linhaFormatadaParaDadoComum(String linhaFormatada) {
        String[] values = linhaFormatada.split("[|]");
        if(values.length == 7) {
            return new DadoComum(
                    "ITAU",
                    values[2],
                    getTipo(values[3].trim()),
                    values[4],
                    values[5],
                    new BigDecimal(values[6])
            );

        } else {
            return new DadoComum(
                    values[0],
                    values[1],
                    values[2],
                    values[3],
                    getTipo(values[4].trim()),
                    values[5],
                    values[6],
                    new BigDecimal(values[7])
            );
        }
    }

    @NonNull
    private String formatarDado(DadoComum dado) {
        return dado.getUUID() + "|" +
                dado.getUUID_MIN() + "|" +
                dado.getNomeBanco() + "|" +
                dado.getNome() + "|" +
                dado.getTipo() + "|" +
                dado.getDataProcessado() + "|" +
                dado.getDataTransferencia() + "|" +
                dado.getValor().toString()
                ;
    }

    private String formatarDadoParaExcell(DadoComum dado) {
        return getDia(dado.getDataProcessado()) + "\t" +
                getTipoFormatado(dado.getTipo(), dado.getValor()) + "\t" +
                dado.getValor().toString().replaceAll("[.]", ",") + "\t" +
                dado.getNome()+ "\t" +
                dado.getNomeBanco()
                ;
    }

    private void incluirParaConciliados(Map<String, List<DadoComum>> mpDadosConciliados, DadoComum dado) {
        String chave = getChaveFrom(dado.getDataProcessado());
        List<DadoComum> dadoConc = mpDadosConciliados.get(chave);
        if (dadoConc == null) {
            dadoConc = new ArrayList<>();
            dadoConc.add(dado);
            mpDadosConciliados.put(chave, dadoConc);
        } else {
            dadoConc.add(dado);
        }
    }

    private Map<String, List<DadoComum>> getDadosConciliadosDoDiretorio(Path path) throws IOException {
        final Map<String, List<DadoComum>> mpDadosConciliados = new HashMap<>();
        try (Stream<Path> paths = Files.walk(path)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().endsWith(".CON"))
                    .forEach(file -> {
                        try {
                            List<DadoComum> dados = carregarDadosDoArquivo(file);
                            String chave = file.getFileName().toString().split("[.]")[1];

                            mpDadosConciliados.put(chave, dados);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        return mpDadosConciliados;
    }

    private String getChaveFrom(String dataProcessado) {
        String mes = dataProcessado.substring(3, 5);
        String ano = dataProcessado.substring(6, 10);

        return ano + "_" + mes + "_" + getMesFormatado(mes);
    }

    private String getMesFormatado(String mes) {
        switch (mes) {
            default:
                return "ERR";
            case "01":
                return "JAN";
            case "02":
                return "FEV";
            case "03":
                return "MAR";
            case "04":
                return "ABR";
            case "05":
                return "MAI";
            case "06":
                return "JUN";
            case "07":
                return "JUL";
            case "08":
                return "AGO";
            case "09":
                return "SET";
            case "10":
                return "OUT";
            case "11":
                return "NOV";
            case "12":
                return "DEZ";
        }
    }

    private String getDia(String dataProcesso) {
        return dataProcesso.substring(0, 2);
    }

    private String getTipoFormatado(Tipo tipo, BigDecimal valor) {
        switch (tipo) {
            case PIX:
                if (valor.compareTo(new BigDecimal("90")) > 0) {
                    return "DIZIMO";
                } else {
                    return "OFERTA";
                }
            case RENDIMENTO:
                return "REND";
            case DESPESA:
                return "DESPESA";
            default:
                return "OFERTA";
        }
    }
}
