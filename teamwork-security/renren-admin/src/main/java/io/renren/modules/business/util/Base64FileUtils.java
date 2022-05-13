package io.renren.modules.business.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.*;

public class Base64FileUtils {
    /**
     *
     * @param path  文件全路径(加文件名)
     * @return String
     * @description 将文件转base64字符串
     * @date 2019年11月24日
     * @author rmk
     */
    public static String fileToBase64(String path) {
        if (StringUtils.isEmpty(path)){
            return null;
        }
        File file = new File(path);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileToBase64(in);
    }

    public static String fileToBase64(InputStream fileStream){
        String base64 = null;
        try {
            byte[] bytes = new byte[fileStream.available()];
            fileStream.read(bytes);
            base64 = new String(Base64.encodeBase64(bytes),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * @param outFilePath  输出文件路径,  base64   base64文件编码字符串,  outFileName  输出文件名
     * @return String
     * @description BASE64解码成File文件
     * @date 2019年11月24日
     * @author rmk
     */
    public static void base64ToFile(String outFilePath,String base64, String outFileName) {
        File file = null;
        //创建文件目录
        String filePath=outFilePath;
        File  dir=new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.decodeBase64(base64);
            file=new File(filePath+"/"+outFileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
