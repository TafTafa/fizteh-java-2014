package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellCreateTable extends SimpleShellCommand {
    private Context context;

    public ShellCreateTable(Context newContext) {
        context = newContext;
        setName("create");
        setNumberOfArgs(2);
        setHint("usage: create <table name>");
    }

    public void run() {
        if (context.provider.createTable(getArg(1)) != null) {
            System.out.println("created");
        } else {
            System.out.println(getArg(1) + " exists");
        }
    }
}
