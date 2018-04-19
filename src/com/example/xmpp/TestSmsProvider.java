/**
 * 
 */
package com.example.xmpp;

import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.provider.SmsProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.Telephony.Sms;
import android.test.AndroidTestCase;

/**
 * @author Administrator
 *
 */
public class TestSmsProvider extends AndroidTestCase{
	public void testInsert()
	{
		ContentValues values = new ContentValues();
		values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,"billy@myopenfire");
		values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,"cang@myopenfire");
		values.put(SmsOpenHelper.SmsTable.BODY,"今晚约吗?");
		values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
		values.put(SmsOpenHelper.SmsTable.TYPE,"chat");
		values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
		values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,"cang@myopenfire");
		getContext().getContentResolver().insert(SmsProvider.URI_SMS,values);
		
	}

	public void testDelete()
	{
		getContext().getContentResolver().delete(SmsProvider.URI_SMS,SmsOpenHelper.SmsTable.FROM_ACCOUNT+"=?",new String[]{"billy@myopenfire"});
		
	}

	public void testUpdate()
	{
		ContentValues values = new ContentValues();
		values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,"billy@myopenfire");
		values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,"cang@myopenfire");
		values.put(SmsOpenHelper.SmsTable.BODY,"今晚约吗?我好久没有见到你");
		values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
		values.put(SmsOpenHelper.SmsTable.TYPE,"chat");
		values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
		values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,"cang@myopenfire");
		getContext().getContentResolver().update(SmsProvider.URI_SMS,values,SmsOpenHelper.SmsTable.FROM_ACCOUNT+"=?",new String[]{"billy@myopenfire"});
		
	}
	
	public void testQuery()
	{
		
		Cursor c = getContext().getContentResolver().query(SmsProvider.URI_SMS,null,null,null,null);
		//得到所有的列
		int columnCount = c.getColumnCount();
		while(c.moveToNext())
		{
			for(int i = 0;i< columnCount;i++)
			{
				System.out.println(c.getString(i)+" ");
			}
			System.out.println("  ");
		}
		
	}
}
