package ru.fizteh.fivt.students.kotsurba.storeable;


import ru.fizteh.fivt.storage.structured.TableProvider;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;

public class DataBaseFile {

    public enum NodeStatus {OLD_NODE, NEW_NODE, MODIFIED_NODE, DELETED_NODE}

    public static final int DIRECTORY_COUNT = 16;
    public static final int FILES_COUNT = 16;

    public final class Node {
        private NodeStatus status;
        private boolean old;
        private byte[] key;
        private byte[] value;
        private byte[] oldValue;

        public int getZeroByte() {
            return Math.abs(key[0]);
        }

        public Node(final byte[] newKey, final byte[] newValue) {
            status = NodeStatus.NEW_NODE;
            key = newKey;
            value = newValue;
            oldValue = null;
            old = false;
        }

        public Node(final RandomAccessFile inputFile) throws IOException {
            try {
                int keyLength = inputFile.readInt();
                int valueLength = inputFile.readInt();
                if ((keyLength <= 0) || (valueLength <= 0)) {
                    throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
                }
                try {
                    key = new byte[keyLength];
                    value = new byte[valueLength];
                } catch (OutOfMemoryError e) {
                    throw new DataBaseWrongFileFormat("Some key or value are too large in " + file.getName());
                }
                inputFile.read(key);
                inputFile.read(value);
                setOld();
            } catch (Exception e) {
                throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
            }
        }

        public void setOld() {
            status = NodeStatus.OLD_NODE;
            old = true;
            oldValue = value;
        }

        public void setKey(final byte[] newKey) {
            key = newKey;
        }

        public void setValue(final byte[] newValue) {
            status = NodeStatus.MODIFIED_NODE;
            if ((oldValue != null) && (Arrays.equals(oldValue, newValue))) {
                status = NodeStatus.OLD_NODE;
            }
            value = newValue;
        }

        public void write(final RandomAccessFile outputFile) throws IOException {
            if (status == NodeStatus.DELETED_NODE) {
                return;
            }
            outputFile.writeInt(key.length);
            outputFile.writeInt(value.length);
            outputFile.write(key);
            outputFile.write(value);
        }

        public NodeStatus getStatus() {
            return status;
        }

        public void setStatus(NodeStatus newStatus) {
            status = newStatus;
        }

        public void remove() {
            value = null;
            status = NodeStatus.DELETED_NODE;
        }

    }

    protected final String fileName;
    protected File file;
    private File dir;
    protected List<Node> data;
    private int fileNumber;
    private int direcotryNumber;
    private DataBase table;
    private TableProvider provider;

    public DataBaseFile(final String newFileName, final int newDirectoryNumber, final int newFileNumber,
                        DataBase newTable, TableProvider newProvider) throws IOException {
        table = newTable;
        provider = newProvider;
        fileName = newFileName;
        file = new File(fileName);
        data = new ArrayList<Node>();
        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;
        String path = file.getParent();
        dir = new File(path);
        load();
        check();
    }

    public boolean check() throws IOException {
        for (Node node : data) {
            if (!((node.getZeroByte() % DIRECTORY_COUNT == direcotryNumber)
                    && ((node.getZeroByte() / DIRECTORY_COUNT) % FILES_COUNT == fileNumber))) {
                throw new IOException("Wrong file format key[0] =  " + String.valueOf(node.getZeroByte())
                        + " in file " + fileName);
            }
            try {
                provider.deserialize(table, new String(node.value));
            } catch (ParseException e) {
                throw new IOException("Invalid file format! (parse exception error!)");
            }
        }
        return true;
    }

    private void load() {
        try {
            if (dir.exists() && dir.list().length == 0) {
                throw new IOException("Empty dir!");
            }
            if (!dir.exists() || !file.exists()) {
                return;
            }
            try (RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw")) {
                while (inputFile.getFilePointer() < inputFile.length() - 1) {
                    data.add(new Node(inputFile));
                }
            }
            if (data.size() == 0) {
                throw new IOException("Empty file!");
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File not found!");
        } catch (IOException e) {
            throw new DataBaseException("File load error!");
        }
    }

    public void createPath() {
        if (dir.exists()) {
            return;
        }

        if (!dir.mkdir()) {
            throw new DataBaseException("Cannot create directory!");
        }
    }

    public void deletePath() {
        if (!dir.exists()) {
            return;
        }

        if (dir.list().length != 0) {
            return;
        }

        if (!dir.delete()) {
            throw new DataBaseException("Cannot delete a directory!");
        }
    }

    public void save() {
        try {
            if (getSize() == 0) {
                if ((file.exists()) && (!file.delete())) {
                    throw new DataBaseException("Cannot delete a file!");
                }
                deletePath();
            } else {
                createPath();
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        throw new DataBaseException("Cannot create a file " + fileName);
                    }
                }
                try (RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw")) {
                    for (Node node : data) {
                        node.write(outputFile);
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File save error!");
        } catch (IOException e) {
            throw new DataBaseException("Write to file error!");
        }
    }


    private int search(final byte[] key) {
        for (int i = 0; i < data.size(); ++i) {
            if (Arrays.equals(data.get(i).key, key)) {
                return i;
            }
        }
        return -1;
    }

    public String put(final String keyStr, final String valueStr) {
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
        byte[] value = valueStr.getBytes(StandardCharsets.UTF_8);

        int index = search(key);
        if (index == -1) {
            data.add(new Node(key, value));
            return null;
        } else {
            NodeStatus status = data.get(index).status;
            String result = null;
            if (status != NodeStatus.DELETED_NODE) {
                result = new String(data.get(index).value);
            }
            data.get(index).setValue(value);
            return result;
        }
    }

    public String get(final String keyStr) {
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
        int index = search(key);
        if (index != -1) {
            if (data.get(index).status == NodeStatus.DELETED_NODE) {
                return null;
            }
            return new String(data.get(index).value);
        } else {
            return null;
        }
    }

    public String remove(final String keyStr) {
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
        int index = search(key);
        if (index == -1) {
            return null;
        } else {
            String result;
            if (data.get(index).status == NodeStatus.DELETED_NODE) {
                result = null;
            } else {
                result = new String(data.get(index).value);
            }
            data.get(index).remove();
            return result;
        }
    }

    public int getNewKeys() {
        int result = 0;
        for (Node node : data) {
            if ((node.getStatus() == NodeStatus.NEW_NODE) || (node.getStatus() == NodeStatus.MODIFIED_NODE)
                    || ((node.getStatus() == NodeStatus.DELETED_NODE) && (node.old))) {
                ++result;
            }
        }
        return result;
    }

    public int getSize() {
        int result = 0;
        for (Node node : data) {
            if (node.getStatus() != NodeStatus.DELETED_NODE) {
                ++result;
            }
        }
        return result;
    }

    public List<String> getAllKeys() {

        List result = new ArrayList();
        for (Node node : data) {
            if (node.getStatus() != NodeStatus.DELETED_NODE) {
                result.add(new String(node.key));
            }
        }
        return result;
    }

    public void commit() {
        save();
        for (int i = 0; i < data.size(); ) {
            if (data.get(i).getStatus() == NodeStatus.DELETED_NODE) {
                data.remove(i);
            } else {
                data.get(i).setOld();
                ++i;
            }
        }
    }

    public void rollback() {
        data.clear();
        load();
    }

}
