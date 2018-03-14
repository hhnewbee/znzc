package myset;

import com.gizwits.opensource.appkit.GosApplication;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Switch;

public class MyDialog {
	private static String ifFO;
	private static Switch timeSet;
	public static void dialog(Switch timeSet1,String ifFO1) {
		ifFO=ifFO1;
		timeSet=timeSet1;
		AlertDialog.Builder builder = new Builder(GosApplication.mainc);
		 builder.setMessage("确认是否重置时间");
		 builder.setPositiveButton("确定",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				timeSet.performClick();
				if(ifFO.equals("Time_On")){
					AppConstants.open.getTime();
				}else{
					AppConstants.close.getTime();
				}
			}
		});
		builder.setNegativeButton("取消",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
