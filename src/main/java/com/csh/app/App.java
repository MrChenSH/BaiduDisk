package com.csh.app;

import com.csh.controller.LoginController;
import com.csh.controller.MainController;
import com.csh.http.RequestProxy;
import com.csh.utils.Constant;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Map;

public class App extends Application
{

	private Parent root = new Group();

	private Stage primaryStage;

	public Parent getRoot()
	{
		return root;
	}

	public void setRoot(Parent root)
	{
		this.root = root;
	}

	public Stage getPrimaryStage()
	{
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		this.setPrimaryStage(primaryStage);
		this.generateLoginPanel();
//		this.generateMainPanel();
		primaryStage.show();
	}

	/**
	 * 载入登录界面
	 *
	 * @return
	 * @throws Exception
	 */
	public LoginController generateLoginPanel() throws Exception
	{
		RequestProxy.getToken();
		primaryStage.setResizable(false);
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
	public MainController generateMainPanel() throws Exception
	{
		primaryStage.setMinWidth(900);
		primaryStage.setMinHeight(600);
		primaryStage.setResizable(true);
		primaryStage.setTitle("百度网盘");
		primaryStage.centerOnScreen();
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
	private Initializable loadFXML(String fxml) throws Exception
	{
		FXMLLoader loader = new FXMLLoader();
		InputStream in = App.class.getResourceAsStream(fxml);
		try
		{
			loader.setBuilderFactory(new JavaFXBuilderFactory());
			loader.setLocation(App.class.getResource(fxml));
			primaryStage.setScene(new Scene(loader.load(in)));
			return loader.getController();
		} finally
		{
			if (in != null) in.close();
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
