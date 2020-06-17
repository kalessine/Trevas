package fr.insee.vtl.engine.visitors.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.assertj.core.api.Assertions.assertThat;

public class ArithmeticExprTest {

    private ScriptEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ScriptEngineManager().getEngineByName("vtl");
    }

    @Test
    public void testArithmeticExpr() throws ScriptException {
        ScriptContext context = engine.getContext();
        engine.eval("mul := 2 * 3;");
        assertThat(context.getAttribute("mul")).isEqualTo(6L);
        engine.eval("div := 6 / 3;");
        assertThat(context.getAttribute("div")).isEqualTo(2L);
    }
}
