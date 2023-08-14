package com.concilicacaobancaria.conciliador.model;

import lombok.Getter;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
public class DadoComum {
    private final String UUID;
    private final String UUID_MIN;

    private final String nomeBanco;
    private final String nome;
    private final Tipo tipo;
    private final String dataProcessado;
    private final String dataTransferencia;
    private final BigDecimal valor;

    public DadoComum(String UUID, String UUID_MIN, String nomeBanco, String nome, Tipo tipo, String dataProcesso, String dataTransferencia, BigDecimal valor) {
        this.nome = nome;
        this.nomeBanco = nomeBanco;
        this.tipo = tipo;
        this.dataProcessado = dataProcesso;
        this.dataTransferencia = dataTransferencia;
        this.valor = valor;

        this.UUID = UUID;
        this.UUID_MIN = UUID_MIN;
    }

    public DadoComum(String nomeBanco, String nome, Tipo tipo, String dataProcesso, String dataTransferencia, BigDecimal valor) {
        this.nome = nome;
        this.nomeBanco = nomeBanco;
        this.tipo = tipo;
        this.dataProcessado = dataProcesso;
        this.dataTransferencia = dataTransferencia;
        this.valor = valor;

        String uuidHash = nomeBanco + "|" + nome + "|" + tipo + "|" + dataProcesso + "|" + dataTransferencia + "|" + valor.toString();
        this.UUID = Base64.encodeBase64String(uuidHash.getBytes());
        this.UUID_MIN = UUID.substring(UUID.length() - 10, UUID.length() - 2);
    }

    public LocalDate getLocalDateProcessado() {
        String[] date = dataProcessado.split("/");
        return LocalDate.of(
                Integer.parseInt(date[2]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[0])
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DadoComum dadoComum = (DadoComum) o;
        return Objects.equals(UUID_MIN, dadoComum.UUID_MIN) && Objects.equals(UUID, dadoComum.UUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID, UUID_MIN);
    }

    @Override
    public String toString() {
        return "DadoComum{" +
                "UUID='" + UUID + '\'' +
                ", UUID_MIN='" + UUID_MIN + '\'' +
                ", nomeBanco='" + nomeBanco + '\'' +
                ", nome='" + nome + '\'' +
                ", tipo='" + tipo + '\'' +
                ", dataProcesso='" + dataProcessado + '\'' +
                ", dataTransferencia='" + dataTransferencia + '\'' +
                ", valor=" + valor +
                '}';
    }
}
