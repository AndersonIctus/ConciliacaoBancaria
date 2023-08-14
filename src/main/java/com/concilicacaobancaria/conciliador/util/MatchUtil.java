package com.concilicacaobancaria.conciliador.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtil {
    public static MyMatching findMatch(String valor, String[] patterns) {
        Matcher match = Pattern.compile(
                String.join("|", patterns)
        ).matcher(valor.trim());

        return match.find() ? new MyMatching(valor, match) : null;
    }
}
