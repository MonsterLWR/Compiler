import Lex.Lexer;
import org.junit.jupiter.api.Test;
import syntax.SnytaxAnalysor;
import utils.Token;

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
    public void testSnytax() {
        Lexer lexer = new Lexer(("while 2>3 \ndo " +
                "begin if a>2 then a:=3 end.").toCharArray());
        SnytaxAnalysor snytaxAnalysor = new SnytaxAnalysor(lexer);
        snytaxAnalysor.analyse();
    }
}
