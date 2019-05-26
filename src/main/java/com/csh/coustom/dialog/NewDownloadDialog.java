package com.csh.coustom.dialog;

import cn.hutool.core.util.StrUtil;
import com.csh.app.App;
import com.csh.model.BaiduFile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class NewDownloadDialog extends Dialog {

	private String path;

	private StringProperty url = new SimpleStringProperty();

	private VBox box = new VBox();

	private DialogPane dialogPane = this.getDialogPane();

	public String getPath() {
		return path;
	}

	public String getUrl() {
		return url.get();
	}

	public StringProperty urlProperty() {
		return url;
	}

	public void setUrl(String url) {
		this.url.set(url);
	}

	public NewDownloadDialog() {
		Label linkLabel = new Label("填写下载链接");
		linkLabel.getStyleClass().add("link-label");

		TextField input = new TextField();
		input.getStyleClass().add("link-input");
		input.textProperty().bindBidirectional(url);
		input.setPromptText("支持HTTP、FTP、磁力链接、电驴链接下载");

		Label error = new Label("请输入下载链接");
		error.setVisible(false);
		error.setPrefHeight(40);
		error.setTextFill(Color.RED);

		Label pathLabel = new Label("保存到：我的网盘");

		Button btn = new Button("更改");
		btn.setPrefWidth(80);
		btn.setOnAction(event -> {
			PathChooserDialog dialog = new PathChooserDialog();
			dialog.showAndWait().ifPresent(buttonType -> {
				if (ButtonType.OK.equals(buttonType)) {
					BaiduFile selected = dialog.getSelected();
					this.path = selected.getPath();
					pathLabel.setText("保存到：我的网盘" + this.path);
					pathLabel.getTooltip().setText("我的网盘" + this.path);
				}
			});
		});

		pathLabel.setGraphic(btn);
		pathLabel.setTooltip(new Tooltip("我的网盘"));
		pathLabel.setContentDisplay(ContentDisplay.RIGHT);

		box.getStyleClass().add("download-box");
		box.getChildren().addAll(linkLabel, input, error, pathLabel);

		url.addListener((observable, oldValue, newValue) -> {
			if (StrUtil.isBlank(newValue)) {
				error.setVisible(true);
				input.getStyleClass().add("error");
			} else {
				error.setVisible(false);
				input.getStyleClass().remove("error");
			}
		});


		this.setTitle("新建下载任务");
		this.initOwner(App.primaryStage);
		this.setOnCloseRequest(event -> {
			if (url.isEmpty().get()) {
				event.consume();
				error.setVisible(true);
				input.requestFocus();
				input.getStyleClass().add("error");
			} else input.getStyleClass().remove("error");
		});

		dialogPane.setContent(box);
		dialogPane.setPrefWidth(550);
		dialogPane.setPrefHeight(200);
		dialogPane.getStylesheets().add("css/dialog.css");
		dialogPane.getButtonTypes().addAll(new ButtonType("开始下载", ButtonBar.ButtonData.OK_DONE));
	}

}
