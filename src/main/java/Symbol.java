import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by carl on 02.06.17.
 */
public class Symbol {
    static final public String EPSILON = "\u03B5";

    public enum Art {
        TERMINAL, LITERAL
    }

    private final String zustand;
    List<String> ableitungsRegeln;
    Art art;


    public Symbol(String zustand) {
        this.zustand = zustand;
        Pattern isTerm = Pattern.compile("[a-z]+");
        if (isTerm.matcher(zustand).matches()) {
            art = Art.TERMINAL;
        } else {
            art = Art.LITERAL;
        }
        ableitungsRegeln = new ArrayList<>();
    }

    public void addRegeln(String regel) {
        ableitungsRegeln.add(regel);
    }

    public Set<String> firstMenge(List<Symbol> regeln) {
        if (art == Art.TERMINAL) {
            return new HashSet(Collections.singletonList(zustand));
        }
        Set<String> terminale = new HashSet<>();
        for (String ableitungsRegel : ableitungsRegeln) {
            int count = 0;
            boolean isEpsilon;
            for (String letter : ableitungsRegel.split("")) {
                Pattern startWithTerm = Pattern.compile("^[a-z].*");
                Pattern startWithLit = Pattern.compile("^[A-Z].*");
                isEpsilon = false;
                //Startet mit einem Terminal
                if (startWithTerm.matcher(letter).matches()) {
                    terminale.add(letter);
                    break;
                } else {
                    //startet mit einem Literal
                    if (startWithLit.matcher(letter).matches()) {
                        String lit = letter;
                        //Durchsuche alle Regeln nach Literal
                        for (Symbol symbol : regeln) {
                            if (!symbol.equals(this) && symbol.getZustand().equals(lit)) {
                                Set<String> litFirstMenge = symbol.firstMenge(regeln);
                                for (String firstString : litFirstMenge) {
                                    if (firstString.equals(EPSILON)) {
                                        isEpsilon = true;
                                    }
                                }
                                litFirstMenge.remove(EPSILON);
                                terminale.addAll(litFirstMenge);
                                break;
                            }
                        }
                        if (isEpsilon) {
                            count++;
                            continue;
                        } else {
                            break;
                        }
                    }
                    if (letter.startsWith(EPSILON)) {
                        terminale.add(EPSILON);
                        break;
                    }
                }
            }
            if (count == ableitungsRegel.split("").length) {
                terminale.add(EPSILON);
            }
        }
        return terminale;
    }

    //todo jb was gegen unendlich oft reingehen tun
    public Set<String> followMenge(List<Symbol> regeln) {
        Set<String> retFollowMenge = new HashSet<>();
        for (Symbol regel : regeln) {
            List<String> ableitungsRegelnMitSymbol = regel.getAbleitungsRegelnMitSymbol(zustand);
            for (String arms : ableitungsRegelnMitSymbol) {
                for (int i = arms.indexOf(zustand) + 1; i < arms.length(); i++) {
                    String symbol = arms.split("")[i];
                    for (Symbol regelSuche : regeln) {
                        if (regelSuche.getZustand().equals(symbol)) {
                            Set<String> firstMenge = regelSuche.firstMenge(regeln);
                            if (firstMenge.contains(EPSILON)) {
                                Set<String> followMenge = new HashSet<>();
                                //todo jb vielleicht hier schon getan ??
                                if (!this.equals(regelSuche)) {
                                    followMenge = regelSuche.followMenge(regeln);
                                }
                                firstMenge.remove(EPSILON);
                                firstMenge.addAll(followMenge);
                            }
                            retFollowMenge.addAll(firstMenge);
                        }
                    }
                }
            }
        }
        return retFollowMenge;
    }

    public List<String> getAbleitungsRegelnMitSymbol(String symbol) {
        List<String> ableitungsRegelMitSymbol = new ArrayList<>();
        for (String ableitungsRegel : ableitungsRegeln) {
            if (ableitungsRegel.contains(symbol)) {
                ableitungsRegelMitSymbol.add(ableitungsRegel);
            }
        }
        return ableitungsRegelMitSymbol;
    }

    public String getZustand() {
        return zustand;
    }

    @Override
    public boolean equals(Object obj) {
        return zustand.equals(((Symbol) obj).getZustand());
    }
}
