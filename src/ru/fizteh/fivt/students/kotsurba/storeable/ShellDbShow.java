package ru.fizteh.fivt.students.kotsurba.storeable;

import java.util.List;

public class ShellDbShow extends SimpleShellCommand {

    public ShellDbShow(Context newContext) {
        super("show", 2, "usage: show tables", newContext);
    }

    public void run() {
        if (getArg(1).equals("tables")) {
            List<String> names = context.getProvider().getTableNames();
            for (String name : names) {
                System.out.println(name + " " + context.getProvider().getTable(name).size());
            }
        } else {
            System.out.println("Invalid command!");
        }
    }
}
