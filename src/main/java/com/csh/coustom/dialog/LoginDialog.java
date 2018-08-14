package com.csh.coustom.dialog;

import com.csh.app.App;
import com.csh.http.RequestProxy;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.CookieHandler;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoginDialog extends Stage {

	private static final Logger logger = Logger.getLogger(LoginDialog.class);

	private WebView webView = new WebView();

	private WebEngine engine = webView.getEngine();

	public LoginDialog() {
		this.centerOnScreen();
		this.initOwner(App.primaryStage);
		this.initModality(Modality.WINDOW_MODAL);
		this.setScene(new Scene(webView, 400, 400));
		this.getIcons().add(new Image("/image/BaiduNetdisk.png"));
		try {
			URI uri = URI.create(Constant.HOME_URL);

			Map<String, List<String>> headers = new LinkedHashMap<>();
			headers.put("Set-Cookie", CookieUtil.COOKIES);
			CookieHandler.getDefault().put(uri, headers);

			engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
				if (Worker.State.SUCCEEDED.equals(newValue) && engine.getLocation().startsWith(Constant.HOME_URL)) {
					try {
						List<String> cookies = CookieHandler.getDefault().get(uri, headers).get("Cookie");
						// 保存cookie至本地
						CookieUtil.setCookies(cookies.get(0));
						// 清除Cookie
						CookieHandler.getDefault().get(uri, headers).clear();
						RequestProxy.getYunData();
						this.close();
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
			engine.load(uri.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}