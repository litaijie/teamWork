package io.renren.modules.sys.controller;

import com.google.code.kaptcha.Constants;
import io.renren.common.utils.R;
import io.renren.modules.sys.entity.SysFile;
import io.renren.modules.sys.service.SysFileService;
import io.renren.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/sys/file")
public class SysFileController {
    @Autowired
    SysFileService sysFileService;

    private static String SUCCESS_KEY="success";
    private static String FAIL_KEY="fail";

    @PostMapping("/batchFileUpload")
    public R handleFileUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files.isEmpty()) return R.error("上传文件为空！");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        String filePath = sysFileService.createFilePath();
        Map<Object,Object> msg =new HashMap<>();
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            //文件绝对路径,项目中一般使用相对类路径,即使文件变更路径也会跟着变
//            String filePath = request.getServletContext().getRealPath("G:\\dev_workspace\\springboot-learning-examples\\springboot-13-fileupload\\src\\main\\resources\\static");
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String fullFilePath = filePath+ UUID.randomUUID().toString()+"."+StringUtils.substringAfterLast(originalFilename,".");
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(fullFilePath)));//设置文件路径及名字
                    stream.write(bytes);// 写入
                    stream.close();
                    SysFile sysFile =new SysFile();
                    sysFile.setFilePath(fullFilePath);
                    String fileName = StringUtils.substringAfterLast(fullFilePath, File.separator);
                    sysFile.setFileName(StringUtils.substringBefore(fileName,"."));
                    sysFile.setFileFormat(StringUtils.substringAfter(fileName,"."));
                    sysFile.setOriginalName(originalFilename);
                    sysFile.setFileSize(file.getSize());
                    boolean save = sysFileService.save(sysFile);
                    if (save){ msg.put(SUCCESS_KEY,sysFile);}else {msg.put(FAIL_KEY,"文件信息存档失败！");}
                } catch (Exception e) {
                    stream = null;
                    e.printStackTrace();
                    msg.put(FAIL_KEY,"第 " + (i+1) + " 个文件上传失败 ==> "+ e.getMessage());
                }
            } else {
                msg.put(FAIL_KEY,"第 " + (i+1) +" 个文件上传失败因为文件为空");
            }
        }
        return R.ok().put("msg",msg);
    }

    @RequestMapping(value = "/image/{imageName}",method = RequestMethod.GET)
    public void captcha(@PathVariable(required = true) String imageName, HttpServletResponse response)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        String name = StringUtils.substringBeforeLast(imageName, ".");
        String format = StringUtils.substringAfterLast(imageName, ".");

        BufferedImage image = sysFileService.getFile(name);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, format, out);
    }
}
