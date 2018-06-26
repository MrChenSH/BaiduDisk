package com.csh.http;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestProxy {

	private static final Logger logger = Logger.getLogger(RequestProxy.class);

	public static JSONObject YUN_DATA = new JSONObject();

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final File JSON_FILE = new File(System.getProperty("user.dir") + "/config/yunData.json");

	static {
		try {
			if (!JSON_FILE.exists()) {
				JSON_FILE.getParentFile().mkdir();
				JSON_FILE.createNewFile();
			}
			YUN_DATA = JSONUtil.readJSONObject(JSON_FILE, CharsetUtil.CHARSET_UTF_8);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void setYunData(JSONObject yunData) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(JSON_FILE);

			(RequestProxy.YUN_DATA = yunData).write(writer, 4, 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static HttpResponse httpGet(String url, Map<String, Object> params) {
		return HttpRequest.get(url).cookie(CookieUtil.COOKIE_STR).form(params).execute();
	}

	/**
	 * 获取网盘容量使用信息
	 *
	 * @return
	 */
	public static JSONObject getQuotaInfos() {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("checkexpire", 1);
			put("checkfree", 1);
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("clienttype", 0);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		HttpResponse rs = httpGet(Constant.QUOTA_URL, params);

		try {
			return JSONUtil.parseObj(rs.body());
		} finally {
			if (rs != null) rs.close();
		}
	}

	public static List<BaiduFile> getFileList(String path) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("dir", path);
			put("order", "name");
			put("desc", 0);
			put("clienttype", 0);
			put("showempty", 0);
			put("web", 1);
			put("channel", "chunlei");
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		HttpResponse rs = httpGet(Constant.LIST_URL, params);

		try {
			JSONObject json = JSONUtil.parseObj(rs.body());

			logger.info(json.toJSONString(4));

			if (json.containsKey("list")) {
				JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

				List<BaiduFile> baiduFiles = MAPPER.readValue(json.getJSONArray("list").toString(), javaType);

				return baiduFiles;
			} else {
				logger.error("文件列表获取失败！");
				logger.error(json.toString());
				throw new RuntimeException("文件列表获取失败！");
			}
		} finally {
			if (rs != null) rs.close();
		}
	}

	public static List<BaiduFile> searchFileList(String keyword) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("recursion", 1);
			put("order", "name");
			put("desc", 0);
			put("clienttype", 0);
			put("showempty", 0);
			put("web", 1);
			put("key", keyword);
			put("channel", "chunlei");
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		HttpResponse rs = httpGet(Constant.SEARCH_URL, params);

		try {
			JSONObject json = JSONUtil.parseObj(rs.body());

			logger.info(json.toJSONString(4));

			if (json.containsKey("list")) {
				JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

				List<BaiduFile> baiduFiles = MAPPER.readValue(json.getJSONArray("list").toString(), javaType);

				return baiduFiles;
			} else {
				logger.error("文件列表获取失败！");
				logger.error(json.toString());
				throw new RuntimeException("文件列表获取失败！");
			}
		} finally {
			if (rs != null) rs.close();
		}
	}
}
