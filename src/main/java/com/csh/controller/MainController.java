package com.csh.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.csh.app.App;
import com.csh.coustom.IconButton;
import com.csh.coustom.PathLink;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.csh.utils.FontAwesome;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
	private Button loginBtn;

	@FXML
	private IconButton uploadBtn;

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
	private Hyperlink searchBtn;

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
	private Label fileCount;

	@FXML
	private IconButton prevBtn;

	@FXML
	private IconButton nextBtn;

	private App app;

	private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (StrUtil.isBlank(CookieUtil.COOKIE_STR) || CollectionUtil.isEmpty(RequestProxy.YUN_DATA)) {
			Platform.runLater(() -> {
				try {
					app.getPrimaryStage().hide();
					app.generateLoginPanel();
					app.getPrimaryStage().show();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			});
		} else {
			try {
				fileTable.setPlaceholder(new Label("正在登录，请稍候……"));
				fileTable.itemsProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) fileCount.setText(newValue.size() + "项");
				});

				checkBoxColumn.setCellValueFactory(new PropertyValueFactory("checked"));
				checkBoxColumn.setCellFactory(colums -> new TableCell<BaiduFile, Boolean>() {
					@Override
					protected void updateItem(Boolean checked, boolean empty) {
						TableRow<BaiduFile> row = this.getTableRow();
						if (row != null) {
							BaiduFile item = row.getItem();
							if (item == null) {
								this.setGraphic(null);
							} else {
								CheckBox checkBox = new CheckBox();
								// 属性绑定
								checkBox.selectedProperty().bindBidirectional(item.checkedProperty());

								ObservableList<BaiduFile> items = fileTable.getItems();

								checkBox.setOnAction(event -> {
									int checkeds = items.filtered(file -> file.getChecked()).size();
									checkAllBox.setSelected(checkeds == items.size());
									checkAllBox.setIndeterminate(checkeds != 0 && checkeds < items.size());
								});

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

											editor.onKeyPressedProperty().set(event -> {
												if (event.getCode().equals(KeyCode.ENTER)) {
													item.setFileName(editor.getText());
													pane.setCenter(center);
												}
											});

											editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
												if (newValue == false) {
													item.setFileName(editor.getText());
													pane.setCenter(center);
												}
											});

											pane.setCenter(editor);
											editor.requestFocus();
											editor.selectAll();
										} else {
											Label nameLabel = new Label(name);
											Label icon = new Label();
											Label share = new Label(FontAwesome.SHARE_ALT.value);
											Label download = new Label(FontAwesome.DOWNLOAD.value);
											Label delete = new Label(FontAwesome.TRASH.value);

											if (item.isDir()) {
												icon.setText(FontAwesome.FOLDER_OPEN.value);
											} else {
												String suffix = name.substring(name.lastIndexOf(".") + 1).toLowerCase();

												if (suffix.equals("txt")) {
													icon.setText(FontAwesome.FILE_TEXT.value);
												} else if (suffix.equals("apk")) {
													icon.setText(FontAwesome.ANDROID.value);
												} else if (suffix.equals("pdf")) {
													icon.setText(FontAwesome.FILE_PDF.value);
												} else if (suffix.equals("font")) {
													icon.setText(FontAwesome.FONT.value);
												} else if (suffix.matches("doc|docx")) {
													icon.setText(FontAwesome.FILE_WORD.value);
												} else if (suffix.matches("xls|xlsx")) {
													icon.setText(FontAwesome.FILE_EXCEL.value);
												} else if (suffix.matches("ppt|pptx")) {
													icon.setText(FontAwesome.FILE_PPT.value);
												} else if (suffix.matches(Constant.FileType.IMAGE)) {
													icon.setText(FontAwesome.FILE_IMAGE.value);
												} else if (suffix.matches(Constant.FileType.AUDIO)) {
													icon.setText(FontAwesome.FILE_AUDIO.value);
												} else if (suffix.matches(Constant.FileType.ARCHIVE)) {
													icon.setText(FontAwesome.FILE_ARCHIVE.value);
												} else if (suffix.matches(Constant.FileType.VIDEO)) {
													icon.setText(FontAwesome.FILE_VIDEO.value);
												} else {
													icon.setText(FontAwesome.FILE.value);
												}
											}

											nameLabel.setTooltip(new Tooltip(name));
											nameLabel.getStyleClass().addAll("action-btn");
											icon.getStyleClass().add("icon");
											share.setVisible(false);
											share.setTooltip(new Tooltip("分享文件"));
											share.getStyleClass().addAll("icon", "action-btn");
											download.setVisible(false);
											download.setTooltip(new Tooltip("下载文件"));
											download.getStyleClass().addAll("icon", "action-btn");
											delete.setVisible(false);
											delete.setTooltip(new Tooltip("删除文件"));
											delete.getStyleClass().addAll("icon", "action-btn");

											HBox actionBox = new HBox(share, download, delete);

											BorderPane borderPane = new BorderPane();

											borderPane.setLeft(icon);
											borderPane.setCenter(nameLabel);
											borderPane.setRight(actionBox);
											BorderPane.setAlignment(nameLabel, Pos.CENTER_LEFT);

											nameLabel.setOnMouseClicked(event -> {
												if (event.getButton().equals(MouseButton.PRIMARY)) {
													onClickToOpenFile(new ActionEvent(row, row));
												}
											});

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
										if (item.isDir()) {
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

                /*fileTable.setRowFactory(tableView ->
                {
                    TableRow<BaiduFile> row = new TableRow<>();

                    return row;
                });*/


				Platform.runLater(() -> {
					try {
						JSONObject info = RequestProxy.getQuotaInfos();
						if (new Integer(0).equals(info.getInt("errno"))) {
							userLabel.setText(RequestProxy.YUN_DATA.getStr(Constant.NAME_KEY));
							userImage.setImage(new Image(RequestProxy.YUN_DATA.getStr(Constant.AVATAR_KEY)));
							double used = info.getDouble("used");
							double total = info.getDouble("total");
							quotaBar.setProgress(used / total);
							quotaText.setText(Math.round(used / Constant.M_BYTE_MAX_SIZE) + "GB/" + Math.round(total / Constant.M_BYTE_MAX_SIZE) + "GB");

							this.loadTableData("/");
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				});
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@FXML
	private void onClickToUpload() {
		FileChooser chooser = new FileChooser();

		List<File> files = chooser.showOpenMultipleDialog(this.app.getPrimaryStage());

		System.out.println(files);
	}

	@FXML
	private void onClickToDownload() {

	}

	@FXML
	private void onClickToBack(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
//            BaiduFile file = fileTable.getItems().get(0);
//            String path = file.getPath().substring(0, file.getPath().lastIndexOf("/"));
		}
	}

	@FXML
	private void onClickToLogin(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		stage.setTitle("用户登录");
		stage.initOwner(app.getPrimaryStage());
		stage.initModality(Modality.APPLICATION_MODAL);
		WebView webView = new WebView();
		URI uri = URI.create(Constant.BASE_URL);

		Map<String, List<String>> headers = new LinkedHashMap<>();
		headers.put("Set-Cookie", CookieUtil.COOKIES);
		CookieHandler.getDefault().put(uri, headers);

		webView.getEngine().load(Constant.BASE_URL);
		stage.setScene(new Scene(webView));
//		stage.centerOnScreen();
		stage.show();
	}

	/**
	 * 全选/反选
	 *
	 * @throws Exception
	 */
	@FXML
	private void onClickToCheckAll() {
		fileTable.getItems().forEach(baiduFile -> baiduFile.setChecked(checkAllBox.isSelected()));
	}

	@FXML
	private void onTableMenuHidden() {
//        contextMenu.setStyle("visibility:hidden");
	}

	@FXML
	private void onClickToOpenFile(ActionEvent event) {
		if (event.getSource() instanceof PathLink) {
			PathLink link = (PathLink) event.getSource();
			ObservableList<Node> links = breadcrumb.getChildren();
			int index = links.indexOf(link);

			if (index + 1 < links.size()) this.loadTableData(link.getPath());

			if (index < 0) {
				links.clear();
				links.add(homeLink);
			} else {
				links.remove(index + 1, links.size());
			}
		} else {
			BaiduFile item = fileTable.getSelectionModel().getSelectedItem();
			if (item != null) {
				if (item.isDir()) {
					PathLink link = new PathLink(item.getFileName(), item.getPath());

					link.setVisited(true);
					link.setOnAction(homeLink.getOnAction());
					link.setTooltip(new Tooltip(item.getFileName()));
					link.getStyleClass().addAll(homeLink.getStyleClass());

					breadcrumb.getChildren().addAll(new Label("/"), link);

					this.loadTableData(item.getPath());
				} else {
					String fileName = item.getFileName();
					String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

					if (suffix.matches(Constant.FileType.TEXT)) {
						Stage stage = new Stage();
						stage.setTitle(fileName);
						stage.setWidth(600);
						stage.setHeight(200);
//						stage.initOwner(app.getPrimaryStage());
//						stage.initModality(Modality.WINDOW_MODAL);
//						stage.centerOnScreen();
						stage.show();
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
		System.out.println(event);
		if (event.getY() < 26) contextMenu.hide();
	}

	@FXML
	private void onClickToPagination(ActionEvent event) {
		if (event.getSource().equals(prevBtn)) {

		} else {

		}
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

		public LoadTableDataTask(boolean isSearch) {
			this.isSearch = isSearch;
			checkAllBox.setSelected(false);
			checkAllBox.setIndeterminate(false);
			fileTable.itemsProperty().bind(this.valueProperty());
			fileTable.setPlaceholder(new Label("正在获取文件列表，请稍候……"));
		}

		@Override
		protected ObservableList<BaiduFile> call() {
			ObservableList<BaiduFile> list = FXCollections.emptyObservableList();
			try {
				if (isSearch) {
					list = FXCollections.observableArrayList(RequestProxy.searchFileList(searchField.getText()));
				} else {
					list = FXCollections.observableArrayList(RequestProxy.getFileList(refreshBtn.getPath()));
				}
			} catch (Exception e) {
				Platform.runLater(() -> fileTable.setPlaceholder(new Label(e.getMessage())));
			}
			return list;
		}
	}
}