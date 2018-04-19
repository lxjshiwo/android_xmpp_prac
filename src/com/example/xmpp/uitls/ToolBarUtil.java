/**
 * 
 */
package com.example.xmpp.uitls;

import java.util.ArrayList;
import java.util.List;

import com.example.xmpp.R;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
public class ToolBarUtil {

	private List<TextView> mTextViews = new ArrayList<TextView>();
	@SuppressLint("NewApi") 
	public void createToolBar(LinearLayout container, String[] toolBarTitle, int[] iconArr)
	{ 
		
		for(int i = 0;i < toolBarTitle.length;i++)
		{
			TextView tv = (TextView) View.inflate(container.getContext(),R.layout.inflate_toolbar_btn, null); 
			tv.setText(toolBarTitle[i]);
			//动态修改textView中的drawableTop属性
			tv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, iconArr[i], 0, 0);
			int width = 0;
			int height = LinearLayout.LayoutParams.MATCH_PARENT;
			LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(width,height);
			//设置weight属性
			params.weight = 1;
			container.addView(tv,params); 

			//保存textView到集合中
			mTextViews.add(tv);
			
			//设置点击事件
			final int finalI = i;
			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//不同模块之间传值 需要使用接口回调
					//3.需要传值的地方调用接口方法
					mOnToolBarClickListener.onToolBarClick(finalI);

				}
			});
			
		
		}
	};
	
	public void changeColor(int position){
		//还原所有颜色
		for(TextView tv : mTextViews)
		{
			tv.setSelected(false);
		}
		
		mTextViews.get(position).setSelected(true);//通过设置select属性，控制为选中效果
		
	}
	//1.创建接口和接口方法
	
	public interface OnToolBarClickListener
	{
		void onToolBarClick(int position);

		
	}
	//2.定义接口变量
	OnToolBarClickListener mOnToolBarClickListener;
	
	//4.暴露一个公共的方法
	
	public void setOnToolBarClickListener(OnToolBarClickListener onToolBarClickListener)
	{
		mOnToolBarClickListener = onToolBarClickListener;
	}

}
