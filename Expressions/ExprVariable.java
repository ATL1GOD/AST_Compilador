package Expressions;

import Utils.*;

public class ExprVariable extends Expression { 
    public final Token name; 
    //para corregir el error de la linea 217 del asrchivo AST.java tuve hacer publica la variable name

    public ExprVariable(Token name) {
        this.name = name;
    }
}