package com.concilicacaobancaria.conciliador.impl.cora;

import com.concilicacaobancaria.conciliador.impl.ConciliadorBase;
import com.concilicacaobancaria.conciliador.model.DadoComum;
import com.concilicacaobancaria.conciliador.model.Tipo;

import java.math.BigDecimal;

import static com.concilicacaobancaria.conciliador.model.Tipo.*;

public class ConciliadorCora extends ConciliadorBase {
    @Override
    public String getNomeConciliador() {
        return "CORA BANK";
    }

    @Override
    protected DadoComum formatarLinhaParaDadoComum(String line) {
        // Pulando o cabeçalho do CSV
        if("Data,Transação,Tipo Transação,Identificação,Valor".equals(line))
            return null;

        String[] values = line.split(",");

        String data = values[0];

        String valorFormatado = values[4];
        BigDecimal valor = new BigDecimal(valorFormatado);

        Tipo tipo = getTipo(values[1]);

        String nome = values[3];

        // NUMEROS NEGATIVOS SÃO DESPESAS
//        if (valor.compareTo(BigDecimal.ZERO) < 0) {
//            tipo = Tipo.DESPESA;
//        }

        return new DadoComum(getNomeConciliador(), nome, tipo, data, data, valor);
    }

    @Override
    protected Tipo getTipo(String tipoFormatado) {
        if ("Transf Pix recebida".equals(tipoFormatado)) {
            return PIX;
        }

        return super.getTipo(tipoFormatado);
    }
}
