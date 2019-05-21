package com.csh.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
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
	public static final String QUOTA_URL = "https://pan.baidu.com/api/quota";

	/**
	 * 获取文件列表URL
	 */
	public static final String LIST_URL = "https://pan.baidu.com/api/list";

	/**
	 * 文件分类列表URL
	 */
	public static final String CATEGORY_URL = "https://pan.baidu.com/api/categorylist";

	/**
	 * 搜索文件列表URL
	 */
	public static final String SEARCH_URL = "https://pan.baidu.com/api/search";

	/**
	 * 网盘文件管理URL，重命名、删除等操作
	 */
	public static final String MANAGER_URL = "https://pan.baidu.com/api/filemanager";

	/**
	 * 网盘分享URL
	 */
	public static final String SHARE_URL = "https://pan.baidu.com/share/set";

	/**
	 * 获取下载链接URL
	 */
	public static final String DOWNLOAD_URL = "https://pan.baidu.com/api/download";

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
		 * 代码
		 */
		public static final String CODE = "(.js|.css|.html|.xml|.json|.java|.jsp|.c|.cs|.php|.h|.bat|.sh)$";

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

	public static final Map<String, String> ICON_MAP = new HashMap<>();

	/**
	 * 文件分类菜单
	 */
	public static final JSONArray CATEGORY = JSONUtil.readJSONArray(new File(Constant.class.getResource("/json/category.json").getFile()), CharsetUtil.CHARSET_UTF_8);

	static {
		ICON_MAP.put("(.apk)$", "image/FileType/Middle/ApkType.png");
		ICON_MAP.put("(.pdf)$", "image/FileType/Middle/PdfType.png");
		ICON_MAP.put("(.exe|.iso)$", "image/FileType/Middle/ExeType.png");
		ICON_MAP.put("(.ppt|.pptx)$", "image/FileType/Middle/PptType.png");
		ICON_MAP.put("(.doc|.docx)$", "image/FileType/Middle/DocType.png");
		ICON_MAP.put("(.xls|.xlsx)$", "image/FileType/Middle/XlsType.png");
		ICON_MAP.put("(.torrent)$", "image/FileType/Middle/TorrentType.png");
		ICON_MAP.put("(.font|.ttf)$", "image/FileType/Middle/FontType.png");
		ICON_MAP.put(FileTypeRegx.TEXT, "image/FileType/Middle/TxtType.png");
		ICON_MAP.put(FileTypeRegx.CODE, "image/FileType/Middle/CodeType.png");
		ICON_MAP.put(FileTypeRegx.IMAGE, "image/FileType/Middle/ImgType.png");
		ICON_MAP.put(FileTypeRegx.AUDIO, "image/FileType/Middle/MusicType.png");
		ICON_MAP.put(FileTypeRegx.VIDEO, "image/FileType/Middle/VideoType.png");
		ICON_MAP.put(FileTypeRegx.ARCHIVE, "image/FileType/Middle/RarType.png");
	}
}
