package com.csh.app;

import cn.hutool.cron.CronUtil;
import com.csh.controller.LoginController;
import com.csh.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class App extends Application {

	private static final Logger logger = Logger.getLogger(App.class);

	public static Stage primaryStage;

	public static final Font FontAwesome;

	static {
		FontAwesome = Font.loadFont(App.class.getResourceAsStream("/fonts/fontawesome.ttf"), 16);

		// 程序启动时检测cookie文件是否存在，不存在则新建
		File file = new File(System.getProperty("user.dir") + "/config/cookie.ini");

		if (!file.exists()) {
			try {
				file.getParentFile().mkdir();
				file.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void start(Stage primaryStage) {
		logger.info("程序启动中，请稍候……");
		try {
			App.primaryStage = primaryStage;
			MainController controller = (MainController) loadFXML("/fxml/Main.fxml");
			primaryStage.centerOnScreen();
			primaryStage.setTitle("百度网盘");
			primaryStage.getIcons().add(new Image("/image/logo.png"));
			primaryStage.show();
			new Thread(controller.new LoginCheckTask()).start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 程序停止同时终止定时任务
	 *
	 * @throws Exception
	 */
	@Override
	public void stop() throws Exception {
		CronUtil.stop();
		super.stop();
	}

	/**
	 * 载入登录界面
	 *
	 * @return
	 * @throws Exception
	 */
	public static LoginController generateLoginPanel() throws Exception {
		primaryStage.setTitle("百度网盘 - 登录");
		return (LoginController) loadFXML("/fxml/Login.fxml");
	}

	/**
	 * 载入主界面
	 *
	 * @return
	 * @throws Exception
	 */
	public static MainController generateMainPanel() throws Exception {
		primaryStage.setTitle("百度网盘");
		return (MainController) loadFXML("/fxml/Main.fxml");
	}

	/**
	 * 加载FXML文件
	 *
	 * @param fxml
	 * @return
	 * @throws Exception
	 */
	private static Initializable loadFXML(String fxml) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		InputStream in = App.class.getResourceAsStream(fxml);
		try {
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			loader.setLocation(App.class.getResource(fxml));
			primaryStage.setScene(new Scene(loader.load(in)));
			return loader.getController();
		} finally {
			if (in != null) in.close();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
