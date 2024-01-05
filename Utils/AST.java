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
