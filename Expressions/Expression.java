package Expressions;

import Utils.TablaSimbolos;

public abstract class Expression { //Clase abstracta que representa una expresión
    public abstract Object resolver(TablaSimbolos tabla); //Este es el método que se encarga de resolver las expresiones
}
