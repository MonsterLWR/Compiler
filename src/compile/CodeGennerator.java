package compile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by 李炆睿 on 2018/6/24.
 */
public class CodeGennerator {
    private Writer writer;

    public CodeGennerator(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
    }

    public void genConstDec(String varName, int num) throws IOException {
        writer.write("const int " + varName + " = " + num + ";\n");
    }

    public void genVarDec(String varName) throws IOException {
        writer.write("int " + varName + ";\n");
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void genMain() throws IOException {
        writer.write("\n\nint main(int argc, char* argv[])");
    }

    public void genLparentheses() throws IOException {
        writer.write("{\n");
    }

    public void genRparentheses() throws IOException {
        writer.write("\n}");
    }


    public void genProcDec(String procName) throws IOException {
        writer.write("\n\nvoid " + procName + "()");
    }

    public void genProc(String procName) throws IOException {
        writer.write(procName + "()");
    }

    public void genIdent(String ident) throws IOException {
        writer.write(ident);
    }

    public void genAssign() throws IOException {
        writer.write(" = ");
    }

    public void genPlus() throws IOException {
        writer.write(" + ");
    }

    public void genMinus() throws IOException {
        writer.write(" - ");
    }

    public void genSemi() throws IOException {
        writer.write(";\n");
    }

    public void genTimes() throws IOException {
        writer.write(" * ");
    }

    public void genSlash() throws IOException {
        writer.write(" / ");
    }

    public void genNum(int num) throws IOException {
        writer.write(num + "");
    }

    public void genLSParen() throws IOException {
        writer.write(" ( ");
    }

    public void genRSParen() throws IOException {
        writer.write(" ) ");
    }

    public void genIf() throws IOException {
        writer.write("if");
    }

    public void genEql() throws IOException {
        writer.write("==");
    }

    public void genNeq() throws IOException {
        writer.write("!=");
    }

    public void genLss() throws IOException {
        writer.write("<");
    }

    public void genLeq() throws IOException {
        writer.write("<=");
    }

    public void genGtr() throws IOException {
        writer.write(">");
    }

    public void genGeq() throws IOException {
        writer.write(">=");
    }

    public void genWhile() throws IOException {
        writer.write("while");
    }

    public void genScanf(String ident) throws IOException {
        writer.write("scanf(\"%d\", &" + ident + ");\n");
    }

    public void genPrintf() throws IOException {
        writer.write("printf(\"%d\", ");
    }
}
