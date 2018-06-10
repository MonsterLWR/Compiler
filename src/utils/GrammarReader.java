package utils;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李炆睿 on 2018/4/24.
 */
public class GrammarReader {
    private Map<Character, List<String>> rules = null;
    /*读取文件内的文法转换规则，构造一个map，键值为非终结符，值为可以推导的tringS
    * 规定文件内大写字母为非终结符，小写字母为终结符，ε为epsilon*/
    public Map<Character, List<String>> read(String filePath) throws IOException {
        Map<Character, List<String>> rules = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        constructRules(in, rules);
        return rules;
    }

    private void constructRules(BufferedReader in, Map<Character, List<String>> rules) throws IOException {
        String line = null;
        while ((line = in.readLine()) != null) {
            String[] identifiers = line.split("->");
            if (identifiers[0].length() != 1) throw new IllegalArgumentException("Not a valid grammar!");
            //将该条规则加入map
            Character Vn = identifiers[0].charAt(0);
            List<String> deducible = rules.getOrDefault(Vn, new ArrayList<>());
            deducible.add(identifiers[1].trim());
            rules.putIfAbsent(Vn, deducible);
        }
    }

    @Test
    public void testRead() throws IOException {
        System.out.println(new GrammarReader().read("src/LL1/test.txt"));
    }
}
