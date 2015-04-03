package gather_data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

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

    /**
     * @param o
     * @param file_name
     * Save object o
     */
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

    /**
     * @param file_name
     * @return
     * @throws IOException
     * Load object from File file_name
     */
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

    /**
     * @param file_name
     * @param text_to_write
     * Write text at the end of a File (no overwriting)
     */
    public void append(String file_name, String text_to_write) {
        FileWriter fw;
        try {
            fw = new FileWriter("resources/" + file_name, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(text_to_write);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
