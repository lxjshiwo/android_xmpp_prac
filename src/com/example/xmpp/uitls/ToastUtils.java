/**
 * 
 */
package com.example.xmpp.uitls;

import android.content.Context;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class ToastUtils {
	/**
	 * 可以在上下文中弹出toast 
	 * @param context上下文
	 * @param text 显示的内容
	 */
	public static void showToastSafe(final Context context,final String text)
	{
		ThreadUtils.runInUIThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context.getApplicationContext(),text,Toast.LENGTH_SHORT).show();

				
			}
		});
		
	}
	

}
