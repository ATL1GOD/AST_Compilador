package Statements;
import Expressions.Expression;

public class StmtReturn extends Statement {
    final Expression value;

    public StmtReturn(Expression value) {
        this.value = value;
    }
    
    public Object ejecutar(TablaSimbolos tabla){
        return value.resolver(tabla);
    }
}
