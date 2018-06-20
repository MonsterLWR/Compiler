package compile;

import Lex.Lexer;
import utils.Error;
import utils.Symbol;

/**
 * Created by 李炆睿 on 2018/6/19.
 */
public class Compiler {
    private Lexer lexer;
    private NameTable tables;

    public Compiler(Lexer lexer) {
        this.lexer = lexer;
        this.tables = new NameTable();
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
        //新的符号表入栈
        tables.newTable();

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

            String name = lexer.getCurToken().getId();
            checkSymbol(Symbol.Ident, "Ident should follow a procedure!");
            if (tables.getCurTable().existName(name))
                Error.error("proc name already exist.", lexer.getLineNum());
            tables.addName(name, VarEnum.proc);
            checkSymbol(Symbol.Semicolon, "missing comma!");

            block();
            checkSymbol(Symbol.Semicolon, "missing comma!");
        }

        statement();
        //弹出顶层符号表
        tables.deleteTable();
    }

    private void statement() {
        if (lexer.getCurSymbol() == Symbol.Ident) {
            //<id> :=<表达式>
            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            if (!tables.exsistName(name)) Error.error("No such identifier!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var) Error.error("Only var can be assigned.", lexer.getLineNum());
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

            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            checkSymbol(Symbol.Ident, "Ident should follow call!");
            if (!tables.exsistName(name)) Error.error("No such proc!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.proc) Error.error("Only proc can be called.", lexer.getLineNum());
        } else if (lexer.getCurSymbol() == Symbol.Readsym) {
            //read '('<id>{,<id>}')'
            lexer.nextSymbol();
            checkSymbol(Symbol.Lparen, "missing (");

            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            checkSymbol(Symbol.Ident, "Ident should be in read()");
            if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var) Error.error("Only var can be the parameter of read.", lexer.getLineNum());

            while (lexer.getCurSymbol() == Symbol.Comma) {
                lexer.nextSymbol();
                //查找名字是否合法
                name = lexer.getCurToken().getId();
                checkSymbol(Symbol.Ident, "missing ident after , !");
                if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
                if (tables.nameType(name) != VarEnum.var) Error.error("Only var can be the parameter of read.", lexer.getLineNum());
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
        if (lexer.getCurSymbol() == Symbol.Ident) {
            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var) Error.error("Only var is allowed to be operated.", lexer.getLineNum());
            lexer.nextSymbol();
        }
        else if (lexer.getCurSymbol() == Symbol.Number) lexer.nextSymbol();
        else {
            checkSymbol(Symbol.Lparen, "missing (");
            expression();
            checkSymbol(Symbol.Rparen, "missing )");
        }
    }

    private void varDeclaration() {
        String name = lexer.getCurToken().getId();
        checkSymbol(Symbol.Ident, "Ident should follow var!");
        if (tables.getCurTable().existName(name))
            Error.error("var name already exist.", lexer.getLineNum());
        tables.addName(name, VarEnum.var);
    }

    private void constDeclaration() {
        String name = lexer.getCurToken().getId();
        checkSymbol(Symbol.Ident, "Ident should follow const!");
        if (tables.getCurTable().existName(name))
            Error.error("const name already exist.", lexer.getLineNum());
        tables.addName(name, VarEnum.cosnt);
        checkSymbol(Symbol.Eql, "missing '='!");
        checkSymbol(Symbol.Number, "Number should follow =!");
    }

    private void checkSymbol(Symbol checkSym, String mes) {
        if (!(lexer.getCurSymbol() == checkSym))
            Error.error(mes, lexer.getLineNum());
        lexer.nextSymbol();
    }
}
