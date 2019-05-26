package com.csh.coustom.dialog;

import com.csh.app.App;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * 分享对话框
 */
public class ShareDialog extends Dialog<ButtonType> {

	private GridPane sharePane = new GridPane();

	private DialogPane dialogPane = this.getDialogPane();

	/**
	 * 过期时间属性
	 */
	private ObjectProperty<Period> periodProperty = new SimpleObjectProperty();

	/**
	 * 私密分享属性
	 */
	private BooleanProperty privateProperty = new SimpleBooleanProperty(true);

	public int getPeriod() {
		return periodProperty.get().value;
	}

	public boolean isPrivate() {
		return privateProperty.get();
	}

	public ShareDialog() {
		sharePane.setPrefWidth(400);
		sharePane.setPrefHeight(160);
		sharePane.getColumnConstraints().clear();

		this.createSharePane();
		this.setTitle("分享文件");
		this.initOwner(App.primaryStage);

		dialogPane.setContent(sharePane);
		dialogPane.getStylesheets().add("css/dialog.css");
	}

	/**
	 * 分享完成显示信息面板
	 */
	public void createShareInfoPane(String shareUrl, String password) {
		sharePane.getColumnConstraints().clear();
		sharePane.getColumnConstraints().addAll(new ColumnConstraints());

		ObservableList<Node> children = sharePane.getChildren();
		children.clear();

		String shareMsg = "成功创建" + (isPrivate() ? "私密链接" : "公开链接");
		String periodMsg = "，链接永久有效";

		if (getPeriod() != 0) {
			periodMsg = "，链接" + getPeriod() + "天后失效";
		}

		Label label_1 = new Label(shareMsg + periodMsg);
		label_1.setTextFill(Color.web("#3b8cff"));

		TextField link = new TextField(shareUrl);
		link.setPrefHeight(30);
		link.setPrefWidth(380);
		link.setEditable(false);

		Label label_2 = new Label("提取密码：");
		label_2.visibleProperty().bindBidirectional(privateProperty);

		TextField pwd = new TextField(password);
		pwd.setPrefHeight(30);
		pwd.setPrefWidth(100);
		pwd.setEditable(false);

		label_2.setGraphic(pwd);
		label_2.setContentDisplay(ContentDisplay.RIGHT);

		GridPane.setRowIndex(link, 1);
		GridPane.setRowIndex(label_2, 2);

		children.addAll(label_1, link, label_2);

		dialogPane.getButtonTypes().clear();
		dialogPane.getButtonTypes().addAll(new ButtonType("复制链接" + (isPrivate() ? "及密码" : ""), ButtonBar.ButtonData.OK_DONE),
				new ButtonType("关闭", ButtonBar.ButtonData.CANCEL_CLOSE));
	}

	/**
	 * 构建分享面板
	 *
	 * @return
	 */
	private void createSharePane() {
		sharePane.getColumnConstraints().addAll(new ColumnConstraints() {{
			setPrefWidth(70);
			setHalignment(HPos.RIGHT);
		}}, new ColumnConstraints());

		sharePane.getRowConstraints().addAll(new RowConstraints() {{
			setPrefHeight(45);
		}}, new RowConstraints(), new RowConstraints() {{
			setVgrow(Priority.ALWAYS);
		}});

		ObservableList<Node> children = sharePane.getChildren();
		Label label_1 = new Label("分享形式：");

		ToggleGroup tg = new ToggleGroup();

		RadioButton privateBtn = new RadioButton("加密");
		privateBtn.setToggleGroup(tg);
		privateBtn.setContentDisplay(ContentDisplay.RIGHT);
		privateBtn.selectedProperty().bindBidirectional(privateProperty);

		Label label_2 = new Label("输入密码才能查看、下载，更加私密安全");
		label_2.setTextFill(Color.GRAY);

		privateBtn.setGraphic(label_2);

		RadioButton publicBtn = new RadioButton("公开");
		publicBtn.setToggleGroup(tg);
		publicBtn.setContentDisplay(ContentDisplay.RIGHT);

		Label label_3 = new Label("任何人可查看或下载，同时出现在您的个人主页");
		label_3.setTextFill(Color.GRAY);

		publicBtn.setGraphic(label_3);

		Label label_4 = new Label("有效期：");
		GridPane.setRowIndex(label_4, 2);

		ComboBox<Period> box = new ComboBox<>();
		box.valueProperty().bindBidirectional(periodProperty);
		box.getItems().addAll(new Period("永久有效", 0), new Period("7天", 7), new Period("1天", 1));
		box.getSelectionModel().selectFirst();

		GridPane.setColumnIndex(privateBtn, 1);

		GridPane.setRowIndex(publicBtn, 1);
		GridPane.setColumnIndex(publicBtn, 1);

		GridPane.setRowIndex(box, 2);
		GridPane.setColumnIndex(box, 1);

		children.addAll(label_1, privateBtn, publicBtn, label_4, box);

		dialogPane.getButtonTypes().addAll(new ButtonType("创建链接", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
	}

	private class Period {
		public String text;
		public Integer value;

		public Period(String text, Integer value) {
			this.text = text;
			this.value = value;
		}

		@Override
		public String toString() {
			return this.text;
		}
	}
}
