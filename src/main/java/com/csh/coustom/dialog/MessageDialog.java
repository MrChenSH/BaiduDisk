package com.csh.coustom.dialog;

import com.csh.app.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class MessageDialog {

	public static Optional<ButtonType> show(String message, Throwable e) {
		Alert dialog = new Alert(Alert.AlertType.ERROR);

		dialog.setTitle("系统提示");
		dialog.setHeaderText(message);
		dialog.initOwner(App.primaryStage);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.close();
		pw.flush();

		TextArea textArea = new TextArea(sw.toString());
		textArea.setWrapText(true);
		textArea.setEditable(false);

		try {
			sw.close();
			sw.flush();
		} catch (IOException ex) {
		}

		final DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setBackground(Background.EMPTY);
		dialogPane.setExpandableContent(new BorderPane(textArea));
		return dialog.showAndWait();
	}


	public static Optional<ButtonType> show(String message) {
		return show(message, Alert.AlertType.INFORMATION);
	}

	public static Optional<ButtonType> show(String message, Alert.AlertType type) {
		Alert dialog = new Alert(type);
		dialog.setTitle("系统提示");
		dialog.setHeaderText(message);
		dialog.initOwner(App.primaryStage);
		dialog.getDialogPane().setBackground(Background.EMPTY);
		return dialog.showAndWait();
	}
}
