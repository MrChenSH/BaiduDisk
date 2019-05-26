package com.csh.coustom.dialog;

import cn.hutool.core.util.StrUtil;
import com.csh.app.App;
import com.csh.model.BaiduFile;
import com.csh.service.LoadDataService;
import com.csh.utils.Constant;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class PathChooserDialog extends Dialog<ButtonType> {

	private TreeView<BaiduFile> treeView = new TreeView<>();

	private TreeItem<BaiduFile> rootItem = new TreeItem<>(new BaiduFile());

	private LoadDataService loadDataService = new LoadDataService();

	private DialogPane dialogPane = this.getDialogPane();

	public BaiduFile getSelected() {
		return treeView.getSelectionModel().getSelectedItem().getValue();
	}

	public PathChooserDialog() {
		BaiduFile loading = new BaiduFile();
		loading.setFileName("正在获取子目录");

		treeView.setRoot(rootItem);
		treeView.getSelectionModel().select(rootItem);

		rootItem.setExpanded(true);
		rootItem.getValue().setPath("/");
		rootItem.getValue().setFileName("全部文件");
		rootItem.getChildren().add(new TreeItem<>(loading));

		treeView.setCellFactory(tree -> new TreeCell<BaiduFile>() {

			@Override
			protected void updateItem(BaiduFile item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					this.setGraphic(null);
				} else {
					String name = item.getFileName();
					if (StrUtil.isBlank(name)) {
						String path = item.getPath();
						if (Objects.equals(path, "/apps")) name = "我的应用数据";
						else name = path.substring(path.lastIndexOf("/") + 1);
					}

					ImageView icon = new ImageView(new Image("image/FileType/Small/FolderType.png"));

					if (loading.equals(item)) {
						icon.setImage(new Image("image/loading_20.gif"));
					}

					this.setGraphic(new Label(name, icon));
				}
			}
		});

		this.setTitle("选择网盘保存路径");
		this.initOwner(App.primaryStage);

		dialogPane.setPrefWidth(415);
		dialogPane.setPrefHeight(280);
		dialogPane.setContent(treeView);
		dialogPane.getStylesheets().add("css/dialog.css");
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

		loadDataService.setOnSucceeded(event -> {

			loadDataService.getValue().forEach(file -> {

				TreeItem<BaiduFile> item = new TreeItem<>(file);

				ObservableList<TreeItem<BaiduFile>> children = item.getChildren();

				if (!file.isDirEmpty()) {
					children.addAll(new TreeItem<>(loading));
				}

				item.expandedProperty().addListener((observable, oldValue, newValue) -> {
					treeView.getSelectionModel().select(item);

					if (newValue) {
						if (children.size() == 1 && loading.equals(children.get(0).getValue())) {
							LoadDataService.Query query = loadDataService.new Query();
							query.setUrl(Constant.LIST_URL);
							query.getExtra().put("folder", 1);
							query.setPath(item.getValue().getPath());
							loadDataService.load(query);
						}
					}
				});

				treeView.getSelectionModel().getSelectedItem().getChildren().add(item);
			});

			ObservableList<TreeItem<BaiduFile>> items = treeView.getSelectionModel().getSelectedItem().getChildren();

			if (items.size() > 1 && loading.equals(items.get(0).getValue())) items.remove(0);
		});

		LoadDataService.Query query = loadDataService.new Query();
		query.setPath("/");
		query.setUrl(Constant.LIST_URL);
		query.getExtra().put("folder", 1);

		loadDataService.load(query);
	}
}
