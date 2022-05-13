package io.renren.modules.business.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

public class ExportWordUtil {
    private static Logger logger = LoggerFactory.getLogger(ExportWordUtil.class);
    private Configuration configuration;
    private String encoding;

    /**
     * 构造函数 配置模板路径
     *
     * @param
     */
    public ExportWordUtil() {
        this.encoding = "UTF-8";
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDefaultEncoding(encoding);
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
    }

    /**
     * 获取模板
     *
     * @param name
     * @return
     * @throws Exception
     */
    public Template getTemplate(String name) throws Exception {
        return configuration.getTemplate(name);
    }

    /**
     * 导出word文档到指定目录
     *
     * @param fileName
     * @param tplName
     * @param data
     * @throws Exception
     */
    public void exportDocFile(String fileName,String exportPath, String tplName, Map<String, Object> data) throws Exception {
        // 如果目录不存在，则创建目录
//        File exportDirs = new File(exportPath);
//        if (!exportDirs.exists()) {
//            exportDirs.mkdirs();
//        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(exportPath + fileName), encoding));
            getTemplate(tplName).process(data, writer);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * 导出word文档到客户端
     *
     * @param response
     * @param fileName
     * @param tplName
     * @param data
     * @throws Exception
     */
    public void exportDoc(HttpServletResponse response, String fileName, String tplName, Map<String, Object> data)
            throws Exception {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 把本地文件发送给客户端
        Writer out = response.getWriter();
        Template template = getTemplate(tplName);
        template.process(data, out);
        out.close();
    }
}