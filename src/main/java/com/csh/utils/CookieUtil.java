package com.csh.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CookieUtil {

	private static final Logger logger = Logger.getLogger(CookieUtil.class);

	public static String COOKIE_STR = "";

	public static final List<String> COOKIES = new ArrayList<>();

	private static final String WARN = "#请勿随意更改文件内容，导致登录状态失效！\r\n";

	private static final File COOKIE_FILE = new File(System.getProperty("user.dir") + "/config/cookie.ini");

	static {
		try {
			if (!COOKIE_FILE.exists()) {
				COOKIE_FILE.getParentFile().mkdir();
				COOKIE_FILE.createNewFile();
			}

			new Setting(COOKIE_FILE.getAbsolutePath()).forEach((key, value) -> COOKIES.add(key + "=" + value));

			COOKIE_STR = CollectionUtil.join(COOKIES, "; ");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 将Cookie保存到属性文件中
	 *
	 * @param cookies Cookie值，eg：token=47ce0c62f1dd483899fe0b7f8c4c9a5d
	 */
	public static void setCookies(String... cookies) {
		COOKIES.clear();
		COOKIES.addAll(CollectionUtil.toList(cookies));
		COOKIE_STR = ArrayUtil.join(cookies, "; ");
		FileUtil.writeString(WARN + ArrayUtil.join(cookies, "\r\n"), COOKIE_FILE, CharsetUtil.CHARSET_UTF_8);
	}
}