package io.renren.modules.business.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import io.renren.modules.business.service.serviceImpl.OutDeptDataServiceImpl;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DownloadUtil {

    /**
     * 文件下载配置
     * @param response
     * @param fileName
     * @param fileIn
     */
    public static void download(HttpServletResponse response, String fileName, BufferedInputStream fileIn) {
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();

            responseSetting(response,fileName);

            byte[] buffer = new byte[fileIn.available()];
            int i = fileIn.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = fileIn.read(buffer);
            }

            outputStream.flush();
//            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
                fileIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void responseSetting(HttpServletResponse response,String fileName){
        //跨域
        response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
        //添加响应头信息
        try {
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //设置类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //设置头
        response.setHeader("Pragma", "No-cache");
        //设置头
        response.setHeader("Cache-Control", "no-cache");
        //设置日期头
        response.setDateHeader("Expires", 0);

    }

    public static String zip(List<String> filesName,String filePath,String zipName){
        return zip(filesName,filePath,zipName,null);
    }

    /**
     * 压缩文件
     * @param zipName
     * @param pwd
     * @return
     */
    public static String zip(List<String> filesName,String filePath,String zipName,String pwd){
//        String zipName = String.format("发热门诊数据%s.rar", new DateTime().toString("yyyy-MM-dd"));
        // 生成的压缩文件
        ZipFile zipFile = new ZipFile(filePath+zipName);
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        if(StringUtils.isNotEmpty(pwd)) {
            // 是否设置加密文件
            parameters.setEncryptFiles(true);
            // 设置加密算法
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            // 设置AES加密密钥的密钥强度
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            // 设置密码
            zipFile.setPassword(pwd.toCharArray());
        }

        // 遍历test文件夹下所有的文件、文件夹
        try {
            for (String s : filesName) {
                File f =new File(s);
                if (f.exists()) {
                    if (f.isDirectory()) {
                        zipFile.addFolder(f, parameters);
                    } else {
                        zipFile.addFile(f, parameters);
                    }
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return filePath+zipName;
    }

    public static BufferedInputStream zipIn(List<String> filesName,String filePath,String zipName){
        return zipIn(filesName,filePath,zipName,null);
    }

    /**
     *
     * @param response
     * @param files
     * @param zipPath
     * @param zipName
     * @param pwd
     */
    public static void zipByFiles(HttpServletResponse response,List<File> files,String zipPath,String zipName,String pwd){
        if (files!=null&&!files.isEmpty()) {
            ZipFile zipFile = new ZipFile(zipPath + zipName);
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            if (StringUtils.isNotEmpty(pwd)) {
                // 是否设置加密文件
                parameters.setEncryptFiles(true);
                // 设置加密算法
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                // 设置AES加密密钥的密钥强度
                parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                // 设置密码
                zipFile.setPassword(pwd.toCharArray());
            }

            BufferedInputStream in =null;
            try {
                files.forEach(f->{
                    if (f.exists()) {
                        try {
                            zipFile.addFile(f, parameters);
                        } catch (ZipException e) {
                            e.printStackTrace();
                        }
                    }
                });
                in =new BufferedInputStream(new FileInputStream(new File(zipPath + zipName)));
                download(response,zipName,in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                files.forEach(f->{
                    if (f.exists()) f.delete();
                });
            }
        }
    }

    /**
     * 压缩文件
     * @param filePath
     * @param zipName
     * @param pwd
     * @return
     */
    public static BufferedInputStream zipIn(List<String> filesName,String filePath,String zipName,String pwd){
        String zip = zip(filesName,filePath, zipName, pwd);
        File zipFile =new File(zip);
        BufferedInputStream zipIn = null;
        if (zipFile.exists()){
            try {
                zipIn =new BufferedInputStream(new FileInputStream(zipFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return zipIn;
    }

    public static void delTempFile(String path){
        /**删除tem文件*/
        File tepFile =new File(path);
        if (tepFile.exists()){
            if (tepFile.isFile()){
                tepFile.delete();
            }else {
                File[] files = tepFile.listFiles();
                int length = files.length;
                for (int i = 0;i<length;i++){
                    files[i].delete();
                }
            }

        }
    }

    public static void delTempFile(List<String> path){
        /**删除tem文件*/
        for (String s : path){
            File tempFile =new File(s);
            if (tempFile.exists()){
                tempFile.delete();
            }
        }
    }
}
