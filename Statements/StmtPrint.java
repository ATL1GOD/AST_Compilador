package Statements;
import Expressions.Expression;

public class StmtPrint extends Statement {
    final Expression expression;

    public StmtPrint(Expression expression) {
        this.expression = expression;
    }

    public Object ejecutar(TablaSimbolos tabla){
        Object expresion = expression.resolver(tabla);

        System.out.println(expresion.toString());
        return null;
    }
}
