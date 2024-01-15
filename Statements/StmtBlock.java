package Statements;
import java.util.List;

import Utils.TablaSimbolos;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    public StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    public Object ejecutar(TablaSimbolos tabla){
        TablaSimbolos inferior = new TablaSimbolos(tabla);
        for(Statement stmt : statements){
            if(stmt != null){
                if(stmt instanceof StmtReturn){
                    return stmt.ejecutar(inferior);
                }
                stmt.ejecutar(inferior);
            }
        }
        return null;
    }
}
