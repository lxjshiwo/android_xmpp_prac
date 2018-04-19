/**
 * 
 */
package com.example.xmpp.activity;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.proxy.ProxyInfo;

import com.example.xmpp.R;
import com.example.xmpp.service.IMService;
import com.example.xmpp.service.PushService;
import com.example.xmpp.uitls.ThreadUtils;
import com.example.xmpp.uitls.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class LoginAcitvity extends Activity{
	private static final String tag = "xmpp";
	protected static final int PORT = 5222;//对应端口号
	protected static final String HOST = "119.28.25.120";//主机ip
	public static final String SERVICENAME = "myopenfire";
	private TextView mEtUserName;
	private TextView mEtPassWord;
	private Button mBtnLogin;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		initView();
		initListener();
	}

	/**
	 * 
	 */
	private void initListener() {
		
		mBtnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String userName = mEtUserName.getText().toString();
				final String passWord = mEtPassWord.getText().toString();
				//判断用户名是否为空
				if(TextUtils.isEmpty(userName))
				{
					//用户名为空
					mEtUserName.setError("用户名不能为空");
					return; 
				}
				//判断密码是否为空
				if(TextUtils.isEmpty(passWord))
				{
					//用户名为空
					mEtPassWord.setError("密码不能为空");
					return; 
				}
				//开始创建连接
				ThreadUtils.runInThread(new Runnable() {
					

					@Override
					public void run() {
						try {
								//创建连接配置对象
								ConnectionConfiguration config  = new ConnectionConfiguration(HOST,PORT);	

								//额外配置(方便开发，上线可删除 )
								config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//明文传输
								config.setDebuggerEnabled(true);//开启调试模式,方便开发调试相应内容
								
								//开始创建连接对象
								XMPPConnection conn = new XMPPConnection(config);
								//开始连接
								conn.connect();
								//连接成功
								//开始登陆 
								conn.login(userName, passWord);
								//已经登录成功
								ToastUtils.showToastSafe(LoginAcitvity.this,"登录成功");
								//调到主界面
								Intent intent = new Intent(LoginAcitvity.this,MainActivity.class);
								startActivity(intent);
								//保存连接对象
								IMService.conn = conn;

								//保存当前登录账户
								String account = userName + "@" + LoginAcitvity.SERVICENAME;
								IMService.mCurAccount = account;//admin --> admin@myopenfire
								//启动IMService
								Intent service = new Intent(LoginAcitvity.this,IMService.class);
								startService(service);

								//启动推送服务
								Intent pushService = new Intent(LoginAcitvity.this,PushService.class);
								startService(pushService);
								finish();

								
							} catch (XMPPException e) {
								e.printStackTrace();
								ToastUtils.showToastSafe(LoginAcitvity.this,"登录失败");

							}
				

						
					}
				});
				
				
			}
		});
	}

	/**
	 * 
	 */
	private void initView() {
		mEtUserName = (TextView) findViewById(R.id.et_username);
		mEtPassWord = (TextView) findViewById(R.id.et_password); 
		
		mBtnLogin = (Button) findViewById(R.id.bt_login);
		
	}

}
