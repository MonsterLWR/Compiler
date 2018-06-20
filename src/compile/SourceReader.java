package compile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 李炆睿 on 2018/6/20.
 */
public class SourceReader {
    public String read(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = in.readLine()) != null) {
            sb.append(temp);
            sb.append('\n');
        }
        return sb.toString();
    }
}
