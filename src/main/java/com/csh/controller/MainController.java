package com.csh.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.csh.app.App;
import com.csh.coustom.control.IconButton;
import com.csh.coustom.control.PathLink;
import com.csh.coustom.dialog.ImageDialog;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.coustom.dialog.SaveDialog;
import com.csh.coustom.dialog.ShareDialog;
import com.csh.http.DownloadUtil;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.csh.utils.FontAwesome;
import com.sun.webkit.network.CookieManager;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainController extends BorderPane implements Initializable {

	private static final Logger logger = Logger.getLogger(MainController.class);

	@FXML
	private ImageView userImage;

	@FXML
	private Label userLabel;

	@FXML
	private ProgressBar quotaBar;

	@FXML
	private Label quotaText;

	@FXML
	private ToolBar tabBar;

	@FXML
	private ToggleButton homeTabBtn;

	@FXML
	private ToggleButton transferTabBtn;

	@FXML
	private ToggleButton cloudDownloadTabBtn;

	@FXML
	private Button logoutBtn;

	@FXML
	private IconButton uploadBtn;

	@FXML
	private IconButton downloadBtn;

	@FXML
	private IconButton shareBtn;

	@FXML
	private IconButton deleteBtn;

	@FXML
	private PathLink backBtn;

	@FXML
	private PathLink forwardBtn;

	@FXML
	private PathLink homeBtn;

	@FXML
	private HBox breadcrumb;

	@FXML
	private PathLink homeLink;

	@FXML
	private PathLink refreshBtn;

	@FXML
	private TextField searchField;

	@FXML
	private IconButton searchBtn;

	@FXML
	private TabPane navigationTabPane;

	@FXML
	private TabPane transferTabPane;

	@FXML
	private BorderPane homePane;

	@FXML
	private BorderPane homePaneBar;

	@FXML
	private BorderPane transferPane;

	@FXML
	private ListView<BaiduFile> transferList;

	@FXML
	private BorderPane completedPane;

	@FXML
	private ListView completedList;

	@FXML
	private TableView<BaiduFile> fileTable;

	@FXML
	private TableColumn<BaiduFile, Boolean> checkBoxColumn;

	@FXML
	private TableColumn<BaiduFile, String> fileNameColumn;

	@FXML
	private TableColumn<BaiduFile, String> modifyTimeColumn;

	@FXML
	private TableColumn<BaiduFile, Long> fileSizeColumn;

	@FXML
	private CheckBox checkAllBox;

	@FXML
	private ContextMenu contextMenu;

	@FXML
	private Label statusLabel;

	@FXML
	private IconButton prevBtn;

	@FXML
	private IconButton nextBtn;

	private Property<Number> transferNameLabelMaxWidth = new SimpleDoubleProperty();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ProgressIndicator loading = new ProgressIndicator();
		Label placeholder = new Label("正在登录，请稍候……", loading);

		loading.setPadding(new Insets(5));
		placeholder.setContentDisplay(ContentDisplay.TOP);

		fileTable.setPlaceholder(placeholder);
		fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		userImage.imageProperty().bindBidirectional(RequestProxy.photoProperty);
		userLabel.textProperty().bindBidirectional(RequestProxy.usernameProperty);
		quotaText.textProperty().bindBidirectional(RequestProxy.quotaTextProperty);
		quotaBar.progressProperty().bindBidirectional(RequestProxy.quotaProgressProperty);

		ToggleGroup group = new ToggleGroup();
		homeTabBtn.setToggleGroup(group);
		homeTabBtn.setGraphic(new Label(FontAwesome.CLOUD.value));
		transferTabBtn.setToggleGroup(group);
		transferTabBtn.setGraphic(new Label(FontAwesome.DOWNLOAD.value));
		cloudDownloadTabBtn.setToggleGroup(group);
		cloudDownloadTabBtn.setGraphic(new Label(FontAwesome.CLOUD_DOWNLOAD.value));
		group.selectToggle(homeTabBtn);

		group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) group.selectToggle(oldValue);
			navigationTabPane.getSelectionModel().select(group.getToggles().indexOf(group.getSelectedToggle()));
		});

		checkBoxColumn.setCellValueFactory(new PropertyValueFactory("checked"));
		checkBoxColumn.setCellFactory(colums -> new TableCell<BaiduFile, Boolean>() {
			@Override
			protected void updateItem(Boolean checked, boolean empty) {
				super.updateItem(checked, empty);
				if (empty) this.setGraphic(null);
				else {
					BaiduFile item = (BaiduFile) this.getTableRow().getItem();
					if (item != null) {
						ObservableList<BaiduFile> items = fileTable.getItems();
						CheckBox checkBox = new CheckBox();
						checkBox.setFocusTraversable(false);
						// 属性绑定
						checkBox.selectedProperty().bindBidirectional(item.checkedProperty());

						int checkeds = getCheckeds().size();
						checkAllBox.setSelected(checkeds == items.size());
						checkAllBox.setIndeterminate(checkeds > 0 && checkeds < items.size());
						this.setGraphic(checkBox);
						this.setAlignment(Pos.CENTER);
					}
				}
			}
		});

		fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameColumn.setCellFactory(column -> new TableCell<BaiduFile, String>() {
					@Override
					protected void updateItem(String name, boolean empty) {
						super.updateItem(name, empty);
						if (empty) this.setGraphic(null);
						else {
							BaiduFile item = (BaiduFile) this.getTableRow().getItem();
							if (item != null) {
								if (fileTable.isEditable()) {
									fileTable.setEditable(false);

									Node graphic = this.getGraphic();
									TextField editor = new TextField(name);

									editor.setOnAction(event -> {
										this.setGraphic(graphic);
										JSONArray fileList = new JSONArray();
										fileList.add(new JSONObject() {{
											put("path", item.getPath());
											put("newname", editor.getText());
										}});
										boolean success = RequestProxy.manager(Constant.Operate.RENAME, fileList);
										if (success) item.setFileName(editor.getText());
									});

									editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
										if (!newValue) this.setGraphic(graphic);
									});

									this.setGraphic(editor);
									editor.requestFocus();
									if (item.getIsDir()) editor.selectAll();
									else editor.selectRange(0, name.lastIndexOf('.'));
								} else {
									Label icon = new Label(item.getIcon().value);
									icon.getStyleClass().add("icon");

									Label label = new Label(name, icon);
									label.setTooltip(new Tooltip(name));
									this.setGraphic(label);
								}
							}
						}
					}
				}
		);

		modifyTimeColumn.setCellValueFactory(new PropertyValueFactory("modifyTime"));

		fileSizeColumn.setCellValueFactory(new PropertyValueFactory("size"));
		fileSizeColumn.setCellFactory(column -> new TableCell<BaiduFile, Long>() {
					@Override
					protected void updateItem(Long size, boolean empty) {
						super.updateItem(size, empty);
						if (empty) this.setText(null);
						else {
							BaiduFile item = (BaiduFile) this.getTableRow().getItem();
							if (item != null) {
								if (item.getIsDir()) {
									this.setText("文件夹");
									this.setAlignment(Pos.CENTER_LEFT);
								} else {
									this.setAlignment(Pos.CENTER_RIGHT);
									this.setText(FileUtil.readableFileSize(size).toUpperCase());
								}
							}
						}
					}
				}
		);

		fileTable.setRowFactory(tableView -> {
			TableRow<BaiduFile> row = new TableRow<>();

			row.selectedProperty().addListener((observable, oldValue, newValue) -> {
				this.checkAll(false);
				fileTable.getSelectionModel().getSelectedItems().forEach(item -> item.setChecked(true));
			});

			row.setOnMouseClicked(event -> {
				if (MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
					this.onClickToOpenFile(new ActionEvent(event.getSource(), event.getTarget()));
				}
			});

			return row;
		});

		transferList.widthProperty().addListener((observable, oldValue, newValue) -> transferNameLabelMaxWidth.setValue(newValue.doubleValue() - 400));

		transferList.setCellFactory(view -> new ListCell<BaiduFile>() {
			@Override
			protected void updateItem(BaiduFile item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) this.setGraphic(null);
				else {
					BorderPane pane = new BorderPane();

					Label icon = new Label(item.getIcon().value);
					icon.getStyleClass().add("icon");
					icon.setStyle("-fx-font-size: 20px");
					icon.setPrefWidth(40);

					Label nameLabel = new Label(item.getFileName());
					nameLabel.maxWidthProperty().bindBidirectional(transferNameLabelMaxWidth);
					nameLabel.setTooltip(new Tooltip(item.getFileName()));

					Label progressLabel = new Label("0B/" + FileUtil.readableFileSize(item.getSize()).toUpperCase());
					progressLabel.setTextFill(Color.GRAY);

					VBox nameBox = new VBox(nameLabel, progressLabel);
					nameBox.setAlignment(Pos.CENTER_LEFT);

					ProgressBar progressBar = new ProgressBar(0.2);
					progressBar.setPrefWidth(200);
					progressBar.setMinHeight(15);
					progressBar.setPrefHeight(15);

					Label progressTime = new Label(DateUtil.formatTime(new Date()));
					progressTime.setTextFill(Color.GRAY);

					VBox progressBox = new VBox(progressBar, progressTime);
					progressBox.setAlignment(Pos.CENTER_LEFT);

					Label status = new Label("已暂停");
					status.setPrefWidth(70);
					status.setAlignment(Pos.CENTER);

					IconButton controlBtn = new IconButton(FontAwesome.START);
					controlBtn.setPrefWidth(30);
					controlBtn.setFocusTraversable(false);
					controlBtn.setOnAction(event -> {
						view.requestFocus();
						view.getSelectionModel().select(item);
						controlBtn.setIcon(controlBtn.getIcon().equals(FontAwesome.START) ? FontAwesome.PAUSE : FontAwesome.START);
						if (FontAwesome.PAUSE.equals(controlBtn.getIcon())) {
							status.setText("正在下载");


							String url = RequestProxy.download(item.getId());
							File file = new File("G:/Download/" + item.getFileName());
							DownloadUtil downloadUtil = new DownloadUtil(url, file.getAbsolutePath(), 20);
							downloadUtil.download();
							try {
								new Thread(() -> {
									while (downloadUtil.getTotal() < downloadUtil.getFileSize()) {
										Platform.runLater(() -> {
											String s = FileUtil.readableFileSize(downloadUtil.getTotal());

											logger.info(s);

											progressLabel.setText(s + "/" + FileUtil.readableFileSize(item.getSize()));

											progressBar.setProgress(downloadUtil.getCompleteRate());
										});

										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											logger.error(e.getMessage(), e);
										}
									}
								}).start();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}


						}
					});

					IconButton removeBtn = new IconButton(FontAwesome.REMOVE);
					removeBtn.setPrefWidth(30);
					removeBtn.setFocusTraversable(false);
					removeBtn.setOnAction(event -> {
						view.requestFocus();
						view.getItems().remove(item);
					});

					HBox control = new HBox(progressBox, status, controlBtn, removeBtn);
					control.setAlignment(Pos.CENTER);
					control.setPrefHeight(40);

					pane.setLeft(icon);
					pane.setCenter(nameBox);
					pane.setRight(control);

					BorderPane.setAlignment(icon, Pos.CENTER);

					this.setGraphic(pane);
				}
			}
		});

		transferTabPane.setOnMouseClicked(event -> {
			logger.info(event.getTarget());
		});
	}

	@FXML
	private void onClickToUpload() {
		FileChooser chooser = new FileChooser();

		List<File> files = chooser.showOpenMultipleDialog(App.primaryStage);

		System.out.println(files);
	}

	@FXML
	private void onClickToDownload() {
		List<BaiduFile> checkeds = this.getCheckeds();
		if (CollectionUtil.isEmpty(checkeds)) {
			MessageDialog.show("请至少选择一个文件！");
		} else {
			SaveDialog dialog = new SaveDialog();
			dialog.setFilePath("G:\\Download");
			dialog.show();
			dialog.setOnCloseRequest(event -> {
				if (ButtonBar.ButtonData.OK_DONE.equals(dialog.getResult().getButtonData())) {
					if (StrUtil.isBlank(dialog.getFilePath())) {
						event.consume();
						MessageDialog.show("请选择文件保存路径");
						return;
					}

					ObservableList<BaiduFile> items = transferList.getItems();
					checkeds.forEach(item -> {
						if (item.getIsDir()) {
							this.eachFiles(item, items);
						} else {
							items.add(item.clone());
						}
					});
				}
			});

			/*if (file != null) {
				Object[] ids = checkeds.stream().map(BaiduFile::getId).toArray();
				String link = RequestProxy.download(new JSONArray(ids));

				DownloadUtil downloadUtil = new DownloadUtil(link, file.getAbsolutePath(), 10);

				try {
					downloadUtil.download();

					new Thread(() -> {

						while (downloadUtil.getCompleteRate() <= 1) {
							logger.info("已完成:" + downloadUtil.getCompleteRate());
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
					}).start();

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}*/
		}
	}

	/**
	 * 循环获取下载列表
	 *
	 * @param root
	 * @param files
	 * @return
	 */
	private List<BaiduFile> eachFiles(BaiduFile root, List<BaiduFile> files) {
		if (root.getIsDir()) {
			List<BaiduFile> fileList = RequestProxy.getFileList(root.getPath());
			fileList.forEach(file -> this.eachFiles(file, files));
		} else {
			files.add(root);
		}
		return files;
	}

	@FXML
	private void onClickToShare() {
		List<BaiduFile> checkeds = this.getCheckeds();
		if (CollectionUtil.isEmpty(checkeds)) {
			MessageDialog.show("请至少选择一个文件！");
		} else {
			ShareDialog shareDialog = new ShareDialog();

			shareDialog.show();
			shareDialog.setOnCloseRequest(event -> {
				if (ButtonBar.ButtonData.OK_DONE.equals(shareDialog.getResult().getButtonData())) {
					// 阻止对话框关闭
					event.consume();

					StringBuilder sb = new StringBuilder();
					if ("创建链接".equals(shareDialog.getResult().getText())) {
						Object[] ids = checkeds.stream().map(BaiduFile::getId).toArray();
						JSONObject rs = RequestProxy.share(new JSONArray(ids), shareDialog.isPrivate(), shareDialog.getPeriod());
						String link = rs.getStr("link"), pwd = rs.getStr("pwd");
						shareDialog.createShareInfoPane(link, pwd);
						if (shareDialog.isPrivate()) {
							sb.append("链接：").append(link).append(" 密码：").append(pwd);
						} else {
							sb.append(link);
						}
					} else {
						// 获取系统剪切板
						Clipboard clipboard = Clipboard.getSystemClipboard();

						// 剪切板内容存放对象
						ClipboardContent content = new ClipboardContent();
						content.putString(sb.toString());
						// 将内容放入剪切板
						clipboard.setContent(content);
					}
				}

			});
		}
	}

	@FXML
	private void onClickToDelete() {
		List<BaiduFile> checkeds = this.getCheckeds();

		if (CollectionUtil.isEmpty(checkeds)) {
			MessageDialog.show("请至少选择一个文件！");
		} else {
			MessageDialog.show("确认要把所选文件放入回收站吗？\n删除的文件可在10天内通过回收站还原", Alert.AlertType.CONFIRMATION)
					.filter(res -> ButtonType.OK.equals(res)).ifPresent(res -> {
				Object[] paths = checkeds.stream().map(BaiduFile::getPath).toArray();
				boolean success = RequestProxy.manager(Constant.Operate.DELETE, new JSONArray(paths));
				if (success) {
					fileTable.getItems().removeAll(checkeds);
					fileTable.getSelectionModel().clearSelection();
					this.checkAll(false);
				}
			});
		}

	}

	@FXML
	private void onClickToBack(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
//            BaiduFile file = fileTable.getItems().get(0);
//            String path = file.getPath().substring(0, file.getPath().lastIndexOf(homeBtn.getPath()));
		}
	}

	@FXML
	private void onClickToLogout() {
		MessageDialog.show("您确定退出登录吗？", Alert.AlertType.CONFIRMATION)
				.filter(res -> ButtonType.OK.equals(res)).ifPresent(res -> {
			// 停止定时任务
			CronUtil.stop();
			// 清空yunData信息
			RequestProxy.YUN_DATA.clear();
			// 清空Cookie
			CookieUtil.setCookies(null);
			// 打开登录对话框
			this.showLoginView();
		});
	}

	@FXML
	private void onClickReloadCss() {
		App.primaryStage.getScene().getRoot().getStylesheets().set(0, "css/Main.css");
	}

	/**
	 * 全选/反选
	 *
	 * @throws Exception
	 */
	@FXML
	private void onClickToCheckAll() {
		this.checkAll(checkAllBox.isSelected());
	}

	@FXML
	private void onTableMenuHidden() {
//        contextMenu.setStyle("visibility:hidden");
	}

	@FXML
	private void onClickToOpenFile(ActionEvent event) {
		Object source = event.getSource();
		ObservableList<Node> links = breadcrumb.getChildren();

		if (homeBtn.equals(source)) {
			links.clear();
			links.add(homeLink);
			this.loadTableData(homeLink.getPath());
		} else if (source instanceof PathLink && links.contains(source)) {
			PathLink link = (PathLink) source;
			int index = links.indexOf(link);
			if (index == links.size() - 1) return;
			this.loadTableData(link.getPath());
			links.remove(index + 1, links.size());
		} else {
			BaiduFile item = fileTable.getSelectionModel().getSelectedItem();
			if (item != null) {
				if (item.getIsDir()) {
					PathLink link = new PathLink(item.getFileName(), item.getPath());

					link.setVisited(true);
					link.setOnAction(homeLink.getOnAction());
					link.setTooltip(new Tooltip(item.getFileName()));
					link.getStyleClass().addAll(homeLink.getStyleClass());
					links.addAll(new Label(homeBtn.getPath()), link);

					this.loadTableData(item.getPath());
				} else {
					String fileName = item.getFileName();
					if (ReUtil.contains(Constant.FileTypeRegx.IMAGE, fileName)) {
						List<BaiduFile> files = fileTable.getItems().stream().
								filter(baiduFile -> MapUtil.isNotEmpty(baiduFile.getThumbs())).collect(Collectors.toList());
						ImageDialog.show(files, files.indexOf(item));
					}
				}
			}
		}

	}

	/**
	 * 刷新列表
	 */
	@FXML
	private void onClickToRefresh() {
		new Thread(new LoadTableDataTask(refreshBtn.getPath() == null)).start();
	}

	/**
	 * 搜索文件
	 */
	@FXML
	private void onClickToSearchFile() {
		if (StrUtil.isNotBlank(searchField.getText())) {
			refreshBtn.setPath(null);
			new Thread(new LoadTableDataTask(true)).start();
		}
	}

	@FXML
	private void onClickToRename() {
		fileTable.setEditable(true);
		fileTable.edit(fileTable.getSelectionModel().getSelectedIndex(), fileNameColumn);
	}

	@FXML
	private void onTableContextMenu(ContextMenuEvent event) {
		if (event.getY() < 26) contextMenu.hide();
	}

	@FXML
	private void onClickToPagination(ActionEvent event) {
		if (event.getSource().equals(prevBtn)) {

		} else {

		}
	}

	/**
	 * 获取复选框选中的项目
	 *
	 * @return
	 */
	private FilteredList<BaiduFile> getCheckeds() {
		ObservableList<BaiduFile> items = this.fileTable.getItems();
		if (items == null) items = FXCollections.emptyObservableList();
		return items.filtered(baiduFile -> baiduFile.getChecked());
	}

	/**
	 * 全选
	 *
	 * @param checked 全选状态，为<code>true</code>时全选，<code>false</code>时取消全选
	 */
	private void checkAll(boolean checked) {
		if (CollUtil.isNotEmpty(this.fileTable.getItems())) {
			this.checkAllBox.setSelected(checked);
			this.checkAllBox.setIndeterminate(false);
			this.fileTable.getItems().forEach(item -> item.setChecked(checked));
		}
	}

	/**
	 * 程序启动时登录验证
	 */
	public void loginCheck() {
		if (CookieUtil.COOKIES.isEmpty()) statusLabel.setText("请登录百度网盘账号");
		else if (RequestProxy.getYunData().isEmpty()) statusLabel.setText("登录信息已过期，请重新登录……");
		else this.panTask();

		if (RequestProxy.YUN_DATA.isEmpty()) this.showLoginView();
	}

	private void showLoginView() {
		WebView loginWiew = new WebView();

		WebEngine engine = loginWiew.getEngine();

		this.homePane.setCenter(loginWiew);

		try {
			URI uri = URI.create(Constant.HOME_URL);

			Map<String, List<String>> headers = new LinkedHashMap<>();
			headers.put("Set-Cookie", CookieUtil.COOKIES);

			CookieManager manager = new CookieManager();

			manager.put(uri, headers);

			CookieHandler.setDefault(manager);

			engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
				if (Worker.State.SUCCEEDED.equals(newValue) && engine.getLocation().startsWith(Constant.HOME_URL)) {
					try {
						List<String> cookies = manager.get(uri, headers).get("Cookie");
						// 保存cookie至本地
						CookieUtil.setCookies(cookies.get(0));
						this.panTask();
						this.homePane.setCenter(fileTable);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			});
			engine.load(uri.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 添加一个定时任务，每30分钟刷新一次网盘配额信息和用户信息
	 */
	private void panTask() {
		loadTableData(homeBtn.getPath());
		RequestProxy.getYunData();
		RequestProxy.getQuotaInfos();
		CronUtil.schedule("*/30 * * * *", (Runnable) () -> {
			RequestProxy.getYunData();
			RequestProxy.getQuotaInfos();
		});
		CronUtil.start();
	}

	/**
	 * 加载数据
	 *
	 * @param path 网盘路径
	 */
	private void loadTableData(String path) {
		refreshBtn.setPath(path);
		new Thread(new LoadTableDataTask(false)).start();
	}

	public class LoginCheckTask extends Task {

		@Override
		protected void done() {
			Platform.runLater(() -> {
				if (CookieUtil.COOKIES.isEmpty()) statusLabel.setText("请登录百度网盘账号");
				else if (RequestProxy.getYunData().isEmpty()) statusLabel.setText("登录信息已过期，请重新登录……");
				else panTask();

				if (RequestProxy.YUN_DATA.isEmpty()) showLoginView();
			});
			super.done();
		}

		@Override
		protected Object call() {
			return null;
		}
	}

	private class LoadTableDataTask extends Task<ObservableList<BaiduFile>> {

		private boolean isSearch;

		private ProgressIndicator loading = new ProgressIndicator();

		private Label placeholder = new Label("正在获取文件列表，请稍候……", loading);


		public LoadTableDataTask(boolean isSearch) {
			this.isSearch = isSearch;

			placeholder.setContentDisplay(ContentDisplay.TOP);
			loading.setPadding(new Insets(5));

			checkAllBox.setSelected(false);
			checkAllBox.setIndeterminate(false);

			fileTable.itemsProperty().bind(this.valueProperty());
			fileTable.setPlaceholder(placeholder);
			statusLabel.setText("正在获取文件列表……");
		}

		@Override
		protected void done() {
			Platform.runLater(() -> {
				fileTable.setPlaceholder(null);
				statusLabel.setText("加载完成，共" + this.getValue().size() + "项");
			});
			super.done();
		}

		@Override
		protected ObservableList<BaiduFile> call() {
			ObservableList<BaiduFile> list;
			if (isSearch) {
				list = FXCollections.observableArrayList(RequestProxy.searchFileList(searchField.getText()));
			} else {
				list = FXCollections.observableArrayList(RequestProxy.getFileList(refreshBtn.getPath()));
			}
			if (list == null) list = FXCollections.emptyObservableList();
			return list;
		}
	}
}