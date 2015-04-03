package gather_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Amendil
 * Singleton
 */
public class FileManager {

    private static FileManager instance;

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }

        return instance;
    }

    private FileManager() {
        //Nothing
    }

    public void save(Object o, String file_name) {
        File file = new File("resources/" + file_name);
        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(o);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object load(String file_name) throws IOException {
        Object res = null;
        File file = new File("resources/" + file_name);
        try {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            res = s.readObject();
            s.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

}
