package Utils;
import java.util.*;
import Principal.Interprete;
import Statements.*;
import Statements.StmtExpression;
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
