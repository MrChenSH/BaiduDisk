package com.csh.test;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpTest {

	private static final Log logger = LogFactory.get();


	@Test
	public void regxTest() {
		String str = "var context={\"loginstate\":1,\"username\":\"\\u9648\\u7199Jack\",\"third\":0,\"flag\":1,\"file_list\":null,\"uk\":1479031600,\"task_key\":\"9b86d8cbfee23fe493a57df1956ab564d7db8731\",\"task_time\":1505648876,\"sampling\":{\"expvar\":[\"disk_h5_pdf\"]}," +
				"\"bdstoken\":\"4c4a869c83adc39d224d1b5a33847b9c\",\"is_vip\":0,\"bt_paths\":null,\"applystatus\":1,\"sign1\":\"4a7ce392566971a1afc193d595de74ef68476f8c\",\"sign2\":\"function s(j,r){var a=[];var p=[];var o=\\\"\\\";var v=j.length;for(var q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var t=p[i];p[i]=p[u];p[u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt(q)^k)}return o};\",\"sign3\":\"d76e889b6aafd3087ac3bd56f4d4053a\",\"timestamp\":1505648876,\"timeline_status\":1,\"face_status\":1,\"srv_ts\":1505648876,\"need_tips\":null,\"is_year_vip\":0,\"show_vip_ad\":0,\"vip_end_time\":null,\"is_evip\":0,\"is_svip\":0,\"is_auto_svip\":0,\"activity_status\":0,\"photo\":\"https:\\/\\/ss0.bdstatic.com\\/7Ls0a8Sm1A5BphGlnYG\\/sys\\/portrait\\/item\\/7558ef2c.jpg\",\"curr_activity_code\":0,\"activity_end_time\":0,\"token\":\"d0c97wq\\/3qgHSq58TO1dydGeij0zZW+NptAc1OghCm0Tn30HePUth3PerR5Ydujxc895r1imU0cFhC\\/kmcXcEccHaatgYSUSHBu5xGJImKnskvWOTz\\/\\/c1BW1j63PXWegC9W3cixSnL9iPlwbbkqFriv1PvTfMQGIJymDeHxAhuzGUkJetMgN8oofVvRAboI3sDX+0C8mB2+3OcUG9P2oDs1zeCsLns+PikSzWJ1w6dxK2pQOLVjDn+GNkbNM8uNCXPwdpm4Dui8Lz8qHM4Y2S0EoUgJLY0hBw\",\"pansuk\":\"UP2L9G4qCdDCMdmHoLfwig\",\"sharedir\":0,\"skinName\":\"dusk\",\"urlparam\":[],\"XDUSS\":\"pansec_DCb740ccc5511e5e8fedcff06b081203-tfvKJ0rW02FGQbRDkHpssCK%2BRYGr2B2g3QgHsx%2Bh1LV6zAu2gDD6PLlcY9fuofHt67zHu10iBtIt2MQIFp91vZPkPRZSLjpFlSxr%2Bfb8uqdGghoYEeZisWiefD0deCsLKnv3EftZ3F3DOx71jK1BrvYf0yelVQzv4XkzYOqth26hk9b40bn2t7X1OnOLBE%2FyJuR2fIDaPcesKu%2BL3htlp%2BdbcE7FC00vTaEId91GJl3wRtoWKzbncuObk62JNjIe6ZsG%2FON%2B4M1%2FZ8nepLOV4g%3D%3D\"};\n" +
				"            var yunData";

		Pattern pattern = Pattern.compile("^var context=(.*);");

		Matcher matcher = pattern.matcher(str);

		if (matcher.find()) {

			System.out.println(matcher.group(1));
		}

	}

	@Test
	public void jsonTest() throws Exception {
		File file = new File(this.getClass().getResource("/json/file.json").toURI());

		JSONArray array = JSONUtil.readJSONArray(file, CharsetUtil.CHARSET_UTF_8);

		System.out.println(array.toJSONString(4));
	}

	@Test
	public void byteTest() throws Exception {
//		System.out.println(new DecimalFormat("#.00").format(2.155));
//		System.out.println(new Date(1405403092000L));

		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

		List<BaiduFile> files = mapper.readValue(this.getClass().getResource("/json/file.json"), javaType);


		System.out.println(JSONUtil.parseArray(files).toJSONString(4));
	}

	@Test
	public void getTest() {
		Map<String, Object> map = new HashMap<String, Object>() {{
			put("checkexpire", 1);
			put("checkfree", 1);
			put("channel", "chunlei");
			put("web", 1);
			put("appid", 250528);
			put("clienttype", 0);
			put("bdstoken", "71c5dd92e30a75521a9673df6ed507cd");
			put("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw==");
		}};

		HttpResponse rs = HttpRequest.get(Constant.QUOTA_URL)
				.form(map).cookie(CollectionUtil.join(CookieUtil.COOKIES, "; ")).execute();

		Console.log(rs);

	}

	@Test
	public void cookieTest() {
		CookieUtil.setCookies("aa=11", "bb=22");

	}

	@Test
	public void logTest() {
		logger.error(new RuntimeException("日志测试"));

	}

	@Test
	public void md5Test(){
		System.out.println(SecureUtil.md5("123456"));
		System.out.println(RandomUtil.randomUUID());
		System.out.println(RandomUtil.simpleUUID());
		System.out.println(RandomUtil.randomNumbers(6));
	}

}