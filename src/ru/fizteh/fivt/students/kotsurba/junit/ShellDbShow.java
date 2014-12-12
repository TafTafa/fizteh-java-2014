package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

import java.io.File;

public class ShellDbShow extends SimpleShellCommand {

    public ShellDbShow(Context newContext) {
        setName("show");
        setNumberOfArgs(2);
        setHint("usage: show tables");
    }

    public void run() {
        if (getArg(1).equals("tables")) {
            DataBase mdb;
            String tableDir = System.getProperty("fizteh.db.dir");

            for (String str : new File(tableDir).list()) {
                mdb = new DataBase(tableDir + File.separator + str);
                System.out.println(str + " " + mdb.size());
            }
        } else {
            System.out.println("Invalid command!");
        }
    }
}
