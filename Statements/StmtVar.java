package Statements;
import Expressions.Expression;
import Utils.*;

public class StmtVar extends Statement {
    final Token name;
    final Expression initializer;

    public StmtVar(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public Object ejecutar(TablaSimbolos tabla){
        Object valor;
        if(initializer != null){
            valor = initializer.resolver(tabla);
            tabla.asignar(name.getLexema(), valor);
        } else{
            tabla.asignar(name.getLexema(), null);
        }

        return null;
    }
}
