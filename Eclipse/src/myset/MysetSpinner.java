package myset;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import android.content.ContentValues;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class MysetSpinner {  
	private String data;
	private String sfg;
	private GizWifiDevice mDevice;
	private TextView spinnerText;
	private boolean ifOnOff;
	private boolean stopFirst=false;
//	private Spinner spinner;
	
	public void setPinner(Spinner spinner1,TextView spinnnerText1,GizWifiDevice mDevice1,boolean ifOnOff1,String sfg1){
		this.mDevice=mDevice1;
		this.spinnerText=spinnnerText1;
		this.ifOnOff=ifOnOff1;
		this.sfg=sfg1;
//		this.spinner=spinner1;
	        //注册事件  
	        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){  
	            @Override  
	            public void onItemSelected(AdapterView<?> parent, View view,  
	                    int position, long id) {  
	            	if(!stopFirst){
	            		stopFirst=true;
	            	}else{
	            		Spinner spinner=(Spinner) parent;  
	            		data=(String) spinner.getItemAtPosition(position);
		            	spinnerText.setText(data);
		                if(data.equals("每天执行")){
		                	SendCommand.send(mDevice,new String[]{sfg},new Object[]{true},1);
		                }else{
		                	SendCommand.send(mDevice,new String[]{sfg},new Object[]{false},1);
		                }
		                storeData();
	            	}
	            }  
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
				}
	        });  
	        
	}  
	
	private void storeData(){
	    	TimingData  timingData = new  TimingData();
	    	ContentValues cv = new ContentValues();
	    	timingData.repetition=spinnerText.getText().toString();
	    	if(ifOnOff){
	    		timingData.OnOff = "on";
	    	}else{
	    		timingData.OnOff = "off";
	    	}
	    	cv.put("OnOff", timingData.OnOff);
	        cv.put("repetition", timingData.repetition);
	        DBManager.getinstance().initDb().updateTimingData(cv);
	    }
}
