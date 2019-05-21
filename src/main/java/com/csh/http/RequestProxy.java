package com.csh.http;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.script.JavaScriptEngine;
import cn.hutool.script.ScriptUtil;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.model.BaiduFile;
import com.csh.service.LoadDataService;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestProxy {

	private static final Logger logger = Logger.getLogger(RequestProxy.class);

	public static JSONObject YUN_DATA = new JSONObject();

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * 获取Javascript脚本引擎
	 */
	private static JavaScriptEngine engine = ScriptUtil.getJavaScriptEngine();

	static {
		try {
			// 加载解析脚本
			engine.eval(new FileReader(RequestProxy.class.getResource("/js/util.js").getPath()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 生成签名
	 *
	 * @return
	 */
	public static String sign() {
		try {
			// 执行签名脚本
			Object obj = engine.invokeFunction("sign", YUN_DATA.getStr("sign3"), YUN_DATA.getStr("sign1"));
			// 进行Base64加密
			return Base64.encode(obj.toString(), CharsetUtil.ISO_8859_1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 生成logid
	 *
	 * @return
	 */
	public static String logId() {
		try {
			// 执行脚本
			Object obj = engine.invokeFunction("logId", CookieUtil.getCookie("BAIDUID"));
			return obj.toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private static HttpResponse httpGet(String url, JSONObject params) {
		params = params == null ? new JSONObject() : params;
		logger.info(url + "\r\n" + params.toStringPretty());

		return HttpRequest.get(url).cookie(CollectionUtil.join(CookieUtil.COOKIES, "; ")).form(params).execute();
	}

	private static HttpResponse httpPost(String url, JSONObject params) {
		params = params == null ? new JSONObject() : params;
		logger.info(url + "\r\n" + params.toStringPretty());
		return HttpRequest.post(url).cookie(CollectionUtil.join(CookieUtil.COOKIES, "; ")).form(params).execute();
	}

	private static List<BaiduFile> convertJSON2List(JSONArray array) {
		try {
			JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, BaiduFile.class);
			return MAPPER.readValue(array.toString(), javaType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.show("操作失败，请稍后重试！", e);
		}
		return Collections.emptyList();
	}

	/**
	 * 直接HTTP GET访问网盘首页，获取到yunData信息
	 *
	 * @return
	 */
	public static JSONObject getYunData() {
		try (HttpResponse rs = httpGet(Constant.HOME_URL, null)) {
			Matcher matcher = Pattern.compile("var context=(.*);").matcher(rs.body());
			if (matcher.find()) YUN_DATA = JSONUtil.parseObj(matcher.group(1));
		} catch (Exception e) {
			throw e;
		} finally {
			logger.info(YUN_DATA.toStringPretty());
			return YUN_DATA;
		}
	}

	/**
	 * 获取网盘容量使用信息
	 *
	 * @return
	 */
	public static JSONObject getQuotaInfo() {
		JSONObject params = new JSONObject() {{
			put("checkexpire", 1);
			put("checkfree", 1);
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("clienttype", 0);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", logId());
		}};

		try (HttpResponse rs = httpGet(Constant.QUOTA_URL, params)) {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toStringPretty());

			if (Constant.SUCCEED.equals(result.get("errno"))) return result;
			else throw new RuntimeException("网盘信息获取失败！");
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 加载网盘文件
	 *
	 * @param query
	 * @return
	 */
	public static List<BaiduFile> loadFiles(LoadDataService.Query query) {
		if (StrUtil.isBlank(query.getUrl())) return Collections.emptyList();

		JSONObject params = new JSONObject() {{
			put("order", "name");
			put("desc", 0);
			put("clienttype", 0);
			put("showempty", 0);
			put("web", 1);
			put("channel", "chunlei");
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", logId());
		}};

		switch (query.getUrl()) {
			case Constant.LIST_URL:
				params.put("dir", ObjectUtil.defaultIfNull(query.getPath(), "/"));
				break;
			case Constant.CATEGORY_URL:
				params.put("category", query.getCategroy());
				break;
			case Constant.SEARCH_URL:
				params.put("recursion", 1);
				params.put("key", query.getSerach());
				break;
			default:
				return Collections.emptyList();
		}

		try (HttpResponse rs = httpGet(query.getUrl(), params)) {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toStringPretty());

			if (Constant.SUCCEED.equals(result.get("errno"))) {
				JSONArray list = new JSONArray();
				if (result.containsKey("list")) {
					list = result.getJSONArray("list");
				} else if (result.containsKey("info")) {
					list = result.getJSONArray("info");
				}

				return convertJSON2List(list);
			} else throw new RuntimeException("文件列表获取失败！");
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 网盘文件管理
	 *
	 * @param operate  操作 重命名、删除等
	 * @param fileList
	 * @return
	 * @throws Exception
	 */
	public static boolean manager(String operate, JSONArray fileList) {
		JSONObject params = new JSONObject() {{
			put("opera", operate);
			put("async", 2);
			put("onnest", "fail");
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("clienttype", 0);
			put("logid", logId());
		}};

		JSONObject formData = new JSONObject() {{
			put("filelist", fileList.toString());
		}};

		try (HttpResponse rs = httpPost(Constant.MANAGER_URL + HttpUtil.toParams(params), formData)) {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toStringPretty());

			if (Constant.SUCCEED.equals(result.getInt("errno"))) return true;
			else throw new RuntimeException("操作失败！");
		} catch (Exception e) {
			MessageDialog.show("操作失败，请稍后重试！", e);
			throw e;
		}
	}

	/**
	 * 网盘文件分享
	 *
	 * @param ids       id集合
	 * @param isPrivate 是否为私密分享
	 * @param period    有效期
	 * @return
	 */
	public static JSONObject share(JSONArray ids, boolean isPrivate, int period) {
		JSONObject params = new JSONObject() {{
			put("async", 2);
			put("onnest", "fail");
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("clienttype", 0);
			put("logid", logId());
		}};

		JSONObject formData = new JSONObject() {{
			put("period", period);
			put("channel_list", "[]");
			put("fid_list", ids.toString());
			put("schannel", isPrivate ? 4 : 0);
			if (isPrivate) put("pwd", RandomUtil.randomString(4));
		}};

		try (HttpResponse rs = httpPost(Constant.SHARE_URL + HttpUtil.toParams(params), formData)) {
			JSONObject result = JSONUtil.parseObj(rs.body());
			result.put("pwd", formData.get("pwd"));
			logger.info(result.toStringPretty());

			if (Constant.SUCCEED.equals(result.getInt("errno"))) return result;
			else throw new RuntimeException("文件分享失败！");
		} catch (Exception e) {
			MessageDialog.show("文件分享失败，请稍后重试！", e);
			throw e;
		}
	}

	/**
	 * 获取网盘文件下载链接
	 *
	 * @param id 文件id
	 * @return
	 */
	public static String download(long id) {
		JSONObject params = new JSONObject() {{
			put("sign", sign());
			put("timestamp", YUN_DATA.get("timestamp"));
			put("fidlist", "[" + id + "]");
			// dlink单个文件下载，batch批量下载
			put("type", "dlink");
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("bdstoken", YUN_DATA.getStr(Constant.TOKEN_KEY));
			put("logid", logId());
			put("clienttype", 0);
			put("startLogTime", System.currentTimeMillis());
		}};

		try (HttpResponse rs = httpGet(Constant.DOWNLOAD_URL, params)) {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toStringPretty());
			if (Constant.SUCCEED.equals(result.getInt("errno")))
				return result.getJSONArray("dlink").getJSONObject(0).getStr("dlink");
			else throw new RuntimeException("下载链接获取失败！");
		} catch (Exception e) {
			MessageDialog.show("下载链接获取失败，请稍后重试！", e);
			throw e;
		}
	}
}
