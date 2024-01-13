package Statements;
import Expressions.Expression;
import Utils.*;

public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    public StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Object ejecutar(TablaSimbolos tabla){
        Object condicion = condition.resolver(tabla);

        if(!(condicion instanceof  Boolean)){
            throw new RuntimeException("Condicion incorrecta");
        }

        if((boolean) condicion){
            return thenBranch.ejecutar(tabla);
        } else if(elseBranch != null){
            return elseBranch.ejecutar(tabla);
        } else{
            return null;
        }
    }
}
