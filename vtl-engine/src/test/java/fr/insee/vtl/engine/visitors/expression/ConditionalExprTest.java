package fr.insee.vtl.engine.visitors.expression;

import fr.insee.vtl.engine.exceptions.FunctionNotFoundException;
import fr.insee.vtl.engine.samples.DatasetSamples;
import fr.insee.vtl.model.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConditionalExprTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testNull() throws ScriptException {
        engine.eval("a := if cast(null, boolean) then \"true\" else \"false\";");
        assertThat((Boolean) engine.getContext().getAttribute("a")).isNull();
        engine.eval("b := if true then cast(null, string) else \"false\";");
        assertThat((Boolean) engine.getContext().getAttribute("b")).isNull();
        engine.eval("c := if false then \"true\" else cast(null, string);");
        assertThat((Boolean) engine.getContext().getAttribute("c")).isNull();
        engine.eval("d := if false then cast(null, integer) else cast(null, integer);");
        assertThat((Boolean) engine.getContext().getAttribute("d")).isNull();
    }

    @Test
    public void testIfExpr() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("s := if true then \"true\" else \"false\";");
        assertThat(context.getAttribute("s")).isEqualTo("true");
        engine.eval("l := if false then 1 else 0;");
        assertThat(context.getAttribute("l")).isEqualTo(0L);

        engine.getContext().setAttribute("ds_1", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        engine.getContext().setAttribute("ds_2", DatasetSamples.ds2, ScriptContext.ENGINE_SCOPE);
        engine.eval("ds1 := ds_1[keep id, long1][rename long1 to bool_var]; " +
                "ds2 := ds_2[keep id, long1][rename long1 to bool_var]; " +
                "res := if ds1 > ds2 then ds1 else ds2;");
        var res = engine.getContext().getAttribute("res");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Hadrien", "bool_var", 150L),
                Map.of("id", "Nico", "bool_var", 20L),
                Map.of("id", "Franck", "bool_var", 100L)
        );
        assertThat(((Dataset) res).getDataStructure().get("bool_var").getType()).isEqualTo(Long.class);
    }

    @Test
    public void testNvlExpr() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("s := nvl(\"toto\", \"default\");");
        assertThat(context.getAttribute("s")).isEqualTo("toto");
        engine.eval("s := nvl(cast(null, string), \"default\");");
        assertThat(context.getAttribute("s")).isEqualTo("default");

        engine.getContext().setAttribute("ds", DatasetSamples.ds1, ScriptContext.ENGINE_SCOPE);
        engine.eval("res := nvl(ds[keep id, long1], 0);");
        var res = engine.getContext().getAttribute("res");
        assertThat(((Dataset) res).getDataAsMap()).containsExactlyInAnyOrder(
                Map.of("id", "Toto", "long1", 30L),
                Map.of("id", "Hadrien", "long1", 10L),
                Map.of("id", "Nico", "long1", 20L),
                Map.of("id", "Franck", "long1", 100L)
        );
        assertThat(((Dataset) res).getDataStructure().get("long1").getType()).isEqualTo(Long.class);

        assertThatThrownBy(() -> {
            engine.eval("s := nvl(3, \"toto\");");
        }).isInstanceOf(FunctionNotFoundException.class)
                .hasMessage("function 'nvl(Long, String)' not found");
    }

    @Test
    public void testIfTypeExceptions() {
        assertThatThrownBy(() -> {
            engine.eval("s := if \"\" then 1 else 2;");
        }).isInstanceOf(FunctionNotFoundException.class)
                .hasMessage("function 'ifThenElse(String, Long, Long)' not found");

        assertThatThrownBy(() -> {
            engine.eval("s := if true then \"\" else 2;");
        }).isInstanceOf(FunctionNotFoundException.class)
                .hasMessage("function 'ifThenElse(Boolean, String, Long)' not found");
    }
}
