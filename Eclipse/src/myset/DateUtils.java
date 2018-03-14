package myset;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    private static SimpleDateFormat sf = null;
    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate(String type) {
        Date d = new Date();
        sf=new SimpleDateFormat(type);
        return sf.format(d);
    }
                                      
    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }
                         
//    只能返回距系统时间的差值
    /*将字符串转为时间戳*/
    public static long getStringToDate(String time,String type) {
        sf = new SimpleDateFormat(type);
        Date date = new Date();
        try{
            date = sf.parse(time);
        } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}