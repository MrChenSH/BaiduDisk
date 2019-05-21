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
import com.csh.coustom.control.Icon;
import com.csh.coustom.control.IconButton;
import com.csh.coustom.control.PathLink;
import com.csh.coustom.dialog.ImageDialog;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.coustom.dialog.SaveDialog;
import com.csh.coustom.dialog.ShareDialog;
import com.csh.http.DownloadUtil;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import com.csh.model.Categroy;
import com.csh.service.LoadDataService;
import com.csh.service.LoginService;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import com.csh.utils.FontAwesome;
import com.csh.utils.FontIcon;
import com.sun.webkit.network.CookieManager;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
	private ListView<Categroy> categroyMenu;

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
	private ToggleGroup tabGroup;

	@FXML
	private ToggleButton homeTabBtn;

	@FXML
	private ToggleButton transferTabBtn;

	@FXML
	private ToggleButton cloudDownloadTabBtn;

	@FXML
	private Button logoutBtn;

	@FXML
	private Button uploadBtn;

	@FXML
	private Button downloadBtn;

	@FXML
	private Button shareBtn;

	@FXML
	private Button deleteBtn;

	@FXML
	private PathLink backBtn;

	@FXML
	private PathLink forwardBtn;

	@FXML
	private HBox breadcrumb;

	@FXML
	private PathLink homeLink;

	@FXML
	private PathLink refreshBtn;

	@FXML
	private TextField searchField;

	@FXML
	private Button searchBtn;

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
	private Button prevBtn;

	@FXML
	private Button nextBtn;

	private ProgressIndicator loading = new ProgressIndicator();

	private Label placeholder = new Label("正在登录，请稍候……", loading);

	private Property<Number> transferNameLabelMaxWidth = new SimpleDoubleProperty();

	private LoginService loginService = new LoginService();

	private LoadDataService loadDataService = new LoadDataService();

	/**
	 * 初始化类目菜单
	 */
	public void initMenu() {
		categroyMenu.setCellFactory(list -> new ListCell<Categroy>() {
			@Override
			protected void updateItem(Categroy item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					Icon icon = new Icon();
					icon.getStyleClass().add("el-icon");

					if (StrUtil.isNotBlank(item.getIcon())) {
						icon.setIcon(FontIcon.valueOf(FontIcon.class, item.getIcon()));
					}

					this.setGraphic(new Label(item.getText(), icon));
				}
			}
		});

		categroyMenu.getSelectionModel().selectedItemProperty().addListener(observable -> {
			LoadDataService.Query query = loadDataService.getQuery();
			Categroy categroy = categroyMenu.getSelectionModel().getSelectedItem();

			if (Constant.SEARCH_URL.equals(query.getUrl())) return;

			query.setPath("/");
			query.setUrl(categroy.getUrl());
			query.setText(categroy.getText());
			query.setCategroy(categroy.getCategory());

			ObservableList<Node> links = breadcrumb.getChildren();
			links.clear();
			links.add(homeLink);

			if (categroyMenu.getSelectionModel().getSelectedIndex() != 1) {
				links.addAll(new Label(" > "), new Label(query.getText()));
			}

			if (loginService.getValue().get()) loadDataService.load(query);
		});

		categroyMenu.getItems().addAll(Constant.CATEGORY.toList(Categroy.class));
		categroyMenu.getSelectionModel().select(1);
	}

	private void locinCheck() {
		statusLabel.textProperty().bind(loginService.statusProperty());
		userImage.imageProperty().bind(loginService.avatarProperty());
		userLabel.textProperty().bind(loginService.usernameProperty());
		quotaText.textProperty().bind(loginService.quotaTextProperty());
		quotaBar.progressProperty().bind(loginService.quotaProgressProperty());

		loginService.start();
		loginService.setOnSucceeded(event -> {
			this.initMenu();
			statusLabel.textProperty().bind(loadDataService.statusProperty());
		});
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.locinCheck();

		loading.setPadding(new Insets(5));
		loading.progressProperty().bind(loadDataService.progressProperty());

		placeholder.setContentDisplay(ContentDisplay.TOP);
		placeholder.textProperty().bind(statusLabel.textProperty());
		placeholder.visibleProperty().bind(loadDataService.runningProperty());



		fileTable.setPlaceholder(placeholder);
		fileTable.itemsProperty().bind(loadDataService.valueProperty());
		fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tabGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) tabGroup.selectToggle(oldValue);
			navigationTabPane.getSelectionModel().select(tabGroup.getToggles().indexOf(tabGroup.getSelectedToggle()));
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

						if (checkBox.isSelected()) {
							fileTable.getSelectionModel().select(items.indexOf(item));
						} else {
							fileTable.getSelectionModel().clearSelection(items.indexOf(item));
						}
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

									BorderPane pane = (BorderPane) this.getGraphic();
									Node center = pane.getCenter();

									TextField editor = new TextField(name);
									editor.setStyle("-fx-font-size: 12px");

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
									else editor.selectRange(0, name.lastIndexOf('.'));
								} else {
									Text text = new Text(name);
									ImageView imageView = new ImageView(new Image(item.getIcon()));
									BorderPane pane = new BorderPane(new Text(name));
									pane.setLeft(imageView);
									text.getStyleClass().add("file-name-label");
									text.setOnMouseClicked(event -> {
										if (MouseButton.PRIMARY.equals(event.getButton())) {
											onClickToOpenFile(new ActionEvent(event.getSource(), event.getTarget()));
										}
									});
									BorderPane.setAlignment(pane.getCenter(), Pos.CENTER_LEFT);
									this.setGraphic(pane);
									this.setTooltip(new Tooltip(name));
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
						this.setAlignment(Pos.CENTER_LEFT);
						super.updateItem(size, empty);
						if (empty) this.setText(null);
						else {
							BaiduFile item = (BaiduFile) this.getTableRow().getItem();
							if (item != null) {
								if (item.getIsDir()) {
									this.setText("-");
								} else {
									this.setText(FileUtil.readableFileSize(size).toUpperCase());
								}
							}
						}
					}
				}
		);

		fileTable.setOnMouseClicked(event -> {
//			logger.info(event);
		});

		fileTable.setRowFactory(tableView -> {
			TableRow<BaiduFile> row = new TableRow<>();

			row.setOnMouseClicked(event -> {
				this.checkAll(false);
				tableView.getSelectionModel().getSelectedItems().forEach(item -> item.setChecked(true));
				if (!row.isEmpty() && MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() == 2) {
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

					ImageView icon = new ImageView(new Image(item.getIcon()));
					icon.setFitWidth(40);

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
			LoadDataService.Query query = loadDataService.new Query();
			query.setPath(root.getPath());
			query.setUrl(Constant.LIST_URL);
			List<BaiduFile> fileList = RequestProxy.loadFiles(query);
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
	private void onClickToOpenFile(ActionEvent event) {
		Object source = event.getSource();
		ObservableList<Node> links = breadcrumb.getChildren();
		LoadDataService.Query query = loadDataService.getQuery();
		query.setUrl(Constant.LIST_URL);

		if (source instanceof PathLink && links.contains(source)) {
			PathLink link = (PathLink) source;
			int index = links.indexOf(link);
			if (index == links.size() - 1) return;
			links.remove(index + 1, links.size());

			query.setPath(link.getPath());
			loadDataService.load(query);

			if (source.equals(homeLink)) categroyMenu.getSelectionModel().select(1);
		} else {
			BaiduFile item = fileTable.getSelectionModel().getSelectedItem();
			if (item != null) {
				if (item.getIsDir()) {
					PathLink link = new PathLink(item.getFileName(), item.getPath());

					link.setVisited(true);
					link.setOnAction(homeLink.getOnAction());
					link.setTooltip(new Tooltip(item.getFileName()));
					link.getStyleClass().addAll(homeLink.getStyleClass());
					links.addAll(new Label(" > "), link);

					query.setPath(link.getPath());
					loadDataService.load(query);
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
		loadDataService.restart();
	}

	/**
	 * 搜索文件
	 */
	@FXML
	private void onClickToSearchFile() {
		String str = searchField.getText();
		if (StrUtil.isNotBlank(str)) {
			refreshBtn.setPath(null);
			ObservableList<Node> links = breadcrumb.getChildren();
			links.clear();
			links.addAll(homeLink, new Label(" > "), new Label("\"" + str + "\"的搜索结果"));

			LoadDataService.Query query = loadDataService.new Query();

			query.setSerach(str);
			query.setUrl(Constant.SEARCH_URL);

			loadDataService.load(query);
			categroyMenu.getSelectionModel().select(1);
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

	@FXML
	private void onClick(Event event) {
		logger.info(event);
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

	private void showLoginView() {
		WebView loginWiew = new WebView();

		WebEngine engine = loginWiew.getEngine();

		this.setCenter(loginWiew);

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
		RequestProxy.getYunData();
		RequestProxy.getQuotaInfo();
		CronUtil.schedule("*/30 * * * *", (Runnable) () -> {
			RequestProxy.getYunData();
			RequestProxy.getQuotaInfo();
		});
		CronUtil.start();
	}

	/*public class LoginCheckTask extends Task {

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
	}*/

}