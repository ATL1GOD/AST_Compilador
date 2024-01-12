package Expressions;
import Utils.TablaSimbolos;
import Utils.Token;


public class ExprAssign extends Expression{ // Clase que representa una asignación de variable
    final Token name;
    final Expression value;

    public ExprAssign(Token name, Expression value) { // Constructor de la clase
        this.name = name;
        this.value = value;
    }

    public Object resolver(TablaSimbolos tabla){ // Método que resuelve la asignación de variable
        if(tabla.existeIdentificador(name.getLexema())){ //Aqui se verifica si la variable ya existe
            Object valor = value.resolver(tabla); //En esta linea se obtiene el valor de la variable
            tabla.asignar(name.getLexema(), valor); //En esta linea se asigne el valor a la variable
            return valor;
        }else{
            throw new RuntimeException("La variable no esta definida '" + name.getLexema() + "'."); //por ultimo se lanza una excepción si la variable no existe
        }
    }
}
