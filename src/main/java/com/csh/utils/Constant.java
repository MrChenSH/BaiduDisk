package com.csh.utils;

import java.util.HashMap;
import java.util.Map;

public final class Constant {

	private Constant() {
	}

	public static final long BYTE_MAX_SIZE = 1024;

	public static final long K_BYTE_MAX_SIZE = 1024 * 1024;

	public static final long M_BYTE_MAX_SIZE = 1024 * 1024 * 1024;

	public static final String CONTENT_TYPE_TEXT_HTML = "text/xml;charset=utf-8";

	public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

	public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

	/**
	 * yunData中保存的百度token的key值
	 */
	public static final String TOKEN_KEY = "MYBDSTOKEN";
	/**
	 * yunData中保存的用户名的key值
	 */
	public static final String NAME_KEY = "MYNAME";
	/**
	 * yunData中保存的用户头像的key值
	 */
	public static final String AVATAR_KEY = "MYAVATAR";

	public static final String BASE_URL = "https://pan.baidu.com/";

	public static final String HOME_URL = "https://pan.baidu.com/disk/home";

	/**
	 * 获取网盘配额信息URL
	 */
	public static final String QUOTA_URL = "https://pan.baidu.com/api/quota?";

	/**
	 * 获取文件列表URL
	 */
	public static final String LIST_URL = "https://pan.baidu.com/api/list?";

	/**
	 * 搜索文件列表URL
	 */
	public static final String SEARCH_URL = "https://pan.baidu.com/api/search?";

	/**
	 * 获取公钥URL
	 */
	public static final String PASS_PUBKEY_URL = "https://passport.baidu.com/v2/getpublickey?";

	public static final String PASS_API_URL = "https://passport.baidu.com/v2/api/?";

	/**
	 * 登录URL
	 */
	public static final String PASS_LOGIN_URL = "https://passport.baidu.com/v2/api/?login";

	/**
	 * 获取验证码URL
	 */
	public static final String PASS_VERIFY_CODE_URL = "https://passport.baidu.com/cgi-bin/genimage?";

	/**
	 * 校验验证码URL
	 */
	public static final String PASS_CHECK_VERIFY_CODE_URL = "https://passport.baidu.com/v2/?checkvcode&";

	/**
	 * 登录后跳转URL
	 */
	public static final String PASS_V3_JUMP_URL = "https://pan.baidu.com/res/static/thirdparty/pass_v3_jump.html?";

	/**
	 * App端登录页面地址
	 */
	public static final String WAP_PASS_URL = "https://wappass.baidu.com/passport?login&authsite=1&tpl=netdisk&overseas=1&regdomestic=1&smsLoginLink=1&display=mobile&u=https%3A%2F%2Fpan.baidu.com%2Fwap%2Fhome%3FrealName%3D1";

	/**
	 * 错误信息集合
	 */
	public static final Map<Integer, String> ERRORS = new HashMap<>();

	/**
	 * 文件类型
	 */
	public final class FileType {

		/**
		 * 文本文件
		 */
		public static final String TEXT = "txt|log|ini|properties";

		/**
		 * 图片
		 */
		public static final String IMAGE = "jpg|png|jpeg|gif|bmp";

		/**
		 * 音频
		 */
		public static final String AUDIO = "mp3|wav|m4a|ape|flac|ogg";

		/**
		 * 视频
		 */
		public static final String VIDEO = "avi|wmv|mpeg|mp4|mov|mkv|flv|f4v|m4v|rmvb|rm|3gp|dat|ts|mts|vob";

		/**
		 * 压缩文件
		 */
		public static final String ARCHIVE = "zip|rar|7z|cab|tgz|tar.gz|tar.xz|lz|deb";
	}

	static {
		ERRORS.put(2, "您输入的帐号不存在！");
		ERRORS.put(4, "您输入的帐号或密码有误！");
		ERRORS.put(6, "账号异常！");
		ERRORS.put(7, "您输入的密码不正确！");
		ERRORS.put(257, "请输入验证码！");
		ERRORS.put(120021, "您的帐号可能存在安全隐患，为保障您的帐号安全，请验证后登录。");
	}

	public static void main(String[] args) {
		System.out.println("zip".matches("zip|rar|7z|cab|tgz|tar.gz|tar.xz|lz|deb"));
	}
}
