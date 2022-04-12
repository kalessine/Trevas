package fr.insee.vtl.engine.visitors;

import fr.insee.vtl.model.Dataset;
import fr.insee.vtl.model.InMemoryDataset;
import fr.insee.vtl.model.Structured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;

public class MembershipVisitorTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testSimpleMembership() throws ScriptException {
        var dataset = new InMemoryDataset(
                List.of(
                        new Structured.Component("name", String.class, Dataset.Role.IDENTIFIER),
                        new Structured.Component("age", Long.class, Dataset.Role.MEASURE),
                        new Structured.Component("weight", Long.class, Dataset.Role.MEASURE)
                ),
                Arrays.asList("Toto", null, 100L),
                Arrays.asList("Hadrien", 10L, 11L),
                Arrays.asList("Nico", 11L, 10L),
                Arrays.asList("Franck", 12L, 9L)
        );

        ScriptContext context = engine.getContext();
        context.setAttribute("ds1", dataset, ScriptContext.ENGINE_SCOPE);
        engine.eval("ds2 := ds1#m1;");
    }
}
