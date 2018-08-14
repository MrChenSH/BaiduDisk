package com.csh.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.csh.app.App;
import com.csh.http.RequestProxy;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.log4j.Logger;

import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Deprecated
public class LoginController implements Initializable {

	private static final Logger logger = Logger.getLogger(LoginController.class);

	@FXML
	private WebView webView;

	private WebEngine engine;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			URI uri = URI.create(Constant.BASE_URL);

			Map<String, List<String>> headers = new LinkedHashMap<>();
			headers.put("Set-Cookie", CookieUtil.COOKIES);
			CookieHandler.getDefault().put(uri, headers);

			engine = webView.getEngine();

			engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
				if (Worker.State.SUCCEEDED.equals(newValue)) {
					try {
						Object obj = engine.executeScript("if(typeof yunData === 'object')JSON.stringify(yunData);else '{}'");

						JSONObject yunData = JSONUtil.parseObj(obj);

						if (CollectionUtil.isNotEmpty(yunData)) {
							List<String> cookies = CookieHandler.getDefault().get(uri, headers).get("Cookie");

							CookieUtil.setCookies(cookies.get(0));
							Platform.runLater(() -> {
								try {
									App.primaryStage.hide();
									App.generateMainPanel();
									App.primaryStage.show();
									// 清除Cookie
									CookieHandler.getDefault().get(uri, headers).clear();
								} catch (Exception e) {
									logger.error(e.getMessage(), e);
								}
							});
						}
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
