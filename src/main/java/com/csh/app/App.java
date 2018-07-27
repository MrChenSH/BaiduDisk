package com.csh.app;

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

import java.io.InputStream;

public class App extends Application {

	private static final Logger logger = Logger.getLogger(App.class);

	public static Stage primaryStage;

	public static Font FontAwesome;

	static {
		FontAwesome = Font.loadFont(App.class.getResourceAsStream("/fonts/fontawesome.ttf"), 16);
	}

	@Override
	public void start(Stage primaryStage) {
		logger.info("程序启动中，请稍候……");
		try {
			this.primaryStage = primaryStage;
			this.loadFXML("/fxml/Main.fxml");
//			generateLoginPanel();
			primaryStage.setTitle("百度网盘");
			primaryStage.centerOnScreen();
			primaryStage.getIcons().add(new Image("/icon/BaiduNetdisk.png"));
			primaryStage.show();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

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
