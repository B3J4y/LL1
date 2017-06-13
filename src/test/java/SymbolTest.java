import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

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

        Set<String> expRegel1Menge = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        Set<String> expRegel2Menge = new HashSet<>(Arrays.asList("d", Symbol.EPSILON));
        Set<String> expRegel3Menge = new HashSet<>(Arrays.asList(Symbol.EPSILON));
        assertEquals(expRegel1Menge, symbol1.firstMenge(regeln));
        assertEquals(expRegel2Menge, symbol2.firstMenge(regeln));
        assertEquals(expRegel3Menge, symbol3.firstMenge(regeln));

        Symbol symbolKleinA = new Symbol("a");
        Set<String> expRegelKleinA = new HashSet<>(Collections.singletonList("a"));
        assertEquals(expRegelKleinA, symbolKleinA.firstMenge(regeln));
    }

    @Test
    public void testAufgFirst() {
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

        Set<String> expFirstS = new HashSet<>(Arrays.asList("a", "b", "c"));
        Set<String> expFirstA = new HashSet<>(Arrays.asList("a", Symbol.EPSILON));
        Set<String> expFirstB = new HashSet<>(Arrays.asList("b", Symbol.EPSILON));
        Set<String> expFirstC = new HashSet<>(Arrays.asList("d"));
        expFirstC.addAll(expFirstS);

        assertEquals(expFirstS, symbolS.firstMenge(regeln));
        assertEquals(expFirstA, symbolA.firstMenge(regeln));
        assertEquals(expFirstB, symbolB.firstMenge(regeln));
        assertEquals(expFirstC, symbolC.firstMenge(regeln));
    }

}