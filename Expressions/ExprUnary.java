package Expressions;

import Utils.*;

public class ExprUnary extends Expression{
    final Token operator;
    final Expression right;

    public ExprUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public Object resolver(TablaSimbolos tabla){
        Object derecha = right.resolver(tabla);

        switch (operator.getTipo()){
            case BANG -> {
                if(derecha instanceof Boolean){
                    return !(boolean) derecha;
                } else{
                    throw new RuntimeException("Este operador '!' solo puede ser empleado en expresiones booleanas");
                }
            }
            case MINUS -> {
                if(derecha instanceof Integer || derecha instanceof Double){
                    return -(double)derecha;
                } else{
                    throw new RuntimeException("El operador menos solo puede ser empleado en números");
                }
            }
            default -> throw new RuntimeException("Operador no encontrado");
        }
    }
}
