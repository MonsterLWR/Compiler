import Lex.Lexer;
import org.junit.jupiter.api.Test;
import utils.Token;

/**
 * Created by 李炆睿 on 2018/4/14.
 */
public class Init {
    @Test
    public void testIdent() {
        Lexer lexer = new Lexer("1+1=2\n:=.202*2.".toCharArray());
        for (int i = 0; i < 11; i++) {
            Token token = lexer.getNextToken();
            System.out.println(token);
        }
    }
}
