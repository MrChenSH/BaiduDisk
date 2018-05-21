package com.csh.controller;

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
import net.sf.json.JSONObject;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class LoginController implements Initializable {

    private static Logger logger = Logger.getLogger(LoginController.class);

    @FXML
    private WebView webView;

    private WebEngine engine;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            URI uri = URI.create(Constant.BASE_URL);

            Map<String, List<String>> headers = new LinkedHashMap<>();
            headers.put("Set-Cookie", CookieUtil.cookies);
            CookieHandler.getDefault().put(uri, headers);


            engine = webView.getEngine();

            engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (Worker.State.SUCCEEDED.equals(newValue)) {
                    try {
                        Object obj = engine.executeScript("if(typeof yunData === 'object')JSON.stringify(yunData);else '{}'");

                        JSONObject yunData = JSONObject.fromObject(obj);

                        if (!yunData.isEmpty()) {
                            String strs = CookieHandler.getDefault().get(uri, headers).get("Cookie").get(0);

                            RequestProxy.setYunData(yunData);
                            CookieUtil.setCookies(strs.split("; "));
                            Constant.DEFAULT_HEADERS.add(new BasicHeader("Cookie", strs));

                            Platform.runLater(() -> {
                                try {
                                    app.getPrimaryStage().hide();
                                    app.generateMainPanel();
                                    app.getPrimaryStage().show();
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
