package Expressions;
import Utils.*;

public class ExprGrouping extends Expression {
    final Expression expression;

    public ExprGrouping(Expression expression) {
        this.expression = expression;
    }

    public Object resolver(TablaSimbolos tabla){
        return expression.resolver(tabla);
    }

}
