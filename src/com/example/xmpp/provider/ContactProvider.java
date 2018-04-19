/**
 * 
 */
package com.example.xmpp.provider;

import com.example.xmpp.dbhelper.ContactOpenHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * @author Administrator
 *
 */
public class ContactProvider extends ContentProvider {
	
	
	//得到一个类的完整路径 主机地址常量
	public static final String AUTHORITIES = ContactProvider.class.getCanonicalName();
	static UriMatcher mUriMatcher ;
	//对应联系人表的Uri常量
	public static Uri URI_CONTACT = Uri.parse("content://"+AUTHORITIES+"/contact");
	private static final int CONTACT = 1;
	private ContactOpenHelper mHelper;
	//地址匹配对象创建
	static{
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//添加一个匹配的规则
		mUriMatcher.addURI(AUTHORITIES,"/contact",CONTACT);
		//content://ContactProvider/contact --> CONTACT
		
		
	}

	@Override
	public boolean onCreate() {
		mHelper = new ContactOpenHelper(getContext());
		if(mHelper != null)
		{
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*-------------------crud----------------------*/

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//数据存到sqlite -> 创建db文件，建立表 sqliteOpenHelper
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CONTACT:
			SQLiteDatabase db = mHelper.getWritableDatabase();
			//新插入的id
			long id = db.insert(ContactOpenHelper.T_CONTACT,"",values);
			if(id != -1)
			{
				System.out.println("----ContactProvider----insertSuccess-----");
				//拼结
				uri = ContentUris.withAppendedId(uri,id);
				//通知ContentObserver数据改变了
				getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);//为null表示所有都可以收到
//				getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,"指定只有某一个observer可收到");
			}
			break;

		default:
			break;
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int deleteCount = 0;
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CONTACT:
			SQLiteDatabase db = mHelper.getWritableDatabase();
			//影响的行数
			deleteCount = db.delete(ContactOpenHelper.T_CONTACT,selection,selectionArgs);
			if(deleteCount > 0)
			{
				System.out.println("----ContactProvider----deleteSuccess-----");
				getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);//为null表示所有都可以收到
			}
			break;

		default:
			break;
		}
		return deleteCount;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int updateCount = 0 ;
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CONTACT:
			SQLiteDatabase db = mHelper.getWritableDatabase();
			//更新的条目总数
			updateCount = db.update(ContactOpenHelper.T_CONTACT, values, selection, selectionArgs);
			if(updateCount>0)
			{
				System.out.println("----ContactProvider----updateSuccess-----");
				getContext().getContentResolver().notifyChange(ContactProvider.URI_CONTACT,null);//为null表示所有都可以收到
			}
			break;

		default:
			break;
		}
		return updateCount;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		int code = mUriMatcher.match(uri);
		switch (code) {
		case CONTACT:
			SQLiteDatabase db = mHelper.getWritableDatabase();
			cursor = db.query(ContactOpenHelper.T_CONTACT,projection, selection, selectionArgs,null,null,sortOrder);
			System.out.println("----ContactProvider----querySuccess-----");
			break;

		default:
			break;
		}

		return cursor;
	}

}
