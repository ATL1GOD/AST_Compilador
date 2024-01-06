//ASDR es la gramatica
package Utils;
import java.util.*;
import Principal.Interprete;

public class ASDR implements Parser{
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;

    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    public boolean parse() {
        i=0;
        PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Codigo sintacticamente correcto");
            return  true;
        }else {
            return false;
        }
    }
    //inica la gramatica del pdf
    private void PROGRAM(){ //PROGRAM -> DECLARATION
        if(isEXPR_STMTderiv() || preanalisis.tipo == TipoToken.FUN || preanalisis.tipo == TipoToken.VAR || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE || preanalisis.tipo == TipoToken.EOF) {
            DECLARATION();
        } else{
            System.out.println( "Error sintactico encontrado, se esperaba 'fun', 'var', 'for', 'if', 'print', 'return', 'while', '{', '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('");
            Interprete.error(1,"Error");
        }
    }

    private void DECLARATION(){ //DECLARATION -> FUN_DECL DECLARATION | VAR_DECL DECLARATION | STATEMENT DECLARATION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN){
            FUN_DECL();
            DECLARATION();
        } else if(preanalisis.tipo == TipoToken.VAR){
            VAR_DECL();
            DECLARATION();            
        } else if (isEXPR_STMTderiv() || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE) {
            STATEMENT();
            DECLARATION();
        }
    }

    private void FUN_DECL(){ //FUN_DECL -> fun FUNCTION
        if(hayErrores)
            return;

        coincidir(TipoToken.FUN);
        FUNCTION();
    }

    private void VAR_DECL(){ //VAR_DECL -> var id VAR_INIT;
        if(hayErrores)
            return;

        coincidir(TipoToken.VAR);
        coincidir(TipoToken.IDENTIFIER);
        VAR_INIT();
        coincidir(TipoToken.SEMICOLON);
    }

    private void VAR_INIT(){ //VAR_INIT -> = EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.EQUAL) {
            coincidir(TipoToken.EQUAL);
            EXPRESSION();
        }
    }

    
    private void STATEMENT(){ //STATEMENT -> EXPR_STMT | FOR_STMT | IF_STMT | PRINT_STMT | RETURN_STMT | WHILE_STMT | BLOCK
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPR_STMT();
        } else if(preanalisis.tipo == TipoToken.FOR){
            FOR_STMT();
        } else if(preanalisis.tipo == TipoToken.IF){
            IF_STMT();
        } else if(preanalisis.tipo == TipoToken.PRINT){
            PRINT_STMT();
        } else if(preanalisis.tipo == TipoToken.RETURN){
            RETURN_STMT();
        } else if(preanalisis.tipo == TipoToken.WHILE){
            WHILE_STMT();
        } else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            BLOCK();
        } else{
            hayErrores = true;
            System.out.println( "Error sintactico encontrado");
            Interprete.error(1,"Error");
        }
    }

    private void EXPR_STMT(){ //EXPR_STMT -> EXPRESSION;
        if(hayErrores)
            return;

        EXPRESSION();
        coincidir(TipoToken.SEMICOLON);
    }

    private void FOR_STMT(){ //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.FOR);
        coincidir(TipoToken.LEFT_PAREN);
        FOR_STMT_1();
        FOR_STMT_2();
        FOR_STMT_3();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
    }

    private void FOR_STMT_1(){ //FOR_STMT_1 -> VAR_DECL | EXPR_STMT | ;
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.VAR){
            VAR_DECL();
        } else if(isEXPR_STMTderiv()){
            EXPR_STMT();
        } else if(preanalisis.tipo == TipoToken.SEMICOLON){
            coincidir(TipoToken.SEMICOLON);
        } else{
            hayErrores = true;
            System.out.println("Error sintactico encontrado");
            Interprete.error(1,"Error");
        }
    }

    private void FOR_STMT_2(){ //FOR_STMT_2 -> EXPRESSION | ;
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPRESSION();
            coincidir(TipoToken.SEMICOLON);
        } else if(preanalisis.tipo == TipoToken.SEMICOLON){
            coincidir(TipoToken.SEMICOLON);
        } else{
            hayErrores = true;
           System.out.println( "Error sintactico encontrado");
            Interprete.error(1,"Error");
        }
    }

    private void FOR_STMT_3(){ //FOR_STMT_3 -> EXPRESSION | Ɛ
        if(hayErrores)
            return;
        
        if(isEXPR_STMTderiv()){
            EXPRESSION();
        }
    }

    private void IF_STMT(){ //IF_STMT -> if ( EXPRESSION ) STATEMENT ELSE_STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.IF);
        coincidir(TipoToken.LEFT_PAREN);
        EXPRESSION();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
        ELSE_STATEMENT();
    }

    private void ELSE_STATEMENT(){ //ELSE_STATEMENT -> else STATEMENT | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.ELSE){
            coincidir(TipoToken.ELSE);
            STATEMENT();
        }
    }

    private void PRINT_STMT(){ //PRINT_STMT -> print EXPRESSION;
        if(hayErrores)
            return;

        coincidir(TipoToken.PRINT);
        EXPRESSION();
        coincidir(TipoToken.SEMICOLON);
    }

    private void RETURN_STMT(){ //RETURN_STMT -> return RETURN_EXP_OPC;
        if(hayErrores)
            return;

        coincidir(TipoToken.RETURN);
        RETURN_EXP_OPC();
        coincidir(TipoToken.SEMICOLON);
    }

    private void RETURN_EXP_OPC(){ //RETURN_EXP_OPC -> EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPRESSION();
        }
    }

    private void WHILE_STMT(){ //WHILE_STMT -> while ( EXPRESSION ) STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.WHILE);
        coincidir(TipoToken.LEFT_PAREN);
        EXPRESSION();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
    }

    private void BLOCK(){ //BLOCK -> { DECLARATION }
        if(hayErrores)
            return;

        coincidir(TipoToken.LEFT_BRACE);
        DECLARATION();
        coincidir(TipoToken.RIGHT_BRACE);
    }

    private void EXPRESSION(){ //EXPRESSION -> ASSIGNMENT
        if(hayErrores)
            return;

        ASSIGNMENT();
    }

    private void ASSIGNMENT(){ //ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
        if(hayErrores)
            return;

        LOGIC_OR();
        ASSIGNMENT_OPC();
    }

    private void ASSIGNMENT_OPC(){ //ASSIGNMENT_OPC -> = EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.EQUAL) {
            coincidir(TipoToken.EQUAL);
            EXPRESSION();
        }
    }
