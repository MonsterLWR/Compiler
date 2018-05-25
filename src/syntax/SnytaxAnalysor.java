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
        if (lexer.getCurSymbol() != Symbol.Period) Error.error("missing period!", lexer.getLineNum());
    }

    //<分程序>
    private void block() {
        boolean flag = true;
        while (flag) {
            flag = false;
            if (lexer.getCurSymbol() == Symbol.Constantsym) {
                lexer.nextSymbol();
                //const<常量定义>{，<常量定义>};
                constDeclaration();
                while (lexer.getCurSymbol() == Symbol.Comma) {
                    lexer.nextSymbol();
                    constDeclaration();
                }
                checkSymbol(Symbol.Semicolon,"missing comma or semicolon!");
//                if (lexer.getCurSymbol() != Symbol.Semicolon)
//                    Error.error("missing comma or semicolon!", lexer.getLineNum());
//                lexer.nextSymbol();

                flag = true;
            } else if (lexer.getCurSymbol() == Symbol.Varsym) {
                lexer.nextSymbol();
                //var<变量定义>{，<变量定义>};
                varDeclaration();
                while (lexer.getCurSymbol() == Symbol.Comma) {
                    lexer.nextSymbol();
                    varDeclaration();
                }
                checkSymbol(Symbol.Semicolon,"missing comma or semicolon!");
//                if (lexer.getCurSymbol() != Symbol.Semicolon)
//                    Error.error("missing comma or semicolon!", lexer.getLineNum());
//                lexer.nextSymbol();

                flag = true;
            } else {
                //{procedure<id>;<分程序>;}
                while (lexer.getCurSymbol() == Symbol.Procsym) {
                    lexer.nextSymbol();
                    checkSymbol(Symbol.Ident,"Ident should follow a procedure!");
//                    if (!(lexer.getCurSymbol() == Symbol.Ident))
//                        Error.error("Ident should follow a procedure!", lexer.getLineNum());
//                    lexer.nextSymbol();
                    checkSymbol(Symbol.Semicolon,"missing comma!");
//                    if (!(lexer.getCurSymbol() == Symbol.Semicolon))
//                        Error.error("missing comma!", lexer.getLineNum());
//                    lexer.nextSymbol();
                    block();
                    checkSymbol(Symbol.Semicolon,"missing comma!");
//                    if (!(lexer.getCurSymbol() == Symbol.Semicolon))
//                        Error.error("missing comma!", lexer.getLineNum());
//                    lexer.nextSymbol();

                    flag = true;
                }
            }
        }

    }

    private void varDeclaration() {
        checkSymbol(Symbol.Ident, "Ident should follow var!");
    }

    private void constDeclaration() {
        checkSymbol(Symbol.Ident, "Ident should follow const!");
        checkSymbol(Symbol.Eql, "missing '='!");
        checkSymbol(Symbol.Number, "Number should follow =!");
    }

    private void checkSymbol(Symbol checkSym, String mes) {
        if (!(lexer.getCurSymbol() == checkSym))
            Error.error(mes, lexer.getLineNum());
        lexer.nextSymbol();
    }
}
