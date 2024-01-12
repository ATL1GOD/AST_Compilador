package Statements;
import java.util.List;

import Utils.TablaSimbolos;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    public StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    public Object ejecutar(TablaSimbolos tabla){
        for(Statement stmt : statements){
            if(stmt != null){
                stmt.ejecutar(tabla);
            }
            if(stmt instanceof StmtReturn){
                return stmt.ejecutar(tabla);
            }
        }
        return null;
    }
}
