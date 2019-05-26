package com.csh.coustom.dialog;

import cn.hutool.core.io.FileUtil;
import com.csh.app.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * 保存文件对话框
 */
public class SaveDialog extends Dialog<ButtonType> {

	private DialogPane dialogPane = this.getDialogPane();

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
		input.setPromptText("请选择保存路径");
		input.getStyleClass().add("path-input");
		input.textProperty().bindBidirectional(filePath);

		Button button = new Button("浏览");
		button.setPrefWidth(75);


		Label capacity = new Label("磁盘剩余空间");
		capacity.setAlignment(Pos.CENTER_LEFT);
		capacity.getStyleClass().add("capacity");

		CheckBox checkBox = new CheckBox("设置为默认下载路径");

		button.setOnAction(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File file = chooser.showDialog(App.primaryStage);
			if (file != null) {
				this.setFilePath(file.getAbsolutePath());
				capacity.setText("磁盘剩余空间：" + FileUtil.readableFileSize(file.getFreeSpace()));
			}
		});

		VBox box = new VBox(capacity, checkBox);

		BorderPane content = new BorderPane();
		content.setLeft(label);
		content.setCenter(input);
		content.setRight(button);
		content.setBottom(box);
		content.getStyleClass().add("save-box");

		BorderPane.setAlignment(label, Pos.CENTER);
		BorderPane.setAlignment(capacity, Pos.CENTER_LEFT);

		this.setTitle("设置下载保存路径");
		this.initOwner(App.primaryStage);
		this.setOnCloseRequest(event -> {
			System.out.println(event);
		});

		dialogPane.setPrefWidth(525);
		dialogPane.setContent(content);
		dialogPane.getStylesheets().add("css/dialog.css");
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
	}
}
