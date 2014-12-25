package ru.fizteh.fivt.students.kotsurba.storeable;

import java.util.List;

public final class ShellDbList extends SimpleShellCommand {

    public ShellDbList(final Context newContext) {
        super("list", 1, "usage: list", newContext);
    }

    @Override
    public void run() {
        if (context.getTable() == null) {
            System.out.println("no table");
            return;
        }
        List<String> str = context.getTable().list();
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
