import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by carl on 02.06.17.
 */
public class SymbolTest {

    @Test
    public void testFirstMenge() {
        List<Symbol> regeln = new ArrayList<>();

        Symbol symbol1 = new Symbol("A");
        regeln.add(symbol1);
        symbol1.addRegeln("a");
        symbol1.addRegeln("bb");
        symbol1.addRegeln("Bce");

        Symbol symbol2 = new Symbol("B");
        regeln.add(symbol2);
        symbol2.addRegeln("B");
        symbol2.addRegeln("d");
        symbol2.addRegeln(Symbol.EPSILON);

        Symbol symbol3 = new Symbol("C");
        regeln.add(symbol3);
        symbol3.addRegeln(Symbol.EPSILON);

        Operator operator = new Operator();
        operator.addRegel(regeln);

        Set<String> expRegel1Menge = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        Set<String> expRegel2Menge = new HashSet<>(Arrays.asList("d", Symbol.EPSILON));
        Set<String> expRegel3Menge = new HashSet<>(Arrays.asList(Symbol.EPSILON));
        assertEquals(expRegel1Menge, symbol1.firstMenge(operator));
        assertEquals(expRegel2Menge, symbol2.firstMenge(operator));
        assertEquals(expRegel3Menge, symbol3.firstMenge(operator));

        Symbol symbolKleinA = new Symbol("a");
        Set<String> expRegelKleinA = new HashSet<>(Collections.singletonList("a"));
        assertEquals(expRegelKleinA, symbolKleinA.firstMenge(operator));
    }

    @Test
    public void testAufgFirst() {
        Operator operator = getSABCOperator();

        Set<String> expFirstS = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> expFirstA = new HashSet<>(Arrays.asList("a", Symbol.EPSILON));
        Set<String> expFirstB = new HashSet<>(Arrays.asList("b", Symbol.EPSILON));
        Set<String> expFirstC = new HashSet<>(Arrays.asList("d"));
        expFirstC.addAll(expFirstS);

        assertEquals(expFirstS, operator.searchRegel("S").firstMenge(operator));
        assertEquals(expFirstA, operator.searchRegel("A").firstMenge(operator));
        assertEquals(expFirstB, operator.searchRegel("B").firstMenge(operator));
        assertEquals(expFirstC, operator.searchRegel("C").firstMenge(operator));
    }

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
}