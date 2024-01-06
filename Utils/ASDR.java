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
