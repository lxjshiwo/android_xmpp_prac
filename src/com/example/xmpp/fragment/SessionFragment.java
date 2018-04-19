/**
 * 
 */
package com.example.xmpp.fragment;

import com.example.xmpp.R;
import com.example.xmpp.activity.ChatActivity;
import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.fragment.ContactFragment.MyContentObserver;
import com.example.xmpp.provider.ContactProvider;
import com.example.xmpp.provider.SmsProvider;
import com.example.xmpp.service.IMService;
import com.example.xmpp.uitls.ThreadUtils;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
 * 会话的fragment
 *
 */
public class SessionFragment extends Fragment {
	
	
	

	private ListView mListView;
	private CursorAdapter mAdapter;

	/**
	 * 
	 */
	public SessionFragment() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_session,container,false);
		initView(view);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		initData();
		initListener();
		super.onActivityCreated(savedInstanceState);
	}
	
	
	/**
	 * 
	 */
	private void init() {
		// TODO Auto-generated method stub
		registerContentObserver();
		
	}

	/**
	 * 
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Cursor c = mAdapter.getCursor();
				c.moveToPosition(position);
				//拿到相应的id(账号) --> 发送消息的时候
				String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
				String nickname = getNickNameByAccount(account);
				
				Intent intent = new Intent(getActivity(),ChatActivity.class);
				intent.putExtra(ChatActivity.CLICKACCOUNT,account);
				intent.putExtra(ChatActivity.CLICKNICKNAME,nickname);
				
				startActivity(intent);
			}
		});
		
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unRegisterContentObserver();
		super.onDestroy();
	}
	

	/**
	 * 
	 */
	private void initData() {
		// TODO Auto-generated method stub
		setOrNotifyAdapter();
		
	}

	private void initView(View view) {
		mListView = (ListView) view.findViewById(R.id.listView);
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
				final Cursor c = getActivity().getContentResolver().query(SmsProvider.URI_SESSION,null,null,new String[]{
						IMService.mCurAccount,IMService.mCurAccount },null);
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
		
								View view = View.inflate(context,R.layout.item_session,null);
								return view;
							}
							
							//设置数据显示数据
							@Override
							public void bindView(View view, Context context, Cursor cursor) {
								ImageView ivHead = (ImageView) view.findViewById(R.id.head);
								TextView tvBody = (TextView) view.findViewById(R.id.body);
								TextView tvNickName = (TextView) view.findViewById(R.id.nickname);
								String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
								String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
								String nickName = getNickNameByAccount(account);
								//account但是在聊天记录表 (sms)中没有保存别名信息,只有(Contact表中含有)
								tvBody.setText(body);
								tvNickName.setText(nickName);
								
							}
						};
						
						mListView.setAdapter(mAdapter);
		
						
					}
				});
				
			}
		});

	}
	public String getNickNameByAccount(String account)
	{

		Cursor c = getActivity().getContentResolver().query(ContactProvider.URI_CONTACT,null,ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account}, null);
		String nickName = "";
		if(c.getCount() > 0)
		{
			//有数据
			c.moveToFirst();
			nickName = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
		}
		return nickName;
		
	}
	
	
	
	/*============= 监听数据库改变=====================*/
	MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());
	//注册监听
	public void registerContentObserver()
	{
		//content://xxx/contact
		//content://xxx/contact/i
		getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS,true,mMyContentObserver);
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
