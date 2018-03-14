package myset;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.opensource.appkit.GosApplication;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Timing {

    TextView showTime = null;
    private Switch timeSet;
    private GizWifiDevice mDevice;
    String houre;
    String minute;
//  判断是关还是开
    private String ifOF[];
//  repect
    private TextView showRepetiontext;

    public void initEvent(Button setTime,TextView showTime1,Switch timeSet1, GizWifiDevice mDevice1,String ifOF1[],TextView showRepetiontext1) {
    	this.showTime=showTime1;
    	this.timeSet=timeSet1;
    	this.mDevice=mDevice1;
    	this.ifOF=ifOF1;
    	this.showRepetiontext=showRepetiontext1;
    	
//    	设置时间
    	setTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(timeSet.isChecked()){
					MyDialog.dialog(timeSet,ifOF[0]);
					return;
				}else{
					getTime();
				}
			}
		});
    	
//    	开启倒计时
    	this.timeSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(timeSet.isChecked()){
					String a=showTime.getText().toString();
					Log.d("ddd",showTime.getText().toString());
					if(showTime.getText().toString().equals("")){
						Toast.makeText(GosApplication.mianl,"请设置时间",Toast.LENGTH_SHORT).show();
						timeSet.performClick();
						return;
					}if(lanchTimePrevent()){
						Toast.makeText(GosApplication.mianl,"已过时，请重置",Toast.LENGTH_SHORT).show();
						timeSet.performClick();
						return;
					}
					showTime.setTextColor(Color.GREEN);
//					记录数据
//					 storeData(String ifOF);
//					发送数据
//					发送的数据不要类型错误和大小不符合，不然发送错误
					if("".equals(showRepetiontext.getText().toString())){
						SendCommand.send(mDevice,ifOF, new Object[]{true,setTime(),0},3);
					}else if("执行一次".equals(showRepetiontext.getText().toString())){
						SendCommand.send(mDevice,ifOF, new Object[]{true,setTime(),0},3);
					}else{
						SendCommand.send(mDevice,ifOF, new Object[]{true,setTime(),1},3);
					}
					storeData("1");
				}else{
					if("".equals(showRepetiontext.getText().toString())){
						SendCommand.send(mDevice,ifOF, new Object[]{false,0},2);
					}else if("执行一次".equals(showRepetiontext.getText().toString())){
						SendCommand.send(mDevice,ifOF, new Object[]{false,0},2);
					}else{
						SendCommand.send(mDevice,ifOF, new Object[]{false,1},2);
					}
//					SendCommand.send(mDevice,ifOF, new Object[]{false},1);
					showTime.setTextColor(Color.RED);
					storeData("0");
				}
			}
		});
    }
//    日期防止
//   private boolean datePrevent(){
//	   String time=year+"/"+month+"/"+day;
////	   long ifTime=DateUtils.getStringToDate(time,"yyyy/MM/dd")-DateUtils.getStringToDate(DateUtils.getCurrentDate("yyyy/MM/dd"),"yyyy/MM/dd");
//	   long timet02=DateUtils.getStringToDate(time,"yyyy/MM/dd");
//	   long timet03=DateUtils.getStringToDate(DateUtils.getCurrentDate("yyyy/MM/dd"),"yyyy/MM/dd");
//	   time04=timet02-timet03;
//	   if(time04<0){
//		   return true;
//	   }else{
//		   return false;
//	   }
//   }
//   设置时时间防止
   private boolean setTimePrevent(){
	   String time=houre+":"+minute;
	   long timet02=DateUtils.getStringToDate(time,"HH:mm");
	   long timet03=DateUtils.getStringToDate(DateUtils.getCurrentDate("HH:mm"),"HH:mm");
	   if(timet02-timet03<=0){
		     return true;
	   }else{
		   return false;
	   }
   }
//   启动时时间防止
   private boolean lanchTimePrevent(){
	   String time=showTime.getText().toString();
	   long timet02=DateUtils.getStringToDate(time,"HH:mm");
	   long timet03=DateUtils.getStringToDate(DateUtils.getCurrentDate("HH:mm"),"HH:mm");
	   if(timet02-timet03<=0){
		     return true;
	   }else{
		   return false;
	   }
   }
    //数据处理
    private int setTime(){
    	String setTime=showTime.getText().toString();
    	//经过计算后发送的
//    	long time1=DateUtils.getStringToDate(setTime,"HH:mm")-DateUtils.getStringToDate(DateUtils.getCurrentDate("HH:mm"),"HH:mm");
//    	直接发送时间
//    	long time1=DateUtils.getStringToDate(setTime,"hh:mm");
//    	long time2=StringUtils.timingTimechange(time1);
//    	暴力修改时间
    	int time=StringUtils.timingMinuteChange(setTime);
		return time;
    }

    // 点击事件,获取日期
//    public void getDate() {
//
//        DatePickerDialog dateDialog=new DatePickerDialog(GosApplication.mainc, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                    int dayOfMonth) {
//                Timing.this.year = year;
//                month = monthOfYear+1;
//                day = dayOfMonth;
//            }
//        }, setDefaultTime("yyyy"),setDefaultTime("MM")-1,setDefaultTime("dd"));
//        
//        dateDialog.show();
//        
//        dateDialog.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				// TODO Auto-generated method stub
//				if(datePrevent()){
//					Toast.makeText(GosApplication.mianl,"已过时，请重设",Toast.LENGTH_SHORT).show();
//					getDate();
//				}
//			}
//		});
//        
//    }

    // 点击事件,获取日期
    public void getTime() {
    	TimePickerDialog timeDialog=new TimePickerDialog(GosApplication.mainc, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute1) {
            	if(hourOfDay<10){
            		houre = "0"+hourOfDay;
            	}else{
            		houre = hourOfDay+"";
            	}
            	if(minute1<10){
            		minute = "0"+minute1;
            	}else{
            		minute = minute1+"";
            	}
                showTime();
            }
        },setDefaultTime("HH"),setDefaultTime("mm"), true);
    	timeDialog.show();
    	
    	timeDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				   if(setTimePrevent()){
	                	Toast.makeText(GosApplication.mianl,"已过时，请重设",Toast.LENGTH_SHORT).show();
	                	getTime();
	              }
			}
		});
    	
    }

    // 显示选择日期
    private void showTime() {
        showTime.setText(houre+":"+minute);
    }
    
//  设置默认的日期和时间
    private int setDefaultTime(String type){
    	return Integer.parseInt(DateUtils.getCurrentDate(type));
    }
//   数据存储
    private void storeData(String ifOnOff){
    	TimingData  timingData = new  TimingData();
    	ContentValues cv = new ContentValues();
    	if(ifOF[0].equals("Time_On")){
    		timingData.OnOff = "on";
    	}else{
    		timingData.OnOff = "off";
    	}
    	timingData.ifWork = ifOnOff;
    	if("1".equals(ifOnOff)){
    		timingData.time=showTime.getText().toString();
    		cv.put("time", timingData.time);
    	}
    	cv.put("OnOff", timingData.OnOff);
        cv.put("ifWork", timingData.ifWork);
        DBManager.getinstance().initDb().updateTimingData(cv);
    }
}