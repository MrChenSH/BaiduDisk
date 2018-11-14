package com.csh.utils;

import java.util.HashMap;
import java.util.Map;

public final class Constant {

	private Constant() {
	}

	public static final long BYTE_MAX_SIZE = 1024;

	public static final long K_BYTE_MAX_SIZE = 1024 * 1024;

	public static final long M_BYTE_MAX_SIZE = 1024 * 1024 * 1024;

	/**
	 * yunData中保存的百度token的key值
	 */
	public static final String TOKEN_KEY = "bdstoken";
	/**
	 * yunData中保存的用户名的key值
	 */
	public static final String NAME_KEY = "username";
	/**
	 * yunData中保存的用户头像的key值
	 */
	public static final String AVATAR_KEY = "photo";

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
	 * 网盘文件管理URL，重命名、删除等操作
	 */
	public static final String MANAGER_URL = "https://pan.baidu.com/api/filemanager?";

	/**
	 * 网盘分享URL
	 */
	public static final String SHARE_URL = "https://pan.baidu.com/share/set?";

	/**
	 * 获取下载链接URL
	 */
	public static final String DOWNLOAD_URL = "https://pan.baidu.com/api/download?";

	public static final Integer SUCCEED = 0;

	/**
	 * 网盘文件操作
	 */
	public final class Operate {
		/**
		 * 重命名
		 */
		public static final String RENAME = "rename";

		/**
		 * 删除
		 */
		public static final String DELETE = "delete";
	}

	/**
	 * 文件类型正则
	 */
	public final class FileTypeRegx {

		/**
		 * 文本文件
		 */
		public static final String TEXT = "(.txt|.log|.ini|.properties)$";

		/**
		 * 图片
		 */
		public static final String IMAGE = "(.jpg|.png|.jpeg|.gif|.bmp)$";

		/**
		 * 音频
		 */
		public static final String AUDIO = "(.mp3|.wav|.m4a|.ape|.flac|.ogg)$";

		/**
		 * 视频
		 */
		public static final String VIDEO = "(.avi|.wmv|.mpeg|.mp4|.mov|.mkv|.flv|.f4v|.m4v|.rmvb|.rm|.3gp|.dat|.ts|.mts|.vob)$";

		/**
		 * 压缩文件
		 */
		public static final String ARCHIVE = "(.zip|.rar|.7z|.cab|.tgz|.tar.gz|.tar.xz|.lz|.deb)$";
	}

	public static final Map<String, FontAwesome> ICON_MAP = new HashMap<String, FontAwesome>() {{
		put("(.apk)$", FontAwesome.ANDROID);
		put("(.pdf)$", FontAwesome.FILE_PDF);
		put("(.font|.ttf)$", FontAwesome.FILE_FONT);
		put("(.ppt|.pptx)$", FontAwesome.FILE_PPT);
		put("(.doc|.docx)$", FontAwesome.FILE_WORD);
		put("(.xls|.xlsx)$", FontAwesome.FILE_EXCEL);
		put(FileTypeRegx.TEXT, FontAwesome.FILE_TEXT);
		put(FileTypeRegx.IMAGE, FontAwesome.FILE_IMAGE);
		put(FileTypeRegx.AUDIO, FontAwesome.FILE_AUDIO);
		put(FileTypeRegx.VIDEO, FontAwesome.FILE_VIDEO);
		put(FileTypeRegx.ARCHIVE, FontAwesome.FILE_ARCHIVE);
	}};
}
