package myset;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager
{
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    public static DBManager dbm;
    public static DBManager getinstance(){
    	if(dbm==null){
    		dbm=new DBManager();
    	}
		return dbm;
    }
    private DBManager()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> Constructor");
        helper = DatabaseHelper.getinstance();
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    }

    public  DBManager initDb(){
    	db = helper.getWritableDatabase();
		return dbm;
    }
    /**
     * add ELectricUseDatas
     * 
     * @param ELectricUseDatas
     */
    public void add(List<ELectricUseData> ELectricUseDatas)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> add");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (ELectricUseData ELectricUseData : ELectricUseDatas)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(null, ?, ?, ?, ?, ?)", new Object[] { ELectricUseData.name,
                        ELectricUseData.timeStart, ELectricUseData.timeOver ,ELectricUseData.timeUse,ELectricUseData.electricQuantity});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    
    public void addTimingData(List<TimingData> timingDatas)
    {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (TimingData timingData : timingDatas)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME_TIMING
                        + " VALUES(null, ?, ?, ?, ?)", new Object[] { 
                        		timingData.OnOff,
                        		timingData.time, 
                        		timingData.repetition,
                        		timingData.ifWork
                 });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * update ELectricUseData's age
     * 
     * @param ELectricUseData
     */
    public void updateAge(ELectricUseData ELectricUseData)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> updateAge");
        ContentValues cv = new ContentValues();
        cv.put("timeOver", ELectricUseData.timeOver);
        cv.put("timeUse", ELectricUseData.timeUse);
        cv.put("electricQuantity", ELectricUseData.electricQuantity);
        cv.put("name", ELectricUseData.name);
        db.update(DatabaseHelper.TABLE_NAME, cv, "timeStart = ?",
                new String[] { ELectricUseData.timeStart });
    }
    
    public void updateTimingData( ContentValues cv)
    {
        db.update(DatabaseHelper.TABLE_NAME_TIMING, cv, "OnOff = ?",
                new String[] { (String) cv.get("OnOff") });
    }

    /**
     * delete old ELectricUseData
     * 
     * @param ELectricUseData
     */
//    public void deleteOldELectricUseData(ELectricUseData ELectricUseData)
//    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteOldELectricUseData");
//        db.delete(DatabaseHelper.TABLE_NAME, "age >= ?",
//                new String[] { String.valueOf(ELectricUseData.timeStart) });
//    }

    /**
     * query all ELectricUseDatas, return list
     * 
     * @return List<ELectricUseData>
     */
    public List<ELectricUseData> query(int num)
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<ELectricUseData> ELectricUseDatas = new ArrayList<ELectricUseData>();
        Cursor c = queryTheCursor();
        int i=num;
        while (c.moveToNext())
        {
        	i--;
            ELectricUseData ELectricUseData = new ELectricUseData();
            ELectricUseData._id = c.getString(c.getColumnIndex("_id"));
            ELectricUseData.name = c.getString(c.getColumnIndex("name"));
            ELectricUseData.timeStart = c.getString(c.getColumnIndex("timeStart"));
            ELectricUseData.timeOver = c.getString(c.getColumnIndex("timeOver"));
            ELectricUseData.timeUse = c.getString(c.getColumnIndex("timeUse"));
            ELectricUseData.electricQuantity=c.getString(c.getColumnIndex("electricQuantity"));
            ELectricUseDatas.add(ELectricUseData);
            if(i<=0){
            	break;
            }
        }
        c.close();
        return ELectricUseDatas;
    }
    
    public List<TimingData> queryTimingData()
    {
        ArrayList<TimingData> timingDatas = new ArrayList<TimingData>();
        Cursor c = queryTheCursorTimingData();
        if(c==null){
        	return null;
        }
        while (c.moveToNext())
        {
        	TimingData timingData = new TimingData();
        	timingData.OnOff = c.getString(c.getColumnIndex("OnOff"));
        	timingData.time = c.getString(c.getColumnIndex("time"));
        	timingData.repetition = c.getString(c.getColumnIndex("repetition"));
        	timingData.ifWork = c.getString(c.getColumnIndex("ifWork"));
        	timingDatas.add(timingData);
        }
        c.close();
        return timingDatas;
    }
    /**
     * query all ELectricUseDatas, return cursor
     * 
     * @return Cursor
     */
    public Cursor queryTheCursor()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
                null);
        return c;
    }
    
    public Cursor queryTheCursorTimingData()
    {
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME_TIMING,null);
        return c;
    }


    /**
     * close database
     */
    public void closeDB()
    {
        Log.d(AppConstants.LOG_TAG, "DBManager --> closeDB");
        // 释放数据库资源
        if(db!=null&&db.isOpen())
        db.close();
    }

}