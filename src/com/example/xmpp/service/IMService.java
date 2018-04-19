/**
 * 
 */
package com.example.xmpp.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.example.xmpp.activity.LoginAcitvity;
import com.example.xmpp.dbhelper.ContactOpenHelper;
import com.example.xmpp.dbhelper.SmsOpenHelper;
import com.example.xmpp.provider.ContactProvider;
import com.example.xmpp.provider.SmsProvider;
import com.example.xmpp.uitls.PinyinUtils;
import com.example.xmpp.uitls.ThreadUtils;
import com.example.xmpp.uitls.ToastUtils;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author Administrator
 *
 */
public class IMService extends Service{
//实时获取相应化名册	
	public static String mCurAccount ;//当前登录的用户的jid
	public static XMPPConnection conn;
	private Roster mRoster;
	private MyRosterListener mRosterListener;
	MyChatManagerListener mMyChatListener = new MyChatManagerListener();
	MyMessageListener mMyMessageListener = new MyMessageListener();

	//消息管理
	private Chat mCurChat;
	private ChatManager mChatManager;
	
	//保存所有chat对象
	private Map<String,Chat> mChatMap = new HashMap<String, Chat>();
	


	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	
	public class MyBinder extends Binder
	{
		/**
		 * 返回Service的实例
		 * @return 
		 */
		public IMService getService(){

			return IMService.this;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		System.out.println("--------Service onCreate-----------");
		/*===========同步联系人================*/
		ThreadUtils.runInThread(new Runnable() {
			
			@Override
			public void run() {
				/*=========同步花名册 begin===============*/
				System.out.println("同步花名册 begin");
				mRoster = IMService.conn.getRoster();
				
				//得到所有联系人
				final Collection<RosterEntry> entries = mRoster.getEntries();
				
				//监听联系人的改变 
				mRosterListener = new MyRosterListener();
				mRoster.addRosterListener(mRosterListener);
		

				for(RosterEntry entry : entries)
				{
					saveOrUpdateEntry(entry);
				}
				
				System.out.println("同步花名册 end");

				/*=========同步花名册 end===============*/
				/*=========创建消息管理者 注册监听begin===============*/
				//1.获取消息管理者
				if(mChatManager == null)
				{
					mChatManager = IMService.conn.getChatManager();
				}
				
				//监听被动对话请求 
				mChatManager.addChatListener(mMyChatListener);
				/*=========创建消息管理者 注册监听end===============*/

				
			}
		});
		
		
		super.onCreate();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("--------startCommand-----------");
		return super.onStartCommand(intent, flags, startId);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		System.out.println("--------Destroy-----------");
		//移除rosterListener
		if(mRoster!= null && mRosterListener!= null)
		{
			mRoster.removeRosterListener(mRosterListener);
		}
		//移除messageListener
		if(mCurChat != null && mMyMessageListener!=null)
		{
			mCurChat.removeMessageListener(mMyMessageListener);
		}

		super.onDestroy();
	}
	class MyRosterListener implements RosterListener
	{

		/* (non-Javadoc)
		 * @see org.jivesoftware.smack.RosterListener#entriesAdded(java.util.Collection)
		 */
		@Override
		public void entriesAdded(Collection<String> arg0) {
			//联系人增加
			System.out.println("--------entriesAdded---------");
			//对应更新数据库
			for(String address : arg0)
			{
				RosterEntry entry = mRoster.getEntry(address);
				//要么更新，要么插入
				saveOrUpdateEntry(entry);

				
			}
			
		}

		/* (non-Javadoc)
		 * @see org.jivesoftware.smack.RosterListener#entriesDeleted(java.util.Collection)
		 */
		@Override
		public void entriesDeleted(Collection<String> arg0) {
			System.out.println("--------entriesAdded---------");
			//联系人删除了
			for(String account : arg0)
			{
				//执行删除操作
				getContentResolver().delete(ContactProvider.URI_CONTACT,ContactOpenHelper.ContactTable.ACCOUNT+"=?", new String[]{account});
			}
			
		}

		/* (non-Javadoc)
		 * @see org.jivesoftware.smack.RosterListener#entriesUpdated(java.util.Collection)
		 */
		@Override
		public void entriesUpdated(Collection<String> arg0) {
			//联系人修改了
			for(String address : arg0)
			{
				RosterEntry entry = mRoster.getEntry(address);
				//要么更新，要么插入
				saveOrUpdateEntry(entry);
			}
		}

		/* (non-Javadoc)
		 * @see org.jivesoftware.smack.RosterListener#presenceChanged(org.jivesoftware.smack.packet.Presence)
		 */
		@Override
		public void presenceChanged(Presence arg0) {
			//联系人状态改变
			
		}
	}
	
	//处理收入相应消息
	class MyMessageListener implements MessageListener
	{

		@Override
		public void processMessage(Chat arg0, Message arg1) {
			String body = arg1.getBody();
			Message.Type type = arg1.getType();
			if(body == null)
			{
				return ;
			}
			System.out.println(body);
			//ToastUtils.showToastSafe(ChatActivity.this,body);
			//收到消息保存消息
			//小蜜 (from)--> 我 (to)===> 小蜜
			String participant = arg0.getParticipant();
			participant = filterAccount(participant);
			saveMessage(participant,arg1);
			
		}
		
	}
	
	
	class MyChatManagerListener implements ChatManagerListener{
			
		@Override
		public void chatCreated(Chat arg0, boolean arg1) {
			System.out.println("============chatCreated===============");
			//判断chat是否存在在map中
			//因为别人创建和我自己创建参与者(和我聊天的人对应的jid不同) 需要统一处理
			String participant = arg0.getParticipant();
			
			participant = filterAccount(participant);
			
			if(!mChatMap.containsKey(participant))
			{
				//保存chat
				mChatMap.put(participant,arg0);
				arg0.addMessageListener(mMyMessageListener);
				
			}

			if(arg1)
			{
				//true
				System.out.println("============我创建了 一个chat==========");
			}
			else
			{
				//false
				System.out.println("============别人 创建了 一个chat==========");
				
			}
			
		}
		
	}

	/**
	 * @param entry用于更行相应的联系人
	 */
	private void saveOrUpdateEntry(RosterEntry entry) {
		ContentValues values = new ContentValues();
		String account = entry.getUser();
		//保证account的格式
//					account = account.substring(0,account.indexOf("@"))+"@"+LoginAcitvity.SERVICENAME;

		String nickname = entry.getName();
		if(nickname == null || "".equals(nickname))
		{
			nickname = account.substring(0,account.indexOf("@"));
		}

		values.put(ContactOpenHelper.ContactTable.ACCOUNT,account);
		values.put(ContactOpenHelper.ContactTable.NICKNAME,nickname);
		values.put(ContactOpenHelper.ContactTable.AVATAR,"0");
		values.put(ContactOpenHelper.ContactTable.PINYIN,PinyinUtils.getPinyin(account));
		
		//线update后插入
		int updateCount = getContentResolver().update(ContactProvider.URI_CONTACT, values,ContactOpenHelper.ContactTable.ACCOUNT + "=?",new String[]{account});
		if(updateCount <= 0)
		{
			//没有更新
			getContentResolver().insert(ContactProvider.URI_CONTACT, values);
		}
	}
	
	/*
	 * 发送消息
	 */
	public void sendMessage(final Message msg){

		try {
			
			//判断chat对象是否已经创建
			String toAccount = msg.getTo();
			toAccount = filterAccount(toAccount);
			System.out.println(toAccount);
			if(mChatMap.containsKey(toAccount))
			{
				mCurChat = mChatMap.get(toAccount);
			}
			else
			{
				mCurChat = mChatManager.createChat(toAccount,mMyMessageListener);
				mChatMap.put(toAccount,mCurChat);
			}
			
			//发送消息
			mCurChat.sendMessage(msg);
			//保存消息
			//我(from ) --> 小蜜(to) ===> 小蜜
			saveMessage(toAccount,msg);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				
		
		
	}
	/**
	 * 保存消息 ->contentResolver->contentProvider -> sqlite
	 * 
	 * @param msg
	 */
	private void saveMessage(String sessionAccount,Message msg) {
		ContentValues values = new ContentValues();
		//我(from ) --> 小蜜(to) ===> 小蜜
		//小蜜 (from)--> 我 (to)===> 小蜜
		sessionAccount = filterAccount(sessionAccount);
		String from = msg.getFrom();
		from = filterAccount(from);
		String to = msg.getTo();
		to = filterAccount(to);
		values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,from);
		values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,to);
		values.put(SmsOpenHelper.SmsTable.BODY,msg.getBody());
		values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
		values.put(SmsOpenHelper.SmsTable.TYPE,msg.getType().name());
		values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
		values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,sessionAccount);
		getContentResolver().insert(SmsProvider.URI_SMS,values);
		
	}

	/**
	 * @param account
	 * @return
	 */
	private String filterAccount(String account) {
		return account.substring(0,account.indexOf("@")) + "@" + LoginAcitvity.SERVICENAME;
	}
	
	
	

}
