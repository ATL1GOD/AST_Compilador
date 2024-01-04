package Utils;
import java.util.*; //manera sencilla de tomar toda la libreria .util
/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;*/
import Principal.Interprete;
public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;
    //private static final Map<String, TipoToken> simbolos;
    public static final Map<String, TipoToken> simbolos;

    static boolean error = false;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);

        simbolos = new HashMap<>();
        simbolos.put("+",   TipoToken.PLUS);
        simbolos.put("-",   TipoToken.MINUS);
        simbolos.put("*",   TipoToken.STAR);
        simbolos.put("{",   TipoToken.LEFT_BRACE);
        simbolos.put("}",   TipoToken.RIGHT_BRACE);
        simbolos.put("(",   TipoToken.LEFT_PAREN);
        simbolos.put(")",   TipoToken.RIGHT_PAREN);
        simbolos.put(",",   TipoToken.COMMA);
        simbolos.put(".",   TipoToken.DOT);
        simbolos.put(";",   TipoToken.SEMICOLON);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    //siguiente linea estudiarla
    private final List<String> caracteres = Arrays.asList("+", "-", "*", "{", "}", "(", ")", ",", ".",";");

    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        int estado = 0;
        String lexema = "";
        //por que int linea=1?
        int linea = 1;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);
            //por que una condicional?
            if(i>=1){
                if(source.charAt(i - 1) == '\n'){  //solo aumentamos el número de linea después de analizar el salto con su respectiva linea
                    linea += 1; //si el caracter anterior era salto, entonces es otra linea
                }
            }
            //se retoma el case de los archivos del profe
            switch (estado){
                case 0:
                    if(Character.isLetter(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    //nuevo
                    else if(c == '>'){
                        estado = 1;
                        lexema += c;
                    }
                    else if(c == '<'){
                        estado = 4;
                        lexema += c;
                    }
                    else if(c == '='){
                        estado = 7;
                        lexema += c;
                    }
                    else if(c == '!'){
                        estado = 10;
                        lexema += c;
                    }

                    else if(c == '/'){
                        estado = 26; //comentarios
                        lexema += c;
                    }
                    else if(c == '"'){
                        estado = 24;
                        lexema += c;
                    }
                    else if(caracteres.contains(c+"")){
                        estado = 33;
                        lexema += c;
                    }

                    break;
                case 1:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 4:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 7:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;

