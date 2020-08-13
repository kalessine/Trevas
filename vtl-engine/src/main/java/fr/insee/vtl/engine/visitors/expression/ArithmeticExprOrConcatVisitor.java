package fr.insee.vtl.engine.visitors.expression;

import fr.insee.vtl.model.DoubleExpression;
import fr.insee.vtl.model.LongExpression;
import fr.insee.vtl.model.ResolvableExpression;
import fr.insee.vtl.model.StringExpression;
import fr.insee.vtl.parser.VtlBaseVisitor;
import fr.insee.vtl.parser.VtlParser;

import java.util.Objects;

import static fr.insee.vtl.engine.utils.TypeChecking.*;

public class ArithmeticExprOrConcatVisitor extends VtlBaseVisitor<ResolvableExpression> {

    private final ExpressionVisitor exprVisitor;

    public ArithmeticExprOrConcatVisitor(ExpressionVisitor expressionVisitor) {
        exprVisitor = Objects.requireNonNull(expressionVisitor);
    }

    @Override
    public ResolvableExpression visitArithmeticExprOrConcat(VtlParser.ArithmeticExprOrConcatContext ctx) {
        switch (ctx.op.getType()) {
            case VtlParser.PLUS:
                return handlePlus(ctx.left, ctx.right);
            case VtlParser.MINUS:
                return handleMinus(ctx.left, ctx.right);
            case VtlParser.CONCAT:
                return handleConcat(ctx.left, ctx.right);
            default:
                throw new UnsupportedOperationException("unknown operator " + ctx);
        }
    }

    private ResolvableExpression handlePlus(VtlParser.ExprContext left, VtlParser.ExprContext right) {
        var leftExpression = assertNumber(exprVisitor.visit(left), left);
        var rightExpression = assertNumber(exprVisitor.visit(right), right);
        if (isLong(leftExpression) && isLong(rightExpression)) {
            return LongExpression.of(context -> {
                Long leftValue = (Long) leftExpression.resolve(context);
                Long rightValue = (Long) rightExpression.resolve(context);
                return leftValue + rightValue;
            });
        }
        return DoubleExpression.of(context -> {
            var leftValue = leftExpression.resolve(context);
            var rightValue = rightExpression.resolve(context);
            var leftDouble = leftValue instanceof Long ? ((Long) leftValue).doubleValue() : (Double) leftValue;
            var rightDouble = rightValue instanceof Long ? ((Long) rightValue).doubleValue() : (Double) rightValue;
            return leftDouble + rightDouble;
        });
    }

    private ResolvableExpression handleMinus(VtlParser.ExprContext left, VtlParser.ExprContext right) {
        var leftExpression = assertNumber(exprVisitor.visit(left), left);
        var rightExpression = assertNumber(exprVisitor.visit(right), right);
        if (isLong(leftExpression) && isLong(rightExpression)) {
            return LongExpression.of(context -> {
                Long leftValue = (Long) leftExpression.resolve(context);
                Long rightValue = (Long) rightExpression.resolve(context);
                return leftValue - rightValue;
            });
        }
        return DoubleExpression.of(context -> {
            var leftValue = leftExpression.resolve(context);
            var rightValue = rightExpression.resolve(context);
            var leftDouble = leftValue instanceof Long ? ((Long) leftValue).doubleValue() : (Double) leftValue;
            var rightDouble = rightValue instanceof Long ? ((Long) rightValue).doubleValue() : (Double) rightValue;
            return leftDouble - rightDouble;
        });
    }

    private ResolvableExpression handleConcat(VtlParser.ExprContext left, VtlParser.ExprContext right) {
        var leftExpression = assertString(exprVisitor.visit(left), left);
        var rightExpression = assertString(exprVisitor.visit(right), right);
        return StringExpression.of(context -> {
            String leftValue = (String) leftExpression.resolve(context);
            String rightValue = (String) rightExpression.resolve(context);
            return leftValue.concat(rightValue);
        });
    }
}
