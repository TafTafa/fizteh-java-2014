package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class DataBaseTable implements TableProvider {
    private String tableDir;
    private Map<String, DataBase> tableInUse;

    public DataBase getTableFromMap(final String name) {
        if (!tableInUse.containsKey(name)) {
            tableInUse.put(name, new DataBase(name));
        }
        return tableInUse.get(name);
    }

    public void deleteTableFromMap(final String name) {
        if (tableInUse.containsKey(name)) {
            tableInUse.remove(name);
        }
    }

    public DataBaseTable(String newTableDir) {
        tableDir = newTableDir;
        tableInUse = new HashMap<String, DataBase>();
    }

    private void checkName(final String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table! Wrong name!");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols in name!");
        }
    }

    @Override
    public Table createTable(final String tableName) {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new MultiDataBaseException("Cannot create table " + tableName);
        }

        return getTableFromMap(fullPath);
    }

    @Override
    public void removeTable(final String tableName) {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist already!");
        }

        DataBase base = getTableFromMap(fullPath);
        base.drop();
        if (!file.delete()) {
            throw new DataBaseException("Cannot delete a file " + tableName);
        }
        deleteTableFromMap(fullPath);
    }

    @Override
    public Table getTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDir + File.separator + tableName;

        File file = new File(fullPath);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        return getTableFromMap(fullPath);
    }
}
