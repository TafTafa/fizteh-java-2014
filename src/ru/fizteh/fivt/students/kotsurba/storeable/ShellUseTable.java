package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.storage.structured.Table;

public class ShellUseTable extends SimpleShellCommand {

    public ShellUseTable(Context newContext) {
        super("use", 2, "usage: use <table name>", newContext);
    }

    public void run() {
        if ((context.getTable() != null) && (context.getChanges() != 0)) {
            System.out.println(context.getChanges() + " unsaved changes");
            return;
        }

        Table old = context.getTable();
        context.setTable(context.getProvider().getTable(getArg(1)));
        if (context.getTable() != null) {
            System.out.println("using " + getArg(1));
        } else {
            context.setTable(old);
            System.out.println(getArg(1) + " not exists");
        }
    }


}
