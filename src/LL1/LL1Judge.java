package LL1;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by 李炆睿 on 2018/4/24.
 */
public class LL1Judge {
    private Map<Character, DeduceEpsilon> deduceEpsilons;
    private List<Character> Vns;
    private Map<Character, List<String>> rules;
    private Map<Character, Set<Character>> firstSet;

    public void judge(String filePath) throws IOException {
        GrammarReader reader = new GrammarReader();
        rules = reader.read(filePath);
        System.out.println("rules: " + rules);
        constructDeduceEpsilon();
        computeFirst();
    }

    private void constructDeduceEpsilon() {
        //该函数对应书中第一步
        Map<Character, java.util.List<String>> tempRules = new HashMap<>();
        Vns = new ArrayList<>(rules.keySet());

        //书中第一步-(1)
        deduceEpsilons = new HashMap<>();
        for (Character Vn : Vns) deduceEpsilons.put(Vn, DeduceEpsilon.UNKNOWN);

        //书中第一步-(2)
        //删除右部含有终结符的产生式
        Pattern Vt = Pattern.compile(".*[a-z].*");
        for (Character Vn : Vns) {
            //对每一个非终结符
            for (String rPart : rules.get(Vn)) {
                //对应的每一条规则右部
                if (!Vt.matcher(rPart).matches()) {
                    //如果不包含终结符,则保存到tempRules中
                    List<String> list = tempRules.getOrDefault(Vn, new ArrayList<>());
                    list.add(rPart);
                    tempRules.putIfAbsent(Vn, list);
                }
            }
        }

//        System.out.println(tempRules);

        //删除右部含有终结符的产生式后，
        //对每一个非终结符
        for (Character Vn : Vns) {
            List<String> list = tempRules.get(Vn);
            //如果该非终结符已经没有产生式，则该非终结符无法推导出epsilon
            if (list == null) deduceEpsilons.put(Vn, DeduceEpsilon.NO);
                //如果该非终结符拥有只推导出ε的产生式。则该非终结符可以推导出epsilon
            else {
                for (String rule : list) {
                    if (rule.equals("ε")) {
                        deduceEpsilons.put(Vn, DeduceEpsilon.YES);
                        tempRules.put(Vn, null);
                        break;
                    }
                }
            }
        }

//        System.out.println(tempRules);
//        System.out.println(deduceEpsilons);

        //书中第一步-(3)(4)
        boolean changeFlag = true;
        while (changeFlag) {
            //假设本次循环不改变changeFlag，即没有Vn的deduceEpsilons值发生改变
            changeFlag = false;
            //对每一个非终结符
            for (Character Vn : Vns) {
                //得到右部的产生式的list
                List<String> ruleList = tempRules.get(Vn);
                List<String> newRuleList = new ArrayList<>();
                if (ruleList != null) {
                    //对每一个产生式
                    for (int i = 0; i < ruleList.size(); i++) {
                        String rule = ruleList.get(i);
                        StringBuilder tempRule = new StringBuilder();
                        //表示能否确定该产生式不能推出epsilon
                        boolean determineEpsilonFlag = false;
                        //所包含的每一个非终结符
                        for (int j = 0; j < rule.length(); j++) {
                            char curVn = rule.charAt(j);
                            if (deduceEpsilons.get(curVn) == DeduceEpsilon.YES) {
                                //该非终结符能推出epsilon，则不将其添加入tempRule,表示去除该非终结符
                            } else if (deduceEpsilons.get(curVn) == DeduceEpsilon.NO) {
                                //该非终结符不能推出epsilon,删去该产生式
                                determineEpsilonFlag = true;
                                break;
                            } else {
                                //该非终结符能不能推出epsilon未知
                                tempRule.append(curVn);
                            }
                        }
                        //该产生式不能推出epsilon，则不将其添加入newRuleList，表示将其删除
                        if (determineEpsilonFlag) ;
                            //若产生式右部为空，说明外层循环对应的非终结符能够推导出epsilon
                        else if (tempRule.length() == 0) {
                            deduceEpsilons.put(Vn, DeduceEpsilon.YES);
                            changeFlag = true;
                            //删除外层循环对应的非终结符为左部的所有产生式
                            tempRules.put(Vn, null);
                        } else
                            //添加修改后的右部产生式
                            newRuleList.add(tempRule.toString());
                    }
                    //若该非终结符对应的产生式都被删除，并且该非终结符仍未知是否能够推出epsilon，则该终结符无法推出epsilon
                    if (newRuleList.size() == 0 && deduceEpsilons.get(Vn) == DeduceEpsilon.UNKNOWN) {
                        deduceEpsilons.put(Vn, DeduceEpsilon.NO);
                        changeFlag = true;
                    }
                    //以Vn为左部的产生式若为被全部删除，则更新其为新的产生式
                    if (tempRules.get(Vn) != null)
                        tempRules.put(Vn, newRuleList);
                }
            }
        }

//        System.out.println(tempRules);
        System.out.println("deduceEpsilons: " + deduceEpsilons);
    }

    private void computeFirst() {
        //第二步
        firstSet = new HashMap<>();
        boolean changeFlag = true;
        while (changeFlag) {
            changeFlag = false;
            for (Character Vn : Vns) {
                for (String rule : rules.get(Vn)) {
                    //遍历所有产生式
                    if (rule.equals("ε")) {
                        //2-3
                        Set<Character> set = firstSet.getOrDefault(Vn, new HashSet<>());
                        if (set.add('ε')) changeFlag = true;
                        firstSet.putIfAbsent(Vn, set);
                    } else {
                        char begc = rule.charAt(0);
                        if (!Character.isUpperCase(begc) && begc != 'ε') {
                            //2-2
                            //如果该产生式以终结符开始
                            Set<Character> set = firstSet.getOrDefault(Vn, new HashSet<>());
                            if (set.add(begc)) changeFlag = true;
                            firstSet.putIfAbsent(Vn, set);
                        } else if (Character.isUpperCase(begc)) {
                            //2-4
                            //以非终结符开始
                            for (int i = 0; i < rule.length(); i++) {
                                char v = rule.charAt(i);
                                if (!Character.isUpperCase(v)) {
                                    //遇到了终结符
                                    Set<Character> set = firstSet.getOrDefault(Vn, new HashSet<>());
                                    if (set.add(v)) changeFlag = true;
                                    firstSet.putIfAbsent(Vn, set);
                                    break;
                                } else {
                                    //Character.isUpperCase(v)
                                    if (deduceEpsilons.get(v) == DeduceEpsilon.NO) {
                                        //该非终结符无法推导出epsilon
                                        Set<Character> set = firstSet.getOrDefault(Vn, new HashSet<>());
                                        if (set.addAll(firstSet.getOrDefault(v, new HashSet<>()))) changeFlag = true;
                                        firstSet.putIfAbsent(Vn, set);
                                        break;
                                    } else {
                                        //deduceEpsilons.get(v) == DeduceEpsilon.YES
                                        Set<Character> set = firstSet.getOrDefault(Vn, new HashSet<>());
                                        Set<Character> added = new HashSet<>(firstSet.get(v));
                                        added.remove('ε');
                                        if (set.addAll(added)) changeFlag = true;
                                        if (i == rule.length() - 1) {
                                            if (set.add('ε')) changeFlag = true;
                                        }
                                        firstSet.putIfAbsent(Vn, set);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(firstSet);
    }

    private Set<Character> getFirstFromString(String s) {
        //计算一个符号串的first集合
        Set<Character> res = new HashSet<>();
        return res;
    }

    @Test
    public void test1() throws IOException {
        new LL1Judge().judge("src/LL1/test.txt");
    }
}
