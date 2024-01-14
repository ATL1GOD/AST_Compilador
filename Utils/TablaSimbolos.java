package Utils;
import java.util.*;

public class TablaSimbolos {
    private final Map<String, Object> values;
    private final TablaSimbolos superior;
    public TablaSimbolos(){
        this.superior = null;
        this.values = new HashMap<>();
    }
    public TablaSimbolos(TablaSimbolos superior){
        this.superior = superior;
        this.values = new HashMap<>();
    }

    private final Map<String, Object> values = new HashMap<>();

    public boolean existeIdentificador(String identificador){
        return values.containsKey(identificador);
    }

    public Object obtener(String identificador) {
        if (values.containsKey(identificador)) {
            return values.get(identificador);
        }
        throw new RuntimeException("Variable no definida '" + identificador + "'.");
    }

    public void asignar(String identificador, Object valor){
        values.put(identificador, valor);
    }


}
