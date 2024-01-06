package Utils;
import java.util.*;
import Principal.Interprete;
import Statements.*;
import Expressions.*;


public class AST { //Analizador Sintactico Abstracto (Abstract Syntax Tree)
    private final List<Token> tokens; //se crea una lista de tokens
    private int i = 0; //se crea un contador para recorrer la lista de tokens
    private Token preanalisis; //se crea un token para guardar el token actual

    public AST(List<Token> tokens) { //se realiza el constructor de la clase, el cual recibe una lista de tokens
        this.tokens = tokens; //se asigna la lista de tokens recibida, a la lista de tokens de la clase
        preanalisis = tokens.get(i); //this.tokens.get(i); //se asigna el primer token de la lista a preanalisis
    }

    public List<Statement> program(){ //se crea el metodo program, el cual retorna una lista de statements (declaraciones)
        List<Statement> program = new ArrayList<>(); //Aqui cree una lista de statements, la cual se va a retornar
        if(preanalisis.tipo != TipoToken.EOF){ 
            List<Statement>  res = declaration(program); //se llama al metodo declaration, el cual retorna una lista de statements
            return res; //Aqui se retorna la lista de statements
        }
        return null; //si no se cumple la condicion anterior, se retorna null
    } 

    private List<Statement> declaration(List<Statement> program){//Esta parte del codigo hace lo mismo que el metodo program, pero con la diferencia de que se le pasa una lista de statements, es decir que se le pasa una lista de statements vacia, y se retorna una lista de statements con los statements que se encontraron en el codigo
         if(preanalisis.tipo == TipoToken.FUN){  //Esta parte del codigo se encarga de ver si el token actual es de tipo FUN, si es asi, se llama al metodo funDecl, el cual retorna un statement
            Statement stmt = funDecl();
            program.add(stmt);
            return declaration(program);
         }else if(preanalisis.tipo == TipoToken.VAR){ //Esta parte del codigo se encarga de ver si el token actual es de tipo VAR, si es asi, se llama al metodo varDecl, el cual retorna un statement
            Statement stmt = varDecl();
            program.add(stmt);
            return declaration(program);
         }else if(isEXPR()||preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.LEFT_BRACE){ 
            //esta parte del codigo se encarga de ver si el token actual es de tipo EXPR, PRINT, RETURN, IF, WHILE, FOR o LEFT_BRACE, si es asi, se llama al metodo statement, el cual retorna un statement
            Statement stmt = statement(); 
            program.add(stmt);
            return declaration(program);
         } 
            return program;
    }

    private Statement funDecl(){ //Este metodo se encarga de crear un statement de tipo funcion (funcion nombreFuncion(parametros){statements})
        match(TipoToken.FUN);//se verifica que el token actual sea de tipo FUN
        return function();
    }

    private Statement varDecl(){ //esta parte del codigo se encarga de crear un statement de tipo variable (var nombreVariable = expresion;)
        match(TipoToken.VAR);//se verifica que el token actual sea de tipo VAR
        match(TipoToken.IDENTIFIER);
        Token id = previous(); //se guarda el token anterior en una variable de tipo Token
        Expression expr = varInit(); //se llama al metodo varInit, el cual retorna una expresion
        match(TipoToken.SEMICOLON);
        return new StmtVar(id,expr);
    }

    private Expression varInit(){ //AQui se crea una expresion de tipo variable inicializada (variable = expresion)
        if(preanalisis.tipo == TipoToken.EQUAL){ //se verifica que el token actual sea de tipo EQUAL
            match(TipoToken.EQUAL);
            return expression();
        }
        return null;
    }

    
    private Statement statement(){
        if(isEXPR()){
            return exprStmt();
        } else if(preanalisis.tipo == TipoToken.FOR){
            return forStmt();
        } else if(preanalisis.tipo == TipoToken.IF){
            return ifStmt();
        } else if(preanalisis.tipo == TipoToken.PRINT){
            return printStmt();
        } else if(preanalisis.tipo == TipoToken.RETURN){
            return returnStmt();
        } else if(preanalisis.tipo == TipoToken.WHILE){
            return whileStmt();
        } else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            return block();
        }
        return null;
    }

    private Statement exprStmt() {
        Expression expr = expression();
        match(TipoToken.SEMICOLON);
        StmtExpression stmtExpr=new StmtExpression(expr);
        return stmtExpr;
    }

    private Statement forStmt(){
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        Statement initializer = forStmt1();
        Expression condition = forStmt2();
        Expression increment = forStmt3();
        match(TipoToken.RIGHT_PAREN);
        Statement body = statement();
        //return new StmtFor(stmt1,expr2,expr3,body);
        if (increment != null){
            body = new StmtBlock(Arrays.asList(body,
                        new StmtExpression(increment)
                )
            );
        }
        if (condition == null){
            condition = new ExprLiteral(true);
        }
        body = new StmtLoop(condition,body);
        if (initializer != null){
            body = new StmtBlock(Arrays.asList(initializer, body));
        }
        return body;
    }

    private Statement forStmt1(){
        if(preanalisis.tipo == TipoToken.VAR){
            return varDecl();
        } else if(isEXPR()){
            return exprStmt();
        }
        match(TipoToken.SEMICOLON);
        return null;
    }

    private Expression forStmt2(){
        if(isEXPR()){
            Expression expr =  expression();
            match(TipoToken.SEMICOLON);
            return expr;
        }
        match(TipoToken.SEMICOLON);
        return null;
    }

    private Expression forStmt3(){
        if(isEXPR()){
            return expression();
        }
        return null;
    }

     private Statement ifStmt(){ 
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression cond = expression();
        match(TipoToken.RIGHT_PAREN);
        Statement thenBr = statement();
        Statement elseBr = elseStmt();
        return new StmtIf(cond,thenBr,elseBr);
    }

    private Statement elseStmt(){
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            return statement();
        }
        return null;
    }

    private Statement printStmt(){
        match(TipoToken.PRINT);
        Expression expr = expression();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(expr);
    }

    private Statement returnStmt(){
        match(TipoToken.RETURN);
        Expression retExp = retExpOpc();
        match(TipoToken.SEMICOLON);
        return new StmtReturn(retExp);
    }

    private Expression retExpOpc(){
        if(isEXPR()){
            return expression();
        }
        return null;
    }

    private Statement whileStmt(){
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        Expression expr = expression();
        match(TipoToken.RIGHT_PAREN);
        Statement body = statement();
        return new StmtLoop(expr,body);
    }

    private Statement block(){
        match(TipoToken.LEFT_BRACE);
        List<Statement> stmts = new ArrayList<>();
        stmts = declaration(stmts);
        match(TipoToken.RIGHT_BRACE);
        return new StmtBlock(stmts);
    }

    private Expression expression(){
        return assignment();
    }

    private Expression assignment(){
        Expression expr = logicOr();
        expr = assignmentOpc(expr);
        return expr;
    }

    private Expression assignmentOpc(Expression expr){
        Expression expr2;

        if(preanalisis.tipo == TipoToken.EQUAL) {
            Token t;
            if (expr instanceof ExprVariable) {
                t = ((ExprVariable) expr).name;
                match(TipoToken.EQUAL);
                expr2 = expression();
                return new ExprAssign(t, expr2);
            } else {
                //envio de error
                System.out.println("Error, solo los identificadores se pueden asignar.");
                Interprete.error(1, "Error");
            }
        }
        return expr;
    }


