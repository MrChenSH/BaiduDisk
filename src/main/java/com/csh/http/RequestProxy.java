package com.csh.http;


import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.csh.app.App;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

	private static Optional<ButtonType> showErrorAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("系统提示");
		alert.setHeaderText(message);
		alert.initOwner(App.primaryStage);
		return alert.showAndWait();
	}

	private static HttpResponse httpGet(String url, JSONObject params) {
		logger.info(params.toJSONString(4));
		return HttpRequest.get(url).cookie(CookieUtil.COOKIE_STR).form(params).execute();
	}

	private static HttpResponse httpPost(String url, JSONObject params) {
		logger.info(params.toJSONString(4));
		return HttpRequest.post(url).cookie(CookieUtil.COOKIE_STR).form(params).execute();
	}

	private static List<BaiduFile> convertJSON2List(JSONArray array) {
		try {
			JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, BaiduFile.class);
			return MAPPER.readValue(array.toString(), javaType);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			showErrorAlert("操作失败，请稍后重试！");
		}
		return Collections.emptyList();
	}

	/**
	 * 获取网盘容量使用信息
	 *
	 * @return
	 */
	public static JSONObject getQuotaInfos() {
		JSONObject params = new JSONObject() {{
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
			JSONObject result = JSONUtil.parseObj(rs.body());

			if (!Constant.SUCCEED.equals(result.get("errno"))) {
				Optional<ButtonType> optional = showErrorAlert("网盘信息获取失败，请重新登录！");
				if (ButtonType.OK.equals(optional.get())) {
					Platform.runLater(() -> {
						try {
							App.primaryStage.hide();
							App.generateLoginPanel();
							App.primaryStage.show();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					});
				}
			}

			return JSONUtil.parseObj(rs.body());
		} finally {
			if (rs != null) rs.close();
		}
	}

	/**
	 * 根据路径获取文件列表
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static List<BaiduFile> getFileList(String path) {
		JSONObject params = new JSONObject() {{
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
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toJSONString(4));

			if (Constant.SUCCEED.equals(result.get("errno")) && result.containsKey("list")) {
				return convertJSON2List(result.getJSONArray("list"));
			} else {
				showErrorAlert("文件列表获取失败！");
				throw new RuntimeException("文件列表获取失败！");
			}
		} finally {
			if (rs != null) rs.close();
		}
	}

	/**
	 * 搜索网盘文件
	 *
	 * @param keyword
	 * @return
	 * @throws Exception
	 */
	public static List<BaiduFile> searchFileList(String keyword) {
		JSONObject params = new JSONObject() {{
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
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toJSONString(4));

			if (Constant.SUCCEED.equals(result.get("errno")) && result.containsKey("list")) {
				return convertJSON2List(result.getJSONArray("list"));
			} else {
				showErrorAlert("文件列表获取失败！");
				throw new RuntimeException("文件列表获取失败！");
			}
		} finally {
			if (rs != null) rs.close();
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
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		JSONObject formData = new JSONObject() {{
			put("filelist", fileList.toString());
		}};

		HttpResponse rs = httpPost(Constant.MANAGER_URL + HttpUtil.toParams(params), formData);

		try {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toJSONString(4));

			if (Constant.SUCCEED.equals(result.getInt("errno"))) return true;
			else {
				showErrorAlert("操作失败，请稍后重试！");
				return false;
			}
		} finally {
			if (rs != null) rs.close();
		}
	}

	/**
	 * 网盘文件分享
	 *
	 * @param ids       id集合
	 * @param isPrivate 是否为私密分享
	 * @param period    是否为私密分享
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
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		JSONObject formData = new JSONObject() {{
			put("period", period);
			put("channel_list", "[]");
			put("fid_list", ids.toString());
			put("schannel", isPrivate ? 4 : 0);
			if (isPrivate) {
				put("pwd", RandomUtil.simpleUUID().substring(0, 4));
			}
		}};

		HttpResponse rs = httpPost(Constant.SHARE_URL + HttpUtil.toParams(params), formData);

		try {
			JSONObject result = JSONUtil.parseObj(rs.body());

			logger.info(result.toJSONString(4));

			if (Constant.SUCCEED.equals(result.getInt("errno"))) {
				result.put("pwd", formData.get("pwd"));
				return result;
			} else {
				showErrorAlert("文件分享失败，请稍后重试！");
				throw new RuntimeException("文件分享失败！");
			}
		} finally {
			if (rs != null) rs.close();
		}
	}
}
