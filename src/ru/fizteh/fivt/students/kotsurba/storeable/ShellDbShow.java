package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.File;
import java.util.List;

public class ShellDbShow extends SimpleShellCommand {
    private Context context;

    public ShellDbShow(Context newContext) {
        context = newContext;
        setName("show");
        setNumberOfArgs(2);
        setHint("usage: show tables");
    }

    public void run() {
        if (getArg(1).equals("tables")) {
            List<String> names = context.provider.getTableNames();
            for (String name : names) {
                System.out.println(name + " " + context.provider.getTable(name).size());
            }
        } else {
            System.out.println("Invalid command!");
        }
    }
}
