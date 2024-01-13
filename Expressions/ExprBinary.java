package Expressions;
import Utils.TablaSimbolos;
import Utils.Token;

public class ExprBinary extends Expression{ // Clase que representa una expresión binaria
    final Expression left;
    final Token operator;
    final Expression right;

    public ExprBinary(Expression left, Token operator, Expression right) { //Este es el constructor de la clase
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Object resolver(TablaSimbolos tabla){ //Este es el método que se encarga de resolver las expresiones binarias
        Object izquierda = left.resolver(tabla);
        Object derecha = right.resolver(tabla);

        switch (operator.getTipo()){ //En esta linea se verifica el tipo de operador
            case PLUS -> { //Aqui se hacen los casos para cada operador y se retorna el resultado de la operación
                if(izquierda instanceof String || derecha instanceof String){ 
                    return izquierda.toString() + derecha.toString(); 
                } else{
                    if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){ //Aqui se verifica que los operandos sean números enteros o flotantes
                        return Double.parseDouble(izquierda.toString()) + Double.parseDouble(derecha.toString());//Aqui se hace la suma de los números
                    } else{//Si no son números enteros o flotantes se lanza una excepción
                        throw new RuntimeException("Solo se permite sumar números o concatenar cadenas"); //por ultimo aqui se hace que se lance una excepción si la variable no existe 
                    }
                }
            }
            case MINUS -> { 
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) - Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case STAR -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) * Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case SLASH -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) / Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case GREATER -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) > Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case LESS -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) < Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case LESS_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) <= Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case GREATER_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) >= Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case EQUAL_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) == Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            case BANG_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) != Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se permite operar números enteros o flotantes");
                }
            }
            default -> throw new RuntimeException("Operador no fue reconocido");
        }

}
