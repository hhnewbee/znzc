package myset;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983

public class ElectricDataShow extends Activity
{
    private String timeAdd;
    private static ElectricDataShow eds;
    private ElectricDataShow(){};
    public static ElectricDataShow getinstance(){
    	if(eds==null){
    		eds=new ElectricDataShow();
    	}
		return eds;
    }
    @SuppressLint("SimpleDateFormat")
   	private String getTime(){
   	   SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");       
          Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
          String str = formatter.format(curDate);
          return str; 
       }
    
    public void add(String name)
    {
    	timeAdd=getTime();
        ArrayList< ELectricUseData>  ELectricUseDatas = new ArrayList< ELectricUseData>();
	     ELectricUseData  ELectricUseData1 = new  ELectricUseData(name, getTime(), "--","--","--");
	     ELectricUseDatas.add( ELectricUseData1);
        DBManager.getinstance().initDb().add( ELectricUseDatas);
    }

    public void update(String name,String electricNum)
    {
    	 if(timeAdd==null){
    		return;
    	 }
         ELectricUseData  ELectricUseData = new  ELectricUseData();
         if(!name.equals("产品名")){
        	  ELectricUseData.name=name;
         }else{
        	 ELectricUseData.name=AppConstants.productName;
         }
         ELectricUseData.timeStart = timeAdd;
         ELectricUseData.timeOver= getTime();
         ELectricUseData.timeUse=StringUtils.timechange(getTime(),timeAdd);
         ELectricUseData.electricQuantity=electricNum;
         DBManager.getinstance().initDb().updateAge( ELectricUseData);
         AppConstants.productName="产品名";
         timeAdd=null;
    }
}
