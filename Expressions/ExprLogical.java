package Expressions;
import Utils.*;
import Utils.TipoToken;

public class ExprLogical extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    public ExprLogical(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Object resolver(TablaSimbolos tabla){
        Object izquierda = left.resolver(tabla);
        Object derecha = right.resolver(tabla);

        if(izquierda instanceof Boolean && derecha instanceof Boolean){
            switch(operator.getTipo()){
                case AND -> {
                    return (boolean) izquierda && (boolean) derecha;
                }
            }

        }

    }
}
