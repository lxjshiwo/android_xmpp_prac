/**
 * 
 */
package com.example.xmpp.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @author Administrator
 *
 */
public class ContactOpenHelper extends SQLiteOpenHelper {
	
	public static final String T_CONTACT = "t_contact";
	
	
	public class ContactTable implements BaseColumns
	{
		//默认会给我们添加一列 _id,并且键自增
		/*
		 *   1._id:主键
         *   2.account:账号
         *   3.nickname:昵称
         *   4.avatar:头像
         *   5.pinyin:账号拼音
		 * 
		 */
		public static final String ACCOUNT = "account";//账号
		public static final String NICKNAME = "nickname";//昵称
		public static final String AVATAR = "avatar";//头像
		public static final String PINYIN = "pinyin";//账号拼音


		

	}
	
	

	public ContactOpenHelper(Context context) {
		super(context,"contact.db",null,1);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "+T_CONTACT +"(_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
						ContactTable.ACCOUNT + " TEXT, " +
						ContactTable.NICKNAME + " TEXT, " +
						ContactTable.AVATAR + " TEXT, " +
						ContactTable.PINYIN + " TEXT);";//-->表结构
		db.execSQL(sql);

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
