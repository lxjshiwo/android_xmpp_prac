/**
 * 
 */
package com.example.xmpp.activity;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.packet.Message;

import com.example.xmpp.R;
import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.provider.SmsProvider;
import com.example.xmpp.service.IMService;
import com.example.xmpp.service.IMService.MyBinder;
import com.example.xmpp.uitls.ThreadUtils;
import com.example.xmpp.uitls.ToastUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.CursorAdapter;
import android.telephony.gsm.SmsMessage.MessageClass;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class ChatActivity extends Activity {
	public static final String CLICKACCOUNT = "clickAccount";
	public static final String CLICKNICKNAME = "clickNickName";
	private String mClickName;
	private String mClickAccount;
	private TextView mTitle;
	private ListView mListView;
	private EditText mEtBody;
	private Button mBtnSend;
	private CursorAdapter mAdapter;
	
	
	private IMService mImService;

	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		init();
		initView();
		initData();
		initListener();
	}

	/**
	 * 
	 */
	private void initListener() {
		mBtnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				send();
			}

		});
		
	}
	/**
	 * 
	 */
	public void send() {
		ThreadUtils.runInThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String body = mEtBody.getText().toString();
				//3.初始化了一个消息
				Message msg = new Message();
				msg.setFrom(IMService.mCurAccount);//当前登录用户
				msg.setTo(mClickAccount);
				msg.setBody(body);//输入框内的内容
				msg.setType(Message.Type.chat);//内容就是聊天chat
				msg.setProperty("key", "value");//额外属性 -->这里使用不了
				//TODO 调用服务中的sendMessage这个方法
				mImService.sendMessage(msg);

				
				
				ThreadUtils.runInUIThread(new Runnable() {
					
					@Override
					public void run() {
						mEtBody.setText("");
						
					}
				});

		

				
			}
		});
		//清空输入框


	}
	
	
	



	/**
	 * 
	 */
	private void initData() {
		setAdapterOrNotify();

		
	}

	/**
	 * 
	 */
	private void setAdapterOrNotify() {
		//1.首先判断是否存在adapter
		if(mAdapter != null)
		{
			//刷新操作
			Cursor cursor = mAdapter.getCursor();
			cursor.requery();
			
			
			//设置自动选择当前列
			mListView.setSelection(cursor.getCount()-1);
			return;
			
		}
		
		
		ThreadUtils.runInThread(new Runnable() {
			
			@Override
			public void run() { 
				final Cursor c = getContentResolver().query(SmsProvider.URI_SMS,
					null,
					"(from_account = ? and to_account = ?) or (from_account = ? and to_account = ?)",//where条件
					new String[]{ IMService.mCurAccount,mClickAccount,mClickAccount,IMService.mCurAccount},//where条件参数 
					SmsOpenHelper.SmsTable.TIME + " ASC");//根据时间升序
			//asc 升序 des降序

				//如果没有数据直接返回
				if(c.getCount() < 1)
				{
					return;
				}
				ThreadUtils.runInUIThread(new Runnable() {
					@Override
					public void run() {
						//cursorAdapter :getView --> newView() --> bindView()
							mAdapter= new CursorAdapter(ChatActivity.this,c) {
							public static final int RECEIVE = 1;
							public static final int SEND = 0;
					
							//如果convertView = null 会调用 --> 返回根布局
//							@Override
//							public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
//								TextView tv = new TextView(arg0);
//								return tv;
//							}
//							
//							@Override
//							public void bindView(View arg0, Context arg1, Cursor arg2) {
//								//具体设置数据
//								TextView tv = (TextView)arg0;
//								String body = arg2.getString(arg2.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
//								tv.setText(body);
//
//								
//							}
							/* (non-Javadoc)
							 * @see android.support.v4.widget.CursorAdapter#getView(int, android.view.View, android.view.ViewGroup)
							 */
							/* (non-Javadoc)
							 * @see android.widget.BaseAdapter#getItemViewType(int)
							 */
							@Override
							public int getItemViewType(int position) {
								c.moveToPosition(position);
								//取出消息创建者
								String fromAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
								if(IMService.mCurAccount.equals(fromAccount))
								{
									//接收
									return SEND;
								}
								else
								{
									//发送
									return RECEIVE;
								}
								//两种情况
								//接收 --> 如果当前账号    	不等于      消息创建者 
								//发送
//								return super.getItemViewType(position);//0 1之间
							}
							
							/* (non-Javadoc)
							 * @see android.widget.BaseAdapter#getViewTypeCount()
							 */
							@Override
							public int getViewTypeCount() {
								//将相应Activity内的有两种view控件 
								return super.getViewTypeCount()+1;//2
							}
							
							
							@Override
							public View getView(int arg0, View arg1,
									ViewGroup arg2) {
								ViewHolder holder;
								if(getItemViewType(arg0) == RECEIVE)
								{//接收
									if(arg1 == null)
									{
										arg1 = View.inflate(ChatActivity.this, R.layout.item_chat_receive, null);
										holder = new ViewHolder();
										arg1.setTag(holder);
										//holder 赋值
										holder.time = (TextView) arg1.findViewById(R.id.time);
										holder.body = (TextView) arg1.findViewById(R.id.content);
										holder.head = (ImageView) arg1.findViewById(R.id.head);
									}
									else
									{
										holder = (ViewHolder) arg1.getTag();
									} 
									
									//得到数据展示数据
								}
								else
								{//发送
									if(arg1 == null)
									{
										arg1 = View.inflate(ChatActivity.this, R.layout.item_chat_send, null);
										holder = new ViewHolder();
										arg1.setTag(holder);
										//holder 赋值
										holder.time = (TextView) arg1.findViewById(R.id.time);
										holder.body = (TextView) arg1.findViewById(R.id.content);
										holder.head = (ImageView) arg1.findViewById(R.id.head);
									}
									else
									{
										holder = (ViewHolder) arg1.getTag();
									} 
									//得到数据展示数据

									
								}
								//得到数据
								//有相同的holder直接精简代码
								c.moveToPosition(arg0);
								String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
								String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
								
								String formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(time)));
								holder.time.setText(time);
								holder.body.setText(body);
								return super.getView(arg0, arg1, arg2);
							}

							@Override
							public void bindView(View arg0, Context arg1,
									Cursor arg2) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public View newView(Context arg0, Cursor arg1,
									ViewGroup arg2) {
								// TODO Auto-generated method stub
								return null;
							}
							
							
							class ViewHolder
							{
								TextView body;
								TextView time;
								ImageView head;
							}

						};
						

						mListView.setAdapter(mAdapter);
						//滚动到最后一行
						mListView.setSelection(mAdapter.getCount()-1);
					}
				});
				
			}
		});
	}

	/**
	 * 
	 */
	private void initView() {
		mTitle = (TextView) findViewById(R.id.title);
		mListView = (ListView) findViewById(R.id.listView);
		mEtBody = (EditText) findViewById(R.id.et_body);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		
		mTitle.setText("与"+mClickName +"聊天中");

		
	}

	/**
	 * 
	 */
	private void init() {
		registerContentObserver();
		//绑定服务
		Intent service = new Intent(ChatActivity.this,IMService.class);
		bindService(service,mMyServiceConnection,BIND_AUTO_CREATE);

		mClickAccount = getIntent().getStringExtra(ChatActivity.CLICKACCOUNT);
		mClickName = getIntent().getStringExtra(ChatActivity.CLICKNICKNAME);

	}
	MyServiceConnection mMyServiceConnection = new MyServiceConnection(); 
	
	@Override
	protected void onDestroy() {
		unRegisterContentObserver();
		//接绑服务
		if(mMyServiceConnection != null)
		{
			unbindService(mMyServiceConnection);
		}
		super.onDestroy();
	}
	
	
	/*====================使用content Observer 监听处理信息=============*/

	MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());
	//注册监听
	public void registerContentObserver()
	{
		getContentResolver().registerContentObserver(SmsProvider.URI_SMS,true,mMyContentObserver);
	}
	//反注册监听
	public void unRegisterContentObserver()
	{
		getContentResolver().unregisterContentObserver(mMyContentObserver);
	}
	
	class MyContentObserver extends ContentObserver
	{

		/**
		 * @param handler
		 */
		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		
		/*接受到数据记录的改变*/
		@Override
		public void onChange(boolean selfChange) {
			//设置adapter或者notifyadapter
			setAdapterOrNotify();
			super.onChange(selfChange);
		}

		
	}
	
	class MyServiceConnection implements ServiceConnection
	{


		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			System.out.println("---onService connected------");
			IMService.MyBinder binder = (IMService.MyBinder) service;
			mImService = binder.getService();
		}

		/* (non-Javadoc)
		 * @see android.content.ServiceConnection#onServiceDisconnected(android.content.ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			System.out.println("---onService connected------");
		}
		
	}
	


}
