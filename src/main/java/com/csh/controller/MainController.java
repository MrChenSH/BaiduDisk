package com.csh.controller;

import com.csh.app.App;
import com.csh.coustom.PathLink;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.csh.utils.FontAwesome;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController extends BorderPane implements Initializable {

	private static Logger logger = Logger.getLogger(MainController.class);

	@FXML
	private ImageView userImage;

	@FXML
	private Label userLabel;

	@FXML
	private ProgressBar quotaBar;

	@FXML
	private Label quotaText;

	@FXML
	private PathLink backBtn;

	@FXML
	private PathLink forwardBtn;

	@FXML
	private PathLink homeBtn;

	@FXML
	private HBox pathFlow;

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
	private Hyperlink prevBtn;

	@FXML
	private Hyperlink nextBtn;

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
		homeBtn.setText(FontAwesome.HOME.value);
		backBtn.setText(FontAwesome.ARROW_LEFT.value);
		forwardBtn.setText(FontAwesome.ARROW_RIGHT.value);
		refreshBtn.setText(FontAwesome.REFRESH.value);
		searchBtn.setText(FontAwesome.SEARCH.value);
		prevBtn.setText(FontAwesome.CHEVRON_LEFT.value);
		nextBtn.setText(FontAwesome.CHEVRON_RIGHT.value);

		if (RequestProxy.yunData.isEmpty()) {
			Platform.runLater(() -> {
				try {
					app.getPrimaryStage().hide();
					app.generateLoginPanel();
					app.getPrimaryStage().show();
				} catch (Exception e) {
					logger.error(e);
				}
			});
		} else {
			try {

				JSONObject info = RequestProxy.getQuotaInfos();

				if (info.getInt("errno") == 0) {
					userLabel.setText(RequestProxy.yunData.getString("MYNAME"));
					userImage.setImage(new Image(RequestProxy.yunData.getString("MYAVATAR")));
					double used = info.getDouble("used");
					double total = info.getDouble("total");
					quotaBar.setProgress(used / total);
					quotaText.setText(Math.round(used / Constant.M_BYTE_MAX_SIZE) + "GB/" + Math.round(total / Constant.M_BYTE_MAX_SIZE) + "GB");
				}

				fileTable.itemsProperty().addListener((observable, oldValue, newValue) -> fileCount.setText(newValue.size() + "项"));

				checkBoxColumn.setCellValueFactory(new PropertyValueFactory("checked"));
				checkBoxColumn.setCellFactory(colums -> new TableCell<BaiduFile, Boolean>() {
					@Override
					protected void updateItem(Boolean checked, boolean empty) {
						super.updateItem(checked, empty);
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
									if (checkeds == 0) {
										checkAllBox.setSelected(false);
										checkAllBox.setIndeterminate(false);
									} else if (checkeds == items.size()) {
										checkAllBox.setSelected(true);
									} else {
										checkAllBox.setIndeterminate(true);
									}
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
												switch (name.substring(name.lastIndexOf(".") + 1)) {
													case "txt":
														icon.setText(FontAwesome.FILE_TEXT.value);
														break;
													case "apk":
														icon.setText(FontAwesome.ANDROID.value);
														break;
													default:
														icon.setText(FontAwesome.FOLDER_OPEN.value);
														break;
												}
											}


											nameLabel.setCursor(Cursor.HAND);
											icon.getStyleClass().add("icon");
											share.setTooltip(new Tooltip("分享文件"));
											share.getStyleClass().addAll("icon", "action-btn");
											download.setTooltip(new Tooltip("下载文件"));
											download.getStyleClass().addAll("icon", "action-btn");
											delete.setTooltip(new Tooltip("删除文件"));
											delete.getStyleClass().addAll("icon", "action-btn");

											HBox actionBox = new HBox(share, download, delete);

											BorderPane borderPane = new BorderPane();

											borderPane.setLeft(icon);
											borderPane.setCenter(nameLabel);
											borderPane.setRight(actionBox);
											BorderPane.setAlignment(nameLabel, Pos.CENTER_LEFT);

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


				loadTableData("/");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void onClickToBack(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
//            BaiduFile file = fileTable.getItems().get(0);
//            String path = file.getPath().substring(0, file.getPath().lastIndexOf("/"));
		}
	}

	/**
	 * 全选/反选
	 *
	 * @param event
	 * @throws Exception
	 */
	public void onClickToCheckAll(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			fileTable.getItems().forEach(baiduFile -> baiduFile.setChecked(checkAllBox.isSelected()));
		}
	}

	public void onTableMenuHidden() {
//        contextMenu.setStyle("visibility:hidden");
	}

	public void onClickToOpenFile(ActionEvent event) {
		if (event.getSource() instanceof PathLink) {
			PathLink link = (PathLink) event.getSource();
			ObservableList<Node> links = pathFlow.getChildren();
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
			if (item != null && item.isDir()) {
				PathLink link = new PathLink();

				link.setVisited(true);
				link.setPath(item.getPath());
				link.setText(item.getFileName());
				link.setOnAction(homeLink.getOnAction());
				link.getStyleClass().addAll(homeLink.getStyleClass());


				backBtn.setDisable(false);
				pathFlow.getChildren().addAll(new Label("/"), link);

				this.loadTableData(item.getPath());
			}
		}

	}

	public void onClickToSearchFile() {
		try {
			String keyword = searchField.getText();
			if (!keyword.isEmpty()) {
				fileTable.setItems(FXCollections.observableArrayList(RequestProxy.searchFileList(keyword)));
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void onClickToRename() {
		fileTable.setEditable(true);
		fileTable.edit(fileTable.getSelectionModel().getSelectedIndex(), fileNameColumn);
	}


	public void onTableContextMenu(ContextMenuEvent event) {
		if (event.getY() < 26) contextMenu.hide();
	}

	public void onClickToPagination(ActionEvent event) {
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
		try {
			refreshBtn.setPath(path);
			fileTable.setItems(FXCollections.observableArrayList(RequestProxy.getFileList(path)));
		} catch (Exception e) {
			logger.error(e);
		}
	}
}