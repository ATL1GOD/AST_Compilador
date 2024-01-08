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

    private Expression logicOr(){ //Aqui el codigo se encarga de verificar si el token actual es de tipo OR (||)
        Expression expr = logicAnd();
        expr = logicOr2(expr);
        return expr;
    }

    private Expression logicOr2(Expression expr){ //este funcion se encarga de verificar si el token actual es de tipo OR (||)
        Token operador;
        Expression expr2, expb;

        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            operador = previous();
            expr2 = logicAnd();
            expb = new ExprLogical(expr, operador, expr2);
            return logicOr2(expb);
        }
        return expr;
    }

    private Expression logicAnd(){ //En esta funcion se verifica si el token actual es de tipo AND (&&)
        Expression expr = equality();
        expr = logicAnd2(expr);
        return expr;
    }

    private Expression logicAnd2(Expression expr){ //este metodo se encarga de verificar si el token actual es de tipo AND (&&)
        Token operador;
        Expression expr2, expb;

        if(preanalisis.tipo == TipoToken.AND){
            match(TipoToken.AND);
            operador = previous();
            expr2 = equality();
            expb = new ExprLogical(expr, operador, expr2);
            return logicAnd2(expb);
        }
        return expr;
    }

    private Expression equality(){ //este metodo se encarga de verificar si el token actual es de tipo BANG_EQUAL (!=) o de tipo EQUAL_EQUAL (==)
        Expression expr = comparison();
        expr = equality2(expr);
        return expr;
    }

    private Expression equality2(Expression expr){ //este metodo se encarga de verificar si el token actual es de tipo BANG_EQUAL (!=) o de tipo EQUAL_EQUAL (==)
        Token operador;
        Expression expr2, expb;
        if(preanalisis.tipo==TipoToken.BANG_EQUAL){ //se verifica que el token actual sea de tipo BANG_EQUAL (!=)
                match(TipoToken.BANG_EQUAL);
                operador = previous();
                expr2 = comparison();
                expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
        }
        else if(preanalisis.tipo==TipoToken.EQUAL_EQUAL){ //se verifica que el token actual sea de tipo EQUAL_EQUAL (==)
                match(TipoToken.EQUAL_EQUAL);
                operador = previous();
                expr2 = comparison();
                expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
        }
        return expr;
    }

    private Expression comparison(){ //este metodo se encarga de verificar si el token actual es de tipo GREATER (>), GREATER_EQUAL (>=), LESS (<) o LESS_EQUAL (<=)
        Expression expr = term();
        expr = comparison2(expr);
        return expr;
    }

    private Expression comparison2(Expression expr){ //En este metodo se hace lo mismo que en el metodo anterior, pero con la diferencia de que se le pasa una expresion como parametro
        Token operador;
        Expression expr2, expb;
        if(preanalisis.tipo==TipoToken.GREATER){ //este if se encarga de verificar que el token actual sea de tipo GREATER (>)
            match(TipoToken.GREATER); 
            operador = previous(); //se guarda el token anterior en una variable de tipo Token
            expr2 = term(); //se llama al metodo term, el cual retorna una expresion
            expb = new ExprBinary(expr, operador, expr2); 
            return comparison2(expb);
        }
        else if(preanalisis.tipo==TipoToken.GREATER_EQUAL){ //Aqui el codigo hace lo mismo que el if anterior, pero con el operador GREATER_EQUAL (>=)
                match(TipoToken.GREATER_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
        }
        else if(preanalisis.tipo == TipoToken.LESS){ //este else if hace lo mismo que el anterior, pero con el operador LESS (<)
                match(TipoToken.LESS);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
        }
        else if(preanalisis.tipo == TipoToken.LESS_EQUAL){ //se verifica que el token actual sea de tipo LESS_EQUAL (<=)
                match(TipoToken.LESS_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
        }
        return expr;
    }

    
    private Expression term(){
        Expression expr = factor();
        expr = term2(expr);
        return expr;
    }

    private Expression term2(Expression expr){
        Token operador;
        Expression expr2, expb;
        if(preanalisis.tipo == TipoToken.MINUS){
                match(TipoToken.MINUS);
                operador = previous();
                expr2 = factor();
                expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
        }
        else if(preanalisis.tipo == TipoToken.PLUS){
                match(TipoToken.PLUS);
                operador = previous();
                expr2 = factor();
                expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
        }
        return expr;
    }

    private Expression factor(){
        Expression expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr){
        Token operador;
        Expression expr2, expb;

        if(preanalisis.tipo==TipoToken.SLASH){
                match(TipoToken.SLASH);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb); 
        }
        else if(preanalisis.tipo==TipoToken.STAR){
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
        }
        return expr;
    }

    private Expression unary(){
        Token operador;
        Expression expr;
        switch (preanalisis.tipo) {
            case BANG -> {
                match(TipoToken.BANG);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            }
            case MINUS -> {
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            }
            default -> {
                return call();
            }
        }
    }

    private Expression call(){
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr){
        if (preanalisis.tipo == TipoToken.LEFT_PAREN) {
            match(TipoToken.LEFT_PAREN);
            List<Expression> lstArguments = argumentsOpc();
            match(TipoToken.RIGHT_PAREN);
            ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
            return call2(ecf);
        }
        return expr;
    }

    private Expression primary(){
        if(preanalisis.tipo==TipoToken.TRUE){
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
        }
        else if(preanalisis.tipo==TipoToken.FALSE){
                match(TipoToken.FALSE);
                return new ExprLiteral(false);            
        }
        else if(preanalisis.tipo==TipoToken.NULL){
                match(TipoToken.NULL);
                return new ExprLiteral(null);            
        }
        //IDENTIFIER,LEFT_PAREN,RIGHT_PAREN
        else if(preanalisis.tipo==TipoToken.NUMBER){
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.literal);
        }    
        else if(preanalisis.tipo==TipoToken.STRING){
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.literal);             
        }    
        else if(preanalisis.tipo==TipoToken.IDENTIFIER){
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
        }
        else if(preanalisis.tipo==TipoToken.LEFT_PAREN || preanalisis.tipo==TipoToken.RIGHT_PAREN){
                match(TipoToken.LEFT_PAREN);
                Expression expr = expression();
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
        }
        return null;
    }

     private Statement function(){
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        match(TipoToken.LEFT_PAREN);
        List<Token> params = parametersOpc();
        match(TipoToken.RIGHT_PAREN);
        Statement body = block();
        return new StmtFunction(id, params, (StmtBlock) body);
    }

    private List<Token> parametersOpc(){
        List<Token> params = new ArrayList<>();

        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            params = parameters(params);
            return params;
        }
        return null;
    }

    private List<Token> parameters(List<Token> params){
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        params.add(id);
        params = parameters2(params);
        return params;
    }

    private List<Token> parameters2(List<Token> params){
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token id = previous();
            params.add(id);
            return parameters2(params);
        }
        return params;
    }

    private List<Expression> argumentsOpc(){
        List<Expression> args = new ArrayList<>();

        if(isEXPR()){
            Expression expr = expression();
            args.add(expr);
            arguments(args);
            return args;
        }
        return null;
    }

    private void arguments(List<Expression> args){
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            Expression expr = expression();
            args.add(expr);
            arguments(args);
        }
    }

    private boolean isEXPR(){
        return preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN;
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error. Se esperaba " + preanalisis.tipo +
                    " pero se encontró " + tt;
            System.out.println(message);
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }
}





