package Expressions;
//import Utils.Token
public class ExprLiteral extends Expression {
    final Object value;

    public ExprLiteral(Object value) {
        this.value = value;
    }
}
