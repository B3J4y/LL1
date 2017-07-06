import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by carl on 06.07.17.
 */
public class Operator {
    private List<Symbol> regeln;

    public Operator() {
        regeln = new ArrayList<>();
    }

    public void addRegel(Symbol symbol) {
        Symbol foundSymbol = searchRegel(symbol.getZustand());
        if (foundSymbol == null) {
            regeln.add(symbol);
        } else {
            foundSymbol.addRegeln(symbol.ableitungsRegeln);
        }
    }

    public void addRegel(List<Symbol> symbol) {
        symbol.forEach(this::addRegel);
    }

    public Symbol searchRegel(String zustand) {
        if (regeln == null) {

        }
        List<Symbol> filteredRegeln = regeln.stream()
                .filter(x -> Objects.equals(x.getZustand(), zustand)).collect(Collectors.toList());
        if (filteredRegeln == null || filteredRegeln.isEmpty()) {
            return null;
        }
        return filteredRegeln.get(0);
    }

    public List<Symbol> getZustaende() {
        return regeln;
    }
}
