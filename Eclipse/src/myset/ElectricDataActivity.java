package myset;

import java.util.List;
import java.util.regex.Pattern;

import com.gizwits.opensource.appkit.GosApplication;
import com.gizwits.opensource.appkit.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983

public class ElectricDataActivity extends Activity
{
    private LinearLayout layout;
    private LayoutInflater inflater;
    private EditText editNum;
    private String numberRegEx = "\\d+";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
        initViewDb();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DBManager.getinstance().initDb().closeDB();// 释放数据库资源
    }
    
    public void initViewDb(){
    	  layout=(LinearLayout) findViewById(R.id.managelayout);
          inflater = LayoutInflater.from(this); 
          editNum=(EditText) findViewById(R.id.editNum);
    }
    public void flash(View view)
    {	
    	String numStr=editNum.getText().toString();
    	if(!Pattern.compile(numberRegEx).matcher(numStr).matches()){
			Toast.makeText(GosApplication.mianl,"请输入数字！",Toast.LENGTH_SHORT).show(); 
    		return;
    	}
    	int num=Integer.parseInt(editNum.getText().toString());
    	flashView();
    	String name=null;
        List< ELectricUseData>  ELectricUseDatas = DBManager.getinstance().initDb().query(num);
        for (int i= ELectricUseDatas.size()-1;i>=0;i--)
        {
        	 ELectricUseData  ELectricUseData= ELectricUseDatas.get(i);
        	 if(ELectricUseData.name.equals("产品名")){
        		 name=ELectricUseData._id;
        		 Log.i("name", ELectricUseData._id);
        	 }else{
        		 name=ELectricUseData.name;
        	 }
        	 initDataItem( name, ELectricUseData.timeStart, ELectricUseData.timeOver, ELectricUseDatas.get(i).timeUse, ELectricUseData.electricQuantity);
        }
    }
    public void initDataItem(String name,String timeStart,String timeOver,String timeUse,String electricQuantity){
		LinearLayout dataitem=(LinearLayout) inflater.inflate(R.layout.dataitem,null);
		((TextView)dataitem.findViewById(R.id.text1)).setText(name);
		((TextView)dataitem.findViewById(R.id.text2)).setText(timeStart);
		((TextView)dataitem.findViewById(R.id.text3)).setText(timeOver);
		((TextView)dataitem.findViewById(R.id.text4)).setText(timeUse);
		((TextView)dataitem.findViewById(R.id.text5)).setText(electricQuantity);
		layout.addView(dataitem);
    }
    public void flashView(){
    	layout.removeAllViews();
    }
}
