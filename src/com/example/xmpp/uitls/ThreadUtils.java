/**
 * 
 */
package com.example.xmpp.uitls;

import android.os.Handler;

/**
 * @author Administrator
 *
 */
public class ThreadUtils {
	/*子线程执行task*/
	public static void runInThread(Runnable task)
	{
		new Thread(task).start();
	}
	/*UI线程执行task*/
	/*
	*	主线程下的一个handler
	*/
	public static Handler mHandler = new Handler();
		
	public static void runInUIThread(Runnable task)
	{
		mHandler.post(task);
		
	}

}
