package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandString;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;

import java.text.ParseException;

public final class ShellDbPut extends SimpleShellCommand {

    public ShellDbPut(final Context newContext) {
        super("put", 3, "usage: put <key> <value>", newContext);
    }

    @Override
    public void run() {
        if (context.getTable() == null) {
            System.out.println("no table");
            return;
        }
        try {
            Storeable storeable = ((DataBase) context.getTable()).putStoreable(getArg(1), getSpacedArg(2));
            if (storeable == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(context.getProvider().serialize(context.getTable(), storeable));
            }
        } catch (ParseException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean isMyCommand(final CommandString command) {
        if (name.equals(command.getArg(0))) {
            if (command.length() < numberOfArgs) {
                throw new InvalidCommandException(name + " " + hint);
            }
            args = command;
            return true;
        }
        return false;
    }
}
