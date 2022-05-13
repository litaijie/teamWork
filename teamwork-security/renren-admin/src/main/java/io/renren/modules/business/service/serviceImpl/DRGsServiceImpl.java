package io.renren.modules.business.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.business.dao.DGRsExpensesMapper;
import io.renren.modules.business.dao.DGRsIntegralMapper;
import io.renren.modules.business.entity.drgs.DRGsDatas;
import io.renren.modules.business.entity.drgs.DRGsIntegral;
import io.renren.modules.business.entity.drgs.DRGsRequestResults;
import io.renren.modules.business.service.DRGsService;
import io.renren.modules.business.util.DateUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@Service("dRGsServiceImpl")
public class DRGsServiceImpl extends ServiceImpl<DGRsExpensesMapper, DRGsDatas> implements DRGsService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DGRsIntegralMapper dgRsIntegralMapper;

    @Transactional
    @Override
    public void request(DateTime date){
        String paramDate = date.toString("yyyy-MM");
        String apiUrl="http://59.211.220.103:1016/DrgsWeb/medicalsearch/pubintegratedquery/GetDRGsDieases";//病例点数查询
        String loginUrl="http://59.211.220.103:1016/DrgsWeb/AccountSso/LoginOn";//登录

        MultiValueMap<String,Object> loginParam =new LinkedMultiValueMap<>();
        loginParam.add("UserName","0848d939b97607d0dc73e219a9986fa7705898aa206c03feffc915c6f9734cefd848708be11e755f7b0ab10ccb7f0f1447aff28a0e119c9dfb431e8a305478284c170857541ee5099da050dbf059b6b0142f393f83fec3db7f9026474d1bc7982f31afb75381a382f7ada6661f34896aa0a389f7d445830a4c4e7f5002a6f1fd");
        loginParam.add("UserPwd","9d268313668bde82c12faa05c26a14b3a0c5baeeab23d249331f29bc5add34ae69db3fb54994c8dad139a6832c5d32119df85ee5244916770a45deab074d2ece97cc70d936e42a600e05d4550d4448fcfcdae689cd4e67d9f08b6374d0d0af0876908fb63968d348d09761bca6948970c034b78cb22e1e92a76c907e96e9ce9b");

        HttpEntity<MultiValueMap<String,Object>> entity = this.initApiHeader(loginParam);

        ResponseEntity<String> exchange = restTemplate.postForEntity(loginUrl, entity, String.class);

        if (exchange.getStatusCode().is2xxSuccessful()) {
            StringBuffer apiCookie = new StringBuffer("ASP.NET_SessionId=zr4onrhcycjfyrp20afp5ixl;drgsapp-login_txtName=00000004;");
            HttpHeaders responseHeaders = exchange.getHeaders();
            responseHeaders.get("Set-Cookie").forEach(h -> {
                apiCookie.append(h);
            });
            MultiValueMap<String, Object> bodyParam = new LinkedMultiValueMap<>();
            bodyParam.add("_dc", System.currentTimeMillis());
//            bodyParam.add("param","{\"BeginBillDate\":\"2021-10-01T00:00:00\",\"EndBillDate\":\"2021-10-31T00:00:00\",\"SpecFlagDisabled\":false,\"LongtermDisabled\":true,\"id\":\"CisApp.model.medicalsearch.pubdiseaseintegratedquery.DiseaseintegratedParam-1\",\"GrantMonth\":null,\"HisId\":\"\",\"SpecialAdmissionNo\":\"\",\"IsModify\":\"\",\"IsGroup\":\"\",\"DoctorName\":\"\",\"Deptname\":\"\",\"NewMedicalType\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\"],\"FundType\":[\"202\"]}");
            String monthLastDay = DateUtil.getMonthLastDay(paramDate);

            StringBuffer paramDateBuff = new StringBuffer("{\"BeginBillDate\":\"");
            paramDateBuff.append(paramDate).append("-01").append("T00:00:00\",\"EndBillDate\":\"")
                    .append(monthLastDay).append("T00:00:00\",\"SpecFlagDisabled\":false,\"LongtermDisabled\":true,\"id\":\"CisApp.model.medicalsearch.pubdiseaseintegratedquery.DiseaseintegratedParam-1\",\"NewMedicalType\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\"]}");

            bodyParam.add("param", paramDateBuff.toString());
            bodyParam.add("PageNum", 1);
            bodyParam.add("PageSize", 3000);
            bodyParam.add("TotalCount", 0);
            bodyParam.add("StartIndex", 0);

            HttpEntity<MultiValueMap<String, Object>> apiEntity = this.initApiHeader(apiCookie.toString(), bodyParam);

            /**开始获取比例点数*/
            ResponseEntity<String> result = restTemplate.postForEntity(apiUrl, apiEntity, String.class);
            DRGsRequestResults drGsRequestResults = null;
            if (result.getStatusCode().is2xxSuccessful()) {
                String body = result.getBody();
                JSONObject jsonObject = JSON.parseObject(body);
                drGsRequestResults = JSONObject.toJavaObject(jsonObject, DRGsRequestResults.class);
            }/**结束获取比例点数*/

            /**开始获取每月积分费用*/
            if (drGsRequestResults != null && drGsRequestResults.getResult() != null && drGsRequestResults.getResult().size() > 0) {
                DRGsIntegral drGsIntegral = null;
                String apiUrl1 = "http://59.211.220.103:1016/DrgsWeb/Report/PubMonthGrantQuery/GetMonthGrantQuerys";//分配结果查询
                MultiValueMap<String, Object> param1 = new LinkedMultiValueMap<>();
                param1.add("_dc", System.currentTimeMillis());
                param1.add("Year", date.toString("yyyy"));
                param1.add("PageNum", 1);
                param1.add("PageSize", 10);
                param1.add("StartIndex", 0);
                HttpEntity<MultiValueMap<String, Object>> apiEntity1 = this.initApiHeader(apiCookie.toString(), param1);

                ResponseEntity<String> result1 = restTemplate.postForEntity(apiUrl1, apiEntity1, String.class);
                if (result1.getStatusCode().is2xxSuccessful()) {
                    JSONObject jsonObject = JSON.parseObject(result1.getBody());
                    DRGsRequestResults drGsRequestResults1 = JSONObject.toJavaObject(jsonObject, DRGsRequestResults.class);
                    if (drGsRequestResults1.getResult() != null && drGsRequestResults1.getResult().size() > 0) {
                        drGsIntegral = new DRGsIntegral();
                        String yearMonth = date.toString("yyyyMM");
                        for (DRGsDatas e : drGsRequestResults1.getResult()) {
                            drGsRequestResults.setExpensesDate(yearMonth);
                            drGsIntegral.setExpensesDate(yearMonth);
                            if (yearMonth.equals(e.getYearMonth())) {
                                if ("居民".equals(e.getFundName())) {
                                    drGsRequestResults.setJuminValue(e.getIntegralrate());
                                    drGsIntegral.setJuminValue(e.getIntegralrate());
                                } else if ("职工".equals(e.getFundName())) {
                                    drGsRequestResults.setZhigongValue(e.getIntegralrate());
                                    drGsIntegral.setZhigongValue(e.getIntegralrate());
                                }
                            }
                        }

                        dgRsIntegralMapper.insert(drGsIntegral);
                        this.saveBatch(drGsRequestResults.getResult(), drGsRequestResults.getResult().size());
                    } else {
                        System.out.println("获取月度积分数据失败");
                    }
                }
            } else {
                System.out.println("获取病例点数数据失败");
            }/**结束获取每月积分费用*/

//            System.out.println(drGsRequestResults);
        } else {
            System.out.println("登录失败");
        }

    }

    /**
     * 组装接口请求头参数
     * @param cookie 登录的cookie
     * @param bodyParam 接口请求的参数
     * @return
     */
    private HttpEntity<MultiValueMap<String, Object>> initApiHeader(String cookie,MultiValueMap<String,Object> bodyParam){
        HttpHeaders headers =new HttpHeaders();
        headers.add("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
        headers.add("Cookie",cookie);
        HttpEntity<MultiValueMap<String,Object>> apiEntity =new HttpEntity<MultiValueMap<String,Object>>(bodyParam,headers);
        return apiEntity;
    }

    private HttpEntity<MultiValueMap<String, Object>> initApiHeader(MultiValueMap<String,Object> bodyParam){
        HttpHeaders headers =new HttpHeaders();
        headers.add("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        headers.add("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36");
        HttpEntity<MultiValueMap<String,Object>> apiEntity =new HttpEntity<MultiValueMap<String,Object>>(bodyParam,headers);
        return apiEntity;
    }
}
