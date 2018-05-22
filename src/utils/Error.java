package utils;

/**
 * Created by 李炆睿 on 2018/5/22.
 */
public class Error {
    public static void error(String mes) {
        throw new MyError(mes);
    }
}

class MyError extends java.lang.Error {
    MyError(String mes) {
        super(mes);
    }
}
