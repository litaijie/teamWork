package io.renren.modules.business.service.serviceImpl;

import com.alibaba.excel.EasyExcelFactory;
import com.google.common.base.Strings;
import io.renren.modules.business.DTO.QueryParam;
import io.renren.modules.business.dao.ReportsMapper;
import io.renren.modules.business.entity.HVConsumables;
import io.renren.modules.business.entity.outpatientDepartment.Drug;
import io.renren.modules.business.entity.outpatientDepartment.Examine;
import io.renren.modules.business.entity.outpatientDepartment.Fever;
import io.renren.modules.business.entity.outpatientDepartment.Lab;
import io.renren.modules.business.service.OutDeptDataService;
import io.renren.modules.business.util.DownloadUtil;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service("outDeptDataServiceImpl")
public class OutDeptDataServiceImpl implements OutDeptDataService {
    @Autowired
    private ReportsMapper reportsMapper;

    private static String DATE_PATTEN="yyyy-MM-dd HH:mm:ss";
    private static String DATE_PATTEN_YMD="yyyy-MM-dd";
//    private static String FILE_PATH="D:/temp/";
    private static String FILE_PATH;

    static {
        try {
            FILE_PATH = ResourceUtils.getURL("classpath:").getPath() + "temp/";
            File tempPath =new File(FILE_PATH);
            if (!tempPath.exists()){
                tempPath.mkdir();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void outDeptData(HttpServletResponse response, QueryParam param){
        String startYMD = null;
        String endYMD = null;
        if (param.getEndDate()==null){
            endYMD = this.dayAddAndSub(Calendar.DATE,-1,DATE_PATTEN_YMD);
            param.setEndDate(endYMD);
        }

        if (param.getStartDate()==null){
            startYMD = this.dayAddAndSub(Calendar.DATE,-7,DATE_PATTEN_YMD);
            param.setStartDate(startYMD);
        }

        long startYear = Long.parseLong(startYMD.substring(0,4));
        long endYear = Long.parseLong(endYMD.substring(0,4));

        if (startYear<endYear){
            QueryParam param1 =QueryParam.builder().build();
            //??????????????????
            param.setStartDate(startYMD);
            param.setEndDate(startYear+"-12-31");
            param.setYear(String.valueOf(startYear));
            param1.setStartDate(startYear+"-01-01");
            param1.setEndDate(endYMD);
            param1.setYear(String.valueOf(startYear));
            this.getData(param,param1);
        }else {
            param.setYear(String.valueOf(startYear));
            this.getData(param,null);
        }


        /**??????*/
        String zipName = zip();
        try {
            File zipFile =new File(OutDeptDataServiceImpl.FILE_PATH+zipName);
            BufferedInputStream zipIn = null;
            if (zipFile.exists()){
                zipIn =new BufferedInputStream(new FileInputStream(zipFile));
            }

            DownloadUtil.download(response,zipName,zipIn);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**??????tem??????*/
        File tepFile =new File(FILE_PATH);
        if (tepFile.exists()){
            File[] files = tepFile.listFiles();
            int length = files.length;
            for (int i = 0;i<length;i++){
                files[i].delete();
            }
        }
    }

    /**
     * ?????????????????????
     */
    public void getData(QueryParam param,QueryParam param1){
        try {
            Future<String> result1 = this.getMDrug(param,param1);
            Future<String> result2 = this.getMFever(param,param1);
            Future<String> result3 = this.getMLab(param,param1);
            Future<String> result4 = this.getMExamines(param,param1);
            boolean back = false;
            while (!back){
                Thread.sleep(10000);
                if (result1.isDone() && result2.isDone() && result3.isDone() && result4.isDone()) {
                    back = true;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String zip(){
        String zipName = String.format("??????????????????%s.rar", new DateTime().toString("yyyy-MM-dd"));
        // ?????????????????????
        ZipFile zipFile = new ZipFile(FILE_PATH+zipName);
        ZipParameters parameters = new ZipParameters();
        // ????????????
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // ????????????
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        // ????????????????????????
        parameters.setEncryptFiles(true);
        // ??????????????????
        parameters.setEncryptionMethod(EncryptionMethod.AES);
        // ??????AES???????????????????????????
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        // ????????????
//        if(!Strings.isNullOrEmpty(password)) {
            zipFile.setPassword("3999".toCharArray());
//        }

        // ?????????????????????
        File file =new File(FILE_PATH);
        File[] fs = file.listFiles();

        // ??????test???????????????????????????????????????
        try {
            for (File f : fs) {
                if (f.isDirectory()) {
                    zipFile.addFolder(f, parameters);
                } else {
                    zipFile.addFile(f, parameters);
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return zipName;
    }

    /**
     * ????????????
     * @param currentDay
     * @param day
     * @param patten
     * @return
     */
    public String dayAddAndSub(int currentDay, int day,String patten) {
        SimpleDateFormat sdf = null;
        if (patten==null){
            sdf = new SimpleDateFormat(DATE_PATTEN);
        }else {
            sdf = new SimpleDateFormat(patten);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(currentDay, day);
        Date startDate = calendar.getTime();
        return sdf.format(startDate);
    }

    @Async
    public Future<String> getMDrug(QueryParam param,QueryParam param1) throws InterruptedException {
        String msg = "500";
        String fileName1 = FILE_PATH+"drug_?????????????????????.xls";
        List<Drug> mDrug = reportsMapper.mDrug(param);
        if (param1!=null) {
            List<Drug> mDrug1 = reportsMapper.mDrug(param1);
            if (mDrug1!=null||!mDrug1.isEmpty()) {
                mDrug.addAll(mDrug1);
            }
        }
        EasyExcelFactory.write(fileName1,Drug.class).useDefaultStyle(false).sheet("drug_?????????????????????").doWrite(mDrug);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getMExamines(QueryParam param,QueryParam param1) throws InterruptedException {
        String msg = "500";
        String fileName2 = FILE_PATH+"examine_?????????????????????.xls";
        List<Examine> mExamines = reportsMapper.mExamine(param);
        if (param1!=null) {
            List<Examine> mExamines1 = reportsMapper.mExamine(param1);
            if (mExamines1!=null||!mExamines1.isEmpty()) {
                mExamines.addAll(mExamines1);
            }
        }
        EasyExcelFactory.write(fileName2,Examine.class).useDefaultStyle(false).sheet("examine_?????????????????????").doWrite(mExamines);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getMFever(QueryParam param,QueryParam param1) throws InterruptedException {
        String msg = "500";
        String fileName3 = FILE_PATH+"fever_????????????????????????.xls";
        List<Fever> mFever = reportsMapper.mFever(param);
        if (param1!=null) {
            List<Fever> mFever1 = reportsMapper.mFever(param);
            if (mFever1!=null||!mFever1.isEmpty()) {
                mFever.addAll(mFever1);
            }
        }
        EasyExcelFactory.write(fileName3,Fever.class).useDefaultStyle(false).sheet("fever_????????????????????????").doWrite(mFever);
        return new AsyncResult<String>(msg);
    }

    @Async
    public Future<String> getMLab(QueryParam param,QueryParam param1) throws InterruptedException {
        String msg = "500";
        String fileName4 = FILE_PATH+"lab_??????????????????????????????.xls";
        List<Lab> mLab = reportsMapper.mLab(param);
        if (param1!=null) {
            List<Lab> mLab1 = reportsMapper.mLab(param);
            if (mLab1!=null||!mLab1.isEmpty()) {
                mLab.addAll(mLab1);
            }
        }
        EasyExcelFactory.write(fileName4,Lab.class).useDefaultStyle(false).sheet("lab_??????????????????????????????").doWrite(mLab);
        return new AsyncResult<String>(msg);
    }

    public static void main(String[] args) {
        String a = "";
        System.out.println(a.replace("\n"," ").replace("\t"," ").replace("    "," ").replace("  "," "));
    }
}
