/**
 * 
 */
package com.example.xmpp;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.provider.ContactProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

/**
 * @author Administrator
 *
 */
public class TestContactProvider extends AndroidTestCase{

	
	public void testInsert()
	{

		ContentValues values = new ContentValues();
		values.put(ContactOpenHelper.ContactTable.ACCOUNT,"billy@example.com");
		values.put(ContactOpenHelper.ContactTable.NICKNAME,"老五");
		values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
		values.put(ContactOpenHelper.ContactTable.PINYIN,"laowu");
		getContext().getContentResolver().insert(ContactProvider.URI_CONTACT, values);
	}

	public void testDelete()
	{
		getContext().getContentResolver().delete(ContactProvider.URI_CONTACT,ContactOpenHelper.ContactTable.ACCOUNT+"=?", new String[]{"billy@example.com"});
		
	}
	public void testUpdate()
	{
		ContentValues values = new ContentValues();
		values.put(ContactOpenHelper.ContactTable.ACCOUNT,"billy@example.com");
		values.put(ContactOpenHelper.ContactTable.NICKNAME,"我是老五");
		values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
		values.put(ContactOpenHelper.ContactTable.PINYIN,"woshilaowu");
		getContext().getContentResolver().update(ContactProvider.URI_CONTACT, values,ContactOpenHelper.ContactTable.ACCOUNT + "=?",new String[]{"billy@example.com"});
		
	}
	public void testQuery()
	{
		Cursor c = getContext().getContentResolver().query(ContactProvider.URI_CONTACT,null,null,null,null);
		int columnCount = c.getColumnCount();//一共有多少列
		//获得所有的列的数目 
		System.out.println(columnCount);
		while(c.moveToNext())
		{
			//循环打印列
			for(int i = 0;i < columnCount;i++)
			{
				System.out.println(c.getString(i)+ " ");
			}
			System.out.println("");
			
			
		}
	}
	
	public void testPinyin(){
//		String pinyinString =  PinyinHelper.convertToPinyinString("内容","分割符",拼音的格式);
		String pinyinString =  PinyinHelper.convertToPinyinString("黑马程序员","",PinyinFormat.WITHOUT_TONE);
		System.out.println(pinyinString);
	}


}
