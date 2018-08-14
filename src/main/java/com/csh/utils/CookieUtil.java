package com.csh.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CookieUtil {

	private static final Logger logger = Logger.getLogger(CookieUtil.class);

	public static List<String> COOKIES = new ArrayList<>();

	private static final String WARN = "#请勿随意更改文件内容，导致登录状态失效！\r\n";

	private static final Setting setting;

	static {
		setting = new Setting(System.getProperty("user.dir") + "/config/cookie.ini");
		setting.forEach((key, value) -> COOKIES.add(key + "=" + value));
	}

	public static String getCookie(String name) {
		return setting.get(name);
	}

	/**
	 * 将Cookie保存到属性文件中
	 *
	 * @param cookies Cookie值，eg：token=47ce0c62f1dd483899fe0b7f8c4c9a5d; name=1234
	 */
	public static void setCookies(String cookies) {
		COOKIES = StrSpliter.splitTrim(cookies, "; ", true);

		COOKIES.forEach(s -> {
			if (StrUtil.isNotBlank(s) && s.contains("=")) {
				String arr[] = s.split("=", 2);
				setting.set(arr[0], arr[1]);
			}
		});

		FileUtil.writeString(WARN + CollectionUtil.join(COOKIES, "\r\n"),
				setting.getSettingPath(), CharsetUtil.CHARSET_UTF_8);
	}
}