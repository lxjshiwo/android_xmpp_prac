/**
 * 
 */
package com.example.xmpp.service;

import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.example.xmpp.uitls.ToastUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author Administrator
 *
 */
public class PushService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		IMService.conn.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet arg0) {
				
				Message message = (Message) arg0;
				String body = message.getBody();
				ToastUtils.showToastSafe(getApplicationContext(), body);
				
			}
		}, null);
		super.onCreate();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
