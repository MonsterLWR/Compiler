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
        if (lexer.getCurSymbol() == Symbol.Constantsym) {
            lexer.nextSymbol();
            //const<常量定义>{，<常量定义>};
            constDeclaration();
            while (lexer.getCurSymbol() == Symbol.Comma) {
                lexer.nextSymbol();
                constDeclaration();
            }
            checkSymbol(Symbol.Semicolon, "missing comma or semicolon!");
        }
        if (lexer.getCurSymbol() == Symbol.Varsym) {
            lexer.nextSymbol();
            //var<变量定义>{，<变量定义>};
            varDeclaration();
            while (lexer.getCurSymbol() == Symbol.Comma) {
                lexer.nextSymbol();
                varDeclaration();
            }
            checkSymbol(Symbol.Semicolon, "missing comma or semicolon!");
        }

        //{procedure<id>;<分程序>;}
        while (lexer.getCurSymbol() == Symbol.Procsym) {
            lexer.nextSymbol();
            checkSymbol(Symbol.Ident, "Ident should follow a procedure!");
            checkSymbol(Symbol.Semicolon, "missing comma!");
            block();
            checkSymbol(Symbol.Semicolon, "missing comma!");
        }

        statement();
    }

    private void statement() {
        if (lexer.getCurSymbol() == Symbol.Ident) {
            //<id> :=<表达式>
            lexer.nextSymbol();
            checkSymbol(Symbol.Becomes, "Didn't find := !");
            expression();
        } else if (lexer.getCurSymbol() == Symbol.Ifsym) {
            //id <条件> then <语句>
            lexer.nextSymbol();
            condition();
            checkSymbol(Symbol.Thensym, "missing then!");
            statement();
        } else if (lexer.getCurSymbol() == Symbol.Whilesym) {
            //while <条件> do <语句>
            lexer.nextSymbol();
            condition();
            checkSymbol(Symbol.Dosym, "missing do!");
            statement();
        } else if (lexer.getCurSymbol() == Symbol.Callsym) {
            //call<id>
            lexer.nextSymbol();
            checkSymbol(Symbol.Ident, "Ident should follow call!");
        } else if (lexer.getCurSymbol() == Symbol.Readsym) {
            //read '('<id>{,<id>}')'
            lexer.nextSymbol();
            checkSymbol(Symbol.Lparen, "missing (");
            checkSymbol(Symbol.Ident, "Ident should be in read()");
            while (lexer.getCurSymbol() == Symbol.Comma) {
                lexer.nextSymbol();
                checkSymbol(Symbol.Ident, "missing ident after , !");
            }
            checkSymbol(Symbol.Rparen, "missign )");
        } else if (lexer.getCurSymbol() == Symbol.Writesym) {
            //write '('<表达式>{,<表达式>}')'
            lexer.nextSymbol();
            checkSymbol(Symbol.Lparen, "missing (");
            expression();
            while (lexer.getCurSymbol() == Symbol.Comma) {
                lexer.nextSymbol();
                expression();
            }
            checkSymbol(Symbol.Rparen, "missign )");
        } else if (lexer.getCurSymbol() == Symbol.Beginsym) {
            lexer.nextSymbol();
            statement();
            while (lexer.getCurSymbol() == Symbol.Semicolon) {
                lexer.nextSymbol();
                statement();
            }
            checkSymbol(Symbol.Endsym, "missing end!");
        } else {
            //epsilon
        }
    }

    private void condition() {
        if (lexer.getCurSymbol() == Symbol.Oddsym) {
            lexer.nextSymbol();
            expression();
        } else {
            expression();
            if (lexer.getCurSymbol() == Symbol.Eql || lexer.getCurSymbol() == Symbol.Neq || lexer.getCurSymbol() == Symbol.Lss
                    || lexer.getCurSymbol() == Symbol.Leq || lexer.getCurSymbol() == Symbol.Gtr
                    || lexer.getCurSymbol() == Symbol.Geq) {
                lexer.nextSymbol();
                expression();
            }
        }
    }

    private void expression() {
        if (lexer.getCurSymbol() == Symbol.Plus || lexer.getCurSymbol() == Symbol.Minus) lexer.nextSymbol();
        item();
        while (lexer.getCurSymbol() == Symbol.Plus || lexer.getCurSymbol() == Symbol.Minus) {
            lexer.nextSymbol();
            item();
        }
    }

    private void item() {
        factor();
        while (lexer.getCurSymbol() == Symbol.Times || lexer.getCurSymbol() == Symbol.Slash) {
            lexer.nextSymbol();
            factor();
        }
    }

    private void factor() {
        if (lexer.getCurSymbol() == Symbol.Ident) lexer.nextSymbol();
        else if (lexer.getCurSymbol() == Symbol.Number) lexer.nextSymbol();
        else {
            checkSymbol(Symbol.Lparen, "missing (");
            expression();
            checkSymbol(Symbol.Rparen, "missing )");
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
