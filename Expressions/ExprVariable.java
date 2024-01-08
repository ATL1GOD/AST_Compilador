package Expressions;

import Utils.*;

public class ExprVariable extends Expression {
    final Token name;

    public ExprVariable(Token name) {
        this.name = name;
    }
}