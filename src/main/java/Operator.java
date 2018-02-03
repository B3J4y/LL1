import com.sun.deploy.util.StringUtils;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
        //wird in firstMenge benutzt, was macht diese bedienung, und was macht sie, wenn sie false ist?
        if (regeln == null) {

        }
        //gibt eine Liste mit den Symbolen zurück, die auf den Zustand ableiten
        List<Symbol> filteredRegeln = regeln.stream()
                .filter(x -> Objects.equals(x.getZustand(), zustand)).collect(Collectors.toList());
        //Abfrage zur Sicherheit, falls kein symbol gefunden wird, kann nicht auf das erste Element zugegriffen werden (zweites return)
        if (filteredRegeln == null || filteredRegeln.isEmpty()) {
            return null;
        }
        //gibt das erste Element der Liste zurück
        return filteredRegeln.get(0);
    }

    public List<Symbol> getZustaende() {
        return regeln;
    }

    public static Operator create(String fileName) {
        Operator operator = new Operator();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            boolean  inhalt = true;
            String zeile;
            List<Symbol> symbols = new ArrayList<>();
            while ((zeile = br.readLine()) != null) {
                //trim() nimmt alle leerzeichen und tabs weg (trennzeichen)
                //String[] erzeugt aus einer Zeile ein Array mit deim Zeug vor und hinter dem Trennsymbol
                if (zeile.isEmpty()) {
                    continue;
                }
                String[] litUndRegeln = zeile.trim().split("->");
                if (litUndRegeln.length <= 1) {
                    continue;
                }
                Symbol tempSymb = new Symbol(litUndRegeln[0].trim());
                symbols.add(tempSymb);
                String[] einzelneRegeln = litUndRegeln[1].split("\\|");
                for (String regelString: einzelneRegeln) {
                    String trimmedRegel = regelString.trim();
                    if (trimmedRegel.toUpperCase().equals("EPSILON")) {
                        tempSymb.addRegeln(Symbol.EPSILON);
                    } else {
                        tempSymb.addRegeln(trimmedRegel);
                    }
                }
            }
            operator.addRegel(symbols);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operator;
    }

    public String createParsingTable() {
        Map<Symbol, Pair<Set<String>, Set<String>>> symbolFirstFollow = new HashMap<>();
        Set<String> terminale = new HashSet<>();
        for (Symbol symbol : regeln) {
            Set<String> firstMenge = symbol.firstMenge(this);
            Set<String> followMenge = symbol.followMenge(this, new ArrayList<>());
            symbolFirstFollow.put(symbol, new Pair<>(firstMenge, followMenge));
            terminale.addAll(firstMenge);
            terminale.addAll(followMenge);
        }
        List<String> parsingRow = new ArrayList<>();
        parsingRow.add(" ;" + StringUtils.join(terminale, ";"));
        for (Symbol symbol: symbolFirstFollow.keySet()) {
            String row = symbol.getZustand();
            for (String terminalString : terminale) {
                if (terminalString.equals(Symbol.EPSILON)) {
                    row += ";-";
                    continue;
                }
                Set<String> firstMenge = symbolFirstFollow.get(symbol).getKey();
                Set<String> followMenge = symbolFirstFollow.get(symbol).getValue();
                if (firstMenge.contains(terminalString)) {
                    String firstRegel = symbol.findFirstRegel(this, terminalString);
                    row += ";" + firstRegel;
                } else {
                    if (followMenge.contains(terminalString)) {
                        row += ";" + Symbol.EPSILON;
                    } else {
                        row += ";-";
                    }
                }
            }
            parsingRow.add(row);
        }

        System.out.println(StringUtils.join(parsingRow, "\n"));
        return StringUtils.join(parsingRow, "\n");
    }


}
