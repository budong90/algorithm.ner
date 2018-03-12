package com.wekj.ner.utils;

import java.io.*;

public class StreamUtils {
    public static BufferedReader getReader(String path) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File(path);
        InputStreamReader fileInputStream = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader reader = new BufferedReader(fileInputStream);

        return reader;
    }

    public static void writeFile(String path, String data) {
        writeFile(path, "utf-8", data);
    }

    public static void writeFile(String path, String charset, String data) {
        writeFile(path, charset, data, false);
    }

    public static void writeFile(String path, String charset, String data, boolean isAppend) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(new File(path), isAppend);
            fos.write(data.getBytes(charset));
            fos.flush();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            close((OutputStream)fos);
        }
    }

    public static void close(Closeable reader) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
