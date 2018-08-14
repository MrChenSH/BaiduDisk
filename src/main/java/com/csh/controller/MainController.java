package com.csh.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.csh.app.App;
import com.csh.coustom.control.IconButton;
import com.csh.coustom.control.PathLink;
import com.csh.coustom.dialog.ImageDialog;
import com.csh.coustom.dialog.LoginDialog;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.coustom.dialog.ShareDialog;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.csh.utils.FontAwesome;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
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
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
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

	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

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

		checkBoxColumn.setCellValueFactory(new PropertyValueFactory("checked"));
		checkBoxColumn.setCellFactory(colums -> new TableCell<BaiduFile, Boolean>() {
			@Override
			protected void updateItem(Boolean checked, boolean empty) {
				ObservableList<BaiduFile> items = fileTable.getItems();
				if (CollectionUtil.isEmpty(items)) return;

				TableRow<BaiduFile> row = this.getTableRow();
				if (row != null) {
					BaiduFile item = row.getItem();
					if (item == null) {
						this.setGraphic(null);
					} else {
						CheckBox checkBox = new CheckBox();
						checkBox.setFocusTraversable(false);
						// 属性绑定
						checkBox.selectedProperty().bindBidirectional(item.checkedProperty());

						int checkeds = getCheckeds().size();
						checkAllBox.setSelected(checkeds == items.size());
						checkAllBox.setIndeterminate(checkeds != 0 && checkeds < items.size());

//								fileTable.requestFocus();
//								row.updateSelected(checked);
//								if (checked) {
//									fileTable.getSelectionModel().select(row.getIndex());
//								} else {
//									fileTable.getSelectionModel().clearSelection(row.getIndex());
//								}
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
						TableRow<BaiduFile> row = this.getTableRow();

						if (row != null) {
							BaiduFile item = row.getItem();

							if (item == null) {
								this.setGraphic(null);
							} else {
								if (fileTable.isEditable()) {
									fileTable.setEditable(false);

									TextField editor = new TextField(name);
									BorderPane pane = (BorderPane) this.getGraphic();
									Node center = pane.getCenter();

									editor.setOnAction(event -> {
										pane.setCenter(center);
										JSONArray fileList = new JSONArray();
										fileList.add(new JSONObject() {{
											put("path", item.getPath());
											put("newname", editor.getText());
										}});
										boolean success = RequestProxy.manager(Constant.Operate.RENAME, fileList);
										if (success) item.setFileName(editor.getText());
									});

									editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
										if (!newValue) pane.setCenter(center);
									});

									pane.setCenter(editor);
									editor.requestFocus();
									if (item.getIsDir()) editor.selectAll();
									else editor.selectRange(0, item.getFileName().lastIndexOf('.'));
								} else {
									PathLink nameLink = new PathLink(name, item.getPath());
									PathLink share = new PathLink(FontAwesome.SHARE_ALT);
									PathLink download = new PathLink(FontAwesome.DOWNLOAD);
									PathLink delete = new PathLink(FontAwesome.TRASH);

									if (item.getIsDir()) {
										nameLink.setIcon(FontAwesome.FOLDER_OPEN);
									} else {
										String suffix = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

										if (suffix.equals("txt")) {
											nameLink.setIcon(FontAwesome.FILE_TEXT);
										} else if (suffix.equals("apk")) {
											nameLink.setIcon(FontAwesome.ANDROID);
										} else if (suffix.equals("pdf")) {
											nameLink.setIcon(FontAwesome.FILE_PDF);
										} else if (suffix.matches("font|ttf")) {
											nameLink.setIcon(FontAwesome.FONT);
										} else if (suffix.matches("doc|docx")) {
											nameLink.setIcon(FontAwesome.FILE_WORD);
										} else if (suffix.matches("xls|xlsx")) {
											nameLink.setIcon(FontAwesome.FILE_EXCEL);
										} else if (suffix.matches("ppt|pptx")) {
											nameLink.setIcon(FontAwesome.FILE_PPT);
										} else if (suffix.matches(Constant.FileType.IMAGE)) {
											nameLink.setIcon(FontAwesome.FILE_IMAGE);
										} else if (suffix.matches(Constant.FileType.AUDIO)) {
											nameLink.setIcon(FontAwesome.FILE_AUDIO);
										} else if (suffix.matches(Constant.FileType.ARCHIVE)) {
											nameLink.setIcon(FontAwesome.FILE_ARCHIVE);
										} else if (suffix.matches(Constant.FileType.VIDEO)) {
											nameLink.setIcon(FontAwesome.FILE_VIDEO);
										} else {
											nameLink.setIcon(FontAwesome.FILE);
										}
									}

									nameLink.setVisited(true);
									nameLink.setFocusTraversable(false);
									nameLink.setTooltip(new Tooltip(name));
									nameLink.setOnAction(homeLink.getOnAction());
									nameLink.getStyleClass().add("name-link");
									nameLink.setOnMousePressed(event -> {
										checkAll(false);
										item.setChecked(true);
										fileTable.requestFocus();
										fileTable.getSelectionModel().clearAndSelect(row.getIndex());
									});

									share.setFocusTraversable(false);
									share.setOnAction(shareBtn.getOnAction());
									share.setOnMousePressed(nameLink.getOnMousePressed());
									share.setTooltip(new Tooltip("分享文件"));

									download.setFocusTraversable(false);
									download.setOnAction(downloadBtn.getOnAction());
									download.setOnMousePressed(nameLink.getOnMousePressed());
									download.setTooltip(new Tooltip("下载文件"));

									delete.setFocusTraversable(false);
									delete.setOnAction(deleteBtn.getOnAction());
									delete.setOnMousePressed(nameLink.getOnMousePressed());
									delete.setTooltip(new Tooltip("删除文件"));

									HBox actionBox = new HBox(share, download, delete);

									actionBox.getStyleClass().add("action-box");

									BorderPane borderPane = new BorderPane();

									borderPane.setCenter(nameLink);
									borderPane.setRight(actionBox);
									BorderPane.setAlignment(nameLink, Pos.CENTER_LEFT);

									this.setGraphic(borderPane);
								}

							}
						}
					}
				}
		);

		modifyTimeColumn.setCellValueFactory(new PropertyValueFactory("modifyTime"));
		modifyTimeColumn.setCellFactory(column -> new TableCell<BaiduFile, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				this.setText(item);
				this.setAlignment(Pos.CENTER_LEFT);
			}
		});

		fileSizeColumn.setCellValueFactory(new PropertyValueFactory("size"));
		fileSizeColumn.setCellFactory(column -> new TableCell<BaiduFile, Long>() {
					@Override
					protected void updateItem(Long size, boolean empty) {
						TableRow<BaiduFile> row = this.getTableRow();

						if (row != null) {
							BaiduFile item = row.getItem();
							if (item == null) {
								this.setText(null);
							} else {
								this.setAlignment(Pos.CENTER_LEFT);
								if (item.getIsDir()) {
									this.setText("文件夹");
								} else {
									if (size < Constant.BYTE_MAX_SIZE) {
										this.setText(size + "B");
									} else if (size < Constant.K_BYTE_MAX_SIZE) {
										this.setText(size / Constant.BYTE_MAX_SIZE + "KB");
									} else if (size < Constant.M_BYTE_MAX_SIZE) {
										this.setText(decimalFormat.format(size.doubleValue() / Constant.K_BYTE_MAX_SIZE) + "MB");
									} else {
										this.setText(decimalFormat.format(size.doubleValue() / Constant.M_BYTE_MAX_SIZE) + "GB");
									}
								}
							}
						}
					}
				}
		);

				/*fileTable.setRowFactory(tableView -> {
					TableRow<BaiduFile> row = new TableRow<>();

					row.selectedProperty().addListener((observable, oldValue, newValue) -> {
						row.getItem().setChecked(newValue);
					});
					return row;
				});*/

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
			FileChooser chooser = new FileChooser();
			chooser.setTitle("保存文件");
			chooser.setInitialFileName(checkeds.get(0).getFileName());
			chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有文件", "*.*"));
			File file = chooser.showSaveDialog(App.primaryStage);

			if (file != null) {
				new Thread(() -> {
					Object[] ids = checkeds.stream().map(BaiduFile::getId).toArray();
					String link = RequestProxy.getDownloadLink(new JSONArray(ids));
					if (StrUtil.isNotBlank(link)) {
						// 实验性下载
						HttpRequest.get(link).cookie(CollectionUtil.join(CookieUtil.COOKIES, "; "))
								.timeout(-1).executeAsync().writeBody(file, new StreamProgress() {
							@Override
							public void start() {
								logger.info("文件下载开始……");
							}

							@Override
							public void progress(long size) {
								logger.info("已下载：" + FileUtil.readableFileSize(size));
							}

							@Override
							public void finish() {
								logger.info("文件下载完成……");
							}
						});
					}

				}).start();
			}
		}
	}

	@FXML
	private void onClickToShare() {
		List<BaiduFile> checkeds = this.getCheckeds();
		if (CollectionUtil.isEmpty(checkeds)) {
			MessageDialog.show("请至少选择一个文件！");
		} else {
			StringBuilder sb = new StringBuilder();
			ShareDialog shareDialog = new ShareDialog();

			shareDialog.show();
			shareDialog.setOnCloseRequest(event -> {
				if (ButtonBar.ButtonData.OK_DONE.equals(shareDialog.getResult().getButtonData())) {
					// 阻止对话框关闭
					event.consume();

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
//            String path = file.getPath().substring(0, file.getPath().lastIndexOf("/"));
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
			this.showLoginDialog();
		});
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

					links.addAll(new Label("/"), link);

					this.loadTableData(item.getPath());
				} else {
					String fileName = item.getFileName();
					String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

					if (suffix.matches(Constant.FileType.IMAGE)) {
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
		this.checkAllBox.setSelected(checked);
		this.checkAllBox.setIndeterminate(false);
		this.fileTable.getItems().forEach(item -> item.setChecked(checked));
	}

	/**
	 * 程序启动时登录验证
	 */
	public void loginCheck() {
		if (CookieUtil.COOKIES.isEmpty()) statusLabel.setText("请登录百度网盘账号");
		else if (RequestProxy.getYunData().isEmpty()) statusLabel.setText("登录信息已过期，请重新登录……");
		else this.panTask();

		if (RequestProxy.YUN_DATA.isEmpty()) this.showLoginDialog();
	}

	private void showLoginDialog() {
		LoginDialog dialog = new LoginDialog();
		dialog.show();
		dialog.setOnHiding(event -> {
			if (RequestProxy.YUN_DATA.isEmpty()) System.exit(0);
			else this.panTask();
		});
	}

	/**
	 * 添加一个定时任务，每分钟刷新一次网盘配额信息和用户信息
	 */
	private void panTask() {
		loadTableData("/");
		RequestProxy.getYunData();
		RequestProxy.getQuotaInfos();
		CronUtil.schedule("*/1 * * * * *", (Runnable) () -> {
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
			return list;
		}
	}
}