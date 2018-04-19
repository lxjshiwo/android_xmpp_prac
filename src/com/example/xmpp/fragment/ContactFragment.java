/**
 * 
 */
package com.example.xmpp.fragment;

import java.util.Collection;


import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import com.example.xmpp.R;
import com.example.xmpp.activity.ChatActivity;
import com.example.xmpp.activity.LoginAcitvity;
import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.provider.ContactProvider;
import com.example.xmpp.service.IMService;
import com.example.xmpp.uitls.PinyinUtils;
import com.example.xmpp.uitls.ThreadUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Administrator
 * 联系人的fragment
 */
public class ContactFragment extends Fragment {
	
	private ListView mListView;
	private CursorAdapter mAdapter;
	/**
	 * 
	 */
	public ContactFragment() {
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		init();
		super.onCreate(savedInstanceState);
	}
	

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub;
		View view = inflater.inflate(R.layout.fragment_contacts,container,false);
		initView(view);
		return view;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		initData();
		initListener();
		super.onActivityCreated(savedInstanceState);
	}

	private void init() {
		registerContentObserver();
		
	}

	private void initView(View view) {
		mListView = (ListView) view.findViewById(R.id.listView);
		
	}

	private void initListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor c = mAdapter.getCursor();
				System.out.println(c);
				System.out.println(position);
				c.moveToPosition(position);
				//拿到jid(账号)--> 发送消息的时候需要
				String account = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
				//拿到nickname --> 发送消息的时候需要
				String nickname = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));

				Intent intent = new Intent(getActivity(),ChatActivity.class);
				intent.putExtra(ChatActivity.CLICKACCOUNT,account);
				intent.putExtra(ChatActivity.CLICKNICKNAME,nickname);
				startActivity(intent);

				
			}
		});
		
	}

	private void initData() 
	{
		//开启进程,同步花名册
		setOrNotifyAdapter();
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		//按照常例 我们Fragement销毁了就不应该继续监听
		//但是实际需要一直监听Roster的该改变所以，我们将联系人的监听同步操作放置于service中
		unRegisterContentObserver();
		//移除RosterListener
	
		super.onDestroy();
	}

	/**
	 * 设置或者更新adapter
	 */
	private void setOrNotifyAdapter() 
	{
		//判断Adapter是否存在
		if(mAdapter != null)
		{
			//刷新Adapter
			mAdapter.getCursor().requery();

			return;
		}
		ThreadUtils.runInThread(new Runnable() {
			
			@Override
			public void run() {
				//查询记录 
				final Cursor c = getActivity().getContentResolver().query(ContactProvider.URI_CONTACT,null,null,null,null);
				if(c.getCount() <= 0)
				{
					return;
				}
				//设置adapter，显示数据
				ThreadUtils.runInUIThread(new Runnable() 
				{
					
					@Override
					public void run() {
						/*
						 * 
						 * BaseAdapter -> getView
						 * 		|-CursorAdapter
						 */
						mAdapter = new CursorAdapter(getActivity(),c) {
							
							//如果ConvertView == null时候返回一个具体的根视图
							@Override
							public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
								View view = View.inflate(context,R.layout.item_contact,null);
								return view;
							}
							
							//设置数据显示数据
							@Override
							public void bindView(View view, Context context, Cursor cursor) {
								ImageView ivHead = (ImageView) view.findViewById(R.id.head);
								TextView tvAccount = (TextView) view.findViewById(R.id.account);
								TextView tvNickName = (TextView) view.findViewById(R.id.nickname);
		
								String account = cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
								String nickName = cursor.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
		
								tvAccount.setText(account);
								tvNickName.setText(nickName);
								
							}
						};
						
						mListView.setAdapter(mAdapter);
		
						
					}
				});
				
			}
		});

	}
	
	
	/*============= 监听数据库改变=====================*/
	MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());
	//注册监听
	public void registerContentObserver()
	{
		//content://xxx/contact
		//content://xxx/contact/i
		getActivity().getContentResolver().registerContentObserver(ContactProvider.URI_CONTACT, true,mMyContentObserver);
	}
	//反注册监听
	
	public void unRegisterContentObserver()
	{
		getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
	}

	
	class MyContentObserver extends ContentObserver
	{
	

		/**
		 * @param handler
		 */
		public MyContentObserver(Handler handler) {
			super(handler);
		}
		
		/* 
		 * 如果数据库数据改变会在这个方法收到通知 
		 * 
		 * */
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			//更新adapter
			setOrNotifyAdapter();
		}
		
	}



}
