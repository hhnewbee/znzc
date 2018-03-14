package myset;

public class StringUtils {
	public static String timechange(String N,String O){
		long time1=(DateUtils.getStringToDate(N,"yyyy/MM/dd HH:mm:ss")-DateUtils.getStringToDate(O,"yyyy/MM/dd HH:mm:ss"));
		String time2=String.valueOf(time1);
		String time3=time2.substring(0,time2.length()-3);
		int time4=Integer.parseInt(time3);
		String time5=null;
		if(time4>=60&&time4<3600){
			time5=String.valueOf(time4/60)+"分"+String.valueOf(time4%60)+"秒";
		}else if(time4>=3600){
			time5=String.valueOf(time4/3600)+"时"+String.valueOf(time4%3600/60)+"分"+String.valueOf(time4%3600%60)+"秒";
		}else{
			time5=String.valueOf(time4)+"秒";
		}
		return time5;
	}
	
	public static long timingTimechange(long time){
		String time2=String.valueOf(time);
		String time3=time2.substring(0,time2.length()-3);
		long time4=(Long.parseLong(time3))/60;
		return time4;
	}
	
	public static int timingMinuteChange(String time){
		String time1[]=time.split(":");
		int time2=Integer.parseInt(time1[0])*60+Integer.parseInt(time1[1]);
		return time2;
	}
}
