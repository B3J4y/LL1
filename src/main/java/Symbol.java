import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by carl on 02.06.17.
 */
public class Symbol {
    static final public String EPSILON = "\u03B5";

    //Attribute für die Eingabesymbole
    public enum Art {
        TERMINAL, LITERAL
    }
    //Variablendeklaration
    //final = nur eine referenz der variablen "zustand" möglich (sicherheit)
    private final String zustand;
    //liste von Ableitungsregeln
    List<String> ableitungsRegeln;
    //Variable für das Attribut des Eingabesymbols
    Art art;

    //Erstellt ein Ableitungssymbol
    public Symbol(String zustand) {
        //this -> da "zustand" den gleichen namen hat, wie die locale variable "zustand"
        this.zustand = zustand;
        //der Reguläreausdruck wird als Pattern geladen mit compile
        Pattern isTerm = Pattern.compile("[a-z]+");
        //setzt Attribut TERMINAL oder Literal für das Symbol, wenn "zustand" Kleinbuchstabe, dann wahr, sonst falsch
        if (isTerm.matcher(zustand).matches()) {
            art = Art.TERMINAL;
        } else {
            art = Art.LITERAL;
        }
        //Symbol bekommt eine Liste mit ableitunsregeln
        ableitungsRegeln = new ArrayList<>();
    }

    //Regel die geaddet wird, mit Regeln die die Regel geaddet hat
    public void addRegeln(String regel) {
        ableitungsRegeln.add(regel);
    }

    //mehrere Regeln
    public void addRegeln(List<String> regeln) {
        ableitungsRegeln.addAll(regeln);
    }

    public String findFirstRegel(Operator operator, String terminal) {
        //wenn attribute gleich Terminal -> Firstmenge gleich symbol
        if (art == Art.TERMINAL) {
            //gibt eine Ein-Elementige-Liste zurück, die aus dem Literal besteht
            return null;
        }
        //Set terminale wird erstellt, als HashSet
        Set<String> terminale = new HashSet<>();
        for (String ableitungsRegel : ableitungsRegeln) {
            int count = 0;
            boolean isEpsilon;
            //Ableitungsregeln von dem Symbol, werden für die Firstmenge durchsucht
            for (String letter : ableitungsRegel.split("")) {
                //Pattern für Kleinbuchstaben
                Pattern startWithTerm = Pattern.compile("^[a-z].*");
                //Pattern für Großbuchstaben
                Pattern startWithLit = Pattern.compile("^[A-Z].*");
                isEpsilon = false;
                //Startet mit einem Terminal
                if (startWithTerm.matcher(letter).matches()) {
                    terminale.add(letter);
                    break;
                } else {
                    //startet mit einem Literal
                    //Endlosschleife, wenn das Literal das Gleiche, wie das Symbol ist -> Continue
                    if (startWithLit.matcher(letter).matches()) {
                        String lit = letter;
                        if (lit.equals(zustand)) {
                            //startet einen neuen for-Schritt, wenn das Symbol gleich dem Ableitungssymbol
                            continue;
                        }
                        //Gibt das Symbol an, in deren Ableitungsregel das Literal vorkommt
                        Symbol symbolVonRegel = operator.searchRegel(lit);
                        //erstell die Firstmenge von dem Symbol
                        Set<String> litFirstMenge = symbolVonRegel.firstMenge(operator);
                        //sucht EPSILON, wenn alle "Unter"Firstmengen ein EPSILON bestitzen hat auch die Firstmenge ein EPSILON
                        for (String firstString : litFirstMenge) {
                            if (firstString.equals(EPSILON)) {
                                isEpsilon = true;
                            }
                        }
                        litFirstMenge.remove(EPSILON);
                        //Vereinigung der Firstmengen
                        if (litFirstMenge.contains(terminal)) {
                            return ableitungsRegel;
                        }
                        if (isEpsilon) {
                            Set<String> follower = symbolVonRegel.followMenge(operator, new ArrayList<>());
                            if (follower.contains(terminal)) {
                                return ableitungsRegel;
                            }
                            count++;
                            continue;
                        } else {
                            break;
                        }
                    }
                    //falls regel nicht mit einem Terminal oder Literal startet
                    if (letter.startsWith(EPSILON)) {
                        return EPSILON;
                    }
                }
            }
            //wenn alle Ableitungssymbole ein EPSILON beinhalten, hat auch die Firstmenge eins
            if (count == ableitungsRegel.split("").length) {
                terminale.add(EPSILON);
            }
            if (terminale.contains(terminal)) {
                return ableitungsRegel;
            }
        }
        return null;
    }

    //erstellt von dem Symbol die Firstmenge, Operator ist gleich ein Datentyp für Operanten Bsp.: = < > usw.
    public Set<String> firstMenge(Operator operator) {
        //wenn attribute gleich Terminal -> Firstmenge gleich symbol
        if (art == Art.TERMINAL) {
            //gibt eine Ein-Elementige-Liste zurück, die aus dem Literal besteht
            return new HashSet(Collections.singletonList(zustand));
        }
        //Set terminale wird erstellt, als HashSet
        Set<String> terminale = new HashSet<>();
        for (String ableitungsRegel : ableitungsRegeln) {
            int count = 0;
            boolean isEpsilon;
            //Ableitungsregeln von dem Symbol, werden für die Firstmenge durchsucht
            for (String letter : ableitungsRegel.split("")) {
                //Pattern für Kleinbuchstaben
                Pattern startWithTerm = Pattern.compile("^[a-z].*");
                //Pattern für Großbuchstaben
                Pattern startWithLit = Pattern.compile("^[A-Z].*");
                isEpsilon = false;
                //Startet mit einem Terminal
                if (startWithTerm.matcher(letter).matches()) {
                    terminale.add(letter);
                    break;
                } else {
                    //startet mit einem Literal
                    //Endlosschleife, wenn das Literal das Gleiche, wie das Symbol ist -> Continue
                    if (startWithLit.matcher(letter).matches()) {
                        String lit = letter;
                        if (lit.equals(zustand)) {
                            //startet einen neuen for-Schritt, wenn das Symbol gleich dem Ableitungssymbol
                            continue;
                        }
                        //Gibt das Symbol an, in deren Ableitungsregel das Literal vorkommt
                        Symbol symbolVonRegel = operator.searchRegel(lit);
                        //erstell die Firstmenge von dem Symbol
                        Set<String> litFirstMenge = symbolVonRegel.firstMenge(operator);
                        //sucht EPSILON, wenn alle "Unter"Firstmengen ein EPSILON bestitzen hat auch die Firstmenge ein EPSILON
                        for (String firstString : litFirstMenge) {
                            if (firstString.equals(EPSILON)) {
                                isEpsilon = true;
                            }
                        }
                        litFirstMenge.remove(EPSILON);
                        //Vereinigung der Firstmengen
                        terminale.addAll(litFirstMenge);
                        if (isEpsilon) {
                            count++;
                            continue;
                        } else {
                            break;
                        }
                    }
                    //falls regel nicht mit einem Terminal oder Literal startet
                    if (letter.startsWith(EPSILON)) {
                        terminale.add(EPSILON);
                        break;
                    }
                }
            }
            //wenn alle Ableitungssymbole ein EPSILON beinhalten, hat auch die Firstmenge eins
            if (count == ableitungsRegel.split("").length) {
                terminale.add(EPSILON);
            }
        }
        return terminale;
    }

    public Set<String> followMenge(Operator operator, List<String> besuchteZustände) {
        Set<String> retFollowMenge = new HashSet<>();
        besuchteZustände.add(zustand);
        //getZustaende ist gleiche eine Liste mit Symbolen
        for (Symbol regel : operator.getZustaende()) {
            //regeln für das explizite symbol
            List<String> ableitungsRegelnMitSymbol = regel.getAbleitungsRegelnMitSymbol(zustand);
            for (String arms : ableitungsRegelnMitSymbol) {
                //Array für die einzelnen Symbole der Liste
                String[] literals = arms.split("");
                //indexOf gibt die Stelle an, an der das Symbol als erstes auftritt
                for (int i = arms.indexOf(zustand) + 1; i < arms.length(); i++) {
                    String symbol = literals[i];
                    if (besuchteZustände.contains(symbol)) {
                        continue;
                    }
                    Symbol symbolVomLiteral = operator.searchRegel(symbol);
                    if (symbolVomLiteral == null) {
                        retFollowMenge.add(symbol);
                        break;
                    }
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
