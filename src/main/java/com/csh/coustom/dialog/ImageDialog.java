package com.csh.coustom.dialog;

import com.csh.app.App;
import com.csh.model.BaiduFile;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片查看对话框
 */
public class ImageDialog {

	private static Stage stage = new Stage();

	private static Pagination pagination = new Pagination();

	private static List<BaiduFile> files = new ArrayList<>();

	private static Image loading = new Image("/image/loading.gif");

	static {
		stage.getIcons().addAll(App.primaryStage.getIcons());
		stage.setScene(new Scene(pagination, 1000, 500));

		pagination.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		pagination.setPageFactory(i -> {
			BaiduFile file = files.get(i);
			stage.setTitle(file.getFileName());

			ImageView view = new ImageView(loading);

			Platform.runLater(() -> {
				String url = file.getThumbs().getStr("url1");
				view.setImage(new Image(url.replaceAll("c[0-9]*_u[0-9]*", "c1920_u1080")));
			});

			return view;
		});
	}

	public static void show(List<BaiduFile> files, int index) {
		ImageDialog.files = files;
		stage.show();
		stage.requestFocus();
		stage.centerOnScreen();
		pagination.setPageCount(files.size());
		pagination.setCurrentPageIndex(index);
	}
}
