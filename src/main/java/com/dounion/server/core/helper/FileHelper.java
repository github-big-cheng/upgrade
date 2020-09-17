package com.dounion.server.core.helper;

import com.dounion.server.core.exception.SystemException;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;

public class FileHelper {


    /**
     * 使用随机流获取静态文件
     *
     * @return
     */
    public static byte[] getFile(String path) {

        File file = new File(path);
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new SystemException("file not found : 【" + path + "】");
        }

        byte[] bytes = null;
        RandomAccessFile f;
        try {
            f = new RandomAccessFile(file, "r");
            bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytes;
    }


    /**
     * 读取文件内容
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        File file = new File(path);
        if (file == null || !file.exists()) {
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return readFile(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return null;
    }


    /**
     * 读取文件内容
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String readFile(InputStream is) throws IOException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(is, "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return null;
    }


    /**
     * 写文件
     * @param path
     * @param content
     * @deprecated instead by Files.write
     */
    @Deprecated
    public static void writeFile(String path, String content) throws IOException {

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    /**
     * 指定位置创建文件
     *
     * @param downloadPath
     * @param file
     * @param deleteIfExists
     * @return
     */
    public static String createFile(String downloadPath, File from, boolean deleteIfExists) throws IOException {

        String realPath = downloadPath + from.getName();
        // 文件已存在 删除已存在的文件
        File to = new File(realPath);
        if(to.exists()){
            if(!deleteIfExists){
                throw new SystemException("file :【" + realPath + "】 exists..");
            }
            to.delete();
        }

        Files.copy(from.toPath(), to.toPath());

        return realPath;
    }


    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getFileMD5(File file) {

        try (
            FileInputStream fileInputStream = new FileInputStream(file)
        ){
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
