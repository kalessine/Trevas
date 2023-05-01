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

public class ComparisonFunctionsTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testNull() throws ScriptException {
        // Between
        engine.eval("a := between(null, 1, 100);");
        assertThat((Boolean) engine.getContext().getAttribute("a")).isNull();
        engine.eval("b := between(10, null, 100);");
        assertThat((Boolean) engine.getContext().getAttribute("b")).isNull();
        engine.eval("c := between(10, 1, null);");
        assertThat((Boolean) engine.getContext().getAttribute("c")).isNull();
        // CharsetMatch
        engine.eval("a := match_characters(\"ko\", null);");
        assertThat((Boolean) engine.getContext().getAttribute("a")).isNull();
        engine.eval("b := match_characters(null, \"test\");");
        assertThat((Boolean) engine.getContext().getAttribute("b")).isNull();
    }

    @Test
    public void testBetweenAtom() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("b := between(10, 1,100);");
        assertThat((Boolean) context.getAttribute("b")).isTrue();
        engine.eval("b := between(10, 20,100);");
        assertThat((Boolean) context.getAttribute("b")).isFalse();
        engine.eval("b := between(10.5, 20,100);");
        assertThat((Boolean) context.getAttribute("b")).isFalse();

        context.setAttribute("ds", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        Object res = engine.eval("res := between(ds[keep id, long1, double2], 5, 15);");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Toto", "long1", false, "double2", false),
                Map.of("id", "Hadrien", "long1", true, "double2", true),
                Map.of("id", "Nico", "long1", false, "double2", false),
                Map.of("id", "Franck", "long1", false, "double2", false)
        );
        assertThat(((Dataset) res).getDataStructure().get("long1").getType()).isEqualTo(Boolean.class);

        assertThatThrownBy(() -> {
            engine.eval("b := between(10.5, \"ko\", true);");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type String, expected Number");
    }

    @Test
    public void testCharsetMatchAtom() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("t := match_characters(\"test\", \"(.*)(es)(.*)?\");");
        assertThat((Boolean) context.getAttribute("t")).isTrue();
        engine.eval("t := match_characters(\"test\", \"tes.\");");
        assertThat((Boolean) context.getAttribute("t")).isTrue();
        engine.eval("t := match_characters(\"test\", \"tes\");");
        assertThat((Boolean) context.getAttribute("t")).isFalse();
        engine.eval("t := match_characters(\"test\", \"(.*)(aaaaa)(.*)?\");");
        assertThat((Boolean) context.getAttribute("t")).isFalse();

        context.setAttribute("ds", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        Object res = engine.eval("res := match_characters(ds[keep id, string1, string2], \"(.*)o(.*)\");");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Toto", "string1", true, "string2", false),
                Map.of("id", "Hadrien", "string1", false, "string2", false),
                Map.of("id", "Nico", "string1", true, "string2", false),
                Map.of("id", "Franck", "string1", false, "string2", false)
        );
        assertThat(((Dataset) res).getDataStructure().get("string1").getType()).isEqualTo(Boolean.class);

        assertThatThrownBy(() -> {
            engine.eval("t := match_characters(\"test\", true);");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Boolean, expected String");
        assertThatThrownBy(() -> {
            engine.eval("t := match_characters(10.5, \"pattern\");");
        }).isInstanceOf(InvalidTypeException.class)
                .hasMessage("invalid type Double, expected String");
    }

    @Test
    public void testIsNullAtom() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("n := isnull(null);");
        assertThat((Boolean) context.getAttribute("n")).isTrue();
        engine.eval("n := isnull(\"null\");");
        assertThat((Boolean) context.getAttribute("n")).isFalse();

        context.setAttribute("ds", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        Object res = engine.eval("res := isnull(ds[keep id, string1, bool1]);");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Toto", "string1", false, "bool1", false),
                Map.of("id", "Hadrien", "string1", false, "bool1", false),
                Map.of("id", "Nico", "string1", false, "bool1", false),
                Map.of("id", "Franck", "string1", false, "bool1", false)
        );
        assertThat(((Dataset) res).getDataStructure().get("string1").getType()).isEqualTo(Boolean.class);
    }

}
