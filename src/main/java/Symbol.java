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

    public void addRegeln(List<String> regeln) {
        ableitungsRegeln.addAll(regeln);
    }

    public Set<String> firstMenge(Operator operator) {
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
                        if (lit.equals(zustand)) {
                            continue;
                        }
                        Symbol symbolVonRegel = operator.searchRegel(lit);
                        Set<String> litFirstMenge = symbolVonRegel.firstMenge(operator);
                        for (String firstString : litFirstMenge) {
                            if (firstString.equals(EPSILON)) {
                                isEpsilon = true;
                            }
                        }
                        litFirstMenge.remove(EPSILON);
                        terminale.addAll(litFirstMenge);
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
    public Set<String> followMenge(Operator operator, List<String> besuchteZustände) {
        Set<String> retFollowMenge = new HashSet<>();
        besuchteZustände.add(zustand);
        for (Symbol regel : operator.getZustaende()) {
            List<String> ableitungsRegelnMitSymbol = regel.getAbleitungsRegelnMitSymbol(zustand);
            for (String arms : ableitungsRegelnMitSymbol) {
                String[] literals = arms.split("");
                for (int i = arms.indexOf(zustand) + 1; i < arms.length(); i++) {
                    String symbol = literals[i];
                    if (besuchteZustände.contains(symbol)) {
                        continue;
                    }
                    Symbol symbolVomLiteral = operator.searchRegel(symbol);
                    Set<String> firstMenge = symbolVomLiteral.firstMenge(operator);
                    if (firstMenge.contains(EPSILON)) {
                        retFollowMenge.addAll(symbolVomLiteral.followMenge(operator, besuchteZustände));
                    }
                    retFollowMenge.addAll(firstMenge);
                    break;
                }
            }
        }
        if (retFollowMenge.size() == 0) {
            return Collections.singleton(EPSILON);
        }
        if (retFollowMenge.size() > 1) {
            retFollowMenge.remove(EPSILON);
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
