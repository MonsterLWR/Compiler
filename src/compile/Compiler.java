package compile;

import Lex.Lexer;
import utils.Error;
import utils.Symbol;

import java.io.IOException;

import static utils.Symbol.*;
import static utils.Symbol.Number;

/**
 * Created by 李炆睿 on 2018/6/19.
 */
public class Compiler {
    private Lexer lexer;
    private NameTable tables;
    private CodeGennerator gennerator;

    public Compiler(Lexer lexer, String genFilePath) throws IOException {
        this.lexer = lexer;
        this.tables = new NameTable();
        this.gennerator = new CodeGennerator(genFilePath);
    }

    public void analyse() throws IOException {
        proc();
        gennerator.flush();
    }


    //<程序>
    void proc() throws IOException {
        block();
        if (lexer.getCurSymbol() != Period) Error.error("missing period!", lexer.getLineNum());
    }

    //<分程序>
    private void block() throws IOException {
        //新的符号表入栈
        tables.newTable();
        //判断作用域个数，一个说明为最外层作用域
        if (tables.tableSize() != 1) gennerator.genLparentheses();

        if (lexer.getCurSymbol() == Constantsym) {
            lexer.nextSymbol();
            //const<常量定义>{，<常量定义>};
            constDeclaration();
            while (lexer.getCurSymbol() == Comma) {
                lexer.nextSymbol();
                constDeclaration();
            }
            checkSymbol(Semicolon, "missing comma or semicolon!");
        }
        if (lexer.getCurSymbol() == Varsym) {
            lexer.nextSymbol();
            //var<变量定义>{，<变量定义>};
            varDeclaration();
            while (lexer.getCurSymbol() == Comma) {
                lexer.nextSymbol();
                varDeclaration();
            }
            checkSymbol(Semicolon, "missing comma or semicolon!");
        }

        //{procedure<id>;<分程序>;}
        while (lexer.getCurSymbol() == Procsym) {
            lexer.nextSymbol();

            String name = lexer.getCurToken().getId();
            checkSymbol(Ident, "Ident should follow a procedure!");
            if (tables.getCurTable().existName(name))
                Error.error("proc name already exist.", lexer.getLineNum());
            tables.addName(name, VarEnum.proc);
            checkSymbol(Semicolon, "missing comma!");
            gennerator.genProcDec(name);

            block();
            checkSymbol(Semicolon, "missing comma!");
        }

        statement();
        //判断作用域个数，一个说明为最外层作用域
        if (tables.tableSize() != 1) gennerator.genRparentheses();
        //弹出顶层符号表
        tables.deleteTable();
    }

    private void statement() throws IOException {
        //判断作用域个数，一个说明为最外层作用域
        if (tables.tableSize() == 1) {
            gennerator.genMain();
            gennerator.genLparentheses();
        }

        if (lexer.getCurSymbol() == Ident) {
            //<id> :=<表达式>
            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            if (!tables.exsistName(name)) Error.error("No such identifier!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var) Error.error("Only var can be assigned.", lexer.getLineNum());
            lexer.nextSymbol();
            gennerator.genIdent(name);

            checkSymbol(Becomes, "Didn't find := !");
            gennerator.genAssign();
            expression();
            gennerator.genSemi();
        } else if (lexer.getCurSymbol() == Ifsym) {
            //id <条件> then <语句>
            gennerator.genIf();
            lexer.nextSymbol();

            gennerator.genLSParen();
            condition();
            gennerator.genRSParen();

            checkSymbol(Thensym, "missing then!");
            gennerator.genLparentheses();
            statement();
            gennerator.genRparentheses();
        } else if (lexer.getCurSymbol() == Whilesym) {
            //while <条件> do <语句>
            gennerator.genWhile();
            lexer.nextSymbol();

            gennerator.genLSParen();
            condition();
            gennerator.genRSParen();

            checkSymbol(Dosym, "missing do!");
            gennerator.genLparentheses();
            statement();
            gennerator.genRparentheses();
        } else if (lexer.getCurSymbol() == Callsym) {
            //call<id>
            lexer.nextSymbol();

            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            checkSymbol(Ident, "Ident should follow call!");
            if (!tables.exsistName(name)) Error.error("No such proc!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.proc) Error.error("Only proc can be called.", lexer.getLineNum());
            gennerator.genProc(name);
            gennerator.genSemi();
        } else if (lexer.getCurSymbol() == Readsym) {
            //read '('<id>{,<id>}')'
            lexer.nextSymbol();
            checkSymbol(Lparen, "missing (");

            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            checkSymbol(Ident, "Ident should be in read()");
            if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var)
                Error.error("Only var can be the parameter of read.", lexer.getLineNum());
            gennerator.genScanf(name);

            while (lexer.getCurSymbol() == Comma) {
                lexer.nextSymbol();
                //查找名字是否合法
                name = lexer.getCurToken().getId();
                checkSymbol(Ident, "missing ident after , !");
                if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
                if (tables.nameType(name) != VarEnum.var)
                    Error.error("Only var can be the parameter of read.", lexer.getLineNum());
                gennerator.genScanf(name);
            }
            checkSymbol(Rparen, "missign )");
        } else if (lexer.getCurSymbol() == Writesym) {
            //write '('<表达式>{,<表达式>}')'
            lexer.nextSymbol();
            checkSymbol(Lparen, "missing (");

            gennerator.genPrintf();
            expression();
            gennerator.genRSParen();

            while (lexer.getCurSymbol() == Comma) {
                lexer.nextSymbol();
                gennerator.genPrintf();
                expression();
                gennerator.genRSParen();
            }
            checkSymbol(Rparen, "missign )");
        } else if (lexer.getCurSymbol() == Beginsym) {
            lexer.nextSymbol();
            statement();
            while (lexer.getCurSymbol() == Semicolon) {
                lexer.nextSymbol();
                statement();
            }
            checkSymbol(Endsym, "missing end!");
        } else {
            //epsilon
        }

        //判断作用域个数，一个说明为最外层作用域
        if (tables.tableSize() == 1) {
            gennerator.genRparentheses();
        }
    }

    private void condition() throws IOException {
        if (lexer.getCurSymbol() == Oddsym) {
            //不知道odd是干嘛的，所以没有做生成C代码的处理
            lexer.nextSymbol();
            expression();
        } else {
            expression();
            if (lexer.getCurSymbol() == Eql || lexer.getCurSymbol() == Neq || lexer.getCurSymbol() == Lss
                    || lexer.getCurSymbol() == Leq || lexer.getCurSymbol() == Gtr
                    || lexer.getCurSymbol() == Geq) {
                switch (lexer.getCurSymbol()) {
                    case Eql:
                        gennerator.genEql();
                        break;
                    case Neq:
                        gennerator.genNeq();
                        break;
                    case Lss:
                        gennerator.genLss();
                        break;
                    case Leq:
                        gennerator.genLeq();
                        break;
                    case Gtr:
                        gennerator.genGtr();
                        break;
                    case Geq:
                        gennerator.genGeq();
                        break;
                }
                lexer.nextSymbol();
                expression();
            }
        }
    }

    private void expression() throws IOException {
        if (lexer.getCurSymbol() == Plus || lexer.getCurSymbol() == Minus) {
            if (lexer.getCurSymbol() == Plus) gennerator.genPlus();
            else gennerator.genMinus();
            lexer.nextSymbol();
        }
        item();
        while (lexer.getCurSymbol() == Plus || lexer.getCurSymbol() == Minus) {
            if (lexer.getCurSymbol() == Plus) gennerator.genPlus();
            else gennerator.genMinus();
            lexer.nextSymbol();
            item();
        }
    }

    private void item() throws IOException {
        factor();
        while (lexer.getCurSymbol() == Times || lexer.getCurSymbol() == Slash) {
            if (lexer.getCurSymbol() == Times) gennerator.genTimes();
            else gennerator.genSlash();
            lexer.nextSymbol();
            factor();
        }
    }

    private void factor() throws IOException {
        if (lexer.getCurSymbol() == Ident) {
            //查找名字是否合法
            String name = lexer.getCurToken().getId();
            if (!tables.exsistName(name)) Error.error("No such var!", lexer.getLineNum());
            if (tables.nameType(name) != VarEnum.var)
                Error.error("Only var is allowed to be operated.", lexer.getLineNum());
            gennerator.genIdent(name);
            lexer.nextSymbol();
        } else if (lexer.getCurSymbol() == Number) {
            gennerator.genNum(lexer.getCurToken().getNum());
            lexer.nextSymbol();
        } else {
            checkSymbol(Lparen, "missing (");
            gennerator.genLSParen();
            expression();
            checkSymbol(Rparen, "missing )");
            gennerator.genRSParen();
        }
    }

    private void varDeclaration() throws IOException {
        String name = lexer.getCurToken().getId();
        checkSymbol(Ident, "Ident should follow var!");
        if (tables.getCurTable().existName(name))
            Error.error("var name already exist.", lexer.getLineNum());
        tables.addName(name, VarEnum.var);
        gennerator.genVarDec(name);
    }

    private void constDeclaration() throws IOException {
        String name = lexer.getCurToken().getId();
        checkSymbol(Ident, "Ident should follow const!");
        if (tables.getCurTable().existName(name))
            Error.error("const name already exist.", lexer.getLineNum());
        tables.addName(name, VarEnum.cosnt);

        checkSymbol(Eql, "missing '='!");

        int num = lexer.getCurToken().getNum();
        checkSymbol(Number, "Number should follow =!");
        gennerator.genConstDec(name, num);
    }

    private void checkSymbol(Symbol checkSym, String mes) {
        if (!(lexer.getCurSymbol() == checkSym))
            Error.error(mes, lexer.getLineNum());
        lexer.nextSymbol();
    }
}
