package ru.fizteh.fivt.students.kotsurba.filemap.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
<<<<<<< HEAD
import java.nio.file.Files;
=======
>>>>>>> origin/master
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseFile {

    protected final String fileName;
    protected File file;
<<<<<<< HEAD
    protected Map<String, String> data;
=======
    protected Map<String,String> data;
>>>>>>> origin/master

    public DataBaseFile(final String newFileName) {
        fileName = newFileName;
        file = new File(fileName);
        data = new HashMap<String, String>();
        open();
        load();
    }

    private void open() {
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new DataBaseException("Cannot create " + fileName);
                }
            }
        } catch (IOException e) {
            throw new DataBaseException("Open file error! " + e.getMessage());
        }
    }

    private String getStringLoad(RandomAccessFile f) throws IOException {
        List<Byte> key = new ArrayList<Byte>();
        byte c = 1;
        while (c != 0) {
            c = f.readByte();
            if (c != 0) {
                key.add(c);
            }
        }

        StringBuilder s = new StringBuilder();
<<<<<<< HEAD
        for (Byte t : key) {
=======
        for(Byte t : key) {
>>>>>>> origin/master
            byte x = t;
            s.append((char) x);
        }

        return s.toString();
    }

    private int getDeltaLoad(RandomAccessFile f) throws IOException {
        return f.readInt();
    }

    private String getValueLoad(RandomAccessFile f, int start, int finish) throws IOException {
        byte[] s = new byte[finish - start];
        f.read(s);
        return new String(s);
    }

    private void load() {
        try {
            RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw");
            if (inputFile.length() == 0) {
<<<<<<< HEAD
                inputFile.close();
=======
>>>>>>> origin/master
                return;
            }
            List<String> keys = new ArrayList<String>();
            List<Integer> delta = new ArrayList<Integer>();

            while (true) {
                keys.add(getStringLoad(inputFile));
                delta.add(getDeltaLoad(inputFile));
                if (inputFile.getFilePointer() >= delta.get(0)) {
                    break;
                }
            }

            data.clear();

<<<<<<< HEAD
            for (int i = 0; i < keys.size(); ++i) {
=======
            for(int i = 0; i < keys.size(); ++i) {
>>>>>>> origin/master
                int l = (int) inputFile.length();
                if (i != keys.size() - 1) {
                    l = delta.get(i + 1);
                }
<<<<<<< HEAD
                data.put(keys.get(i), getValueLoad(inputFile, delta.get(i), l));
=======
                data.put(keys.get(i),getValueLoad(inputFile, delta.get(i), l));
>>>>>>> origin/master
            }

            inputFile.close();
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File not found!");
        } catch (IOException e) {
            throw new DataBaseException("File load error!");
        }
    }

    public void save() {
        try {
            if (data.size() == 0) {
<<<<<<< HEAD
                try {
                    if (!Files.deleteIfExists(file.toPath())) {
                        throw new DataBaseException("Cannot delete a file!");
                    }
                } catch (IOException e) {
                    throw new DataBaseException(e.getMessage());
=======
                if (!file.delete()) {
                    throw new DataBaseException("Cannot delete a file!");
>>>>>>> origin/master
                }
            } else {
                RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
                try {
                    int t = 0;
<<<<<<< HEAD
                    for (String x : data.keySet()) {
                        t += x.length() + 5;
                    }

                    for (String x : data.keySet()) {
=======
                    for(String x : data.keySet()) {
                        t += x.length() + 5;
                    }

                    for(String x : data.keySet()) {
>>>>>>> origin/master
                        outputFile.write(x.getBytes());
                        outputFile.writeByte(0);
                        outputFile.writeInt(t);
                        t += data.get(x).length();
                    }

<<<<<<< HEAD
                    for (String x : data.values()) {
=======
                    for(String x : data.values()) {
>>>>>>> origin/master
                        outputFile.write(x.getBytes());
                    }

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

    public String put(final String keyStr, final String valueStr) {
<<<<<<< HEAD
        return data.put(keyStr, valueStr);
=======
        return data.put(keyStr,valueStr);
>>>>>>> origin/master
    }

    public String get(final String keyStr) {
        return data.get(keyStr);
    }

    public boolean remove(final String keyStr) {
        return (data.remove(keyStr) != null);
    }

    public String getKeyList() {
        StringBuilder s = new StringBuilder();

<<<<<<< HEAD
        for (String v : data.keySet()) {
=======
        for(String v : data.keySet()) {
>>>>>>> origin/master
            s.append(v).append(" ");
        }
        if (s.length() != 0) s.deleteCharAt(s.length() - 1);
        return s.toString();
    }
}