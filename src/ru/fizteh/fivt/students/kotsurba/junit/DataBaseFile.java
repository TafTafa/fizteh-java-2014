package ru.fizteh.fivt.students.kotsurba.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;

public class DataBaseFile {

    static final byte OLD_NODE = 1;
    static final byte NEW_NODE = 2;
    static final byte MODIFIED_NODE = 3;
    static final byte DELETED_NODE = 4;


    public final class Node {
        private byte status;
        private boolean old;
        private byte[] key;
        private byte[] value;
        private byte[] oldValue;

        public int getZeroByte() {
            return Math.abs(key[0]);
        }

        public Node(final byte[] newKey, final byte[] newValue) {
            status = NEW_NODE;
            key = newKey;
            value = newValue;
            oldValue = null;
            old = false;
        }

        public Node(final RandomAccessFile inputFile) throws IOException {
            status = OLD_NODE;
            old = true;
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
                oldValue = value;
            } catch (Exception e) {
                throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
            }
        }

        public void setKey(final byte[] newKey) {
            key = newKey;
        }

        public void setValue(final byte[] newValue) {
            status = MODIFIED_NODE;
            if ((oldValue != null) && (Arrays.equals(oldValue, newValue))) {
                status = OLD_NODE;
            }
            value = newValue;
        }

        public void write(final RandomAccessFile outputFile) throws IOException {
            if (status == DELETED_NODE) {
                return;
            }
            outputFile.writeInt(key.length);
            outputFile.writeInt(value.length);
            outputFile.write(key);
            outputFile.write(value);
        }

        public byte getStatus() {
            return status;
        }

        public void setStatus(byte newStatus) {
            status = newStatus;
        }

        public void remove() {
            value = null;
            status = DELETED_NODE;
        }

    }

    protected final String fileName;
    protected File file;
    private File dir;
    protected List<Node> data;
    private int fileNumber;
    private int direcotryNumber;

    public DataBaseFile(final String newFileName, final int newDirectoryNumber, final int newFileNumber) {
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

    public boolean check() {
        for (Node node : data) {
            if (!((node.getZeroByte() % 16 == direcotryNumber) && ((node.getZeroByte() / 16) % 16 == fileNumber))) {
                throw new DataBaseWrongFileFormat("Wrong file format key[0] =  " + String.valueOf(node.getZeroByte())
                        + " in file " + fileName);
            }
        }
        return true;
    }

    private void load() {
        try {
            if (!dir.exists() || !file.exists()) {
                return;
            }
            RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw");
            while (inputFile.getFilePointer() < inputFile.length() - 1) {
                data.add(new Node(inputFile));
            }
            inputFile.close();
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
                RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
                try {
                    for (Node node : data) {
                        node.write(outputFile);
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                } finally {
                    outputFile.close();
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
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");

            int index = search(key);
            if (index == -1) {
                data.add(new Node(key, value));
                return null;
            } else {
                int status = data.get(index).status;
                String result = null;
                if (status != DELETED_NODE) {
                    result = new String(data.get(index).value);
                }
                data.get(index).setValue(value);
                return result;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String get(final String keyStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            int index = search(key);
            if (index != -1) {
                if (data.get(index).status == DELETED_NODE) {
                    return null;
                }
                return new String(data.get(index).value);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String remove(final String keyStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            int index = search(key);
            if (index == -1) {
                return null;
            } else {
                String result;
                if (data.get(index).status == DELETED_NODE) {
                    result = null;
                } else {
                    result = new String(data.get(index).value);
                }
                data.get(index).remove();
                return result;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public int getNewKeys() {
        int result = 0;
        for (Node node : data) {
            if ((node.getStatus() == NEW_NODE) || (node.getStatus() == MODIFIED_NODE)
                    || ((node.getStatus() == DELETED_NODE) && (node.old))) {
                ++result;
            }
        }
        return result;
    }

    public int getSize() {
        int result = 0;
        for (Node node : data) {
            if (node.getStatus() != DELETED_NODE) {
                ++result;
            }
        }
        return result;
    }

    public List<String> getAllKeys() {
        List<String> result = new ArrayList<String>();
        for (Node node : data) {
            if (node.getStatus() != DELETED_NODE) {
                result.add(new String(node.key));
            }
        }
        return result;
    }

    public void commit() {
        save();
        data.clear();
        load();
    }

    public void rollback() {
        data.clear();
        load();
    }
}
