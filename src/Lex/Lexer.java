package Lex;

import utils.Error;
import utils.Symbol;
import utils.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李炆睿 on 2018/4/14.
 */
public class Lexer {
    private Symbol curSymbol;
    private int lineNum;
    private char[] codes;
    private int index;
    private Map<String, Symbol> symbolMap;
    private Map<Character, Symbol> charMap;

    public void nextSymbol() {
        if (!hasNextToken()) Error.error("No more Token!", getLineNum());
        curSymbol = getNextToken().getSym();
    }

    public Symbol getCurSymbol() {
        return curSymbol;
    }

    public Lexer(char[] codes) {
//        传入代码
        this.codes = codes;
//        记录下一个要读取的字符的索引位置
        index = 0;
        //记录行号
        lineNum = 1;

//        初始化保留字表
        symbolMap = new HashMap<>();
        symbolMap.put("begin", Symbol.Beginsym);
        symbolMap.put("call", Symbol.Callsym);
        symbolMap.put("const", Symbol.Constantsym);
        symbolMap.put("do", Symbol.Dosym);
        symbolMap.put("end", Symbol.Endsym);
        symbolMap.put("if", Symbol.Ifsym);
        symbolMap.put("odd", Symbol.Oddsym);
        symbolMap.put("procedure", Symbol.Procsym);
        symbolMap.put("read", Symbol.Readsym);
        symbolMap.put("then", Symbol.Thensym);
        symbolMap.put("var", Symbol.Varsym);
        symbolMap.put("while", Symbol.Whilesym);
        symbolMap.put("write", Symbol.Writesym);

//        单字符表
        charMap = new HashMap<>();
        charMap.put('+', Symbol.Plus);
        charMap.put('-', Symbol.Minus);
        charMap.put('*', Symbol.Times);
        charMap.put('/', Symbol.Slash);
        charMap.put('(', Symbol.Lparen);
        charMap.put(')', Symbol.Rparen);
        charMap.put('=', Symbol.Eql);
        charMap.put(',', Symbol.Comma);
        charMap.put('.', Symbol.Period);
        charMap.put('#', Symbol.Neq);
        charMap.put(';', Symbol.Semicolon);

//        使curSymbol指向最新的symbol
        nextSymbol();
    }

    public int getLineNum() {
        return lineNum;
    }

    private boolean hasNextToken() {
        return index < codes.length;
    }

    private Token getNextToken() {
        if (!hasNextToken()) throw new RuntimeException("Token out of boundary!");
//      index一直指向ch后的索引，注意回退
        char ch = codes[index++];
        while (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r') {
            if (ch == '\n') lineNum++;
//            忽略空格，换行，回车，Tab
            ch = codes[index++];
        }

        if (Character.isAlphabetic(ch)) {
//            获取标识符
            StringBuilder temp = new StringBuilder();
            temp.append(ch);
            ch = codes[index++];
            while (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
                temp.append(ch);
                ch = codes[index++];
            }
//            此时ch已经是下一次getToken时，index应该指向的字符，故需要回退
            index--;

            String sTemp = temp.toString();
            Symbol symbol = symbolMap.get(sTemp);
            if (symbol != null) {
//                是保留字
                return new Token(symbol);
            } else {
                //symbol == null
//                是标识符
                return new Token(Symbol.Ident, sTemp);
            }
        } else if (Character.isDigit(ch)) {
            //            获取数字
            StringBuilder temp = new StringBuilder();
            temp.append(ch);
            ch = codes[index++];
            while (Character.isDigit(ch)) {
                temp.append(ch);
                ch = codes[index++];
            }
//            此时ch已经是下一次getToken时，index应该指向的字符，故需要回退
            index--;

            String sTemp = temp.toString();
            return new Token(Symbol.Number, Integer.parseInt(sTemp));
        } else if (ch == ':') {
            ch = codes[index++];
            if (ch == '=') {
//               赋值符号
                return new Token(Symbol.Becomes);
            } else {
//                未知符号
//                回退index
                index--;
                return new Token(Symbol.NUL);
            }
        } else if (ch == '<') {
//            检测小于或小于等于
            ch = codes[index++];
            if (ch == '=') {
//                小于等于符号
                return new Token(Symbol.Leq);
            } else {
//                小于符号
//                index回退
                index--;
                return new Token(Symbol.Lss);
            }
        } else if (ch == '>') {
//            检测大于或大于等于
            ch = codes[index++];
            if (ch == '=') {
//                大于等于符号
                return new Token(Symbol.Geq);
            } else {
//                大于符号
//                index回退
                index--;
                return new Token(Symbol.Gtr);
            }
        } else {
//            其他符号
            Symbol sym = charMap.get(ch);
            if (sym == null) {
                return new Token(Symbol.NUL);
            } else {
                return new Token(sym);
            }
        }
    }
}
