package ru.fizteh.fivt.students.kotsurba.storeable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public class WorkWithJSON {

    public static String serialize(Table table, Storeable value) throws ColumnFormatException {

        try {
            value.getColumnAt(table.getColumnsCount());
            throw new ColumnFormatException("Too many columns!");
        } catch (IndexOutOfBoundsException e) {
        }

        JSONArray array = new JSONArray();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            try {
                if (value.getColumnAt(i) == null || value.getColumnAt(i).getClass() == table.getColumnType(i)) {
                    array.put(value.getColumnAt(i));
                } else {
                    throw new ColumnFormatException("Column " + i + " has wrong type!");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Too few columns!");
            }
        }
        return array.toString();
    }

    public static Storeable deserialize(Table table, String value) {
        if (value == null) {
            return null;
        }
        Storeable result = new MyStoreable(table);
        JSONArray array = null;
        try {
            array = new JSONArray(value);
        } catch (JSONException e) {
            throw new IllegalArgumentException("can't deserialize");
        }
        for (Integer i = 0; i < array.length(); ++i) {
            try {
                result.setColumnAt(i, array.get(i));
            } catch (JSONException e) {
                throw new IllegalArgumentException("can't deserialize");
            }
        }
        return result;
    }
}
