/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.business.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.commons.dynamic.datasource.annotation.DataSource;
import io.renren.modules.business.DTO.QueryParam;
import io.renren.modules.business.entity.HVConsumables;
import io.renren.modules.business.entity.outpatientDepartment.Drug;
import io.renren.modules.business.entity.outpatientDepartment.Examine;
import io.renren.modules.business.entity.outpatientDepartment.Fever;
import io.renren.modules.business.entity.outpatientDepartment.Lab;
import io.renren.modules.sys.entity.SysDeptEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 报表查询
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface ReportsMapper extends BaseMapper<SysDeptEntity> {
    /**
     *1高值耗材
     */
    String hVConsumables = "with s as\n" +
            "(select a.shoufeiyonghuming,a.zhuyuankeshi,sum(a.shishoufeiyong) jine,sum(a.shuliang) shuliang from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('24','34') and danjia>=500 group by a.shoufeiyonghuming,a.zhuyuankeshi),\n" +
            "t as\n" +
            "(select a.jisuanjibianma,a.zhuyuankeshi,a.danwei,sum(a.shishoufeiyong) jine,sum(a.shuliang) shuliang from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('24','34') and danjia>=500 group by a.jisuanjibianma,a.zhuyuankeshi,a.danwei)\n" +
            "select keshi.keshimingcheng,jine.jine,keshi.names,yisheng.names from \n" +
            "(select a.zhuyuankeshi,sum(a.shishoufeiyong) jine from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('24','34') and danjia>=500 group by a.zhuyuankeshi) jine,\n" +
            "(SELECT hc.keshiid,hc.keshimingcheng,listagg (hc.name, ',') WITHIN GROUP (ORDER BY hc.shuliang desc) names from (\n" +
            "select ks.keshiid,ks.keshimingcheng,x.mingcheng||' '||fy.shuliang||fy.danwei name,fy.shuliang from (\n" +
            "select  t.*,row_number() over(partition by t.zhuyuankeshi order by t.shuliang desc) pm from t) fy,x_keshi ks,x_feiyongbiaozhun x where fy.jisuanjibianma=x.jisuanjibianma and fy.zhuyuankeshi=ks.keshiid and fy.pm<=3) hc group by hc.keshiid,hc.keshimingcheng) keshi,\n" +
            "(SELECT hc.zhuyuankeshi,listagg (hc.name, ',') WITHIN GROUP (ORDER BY hc.jine desc) names from (select fy.zhuyuankeshi,fy.shoufeiyonghuming||' '||fy.jine name,fy.jine from (\n" +
            "select  s.*,row_number() over(partition by s.zhuyuankeshi order by s.jine desc) pm from s) fy where  fy.pm<=3) hc group by hc.zhuyuankeshi) yisheng \n" +
            "where jine.zhuyuankeshi=keshi.keshiid and jine.zhuyuankeshi=yisheng.zhuyuankeshi";
    @Select(hVConsumables)
    List<HVConsumables> hVConsumables(@Param("param")QueryParam param);

    /**
     * 2药品
     */
    String drugs = "with s as\n" +
            "(select a.shoufeiyonghuming,a.shoufeikeshiid,a.danwei,sum(a.shishoufeiyong) jine,sum(a.shuliang) shuliang from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('17','18','19') and a.danjia>=50 group by a.shoufeiyonghuming,a.shoufeikeshiid,a.danwei),\n" +
            "t as\n" +
            "(select a.jisuanjibianma,a.shoufeikeshiid,a.danwei,sum(a.shishoufeiyong) jine,sum(a.shuliang) shuliang from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('17','18','19') and a.danjia>=50  group by a.jisuanjibianma,a.shoufeikeshiid,a.danwei)\n" +
            "\n" +
            "select keshi.keshimingcheng,jine.jine,keshi.names,yisheng.names from\n" +
            "(select a.shoufeikeshiid,sum(a.shishoufeiyong) jine from z_feiyong${param.year}  a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' and caiwuid in ('17','18','19')  and danjia>=50 group by a.shoufeikeshiid) jine,\n" +
            "(SELECT hc.keshiid,hc.keshimingcheng,listagg (hc.name, ',') WITHIN GROUP (ORDER BY hc.shuliang desc) names from (\n" +
            "select ks.keshiid,ks.keshimingcheng,x.mingcheng||' '||fy.shuliang||fy.danwei name,fy.shuliang from (\n" +
            "select  t.*,row_number() over(partition by t.shoufeikeshiid order by t.shuliang desc) pm from t) fy,x_keshi ks,x_yaopin x where fy.jisuanjibianma=x.yaopinid and fy.shoufeikeshiid=ks.keshiid and fy.pm<=5) hc group by hc.keshiid,hc.keshimingcheng) keshi,\n" +
            "(SELECT hc.shoufeikeshiid,listagg (hc.name, ',') WITHIN GROUP (ORDER BY hc.jine desc) names from (select fy.shoufeikeshiid,fy.shoufeiyonghuming||' '||fy.jine name,fy.jine from (\n" +
            "select  s.*,row_number() over(partition by s.shoufeikeshiid order by s.jine desc) pm from s) fy where  fy.pm<=3) hc group by hc.shoufeikeshiid) yisheng\n" +
            "where jine.shoufeikeshiid=keshi.keshiid and jine.shoufeikeshiid=yisheng.shoufeikeshiid";
    @Select(drugs)
    List<HVConsumables> drugs(@Param("param")QueryParam param);

    /**
     * 3各科室耗材使用情况表
     */
    String hVConsumablesByDept = "select keshimingcheng 科室,sum(hcje) 耗材金额,sum(pthc) 普通耗材金额,sum(gzhc) 高值金额,sum(zje) 总金额,round(sum(hcje)/sum(zje),4)*100 耗材占比 from (\n" +
            "select a.zhuyuankeshi,0 hcje,0 pthc,sum(a.shishoufeiyong) gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('24','34') and danjia>=500 group by a.zhuyuankeshi \n" +
            "union all\n" +
            "select a.zhuyuankeshi,0 hcje,sum(a.shishoufeiyong) pthc,0 gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('24','34') and danjia<500 group by a.zhuyuankeshi\n" +
            "union all\n" +
            "select a.zhuyuankeshi,sum(a.shishoufeiyong) hcje,0 pthc,0 gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('24','34') group by a.zhuyuankeshi\n" +
            "union all select a.zhuyuankeshi,0 hcje,0 pthc,0 gzhc,sum(a.shishoufeiyong) zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' group by a.zhuyuankeshi) a,x_keshi b where a.zhuyuankeshi=b.keshiid  group by keshimingcheng";
    @Select(hVConsumablesByDept)
    List<HVConsumables> hVConsumablesByDept(@Param("param")QueryParam param);

    /**
     * 4各科室药品使用情况
     */
    String drugsByDept = "select keshimingcheng 科室,sum(hcje) 药品金额,sum(pthc) 普通药品金额,sum(gzhc) 贵重药品金额,sum(zje) 总金额,round(sum(hcje)/sum(zje),4)*100 药占比 from (\n" +
            "select a.shoufeikeshiid,0 hcje,0 pthc,sum(a.shishoufeiyong) gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('17','18','19') and danjia>=50 group by a.shoufeikeshiid\n" +
            "union all\n" +
            "select a.shoufeikeshiid,0 hcje,sum(a.shishoufeiyong) pthc,0 gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('17','18','19') and danjia<50 group by a.shoufeikeshiid\n" +
            "union all\n" +
            "select a.shoufeikeshiid,sum(a.shishoufeiyong) hcje,0 pthc,0 gzhc,0 zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('17','18','19') group by a.shoufeikeshiid\n" +
            "union all select a.shoufeikeshiid,0 hcje,0 pthc,0 gzhc,sum(a.shishoufeiyong) zje from z_feiyong${param.year} a where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}' group by a.shoufeikeshiid) a,x_keshi b where a.shoufeikeshiid=b.keshiid   group by keshimingcheng having sum(hcje)!=0";
    @Select(drugsByDept)
    List<HVConsumables> drugsByDept(@Param("param")QueryParam param);

    /**
     * 5使用量前10高值耗材
     */
    String hVConsumables10 = "select c.mingcheng 名称,c.guige 规格,a.danjia 单价,a.jine 总金额,a.shuliang 数量,b.names 前三 from (\n" +
            "select jisuanjibianma,danjia,sum(shishoufeiyong) jine,sum(shuliang) shuliang,ROW_NUMBER() over(order by sum(shishoufeiyong) desc) rn from z_feiyong${param.year} where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('24','34') and danjia>=500 group by jisuanjibianma,danjia ) a,(\n" +
            "select jisuanjibianma,listagg (name, ',') WITHIN GROUP (ORDER BY rn) names from (\n" +
            "select jisuanjibianma,shoufeiyonghuming||' '||shuliang||danwei name,rn from (\n" +
            "select jisuanjibianma,shoufeiyonghuming,danwei,sum(shuliang) shuliang,ROW_NUMBER() over(partition by jisuanjibianma order by sum(shishoufeiyong) desc) rn from z_feiyong${param.year} where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('24','34') and danjia>=500 group by jisuanjibianma,shoufeiyonghuming,danwei) where rn<=3) group by jisuanjibianma) b，x_feiyongbiaozhun c where a.jisuanjibianma=b.jisuanjibianma and a.jisuanjibianma=c.jisuanjibianma and a.rn<=10";
    @Select(hVConsumables10)
    List<HVConsumables> hVConsumables10(@Param("param")QueryParam param);

    /**
     * 6使用量前10药品
     */
    String drugs10 = "select c.mingcheng 名称,c.guigemingcheng 规格,a.danjia 单价,a.jine 总金额,a.shuliang 数量,b.names 前三 from (\n" +
            "select jisuanjibianma,danjia,sum(shishoufeiyong) jine,sum(shuliang) shuliang,ROW_NUMBER() over(order by sum(shishoufeiyong) desc) rn from z_feiyong${param.year} where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('17','18','19') group by jisuanjibianma,danjia ) a,(\n" +
            "select jisuanjibianma,listagg (name, ',') WITHIN GROUP (ORDER BY rn) names from (\n" +
            "select jisuanjibianma,shoufeiyonghuming||' '||shuliang||danwei name,rn from (\n" +
            "select jisuanjibianma,shoufeiyonghuming,danwei,sum(shuliang) shuliang,ROW_NUMBER() over(partition by jisuanjibianma order by sum(shishoufeiyong) desc) rn from z_feiyong${param.year} where to_char(jiesuanshijian,'yyyy-MM')='${param.yearMonth}'and caiwuid in ('17','18','19') group by jisuanjibianma,shoufeiyonghuming,danwei) where rn<=3) group by jisuanjibianma) b，x_yaopin c where a.jisuanjibianma=b.jisuanjibianma and a.jisuanjibianma=c.yaopinid and a.rn<=12";
    @Select(drugs10)
    List<HVConsumables> drugs10(@Param("param")QueryParam param);

    /**
     * drug_诊疗处方记录表
     */
    String mDrug = "with z_dangan_lgsb as (select m_danganid M_DANGANID, m_rizhiid m_RIZHIID, '0' AA, sysdate SHIJIAN from m_lczhenduan${param.year} where 1 = 1 and to_date(rizhiid, 'yyyymmdd') >= to_date('${param.startDate} 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and to_date(rizhiid, 'yyyymmdd') <= to_date('${param.endDate} 23:59:59', 'yyyy-mm-dd hh24:mi:ss') and icd10id in (select icd10id from x_icd10 where icd10 like 'J__.%') group by m_danganid, m_rizhiid) select nvl(a.kahao,'000000') P7502, nvl(to_char(rz.shijian, 'yyyy-mm-dd hh24:mi:ss'),'-') P7506, nvl(rz.m_rizhiid,'-') P7000, nvl(a.xingming,'-') P4, nvl(b.chufangid,'-') P7800, nvl(to_char(b.xiadashijian, 'yyyy-mm-dd hh24:mi:ss'),'-') P7801,  '-' P7802, decode(yp.fenleiid, '20', '11', '00', '12', '10', '13', '50', '13') P7803, decode(yp.fenleiid, '20', '1', '00', '2', '10', '3', '50', '9') P7804, yp.yaopinid P7805, nvl(yp.mingcheng,'-') P7806, '-' P7807,  '-' P7808,  '-' P7810, '-' P7811, '-' P7812, '-' P7813, nvl(yp.guigemingcheng,'-') P7814, nvl(pc.suoxie,'-') P7815, c.fuyongliang P7816, c.fuyongjiliang P7817, nvl(c.danwei,'-') P7818, '-' P7819,  '-' P7820, decode(c.pishijieguo, '0', '2', '2', '1', '9', '2'，'-') P7821,  '-' P7822, '-' P7823, c.tianshu P7824, '-' P7825, '-' P7826, '-' P7827, '-' P7828,  '-' P7829, '-' P7830, '-' P7831, '-' P7832 from m_dangan a, m_yizhu${param.year} b, m_yizhu_mx${param.year} c, x_yaopin yp, m_rizhi${param.year} rz, x_yzpinchi  pc, z_dangan_lgsb  lg where a.m_danganid = b.m_danganid and b.chufangid = c.chufangid and c.yaopinid = yp.yaopinid and b.m_rizhiid = rz.m_rizhiid and c.pinchi = pc.pinchiid and rz.m_rizhiid = lg.m_rizhiid";
    @DataSource("slave1")
    @Select(mDrug)
    List<Drug> mDrug(@Param("param")QueryParam param);

    /**
     * examine_辅助检查记录表
     */
    String mExamine = "with z_dangan_lgsb as (select m_danganid M_DANGANID, m_rizhiid m_RIZHIID, '0' AA, sysdate SHIJIAN from m_lczhenduan${param.year} where 1 = 1 and to_date(rizhiid, 'yyyymmdd') >= to_date('${param.startDate} 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and to_date(rizhiid, 'yyyymmdd') <= to_date('${param.endDate} 23:59:59', 'yyyy-mm-dd hh24:mi:ss') and icd10id in (select icd10id from x_icd10 where icd10 like 'J__.%') group by m_danganid, m_rizhiid) select nvl(da.kahao,'000000') P7502, nvl(to_char(rz.shijian, 'yyyy-mm-dd hh24:mi:ss'),'-')  P7506, nvl(rz.m_rizhiid,'-') P7000, nvl(da.xingming,'-') P4, '-' P7701, '-' P7702, '-' P7703, nvl(rz.rizhiid || fj.binglihao || fj.xiangmuid,'-') P7704, '-' P7705, nvl(to_char(fj.jianchashijian, 'yyyy-mm-dd hh24:mi:ss'),'-') P7706, '-' P7707, '-' P7708, '-' P7709, '-' P7710, nvl(xm.xiangmuid,'-') P7711, nvl(xm.mingcheng,'-') P7712, nvl(fl.fenleiming,'-') P7713, decode(fj.yinyangxing, '阳性', '1', '2') P7714, nvl(substr(fj.baogaoxiaojie, 1, instr(baogaoxiaojie, '检查结论') - 1),'-') P7715, '-' P7716, nvl(substr(fj.baogaoxiaojie, instr(baogaoxiaojie, '检查结论')),'-') P7717 from m_lcfujian${param.year}  fj, x_fujianxiangmu xm, x_fujianfenlei  fl, m_dangan da, m_rizhi${param.year} rz, z_dangan_lgsb   lgsb where fj.xiangmuid = xm.xiangmuid and fj.fenleiid = fl.fenleiid and fj.danganid = da.m_danganid and fj.m_rizhiid = rz.m_rizhiid and fj.m_rizhiid = lgsb.m_rizhiid and jianchakeshiid = 'JC03'";
    @DataSource("slave1")
    @Select(mExamine)
    List<Examine> mExamine(@Param("param")QueryParam param);

    /**
     * fever_发热门诊病例信息
     */
    String mFever = "with z_dangan_lgsb as( select m_danganid M_DANGANID, rizhiid M_RIZHIID, '0' AA, sysdate SHIJIAN from m_lczhenduan${param.year}  where 1 = 1 and to_date(rizhiid,'yyyymmdd') >= to_date('${param.startDate} 00:00:00','yyyy-mm-dd hh24:mi:ss')  and to_date(rizhiid,'yyyymmdd') <= to_date('${param.endDate} 23:59:59','yyyy-mm-dd hh24:mi:ss')  and icd10id in (select icd10id from x_icd10 where icd10 like 'J__.%')  group by m_danganid, rizhiid) select  '499373155' P900, '贵港市中西医结合骨科医院' P6891, nvl(da.baoxianhao,'-') P686, '-' P800, nvl(decode(gh.jizhenbiaoji, '1', '02', '01'), '-') P7501, nvl(da.kahao,'000000') P7502, nvl(lgsb.m_rizhiid, '-') P7000, nvl(da.xingming, '-') P4, nvl(decode(da.xingbie, '1', '1', '2', '2', '9'), '-') P5, nvl(to_char(to_date(da.chushengriqi, 'yyyymmdd'), 'yyyy-mm-dd') || ' ' || da.chushengshijian, '-') P6, trunc((trunc(gh.guahaoshijian, 'dd') - trunc(to_date(da.chushengriqi, 'yyyymmdd'), 'dd')) / 365) P7, '-' P12,'-'P11,'-'P8,'-'P9, '01' P7503, nvl(da.shenfenzhenghao, '-') P13, nvl(da.jiatingdizhi,'-') P801, nvl(da.lianxidianhua,'-') P802, nvl(da.youbian,'-') P803, '-' P14,'-'P15,'-'P16,nvl(DA.LIANXIREN,'-') P17,'-' P19,'-' P20,'-' P21, '-' P7505, '-' P7520,'-' P7521, '-' P7504, '-' P7522, nvl(to_char(gh.guahaoshijian, 'yyyy-mm-dd hh24:mi:ss'),'-') P7506, '-' P7507, '-' P7523,'-' P7524,'-' P7525, nvl(zd.jibingming,'-') P7526, '-' P7527,'-' P7528, '-'P7529, nvl(zd.icd10,'-') P28, nvl(zd.jibingming,'-') P281, '-'P7530, '-' P1, (case when fy.shishoufeiyong is null or fy.shishoufeiyong<0 then 0 else fy.shishoufeiyong end) P7508, (case when ghfy.shishoufeiyong is null or ghfy.shishoufeiyong<0 then 0 else ghfy.shishoufeiyong end) P7509, (case when ypfy.shishoufeiyong is null or ypfy.shishoufeiyong<0 then 0 else ypfy.shishoufeiyong end) P7510, (case when jcfy.shishoufeiyong is null or jcfy.shishoufeiyong<0 then 0 else jcfy.shishoufeiyong end) P7511, (case when (fy.shishoufeiyong-ybsj.yyfd) is null or (fy.shishoufeiyong-ybsj.yyfd) <0 then 0 else (fy.shishoufeiyong-ybsj.yyfd) end) P7512  from z_dangan_lgsb lgsb inner join m_dangan da on lgsb.m_danganid = da.m_danganid inner join  ( select m_danganid, rizhiid, keshiid, guahaoshijian, jizhenbiaoji, row_number() over(partition by m_danganid, rizhiid order by  guahaoshijian) rn from m_guahao${param.year}  ) gh on lgsb.m_danganid = gh.m_danganid and lgsb.m_rizhiid = gh.rizhiid and gh.rn = 1 left join  ( select inzd.m_danganid, inzd.rizhiid, inzd.icd10id, inicd.icd10, inicd.jibingming, row_number() over(partition by inzd.m_danganid, inzd.rizhiid order by inzd.zhuzhenduan, inzd.shijian) rn from m_lczhenduan${param.year} inzd, x_icd10 inicd where inzd.icd10id = inicd.icd10id  and inzd.zhuzhenduan = '0' ) zd on gh.m_danganid = zd.m_danganid and gh.rizhiid = zd.rizhiid and zd.rn = 1 left join  ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from  ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong where jiucuobiaoji='0'group by m_danganid, rizhiid union all select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong${param.year} where jiucuobiaoji='0'and chongxiaobiaoji='0' group by m_danganid, rizhiid ) group by m_danganid, rizhiid  ) fy on gh.m_danganid = fy.m_danganid and gh.rizhiid = fy.rizhiid left join  ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from (select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong where shoujuid = '00'and jiucuobiaoji='0' group by m_danganid, rizhiid union all select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong${param.year} where shoujuid = '00' and jiucuobiaoji='0'and chongxiaobiaoji='0'group by m_danganid, rizhiid ) group by m_danganid, rizhiid ) ghfy on gh.m_danganid = ghfy.m_danganid and gh.rizhiid = ghfy.rizhiid left join  ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong where shoujuid in('01','02','03') and jiucuobiaoji='0'group by m_danganid, rizhiid having sum(shishoufeiyong)>=0 union all select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong${param.year} where shoujuid in('01','02','03') and jiucuobiaoji='0'and chongxiaobiaoji='0' group by m_danganid, rizhiid having sum(shishoufeiyong)>=0 ) group by m_danganid, rizhiid ) ypfy on gh.m_danganid = ypfy.m_danganid and gh.rizhiid = ypfy.rizhiid left join (select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from ( select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong where shoujuid = '06'and jiucuobiaoji='0' group by m_danganid, rizhiid union all select m_danganid, rizhiid, sum(shishoufeiyong) shishoufeiyong from m_feiyong${param.year} where shoujuid = '06'and jiucuobiaoji='0'and chongxiaobiaoji='0' group by m_danganid, rizhiid ) group by m_danganid, rizhiid having sum(shishoufeiyong)>=0) jcfy on gh.m_danganid = jcfy.m_danganid and gh.rizhiid = jcfy.rizhiid left join ( select m_danganid, shoufeiriqi rizhiid, sum(JiJinZhiFu + ZhangHuZhiFu) yyfd from m_ybshouju${param.year} where jiesuan='1'group by m_danganid, shoufeiriqi having sum(JiJinZhiFu + ZhangHuZhiFu)>=0 ) ybsj on gh.m_danganid = ybsj.m_danganid and gh.rizhiid = ybsj.rizhiid where 1 = 1";
    @DataSource("slave1")
    @Select(mFever)
    List<Fever> mFever(@Param("param")QueryParam param);

    /**
     * lab_实验室检验详细记录表
     */
    String mLab = "with z_dangan_lgsb as (select m_danganid M_DANGANID, m_rizhiid m_RIZHIID, '0' AA, sysdate SHIJIAN from m_lczhenduan${param.year} where 1 = 1 and to_date(rizhiid, 'yyyymmdd') >= to_date('${param.startDate} 00:00:00', 'yyyy-mm-dd hh24:mi:ss') and to_date(rizhiid, 'yyyymmdd') <= to_date('${param.endDate} 23:59:59', 'yyyy-mm-dd hh24:mi:ss') and icd10id in (select icd10id from x_icd10 where icd10 like 'J__.%') group by m_danganid, m_rizhiid) select nvl(da.kahao,'000000') P7502, nvl(to_char(a.songjianshijian, 'yyyy-mm-dd hh24:mi:ss'),'-') P7506, nvl(a.m_rizhiid,'-') P7000, nvl(a.xingming,'-') P4, '-' P7601, '-' P7602, '一般临床检验' P7603, '1' P7604, nvl(c.tiaoxingma,'-') P7605, '-' P7606, nvl(to_char(d.jianyandate, 'yyyy-mm-dd hh24:mi:ss'),'-') P7607, nvl(to_char(d.biaogaodate, 'yyyy-mm-dd hh24:mi:ss'),'-') P7608, nvl(to_char(d.sjdate, 'yyyy-mm-dd hh24:mi:ss'),'-') P7609, nvl(to_char(d.qsdate, 'yyyy-mm-dd hh24:mi:ss'),'-') P7610, nvl(d.biaobenhao,0) P7611,  nvl(bb.bbmingcheng,'-') P7612, nvl(a.xiangmuid,'-') P7613, nvl(x.mingcheng,'-') P7614, nvl(e.xiangmuid,'-') P7615, nvl(e.mingcheng,'-') P7616, nvl(ckz.xmckz,'-') P7617, nvl(ckz.ckzdw,'-') P7618, case when IsNumeric(mx.jcjieguo) < 1 then '-' else nvl(mx.jcjieguo,'-') end P7620, case when IsNumeric(mx.jcjieguo) > 0 then '-' else nvl(mx.jcjieguo,'-') end P7621, nvl(d.tiaoxingma,'-') P7622, nvl(e.xiangmuid,'-') P7623, nvl(e.mingcheng,'-') P7624, decode(GetJGZ_Gxgg(mx.JCJIEGUO, e.XIAXIAN, e.SHANGXIAN), 'H', '21', 'L', '22', '1') P7625 from m_lcfujian${param.year} a, l_hisjcsqd b, l_lisjcsqd c, l_jianyanshuju d，l_jianyanshuju_mx mx, l_xjcxiangmu       e, l_jianyanshuju_ckz ckz, m_dangan da, l_x_biaoben bb, x_fujianxiangmu x, z_dangan_lgsb      lgsb where a.fujianid = b.hissqid and b.hissqid = c.hissqid and c.lissqid = d.lissqid and d.jysjid = mx.jysjid and mx.xiangmuid = e.xiangmuid and mx.mxid = ckz.mxid and a.danganid = da.m_danganid and d.biaoben = bb.bbid and a.xiangmuid = x.xiangmuid and a.m_rizhiid = lgsb.m_rizhiid";
    @DataSource("slave1")
    @Select(mLab)
    List<Lab> mLab(@Param("param")QueryParam param);

}
