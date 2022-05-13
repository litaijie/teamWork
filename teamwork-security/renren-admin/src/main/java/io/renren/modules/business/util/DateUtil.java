package io.renren.modules.business.util;

import java.util.Calendar;

public class DateUtil {

    /**
     * 获取传过来日期的最后一天
     * @param yearMonth 需要传过来为“yyyy-MM”格式
     * @return 返回“yyyy-MM-dd”格式
     */
    public static String getMonthLastDay(String yearMonth){
        String[] split = yearMonth.split("-");
        if (split==null||split.length==0){
            return null;
        }
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,Integer.parseInt(split[0]));
        calendar.set(Calendar.MONTH,Integer.parseInt(split[1])-1);
        return new StringBuffer(yearMonth).append("-").append(calendar.getActualMaximum(Calendar.DATE)).toString();
    }
}
