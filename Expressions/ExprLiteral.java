package Expressions;
//import Utils.Token
import Utils.*;
public class ExprLiteral extends Expression {
    final Object value;

    public ExprLiteral(Object value) {
        this.value = value;
    }
    public Object resolver (TablaSimbolos tabla){
        return value;
    }
}
