package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

public class ShellDbGet extends SimpleShellCommand {

    public ShellDbGet(final Context newContext) {
        super("get", 2, "usage: get <key>", newContext);
    }

    @Override
    public void run() {
        if (context.getTable() == null) {
            System.out.println("no table");
            return;
        }
        Storeable storeable = context.getTable().get(getArg(1));
        if (storeable == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(context.getProvider().serialize(context.getTable(), storeable));
        }
    }
}
