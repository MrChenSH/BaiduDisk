package com.csh.coustom.dialog;

import com.csh.app.App;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

public class FloatingDialog extends Dialog {

	private static final FloatingDialog dialog = new FloatingDialog();

	private FloatingDialog() {
		DialogPane dialogPane = this.getDialogPane();

		Label icon = new Label();
//		icon.setMaxHeight(Double.MAX_VALUE);
		icon.getStyleClass().add("icon");

		Label status = new Label("拖拽上传");
		status.getStyleClass().add("status");
//		status.setMaxWidth(Double.MAX_VALUE);
//		status.setMaxHeight(Double.MAX_VALUE);

		BorderPane pane = new BorderPane(status);

		pane.setLeft(icon);
		pane.getStyleClass().add("box");

		BorderPane.setAlignment(status, Pos.CENTER);

		dialogPane.setContent(pane);
		dialogPane.getStyleClass().add("floating");
		dialogPane.getButtonTypes().add(ButtonType.CLOSE);
		dialogPane.getStylesheets().add("css/dialog.css");

		this.initOwner(App.primaryStage);
		this.initModality(Modality.NONE);
//		this.initStyle(StageStyle.TRANSPARENT);
	}

	public static FloatingDialog getInstance() {
		return dialog;
	}

	public static void setVisible(boolean visible) {
		if (visible) dialog.show();
		else dialog.close();
	}

}
