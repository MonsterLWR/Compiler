package syntax;

import Lex.Lexer;
import utils.Error;
import utils.Symbol;

/**
 * Created by 李炆睿 on 2018/5/22.
 */
public class SnytaxAnalysor {
    private Lexer lexer;

    public SnytaxAnalysor(Lexer lexer) {
        this.lexer = lexer;
    }

    public void analyse() {
        proc();
    }

    //<程序>
    void proc() {
        block();
        if (lexer.getNextToken().getSym() != Symbol.Period) Error.error("missing period");
    }

    //<分程序>
    private void block() {
    }
}
