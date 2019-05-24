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
import javafx.scene.layout.Background;

public class PathChooserDialog extends Dialog<ButtonType> {

	private TreeView<BaiduFile> treeView = new TreeView<>();

	private BaiduFile root = new BaiduFile();

	private TreeItem<BaiduFile> rootItem = new TreeItem<>(root);

	private LoadDataService loadDataService = new LoadDataService();

	/**
	 * @param source 待移动的文件/文件夹
	 */
	public PathChooserDialog(BaiduFile source) {
		root.setFileName("全部文件");
		rootItem.setExpanded(true);
		treeView.setRoot(rootItem);
		treeView.setPrefWidth(400);
		treeView.setPrefHeight(260);
		treeView.getSelectionModel().select(rootItem);

		treeView.setBackground(Background.EMPTY);

		treeView.setCellFactory(tree -> new TreeCell<BaiduFile>() {

			@Override
			protected void updateItem(BaiduFile item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					this.setGraphic(null);
				} else {
					String name = item.getFileName();
					if (StrUtil.isBlank(name)) name = item.getPath().substring(item.getPath().lastIndexOf("/") + 1);
					this.setGraphic(new Label(name, new ImageView(new Image("image/FileType/Small/FolderType.png"))));
				}
			}
		});

		this.setTitle("选择网盘保存路径");
		this.initOwner(App.primaryStage);
		this.getDialogPane().setContent(treeView);
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

		this.setOnCloseRequest(event -> {
			TreeItem<BaiduFile> target = treeView.getSelectionModel().getSelectedItem();
			if (source == null) {

			} else {

			}
		});

		BaiduFile loading = new BaiduFile();
		loading.setFileName("正在获取子目录");

		loadDataService.setOnSucceeded(event -> {

			loadDataService.getValue().forEach(file -> {

				TreeItem<BaiduFile> item = new TreeItem<>(file);

				ObservableList<TreeItem<BaiduFile>> children = item.getChildren();

				if (!file.isDirEmpty()) {
					children.addAll(new TreeItem<>());
				}

				item.expandedProperty().addListener((observable, oldValue, newValue) -> {
					treeView.getSelectionModel().select(item);

					if (newValue) {


						if (children.size() == 1 && children.get(0).getValue() == null) {
							children.get(0).setValue(loading);

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

		});

		LoadDataService.Query query = loadDataService.new Query();
		query.setPath("/");
		query.setUrl(Constant.LIST_URL);
		query.getExtra().put("folder", 1);

		loadDataService.load(query);
	}
}
