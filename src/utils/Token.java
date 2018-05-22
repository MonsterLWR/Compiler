package utils;

/**
 * Created by 李炆睿 on 2018/4/14.
 */
public class Token {
    private Symbol sym;
    private int num;
    private String id;

    public Token(Symbol sym, String id) {
        if (sym != Symbol.Ident) {
            throw new IllegalArgumentException("symbol type illegal!");
        }
        this.sym = sym;
        this.id = id;
    }

    public Token(Symbol sym, int num) {
        if (sym != Symbol.Number) {
            throw new IllegalArgumentException("symbol type illegal!");
        }
        this.sym = sym;
        this.num = num;
    }

    public Token(Symbol sym) {
        if (sym == Symbol.Ident || sym == Symbol.Number) {
            throw new IllegalArgumentException("symbol type illegal!");
        }
        this.sym = sym;
    }

    public Symbol getSym() {
        return sym;
    }

    public int getNum() {
        return num;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        String res = "Token{" +
                "sym=" + sym;
        if (sym == Symbol.Number) res = res + ", num=" + num;
        if (sym == Symbol.Ident) res = res + ", id='" + id + '\'';
        res += '}';

        return res;
    }
}
