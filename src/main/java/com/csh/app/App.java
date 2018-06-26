package com.csh.app;

import com.csh.controller.LoginController;
import com.csh.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.InputStream;

public class App extends Application {

	private static final Logger logger = Logger.getLogger(App.class);

	private Stage primaryStage;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	static {
		Font.loadFont(App.class.getResourceAsStream("/fonts/fontawesome.ttf"), 16);
	}

	@Override
	public void start(Stage primaryStage) {
		logger.info("程序启动中，请稍候……");
		try {
			this.setPrimaryStage(primaryStage);
			((MainController) this.loadFXML("/fxml/Main.fxml")).setApp(this);
			Rectangle2D bounds = Screen.getPrimary().getBounds();

			primaryStage.setTitle("百度网盘");
			//		primaryStage.setMinWidth(bounds.getWidth() * 3 / 5);
			//		primaryStage.setMinHeight(bounds.getHeight() * 0.75);
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
	public LoginController generateLoginPanel() throws Exception {
//        primaryStage.setResizable(false);
		primaryStage.setTitle("百度网盘 - 登录");
		LoginController controller = (LoginController) this.loadFXML("/fxml/Login.fxml");
		controller.setApp(this);
		return controller;
	}

	/**
	 * 载入主界面
	 *
	 * @return
	 * @throws Exception
	 */
	public MainController generateMainPanel() throws Exception {
		primaryStage.setTitle("百度网盘");
		MainController controller = (MainController) this.loadFXML("/fxml/Main.fxml");
		controller.setApp(this);
		return controller;
	}

	/**
	 * 加载FXML文件
	 *
	 * @param fxml
	 * @return
	 * @throws Exception
	 */
	private Initializable loadFXML(String fxml) throws Exception {
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
