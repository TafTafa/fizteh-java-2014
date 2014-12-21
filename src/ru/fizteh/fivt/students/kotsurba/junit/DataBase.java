package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class DataBase implements Table {

    private String name;
    private String dataBaseDirectory;
    private DataBaseFile[] files;

    public static final int DIRECTORY_COUNT = 16;
    public static final int FILES_COUNT = 16;

    public final class DirFile {
        private int nDir;
        private int nFile;

        public DirFile(int key) {
            key = Math.abs(key);
            nDir = key % DIRECTORY_COUNT;
            nFile = (key / DIRECTORY_COUNT) % FILES_COUNT;
        }

        public DirFile(final int newDir, final int newFile) {
            nDir = newDir;
            nFile = newFile;
        }

        private String getNDirectory() {
            return Integer.toString(nDir) + ".dir";
        }

        private String getNFile() {
            return Integer.toString(nFile) + ".dat";
        }

        public int getId() {
            return nDir * DIRECTORY_COUNT + nFile;
        }
    }

    public DataBase(final String dbDirectory) {
        name = new File(dbDirectory).getName();
        dataBaseDirectory = dbDirectory;
        isCorrect();
        files = new DataBaseFile[DIRECTORY_COUNT * FILES_COUNT];
        loadFiles();
    }

    private void checkNames(final String[] dirs, final String secondName) {
        for (String dir : dirs) {
            String[] name = dir.split("\\.");
            if (name.length != 2 || !name[1].equals(secondName)) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file in path " + dir);
            }

            int firstName;
            try {
                firstName = Integer.parseInt(name[0]);
            } catch (NumberFormatException e) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file first name " + dir);
            }

            if ((firstName < 0) || firstName > FILES_COUNT - 1) {
                throw new MultiDataBaseException(dataBaseDirectory + " wrong file first name " + dir);
            }
        }
    }

    private void isCorrectDirectory(final String dirName) {
        File file = new File(dirName);
        if (file.isFile()) {
            throw new MultiDataBaseException(dirName + " isn't a directory!");
        }
        String[] dirs = file.list();
        checkNames(dirs, "dat");
        for (String dir : dirs) {
            if (new File(dirName, dir).isDirectory()) {
                throw new MultiDataBaseException(dirName + File.separator + dir + " isn't a file!");
            }
        }
    }

    private void isCorrect() {
        File file = new File(dataBaseDirectory);
        if (file.isFile()) {
            throw new MultiDataBaseException(dataBaseDirectory + " isn't directory!");
        }

        String[] dirs = file.list();
        checkNames(dirs, "dir");
        for (String dir : dirs) {
            isCorrectDirectory(dataBaseDirectory + File.separator + dir);
        }
    }

    private void tryDeleteDirectory(final String name) {
        File file = new File(dataBaseDirectory + File.separator + name);
        if (file.exists()) {
            if (file.list().length == 0) {
                if (!file.delete()) {
                    throw new DataBaseException("Cannot delete a directory!");
                }
            }
        }
    }

    private String getFullName(final DirFile node) {
        return dataBaseDirectory + File.separator + node.getNDirectory() + File.separator + node.getNFile();
    }

    public void loadFiles() {
        for (int i = 0; i < DIRECTORY_COUNT; ++i) {
            for (int j = 0; j < FILES_COUNT; ++j) {
                DirFile node = new DirFile(i, j);
                DataBaseFile file = new DataBaseFile(getFullName(node), node.nDir, node.nFile);
                files[node.getId()] = file;
            }
        }
    }

    private void checkKey(final String key) {
        if ((key == null) || (key.trim().length() == 0)) {
            throw new IllegalArgumentException("Wrong key!");
        }
    }

    public void drop() {
        for (byte i = 0; i < DIRECTORY_COUNT; ++i) {
            for (byte j = 0; j < FILES_COUNT; ++j) {
                File file = new File(getFullName(new DirFile(i, j)));
                if (file.exists()) {
                    if (!file.delete()) {
                        throw new DataBaseException("Cannot delete a file!");
                    }
                }
            }
            tryDeleteDirectory(Integer.toString(i) + ".dir");
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String put(final String keyStr, final String valueStr) {
        checkKey(keyStr);
        checkKey(valueStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.put(keyStr, valueStr);
    }

    @Override
    public String get(final String keyStr) {
        checkKey(keyStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.get(keyStr);
    }

    @Override
    public String remove(final String keyStr) {
        checkKey(keyStr);
        DirFile node = new DirFile(keyStr.getBytes()[0]);
        DataBaseFile file = files[node.getId()];
        return file.remove(keyStr);
    }

    @Override
    public int commit() {
        int allNew = 0;
        for (int i = 0; i < DIRECTORY_COUNT * FILES_COUNT; ++i) {
            allNew += files[i].getNewKeys();
            files[i].commit();
        }
        return allNew;
    }

    @Override
    public int size() {
        int allSize = 0;
        for (int i = 0; i < DIRECTORY_COUNT * FILES_COUNT; ++i) {
            allSize += files[i].getSize();
        }
        return allSize;
    }

    @Override
    public int rollback() {
        int allCanceled = 0;
        for (int i = 0; i < DIRECTORY_COUNT * FILES_COUNT; ++i) {
            allCanceled += files[i].getNewKeys();
            files[i].rollback();
        }
        return allCanceled;
    }

    public int getNewKeys() {
        int allNewSize = 0;
        for (int i = 0; i < DIRECTORY_COUNT * FILES_COUNT; ++i) {
            allNewSize += files[i].getNewKeys();
        }
        return allNewSize;
    }

    @Override
    public List<String> list() {
        List<String> result = new ArrayList<String>();
        for (DataBaseFile dbf : files) {
            if (dbf.getSize() != 0) {
                List<String> ans = dbf.getAllKeys();
                for (String x : ans) {
                    result.add(x);
                }
            }
        }
        return result;
    }
}
