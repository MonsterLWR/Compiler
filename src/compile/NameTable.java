package compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李炆睿 on 2018/6/19.
 */
public class NameTable {
    private List<Table> tables;

    public int tableSize() {
        return tables.size();
    }

    public NameTable() {
        tables = new ArrayList<>();
    }

    public Table getCurTable() {
        if (tables.size() != 0)
            return tables.get(tables.size() - 1);
        return null;
    }

    public void newTable() {
        tables.add(new Table());
    }

    public void deleteTable() {
        if (tables.size() != 0)
            tables.remove(tables.size() - 1);
    }

    public void addName(String name, VarEnum kind) {
        tables.get(tables.size() - 1).addName(name, kind);
    }

    public boolean exsistName(String name) {
        if (tables.size() == 0) return false;
        int level = tables.size() - 1;
        for (; level >= 0; level--) {
            if (tables.get(level).existName(name)) return true;
        }
        return false;
    }

    public VarEnum nameType(String name) {
        if (tables.size() == 0) return null;
        int level = tables.size() - 1;
        for (; level >= 0; level--) {
            if (tables.get(level).existName(name)) return tables.get(level).nameType(name);
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Table table : tables) {
            sb.append(table.toString());
            sb.append("->");
        }
        return sb.toString();
    }
}
