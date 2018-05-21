package com.csh.test;


import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

    }


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
    public void urlParamsTest() throws Exception {
        String urlParams = "err_no=0&callback=&codeString=&userName=13098869374&phoneNumber=&mail=&hao123Param=MTRTM05HVmxsc1ltUkNiWE0yZmpKUGZrUnZkVEJpTTFsUldXSXRRMEZJWkdnM2FtWkhTbE42Ym0wdExWWmFTVkZCUVVGQkpDUUFBQUFBQUFBQUFBRUFBQUIxV084c3M4TE85VXBoWTJzQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFPWnV2bG5tYnI1WlUw&u=https://pan.baidu.com/disk/home&tpl=netdisk&secstate=&gotourl=&authtoken=&loginproxy=&resetpwd=&vcodetype=&lstr=&ltoken=&bckv=1&bcsync=mYDGcsTitrcMMaVWlCBYy97wHaJXxQPTOgJ%2FayxfqCCBehO59O8V03NruPgFKIWnssnt%2Bvq06GCy9OGdhwIv8p80gjwUOXZio0xhUmzPAb3Qa%2BNI2tFejVX9CQVLmMzodZIVtbuZWgEuhR0vGFJ2C85lpc0sXPZ4MuNYGbXZj%2BiVNZ8h%2BQCIPU%2F%2F5I6jtmmM%2FyfvONLGS6X2090IGF0bHmTxxPllwKcGTe6nLbTKp9kDeriJn6xNp1eUEVE0WnjTr3Sgfgb3DJ56lLrs237rAOI5XTObU%2BFXe7dewRtmRWKAWx2yhqOti9ViQm9AkKh0eYnyDgXfEvqzFXr68MK%2FIA%3D%3D&bcchecksum=3164022821&code=&bdToken=&realnameswitch=&setpwdswitch=&bctime=1505652454&bdstoken=&authsid=&jumpset=&appealurl=&realnameverifyemail=0&realnameauthsid=";

        List<NameValuePair> params = URLEncodedUtils.parse(urlParams, Charset.defaultCharset());

        System.out.println(URLEncoder.encode(urlParams, Constant.CHARSET_UTF_8));

        JSONObject json = new JSONObject();
        params.forEach(nameValuePair ->
        {
            json.put(nameValuePair.getName(), nameValuePair.getValue());
        });

        System.out.println(json);
    }

    @Test
    public void jsonTest() throws Exception {
        File file = new File(this.getClass().getResource("/json/file.json").toURI());

        JSONArray array = JSONArray.fromObject(FileUtils.readFileToString(file, Constant.CHARSET_UTF_8));


        System.out.println(array);
    }

    @Test
    public void byteTest() throws Exception {
//		System.out.println(new DecimalFormat("#.00").format(2.155));
//		System.out.println(new Date(1405403092000L));

        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

        List<BaiduFile> files = mapper.readValue(this.getClass().getResource("/json/file.json"), javaType);


        System.out.println(JSONArray.fromObject(files));
    }

}