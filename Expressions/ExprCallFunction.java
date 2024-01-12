package Expressions;


import java.util.ArrayList;
import java.util.List;

import Statements.StmtFunction;
import Utils.TablaSimbolos;
import Utils.Token;

public class ExprCallFunction extends Expression{ // Clase que representa una llamada a función
    final Expression callee;
    // final Token paren;
    final List<Expression> arguments;

    public ExprCallFunction(Expression callee, /*Token paren,*/ List<Expression> arguments) { //Aqui se encuentra el constructor de la clase
        this.callee = callee;
        // this.paren = paren;
        this.arguments = arguments;
    }

    public Object resolver(TablaSimbolos tabla){ //Este es el método que se encarga de resolver las llamadas a función
        if(!(callee instanceof ExprVariable)){ //Aqui se verifica que el identificador sea válido
            throw new RuntimeException("No se puedo llamar a la funcion."); 
        }

        Object estrFunc = callee.resolver(tabla); //En esta linea se obtiene el valor de la función
        if(!(estrFunc instanceof StmtFunction)){ //Aqui se verifica que el identificador sea válido
            throw new RuntimeException("El identificador ingresado es inválido para llamar a una función.");
        }

        List<Object> argumentos = new ArrayList<>(); //En esta linea se crea una lista para guardar los argumentos de la función
        for(Expression argument : arguments){ //En este ciclo se recorren los argumentos de la función y se guardan en la lista
            argumentos.add( argument.resolver(tabla)); //En esta linea se guarda el argumento en la lista de argumentos de la función
        }

        if(argumentos.size() != ((StmtFunction) estrFunc).params.size()){ //Aqui se verifica que la cantidad de argumentos sea la correcta
            throw new RuntimeException("Esta llamada a función tiene argumentos inecesarios.");
        }
        int n = 0;
        for (Token token : ((StmtFunction) estrFunc).params){ //En este ciclo se recorren los argumentos de la función y se guardan en la tabla de símbolos
            tabla.asignar(token.getLexema(), argumentos.get(n)); //Aqui se guarda el argumento en la tabla de símbolos
            n++;
        }

        return ((StmtFunction) estrFunc).body.ejecutar(tabla); //por ultimo se retorna el resultado de la función 
    }
}
