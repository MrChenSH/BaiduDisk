package com.csh.http;


import com.csh.utils.Constant;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.crypto.Cipher;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestProxy
{

	private static long initTime;

	public static String bd_token = "";

	public static String rsakey = "";

	public static String fpUid = "undefined";

	public static String fpInfo = "undefined";

	public static String dv = "";

	private static String jumpParams = "";

	public static JSONObject yunData = new JSONObject();

	// 连接管理器
	private static PoolingHttpClientConnectionManager pool;

	private static CloseableHttpClient httpClient = null;

	private static HttpClientContext context = null;

	private static CookieStore cookieStore = null;

	// 请求配置
	private static RequestConfig requestConfig;

	static
	{
		try
		{
			context = HttpClientContext.create();
			cookieStore = new BasicCookieStore();
			// 配置超时时间（连接服务端超时1秒，请求数据返回超时2秒）
			requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
					.setConnectionRequestTimeout(60000).build();
			// 设置默认跳转以及存储cookie
			httpClient = HttpClientBuilder.create().setDefaultHeaders(Constant.DEFAULT_HEADERS)
					.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
					.setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig)
					.setDefaultCookieStore(cookieStore).build();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String visitHome() throws Exception
	{
		CloseableHttpResponse response = httpClient.execute(new HttpGet(Constant.BASE_URL), context);
		try
		{
			return EntityUtils.toString(response.getEntity(), Constant.CHARSET_UTF_8);
		} finally
		{
			response.close();
		}
	}

	/**
	 * 获取token
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getToken() throws Exception
	{
		// 初始化时间戳
		initTime = System.currentTimeMillis();
		// 访问首页获取cookie(BAIDUID)
		visitHome();

		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("getapi", null));
		params.add(new BasicNameValuePair("tpl", "netdisk"));
		params.add(new BasicNameValuePair("subpro", "netdisk_web"));
		params.add(new BasicNameValuePair("apiver", "v3"));
		params.add(new BasicNameValuePair("tt", System.currentTimeMillis() + ""));
		params.add(new BasicNameValuePair("class", "login"));
		params.add(new BasicNameValuePair("gid", UUID.randomUUID().toString().toUpperCase()));
		params.add(new BasicNameValuePair("logintype", "basicLogin"));

		HttpGet httpGet = new HttpGet(Constant.PASS_API_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			cookieStore = context.getCookieStore();

			JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(result.toString(4));

			JSONObject data = result.getJSONObject("data");

			if (data.has("cookie") && data.has("token"))
			{
				if (data.getInt("cookie") == 1 && data.getString("token").length() == 32)
				{
					bd_token = data.getString("token");
				} else
				{
					throw new RuntimeException(data.getString("token"));
				}
			}
		} finally
		{
			if (response != null) response.close();
		}

		return bd_token;
	}

	/**
	 * 判断该用户是否需要输入验证码
	 *
	 * @param username
	 * @return
	 */
	public static String loginCheck(String username) throws Exception
	{
		String verifyCodeString = "";
		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("logincheck", null));
		params.add(new BasicNameValuePair("token", bd_token));
		params.add(new BasicNameValuePair("tpl", "netdisk"));
		params.add(new BasicNameValuePair("subpro", "netdisk_web"));
		params.add(new BasicNameValuePair("apiver", "v3"));
		params.add(new BasicNameValuePair("tt", System.currentTimeMillis() + ""));
		params.add(new BasicNameValuePair("sub_source", "leadsetpwd"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("isphone", "false"));
		params.add(new BasicNameValuePair("dv", dv));

		HttpGet httpGet = new HttpGet(Constant.PASS_API_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(result.toString(4));

			verifyCodeString = result.getJSONObject("data").getString("codeString");
		} finally
		{
			if (response != null) response.close();
		}
		return verifyCodeString;
	}

	/**
	 * 判断验证码输入是否正确
	 *
	 * @param verifyCode
	 * @return
	 */
	public static String checkVerifyCode(String verifyCode, String verifyCodeString) throws Exception
	{
		String message = null;

		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("token", bd_token));
		params.add(new BasicNameValuePair("tpl", "netdisk"));
		params.add(new BasicNameValuePair("subpro", "netdisk_web"));
		params.add(new BasicNameValuePair("apiver", "v3"));
		params.add(new BasicNameValuePair("tt", System.currentTimeMillis() + ""));
		params.add(new BasicNameValuePair("verifycode", verifyCode));
		params.add(new BasicNameValuePair("codestring", verifyCodeString));

		HttpGet httpGet = new HttpGet(Constant.PASS_CHECK_VERIFY_CODE_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(result.toString(4));

			if (result.has("errInfo"))
			{
				message = result.getJSONObject("errInfo").getString("msg");
			}
		} finally
		{
			if (response != null) response.close();
		}

		return message;
	}

	/**
	 * 获取加密公钥
	 *
	 * @return
	 * @throws Exception
	 */
	private static String getPublicKey() throws Exception
	{
		String publicKey;

		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("token", bd_token));
		params.add(new BasicNameValuePair("tpl", "netdisk"));
		params.add(new BasicNameValuePair("subpro", "netdisk_web"));
		params.add(new BasicNameValuePair("apiver", "v3"));
		params.add(new BasicNameValuePair("tt", System.currentTimeMillis() + ""));
		params.add(new BasicNameValuePair("gid", UUID.randomUUID().toString().toUpperCase()));

		HttpGet httpGet = new HttpGet(Constant.PASS_PUBKEY_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(result.toString(4));

			rsakey = result.getString("key");

			publicKey = result.getString("pubkey").replaceAll("-----BEGIN PUBLIC KEY-----", "")
					.replaceAll("-----END PUBLIC KEY-----", "").replaceAll("\\n", "");

		} finally
		{
			if (response != null) response.close();
		}
		return publicKey;
	}

	/**
	 * 用公钥对密码进行加密
	 *
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private static String encrypt(String password) throws Exception
	{

		byte[] keyBytes = Base64.decodeBase64(getPublicKey());

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		byte[] bytes = cipher.doFinal(password.getBytes());

		System.out.println("加密前：" + password);

		password = Base64.encodeBase64String(bytes);

		System.out.println("加密后：" + password);

		return password;
	}

	/**
	 * 用户登录
	 *
	 * @param username
	 * @param password
	 * @param verifyCode
	 * @param verifyCodeString
	 * @return
	 * @throws Exception
	 */
	public static JSONObject login(String username, String password, String verifyCode, String verifyCodeString) throws Exception
	{

		JSONObject json = new JSONObject();
		HttpPost httpPost = new HttpPost(Constant.PASS_LOGIN_URL);

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.addTextBody("staticpage", Constant.PASS_V3_JUMP_URL);
		entityBuilder.addTextBody("charset", Constant.CHARSET_UTF_8);
		entityBuilder.addTextBody("token", bd_token);
		entityBuilder.addTextBody("tpl", "netdisk");
		entityBuilder.addTextBody("subpro", "netdisk_web");
		entityBuilder.addTextBody("apiver", "v3");
		entityBuilder.addTextBody("tt", System.currentTimeMillis() + "");
		entityBuilder.addTextBody("codestring", "");
		entityBuilder.addTextBody("safeflg", "0");
		entityBuilder.addTextBody("u", Constant.HOME_URL);
		entityBuilder.addTextBody("isPhone", "false");
		entityBuilder.addTextBody("detect", "1");
		entityBuilder.addTextBody("gid", UUID.randomUUID().toString().toUpperCase());
		entityBuilder.addTextBody("quick_user", "0");
		entityBuilder.addTextBody("logintype", "basicLogin");
		entityBuilder.addTextBody("logLoginType", "pc_loginBasic");
		entityBuilder.addTextBody("idc", "");
		entityBuilder.addTextBody("loginmerge", "true");
		entityBuilder.addTextBody("foreignusername", "");
		entityBuilder.addTextBody("username", username);
		entityBuilder.addTextBody("password", encrypt(password));
		entityBuilder.addTextBody("verifycode", verifyCode);
		entityBuilder.addTextBody("codestring", verifyCodeString);
		entityBuilder.addTextBody("mem_pass", "on");
		entityBuilder.addTextBody("rsakey", rsakey);
		entityBuilder.addTextBody("crypttype", "12");
		entityBuilder.addTextBody("ppui_logintime", System.currentTimeMillis() - initTime + "");
		entityBuilder.addTextBody("countrycode", "");
		entityBuilder.addTextBody("fp_uid", fpUid);
		entityBuilder.addTextBody("fp_info", fpInfo);
		entityBuilder.addTextBody("dv", dv);
		entityBuilder.addTextBody("callback", "parent.bd__pcbs__v2fsg8");

		httpPost.setEntity(entityBuilder.build());

		CloseableHttpResponse response = httpClient.execute(httpPost, context);

		try
		{
			cookieStore = context.getCookieStore();

			String result = EntityUtils.toString(response.getEntity());

			Matcher matcher = Pattern.compile("err_no=(.*)=").matcher(result);

			if (matcher.find())
			{
				matcher.group();
				URLEncodedUtils.parse(jumpParams = matcher.group(), Charset.defaultCharset()).forEach(parameter ->
				{
					json.put(parameter.getName(), parameter.getValue());
				});

			}

			System.out.println(json);

			return json;

		} finally
		{
			if (response != null) response.close();
		}
	}

	/**
	 * 登录成功后跳转
	 *
	 * @return
	 */
	public static Object jumpHtml() throws Exception
	{
		HttpGet httpGet = new HttpGet(Constant.PASS_V3_JUMP_URL + URLEncoder.encode(jumpParams, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			cookieStore = context.getCookieStore();

			cookieStore.getCookies().forEach(cookie ->
			{
				System.out.println(cookie);
			});

		} finally
		{
			if (response != null) response.close();
		}
		return null;
	}

	/**
	 * 获取网盘容量使用信息
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getQuotaInfos() throws Exception
	{

		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("checkexpire", "1"));
		params.add(new BasicNameValuePair("checkfree", "1"));
		params.add(new BasicNameValuePair("channel", "chunlei"));
		params.add(new BasicNameValuePair("web", "1"));
		params.add(new BasicNameValuePair("appid", "250528"));
		params.add(new BasicNameValuePair("bdstoken", yunData.getString("bdstoken")));
//		params.add(new BasicNameValuePair("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw=="));
		params.add(new BasicNameValuePair("clienttype", "0"));

		HttpGet httpGet = new HttpGet(Constant.QUOTA_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			cookieStore = context.getCookieStore();

			JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(json);

			return json;

		} finally
		{
			if (response != null) response.close();
		}
	}

	public static JSONObject getDir(String path, String order, Integer desc) throws Exception
	{
		List<NameValuePair> params = new ArrayList<>();

		params.add(new BasicNameValuePair("dir", path));
		params.add(new BasicNameValuePair("bdstoken", yunData.getString("bdstoken")));
//		params.add(new BasicNameValuePair("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw=="));
		params.add(new BasicNameValuePair("order", order == null ? "name" : order));
		params.add(new BasicNameValuePair("desc", desc == null ? "0" : desc.toString()));
		params.add(new BasicNameValuePair("clienttype", "0"));
		params.add(new BasicNameValuePair("showempty", "0"));
		params.add(new BasicNameValuePair("web", "1"));
		params.add(new BasicNameValuePair("channel", "chunlei"));
		params.add(new BasicNameValuePair("appid", "250528"));

		HttpGet httpGet = new HttpGet(Constant.LIST_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

		CloseableHttpResponse response = httpClient.execute(httpGet, context);

		try
		{
			cookieStore = context.getCookieStore();

			JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

			System.out.println(json);

			return json;

		} finally
		{
			if (response != null) response.close();
		}

	}

	public static void main(String[] args) throws Exception
	{
		System.out.println(visitHome());
	}

}
