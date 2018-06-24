import Lex.Lexer;
import compile.Compiler;
import compile.SourceReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by 李炆睿 on 2018/4/14.
 */
public class Init {
//    @Test
//    public void testIdent() {
//        Lexer lexer = new Lexer("1+1=2\n:cost.".toCharArray());
//        while (lexer.hasNextToken()) {
//            Token token = lexer.getNextToken();
//            System.out.println(token);
//        }
//    }

    @Test
    public void testSnytax() throws IOException {
        SourceReader reader = new SourceReader();
        char[] source = reader.read("src/source.txt").toCharArray();
        System.out.println(source);

        Lexer lexer = new Lexer(source);
        Compiler compiler = new Compiler(lexer, "src/target.c");
        compiler.analyse();
    }
}
