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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if(file.isDirectory()) {
            int index = 0;
            Set<String> files_name = new HashSet<String>(Arrays.asList(file.list()));
            
            String new_file_name = "";
            do {
                new_file_name = file_name + (++index); //Start at index = 1 on purpose
            } while (files_name.contains(new_file_name));
            
            new_file_name = "resources/" + file_name + "/" + new_file_name;
            file = new File(new_file_name);
        }
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
    public List<Object> load(String file_name) throws IOException {
        List<Object> res = new ArrayList<Object>();
        File file = new File("resources/" + file_name);

        File[] files = { file };
        if (file.isDirectory()) {
            files = file.listFiles();
        }

        for (File current_file : files) {
            try {
                FileInputStream f = new FileInputStream(current_file);
                ObjectInputStream s = new ObjectInputStream(f);
                res.add(s.readObject());
                s.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    private void write(String file_name, String text_to_write, boolean append) {
        FileWriter fw;
        try {
            fw = new FileWriter("resources/" + file_name, append);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            out.println(text_to_write);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file_name
     * @param text_to_write
     * Create a file containing text_to_write
     * Warning: Overwrite existing file!
     */
    public void overwrite(String file_name, String text_to_write) {
        write(file_name, text_to_write, false);
    }

    /**
     * @param file_name
     * @param text_to_write
     * Write text at the end of a File (no overwriting)
     */
    public void append(String file_name, String text_to_write) {
        write(file_name, text_to_write, true);
    }
    
    public boolean isExistingFolder(String file_name) {
        File f = new File("resources/" + file_name);
        return f.isDirectory();
    }

    public boolean isExistingFile(String file_name) {
        File f = new File("resources/" + file_name);
        return f.isFile();
    }

    public File getFile(String file_name) {
        return new File("resources/" + file_name);
    }

}
