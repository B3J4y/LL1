import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by carl on 02.06.17.
 */
public class SymbolTest {

    @Test
    public void testFirstMenge() {
        List<Symbol> regeln = new ArrayList<>();

        //neue Ableitung erstellen
        //A-> a | bb | Bce
        Symbol symbol1 = new Symbol("A");
        regeln.add(symbol1);
        //Ableitungen von symbol1
        symbol1.addRegeln("a");
        symbol1.addRegeln("bb");
        symbol1.addRegeln("Bce");

        //B -> B | d | EPSILON
        Symbol symbol2 = new Symbol("B");
        regeln.add(symbol2);
        symbol2.addRegeln("B");
        symbol2.addRegeln("d");
        symbol2.addRegeln(Symbol.EPSILON);

        //C -> EPSILON
        Symbol symbol3 = new Symbol("C");
        regeln.add(symbol3);
        symbol3.addRegeln(Symbol.EPSILON);

        //Operator beinhaltet die gesamte Grammatik
        Operator operator = new Operator();
        operator.addRegel(regeln);

        //Vergleichsmengen für die Grammatik
        Set<String> expRegel1Menge = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        Set<String> expRegel2Menge = new HashSet<>(Arrays.asList("d", Symbol.EPSILON));
        Set<String> expRegel3Menge = new HashSet<>(Arrays.asList(Symbol.EPSILON));
        //Vergleich
        assertEquals(expRegel1Menge, symbol1.firstMenge(operator));
        assertEquals(expRegel2Menge, symbol2.firstMenge(operator));
        assertEquals(expRegel3Menge, symbol3.firstMenge(operator));

        //Vergleich ob Terminal auf die entsprechende Firstmenge abbildet
        Symbol symbolKleinA = new Symbol("a");
        Set<String> expRegelKleinA = new HashSet<>(Collections.singletonList("a"));
        assertEquals(expRegelKleinA, symbolKleinA.firstMenge(operator));
    }

    @Test
    public void testAufgFirst() {
        //Grammatik aus der Uebung 3
        Operator operator = getSABCOperator();

        //Firstmengen....
        Set<String> expFirstS = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> expFirstA = new HashSet<>(Arrays.asList("a", Symbol.EPSILON));
        Set<String> expFirstB = new HashSet<>(Arrays.asList("b", Symbol.EPSILON));
        Set<String> expFirstC = new HashSet<>(Arrays.asList("d"));
        expFirstC.addAll(expFirstS);

        //Prüfen auf die Firstmengen
        assertEquals(expFirstS, operator.searchRegel("S").firstMenge(operator));
        assertEquals(expFirstA, operator.searchRegel("A").firstMenge(operator));
        assertEquals(expFirstB, operator.searchRegel("B").firstMenge(operator));
        assertEquals(expFirstC, operator.searchRegel("C").firstMenge(operator));
    }





    //Init Gramatik Übung 3
    private Operator getSABCOperator() {


        Symbol symbolS = new Symbol("S");
        symbolS.addRegeln("ABcC");

        Symbol symbolA = new Symbol("A");
        symbolA.addRegeln("aA");
        symbolA.addRegeln(Symbol.EPSILON);

        Symbol symbolB = new Symbol("B");
        symbolB.addRegeln("bB");
        symbolB.addRegeln(Symbol.EPSILON);

        Symbol symbolC = new Symbol("C");
        symbolC.addRegeln("S");
        symbolC.addRegeln("d");

        List<Symbol> regeln = Arrays.asList(symbolS, symbolA, symbolB, symbolC);
        Operator operator = new Operator();
        operator.addRegel(regeln);
        operator.addRegel(new Symbol("a"));
        operator.addRegel(new Symbol("b"));
        operator.addRegel(new Symbol("c"));
        operator.addRegel(new Symbol("d"));
        return operator;
    }

    @Test
    //prüfen der Followmengen von Uebung 3
    public void testAufgFollow() {
        Operator operator = getSABCOperator();
        Set<String> expFollowa = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> expFollowb = new HashSet<>(Arrays.asList("b", "c"));
        Set<String> expFollowc = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        Set<String> expFollowd = new HashSet<>(Collections.singletonList(Symbol.EPSILON));
        Set<String> expFollowA = new HashSet<>(Arrays.asList("b", "c"));
        Set<String> expFollowB = new HashSet<>(Collections.singletonList("c"));
        Set<String> expFollowC = new HashSet<>(Collections.singletonList(Symbol.EPSILON));
        Set<String> expFollowS = new HashSet<>(Collections.singletonList(Symbol.EPSILON));

        assertEquals(expFollowa, operator.searchRegel("a").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowb, operator.searchRegel("b").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowc, operator.searchRegel("c").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowd, operator.searchRegel("d").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowA, operator.searchRegel("A").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowB, operator.searchRegel("B").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowC, operator.searchRegel("C").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowS, operator.searchRegel("S").followMenge(operator, new ArrayList<>()));
    }

    //fabrice 24.08
    private Operator getABCOperator() throws IOException {

        //gramatik einlesen
        List<Symbol> regeln = new ArrayList<>();
        boolean inhalt = true;
        ArrayList<String> gramatikregeln = new ArrayList<String>();
        int count = 0;
        System.out.println("Hey");

        try {
            FileReader fr = new FileReader("/home/fabrice/Test.txt");
            BufferedReader br = new BufferedReader(fr);

            String zeile = null;
            while (inhalt == true) {
                zeile = br.readLine();
                //Abbruchbedingung für leere Zeile/EndofFile
                if (zeile == null)
                    inhalt = false;
                else
                    //Liest die Zeilen aus der Datei
                    for (String buch : zeile.split("\\s")) {
                        //schreib alle Charakter in eine ArrayList, ohne Sonderzeichen
                        if (Pattern.matches("[a-zA-Z]+", buch)) {
                            gramatikregeln.add(buch);
                        } else
                            continue;
                    }
                //Signalisiert das Ende einer Regel
                gramatikregeln.add("*");
            }
//            for (int i = 0; i < gramatikregeln.size(); i++)
//                System.out.println(gramatikregeln.get(i));
            br.close();
        } catch (IOException e) {
            System.out.println("File nicht gefunden, wird angelegt!");
        }




        Symbol symbol = null;

        for (int i = 0; i < gramatikregeln.size(); i++) {
            //wenn symbol nur ein trennzeichen ist
            if (gramatikregeln.get(i) == "*") {
                inhalt = true;
                continue;
            }
            //wenn symbol das erste oder erste nach einem trennzeichen ist = Ableitungskopf
            if (inhalt == true) {
                symbol = new Symbol(gramatikregeln.get(i));
                regeln.add(symbol);
                inhalt = false;
            }
            //sonst Ableitungsrumpf
            else {
                List<String> blubb = null;
                int count2 = 0;

                if (gramatikregeln.get(i).equals("EPSILON")){
                    //System.out.println(gramatikregeln.get(i));
                    symbol.addRegeln(Symbol.EPSILON);
                    continue;
                }
                    //symbol.addRegeln(gramatikregeln.get(i);
                for (String letter : gramatikregeln.get(i).split( "")) {
                    //blubb.add(letter);
                    System.out.println(letter + ":" + count2);
                    count2++;
                    //wirft eine Exception....NullPointerException
                    //muss da überall ein addRegel für den Operator mit rein
                    //symbol.addRegeln(letter);
                }

            }
        }

        Operator operator = new Operator();
        operator.addRegel(regeln);
        System.out.println("§");
        //for (Symbol assy : operator.getZustaende())
        //    System.out.println("§");
        return operator;

    }
    @Test
    //prüfen der Followmengen von Uebung 3
    public void testAufFollow() throws IOException {
        Operator operator = getABCOperator();
        Set<String> expFollowa = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> expFollowb = new HashSet<>(Arrays.asList("b", "c"));
        Set<String> expFollowc = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        Set<String> expFollowd = new HashSet<>(Collections.singletonList(Symbol.EPSILON));
        Set<String> expFollowA = new HashSet<>(Arrays.asList("b", "c"));
        Set<String> expFollowB = new HashSet<>(Collections.singletonList("c"));
        Set<String> expFollowC = new HashSet<>(Collections.singletonList(Symbol.EPSILON));
        Set<String> expFollowS = new HashSet<>(Collections.singletonList(Symbol.EPSILON));
/*
        assertEquals(expFollowa, operator.searchRegel("a").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowb, operator.searchRegel("b").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowc, operator.searchRegel("c").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowd, operator.searchRegel("d").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowA, operator.searchRegel("A").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowB, operator.searchRegel("B").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowC, operator.searchRegel("C").followMenge(operator, new ArrayList<>()));
        assertEquals(expFollowS, operator.searchRegel("S").followMenge(operator, new ArrayList<>()));
*/    }

    @Test
    public void testInput() {
        Operator operator = Operator.create("/home/fabrice/Test.txt");
        operator.createParsingTable();
    }

}