package com.concilicacaobancaria.conciliador.impl.itau;

import com.concilicacaobancaria.conciliador.impl.ConciliadorBase;
import com.concilicacaobancaria.conciliador.model.DadoComum;
import com.concilicacaobancaria.conciliador.model.Tipo;
import com.concilicacaobancaria.conciliador.util.MatchUtil;
import com.concilicacaobancaria.conciliador.util.MyMatching;

import java.math.BigDecimal;
import java.util.Arrays;

import static com.concilicacaobancaria.conciliador.model.Tipo.*;

public class ConciliadorItau extends ConciliadorBase {
    String[] patterns = Arrays.asList(
            "PIX TRANSF[\\s]+",
            "SISPAG PIX[\\s]+",
            "PIX QRS[\\s]+",
            "REND[\\s]+",

            "[A-Za-z ]+",                   // ALPHA
            "[0-9][0-9]/[0-9][0-9]",        // DATA TRANSFERENCIA
            "\\s+",                         // ESPACO
            "//.*"
    ).toArray(new String[8]);

    @Override
    public String getNomeConciliador() {
        return "ITAU";
    }

    @Override
    protected DadoComum formatarLinhaParaDadoComum(String line) {
        String[] values = line.split(";");

        String dataProcesso = values[0];
        String valorFormatado = values[2]
                .replaceAll("[.]", "")
                .replaceAll(",", ".");
        BigDecimal valor = new BigDecimal(valorFormatado);

        MyMatching match = MatchUtil.findMatch(values[1], patterns);
        Tipo tipo = getTipo(match.get(0).trim());

        String nome = "";
        if (tipo == Tipo.OUTRO) {
            nome = match.get(0);
        } else {
            nome = match.get(1);
        }

        String dtTranf = "";
        if (match.size() == 3) {
            dtTranf = match.get(2);
        }

        // NUMEROS NEGATIVOS SÃO DESPESAS
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            tipo = Tipo.DESPESA;
        }

        return new DadoComum(getNomeConciliador(), nome, tipo, dataProcesso, dtTranf, valor);
    }

    @Override
    protected Tipo getTipo(String tipoFormatado) {
        switch (tipoFormatado) {
            case "PIX TRANSF":
            case "SISPAG PIX":
            case "PIX QRS":
                return PIX;

            default:
                return super.getTipo(tipoFormatado);
        }
    }
}
