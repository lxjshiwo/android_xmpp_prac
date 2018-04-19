/**
 * 
 */
package com.example.xmpp.uitls;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * @author Administrator
 *
 */
public class PinyinUtils {
	public static String getPinyin(String str)
	{
		return PinyinHelper.convertToPinyinString(str,"",PinyinFormat.WITHOUT_TONE);
	}

}
