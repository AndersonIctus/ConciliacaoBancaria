package main;

import com.concilicacaobancaria.ConciliacaoBancaria.ConciliacaoBancariaApplication;
import com.concilicacaobancaria.conciliador.model.DadoComum;
import com.concilicacaobancaria.conciliador.model.Tipo;
import com.concilicacaobancaria.conciliador.util.MatchUtil;
import com.concilicacaobancaria.conciliador.util.MyMatching;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.Arrays;

public class TesteConciliacaoBancaria {
    public static void main(String[] args) {
        // testarDadoComum();
        // testarMathcUtil();
        // testarDadoComumComparator();

//        ConciliacaoBancariaApplication.main(
//                "Extrato Conta Corrente-060820231732.txt"
//        );

        ConciliacaoBancariaApplication.main(
                "CORA-extrato-teste.csv"
        );
    }

    private static void testarDadoComumComparator() {
        DadoComum comum = new DadoComum("BANK", "Anderson", Tipo.PIX, "10/12/2023", "10/12", new BigDecimal("10.00"));
        System.out.println(comum);

        DadoComum comum2 = new DadoComum("BANK", "Anderson", Tipo.PIX, "10/12/2023", "10/12", new BigDecimal("10.00"));
        System.out.println(comum2);

        System.out.println("Equal => " + comum.equals(comum2));
        // System.out.println("Compare => " + comum.compareTo(comum2));
    }

    private static void testarMathcUtil() {
        String[] patterns = Arrays.asList(
                "PIX TRANSF[\\s]+",
                "SISPAG PIX[\\s]+",
                "PIX QRS[\\s]+",
                "REND[\\s]+",
                "[A-Za-z ]+",                   // ALPHA
                "[0-9][0-9]/[0-9][0-9]",        // DATA TRANFERENCIA
                "\\s+",                         // SPACE
                "//.*"
        ).toArray(new String[8]);

        String valor = "SISPAG PIX  E L DE ALMEI";

        MyMatching match = MatchUtil.findMatch(valor, patterns);

        System.out.println(match);
    }

    private static void testarDadoComum() {
        DadoComum comum = new DadoComum("BANK", "Anderson", Tipo.PIX, "10/12/2023", "10/12", new BigDecimal("10.00"));
        System.out.println(comum);

        DadoComum comum2 = new DadoComum("BANK", "Anderson", Tipo.OUTRO, "10/12/2023", "10/12", new BigDecimal("10.00"));
        System.out.println(comum2);

        System.out.println(comum.equals(comum2));

        System.out.println("Comum 1 => " + new String(Base64.decodeBase64(comum.getUUID())));
        System.out.println("Comum 2 => " + new String(Base64.decodeBase64(comum2.getUUID())));
    }
}
