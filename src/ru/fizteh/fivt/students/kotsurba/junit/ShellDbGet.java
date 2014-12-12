package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbGet extends SimpleShellCommand {
    private Context context;

    public ShellDbGet(final Context newContext) {
        context = newContext;
        setName("get");
        setNumberOfArgs(2);
        setHint("usage: get <key>");
    }

    @Override
    public void run() {
        if (context.table == null) {
            System.out.println("no table");
            return;
        }
        String str = context.table.get(getArg(1));
        if (str == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(str);
        }
    }
}
