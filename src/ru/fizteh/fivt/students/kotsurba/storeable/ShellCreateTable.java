package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandString;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;

import java.io.IOException;

public class ShellCreateTable extends SimpleShellCommand {

    public ShellCreateTable(Context newContext) {
        super("create", 3, "usage: create <table name> (<type1 type2 type3 ...>)", newContext);
    }

    public void run() {
        try {
            if (context.getProvider().createTable(getArg(1), MySignature.getTypes(getSpacedArg(2))) != null) {
                System.out.println("created");
            } else {
                System.out.println(getArg(1) + " exists");
            }
        } catch (IOException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException("wrong type (" + name + " " + hint + ")");
            }
            args = command;
            return true;
        }
        return false;
    }
}
