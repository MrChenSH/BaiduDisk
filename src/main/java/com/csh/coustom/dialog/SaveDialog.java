package com.csh.coustom.dialog;

import com.csh.app.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * 保存文件对话框
 */
public class SaveDialog extends Dialog<ButtonType> {

	private StringProperty filePath = new SimpleStringProperty();

	public String getFilePath() {
		return filePath.get();
	}

	public StringProperty filePathProperty() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath.set(filePath);
	}

	public SaveDialog() {
		Label label = new Label("下载到 ");

		TextField input = new TextField();
		input.setEditable(false);
		input.setMaxWidth(280);
		input.setPromptText("请选择保存路径");
		input.textProperty().bindBidirectional(filePath);

		Button button = new Button("浏览");
		button.setPrefWidth(75);
		button.setOnAction(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			if (filePath.isNotEmpty().get())
				chooser.setInitialDirectory(new File(filePath.getValue()));
			File file = chooser.showDialog(App.primaryStage);
			if (file != null) this.setFilePath(file.getAbsolutePath());
		});

		CheckBox checkBox = new CheckBox("设置为默认下载路径");
		checkBox.setPrefHeight(30);

		BorderPane content = new BorderPane();
		content.setLeft(label);
		content.setCenter(input);
		content.setRight(button);
		content.setBottom(checkBox);
		content.setStyle("-fx-border-color: #cfcfcf;-fx-border-width: 0 0 1 0;-fx-padding: 20 10 0 10");
		BorderPane.setAlignment(label, Pos.CENTER);

		this.setTitle("设置保存路径");
		this.initOwner(App.primaryStage);
		this.setOnCloseRequest(event -> {
			System.out.println(event);
		});

		DialogPane dialogPane = this.getDialogPane();
		dialogPane.setPrefWidth(435);
		dialogPane.setContent(content);
		dialogPane.setBackground(Background.EMPTY);
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	}
}
