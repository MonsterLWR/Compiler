package compile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 李炆睿 on 2018/6/19.
 */
public class Table {
    private Map<String, VarEnum> nameMap;

    public Table() {
        nameMap = new HashMap<>();
    }

    public boolean existName(String name) {
        return nameMap.containsKey(name);
    }

    public void addName(String name, VarEnum kind) {
        nameMap.put(name, kind);
    }

    public VarEnum nameType(String name) {
        return nameMap.get(name);
    }

    public String toString() {
        return nameMap.toString();
    }
}


