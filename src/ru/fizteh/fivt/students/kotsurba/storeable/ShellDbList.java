package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.util.List;

public final class ShellDbList extends SimpleShellCommand {
    private Context context;

    public ShellDbList(final Context newContext) {
        context = newContext;
        setName("list");
        setNumberOfArgs(1);
        setHint("usage: list");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        List<String> str = context.table.list();
        StringBuilder keys = new StringBuilder();
        for (String string : str) {
            keys.append(string).append(", ");
        }
        if (keys.length() > 1) {
            keys.deleteCharAt(keys.length() - 1);
            keys.deleteCharAt(keys.length() - 1);
        }
        System.out.println(keys);
    }
}
