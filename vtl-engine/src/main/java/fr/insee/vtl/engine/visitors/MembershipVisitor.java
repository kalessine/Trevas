package fr.insee.vtl.engine.visitors;

import fr.insee.vtl.model.DatasetExpression;
import fr.insee.vtl.parser.VtlBaseVisitor;
import fr.insee.vtl.parser.VtlParser;

import java.util.Map;
import java.util.Objects;

public class MembershipVisitor extends VtlBaseVisitor<Object> {

    private final Map<String, Object> context;

    public MembershipVisitor(Map<String, Object> context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public DatasetExpression visitMembershipExpr(VtlParser.MembershipExprContext ctx) {
        return null;
    }
}
