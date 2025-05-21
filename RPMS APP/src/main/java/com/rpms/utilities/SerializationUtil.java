package com.rpms.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility class for object serialization and deserialization.
 * Provides static methods to save and load objects to/from files.
 */
public class SerializationUtil {

    /**
     * Serializes an object to a file.
     * 
     * @param obj The object to serialize
     * @param filePath The file path to save to
     * @return True if successful, false otherwise
     */
    public static boolean serializeObject(Object obj, String filePath) {
        try {
            // Ensure parent directory exists
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath))) {
                oos.writeObject(obj);
                System.out.println("Object serialized to: " + filePath);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error serializing object: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deserializes an object from a file.
     * 
     * @param filePath The file path to read from
     * @return The deserialized object or null if unsuccessful
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            T obj = (T) ois.readObject();
            System.out.println("Object deserialized from: " + filePath);
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error deserializing object: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
