package mk.com.decode.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @ClassName: FileUtils
 * @Description: the util class of read and write files
 * @Author: xiaolan
 * @Date: 2020/6/4 11:20
 */
public class FileUtils {

    /**
     * @method readFile
     * @description
     * @date 2020/6/4 12:55
     * @param filePath
     * @return byte[]
     */
    public static byte[] readFile(String filePath) throws IOException {
        InputStream is = null;
        byte[] buffer = new byte[1024];
        try {
            is = new BufferedInputStream(new FileInputStream(filePath));
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!(is == null)) is.close();
        }
        return buffer;
    }

    /**
     * @method readFileContent
     * @description
     * @date 2020/6/4 12:55
     * @param filePath
     * @return java.lang.String
     */
    public static String readFileContent(String filePath) {
        File file = new File(filePath);
        //polymorphic no support readLine()
        BufferedReader reader = null;
//        reader = new BufferedReader(new FileReader(filePath));
        String str;
        StringBuffer buff;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        buff = new StringBuffer();
        try {
            while ((str = reader.readLine()) != null) {
                buff.append(str + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buff.toString();
    }


    /**
     * @method writeFileHexContent
     * @description
     * @date 2020/6/4 12:56
     * @param buffer
     * @param filePath
     * @return void
     */
    public static void writeFileHexContent(byte[] buffer, String filePath) throws IOException {

        if (filePath == null && !filePath.equals("")) {
            return;
        }
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(StringUtils.bytesToHexString(buffer));
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!(writer == null)) writer.close();
        }
    }
}