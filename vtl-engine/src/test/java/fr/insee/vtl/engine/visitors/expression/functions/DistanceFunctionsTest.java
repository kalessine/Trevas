package fr.insee.vtl.engine.visitors.expression.functions;

import fr.insee.vtl.engine.samples.DatasetSamples;
import fr.insee.vtl.model.Dataset;
import fr.insee.vtl.model.exceptions.InvalidTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DistanceFunctionsTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testNull() throws ScriptException {
        // Levenshtein
        engine.eval("a := levenshtein(null, \"two\");");
        assertThat((Boolean) engine.getContext().getAttribute("a")).isNull();
        engine.eval("b := levenshtein(\"one\", null);");
        assertThat((Boolean) engine.getContext().getAttribute("b")).isNull();
    }

    @Test
    public void testLevenshteinAtom() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("a := levenshtein(\"\", \"\");");
        assertThat(context.getAttribute("a")).isEqualTo(0L);
        engine.eval("b := levenshtein(\"test\", \"tes\");");
        assertThat(context.getAttribute("b")).isEqualTo(1L);

        context.setAttribute("ds", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        Object res = engine.eval("res := levenshtein(ds[keep id, string1], ds[keep id, string2][rename string2 to string1])[rename string1 to lev];");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Toto", "lev", 3L),
                Map.of("id", "Hadrien", "lev", 7L),
                Map.of("id", "Nico", "lev", 4L),
                Map.of("id", "Franck", "lev", 5L)
        );
        assertThat(((Dataset) res).getDataStructure().get("lev").getType()).isEqualTo(Long.class);

        assertThatThrownBy(() -> {
            engine.eval("z := levenshtein(1, \"test\");");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Long, expected String");
    }
}
