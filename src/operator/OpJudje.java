package operator;

import org.junit.jupiter.api.Test;
import utils.GrammarReader;

import java.io.IOException;
import java.util.*;

/**
 * Created by 李炆睿 on 2018/6/4.
 */
public class OpJudje {
    private List<Character> Vns;
    private List<Character> Vts;
    private Stack<Pair> stack;
    private Map<Character, List<String>> rules;
    private Map<Character, List<Character>> firstVT;
    private Map<Character, List<Character>> lastVT;

    public void judge(String filePath) throws IOException {
        GrammarReader reader = new GrammarReader();
        rules = reader.read(filePath);
        Vns = new ArrayList<>(rules.keySet());
        constructVts();
        System.out.println("rules: " + rules);

        System.out.println("Vns: " + Vns);
        System.out.println("Vts: " + Vts);

        boolean[][] bools1 = new boolean[Vns.size()][Vts.size()];
        constructFirstVtBools(bools1);
        firstVT = new HashMap<>();
        constructVT(firstVT, bools1);
        System.out.println(firstVT);

        boolean[][] bools2 = new boolean[Vns.size()][Vts.size()];
        constructLastVTBools(bools2);
        lastVT = new HashMap<>();
        constructVT(lastVT, bools2);
        System.out.println(lastVT);

        //0 代表不存在优先关系，1 代表小于，2 代表等于，3 代表大于
        int[][] relations = new int[Vts.size()][Vts.size()];
        constructOPtable(relations);
        showOPtable(relations);
    }

    private void showOPtable(int[][] relations) {
        System.out.print("|   ");
        for (int i = 0; i < Vts.size(); i++) {
            System.out.print("| " + Vts.get(i) + " ");
        }
        System.out.println("|");
        for (int i = 0; i < Vts.size(); i++) {
            System.out.print("| " + Vts.get(i) + " ");
            for (int j = 0; j < Vts.size(); j++) {
                switch (relations[i][j]) {
                    case 0:
                        System.out.print("|   ");
                        break;
                    case 1:
                        System.out.print("| < ");
                        break;
                    case 2:
                        System.out.print("| = ");
                        break;
                    case 3:
                        System.out.print("| > ");
                        break;
                }
            }
            System.out.println("|");
        }
    }

    private void constructOPtable(int[][] relations) {
        for (Character Vn : Vns) {
            for (String rule : rules.get(Vn)) {
                List<Integer> vts_index = new ArrayList<>();
                //对每一条rule
                for (int i = 0; i < rule.length() - 1; i++) {
                    Character c1 = rule.charAt(i);
                    Character c2 = rule.charAt(i + 1);
                    if (Character.isUpperCase(c1) && !Character.isUpperCase(c2)) {
                        //A->...Bb...
                        int c2Index = Vts.indexOf(c2);
                        List<Character> list = lastVT.get(c1);
                        for (Character c : list) {
                            //lastVT(B) > b
                            int index = Vts.indexOf(c);
                            relations[index][c2Index] = 3;
                        }
                    } else if (!Character.isUpperCase(c1) && Character.isUpperCase(c2)) {
                        //A->...aB...
                        int c1Index = Vts.indexOf(c1);
                        List<Character> list = firstVT.get(c2);
                        for (Character c : list) {
                            //a < firstVT(B)
                            int index = Vts.indexOf(c);
                            relations[c1Index][index] = 1;
                        }
                    }
                    //记录同一条rule中出现的非终结符的index
                    if (i == 0 && !Character.isUpperCase(c1)) {
                        vts_index.add(Vts.indexOf(c1));
                    }
                    if (!Character.isUpperCase(c2)) {
                        vts_index.add(Vts.indexOf(c2));
                    }
                }
                for (int i = 0; i < vts_index.size(); i++) {
                    for (int j = i + 1; j < vts_index.size(); j++) {
                        relations[vts_index.get(i)][vts_index.get(j)] = 2;
                    }
                }
            }
        }
    }

    private void constructVT(Map<Character, List<Character>> vt, boolean[][] bools) {
        for (int i = 0; i < Vns.size(); i++) {
            for (int j = 0; j < Vts.size(); j++) {
                if (bools[i][j]) {
                    List<Character> list = vt.getOrDefault(Vns.get(i), new ArrayList<>());
                    list.add(Vts.get(j));
                    vt.putIfAbsent(Vns.get(i), list);
                }
            }
        }
    }

    private void constructFirstVtBools(boolean[][] bools) {
        stack = new Stack<>();
        for (Character Vn : Vns) {
            int index = Vns.indexOf(Vn);
            for (String rule : rules.get(Vn)) {
                //对每一条rule
                Character c = rule.charAt(0);
                Character c2 = null;
                if (rule.length() > 1) c2 = rule.charAt(1);
                if (!Character.isUpperCase(c) && c != 'ε') {
                    //A->a...的形式
                    insert(index, Vts.indexOf(c), bools);
                } else if (c2 != null && Character.isUpperCase(c) && !Character.isUpperCase(c2)) {
                    //A->Ba...的形式
                    insert(index, Vts.indexOf(c2), bools);
                }
            }
        }
        while (!stack.isEmpty()) {
            Pair pair = stack.pop();
            for (Character Vn : Vns) {
                int index = Vns.indexOf(Vn);
                for (String rule : rules.get(Vn)) {
                    //对每一条rule
                    Character c = rule.charAt(0);
                    if (Character.isUpperCase(c) && c == pair.getVn()) {
                        //A->B... 且a属于firstVT(B)
                        //push(A,a)
                        insert(index, Vts.indexOf(pair.getVt()), bools);
                    }
                }
            }
        }
    }

    private void constructLastVTBools(boolean[][] bools) {
        stack = new Stack<>();
        for (Character Vn : Vns) {
            int index = Vns.indexOf(Vn);
            for (String rule : rules.get(Vn)) {
                //对每一条rule
                Character c = rule.charAt(rule.length() - 1);
                Character c2 = null;
                if (rule.length() > 1) c2 = rule.charAt(rule.length() - 2);
                if (!Character.isUpperCase(c) && c != 'ε') {
                    //A->...a的形式
                    insert(index, Vts.indexOf(c), bools);
                } else if (c2 != null && Character.isUpperCase(c) && !Character.isUpperCase(c2)) {
                    //A->...aB的形式
                    insert(index, Vts.indexOf(c2), bools);
                }
            }
        }
        while (!stack.isEmpty()) {
            Pair pair = stack.pop();
            for (Character Vn : Vns) {
                int index = Vns.indexOf(Vn);
                for (String rule : rules.get(Vn)) {
                    //对每一条rule
                    Character c = rule.charAt(rule.length() - 1);
                    if (Character.isUpperCase(c) && c == pair.getVn()) {
                        //A->...B 且a属于lastVT(B)
                        //push(A,a)
                        insert(index, Vts.indexOf(pair.getVt()), bools);
                    }
                }
            }
        }
    }

    private void insert(int i, int j, boolean[][] bools) {
        if (!bools[i][j]) {
            bools[i][j] = true;
            stack.push(new Pair(Vns.get(i), Vts.get(j)));
        }
    }

    private void constructVts() {
        Set<Character> set = new HashSet<>();

        for (List<String> sList : rules.values()) {
            for (String s : sList) {
                for (Character c : s.toCharArray()) {
                    if (!Character.isUpperCase(c)) set.add(c);
                }
            }
        }
        Vts = new ArrayList<>();
        Vts.addAll(set);
    }

//    private computeFirstVt() {
//
//    }

    @Test
    public void test() throws IOException {
        OpJudje opJudje = new OpJudje();
        opJudje.judge("src/operator/test.txt");
    }
}

class Pair {
    private Character Vn;
    private Character Vt;

    public Character getVn() {
        return Vn;
    }

    public Character getVt() {
        return Vt;
    }

    public Pair(Character Vn, Character Vt) {
        this.Vn = Vn;
        this.Vt = Vt;
    }
}
