package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

public class Context {
    private TableProvider provider;
    private Table table;

    public Context(TableProvider newProvider) {
        provider = newProvider;
        table = null;
    }

    public TableProvider getProvider() {
        return provider;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table newTable) {
        table = newTable;
    }

    public int getChanges() {
        if (table != null) {
            return ((DataBase) table).getNewKeys();
        }
        return 0;
    }
}
