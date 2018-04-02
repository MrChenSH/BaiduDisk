package com.csh.controller;

import com.csh.app.App;
import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends BorderPane implements Initializable
{

	@FXML
	private ImageView userImage;

	@FXML
	private Label userLabel;

	@FXML
	private ProgressBar quotaInfos;

	@FXML
	private TableView<BaiduFile> fileTable;

	@FXML
	private ContextMenu fileTableMenu;

	@FXML
	private CheckBox checkAllBox;

	@FXML
	private TableColumn<BaiduFile, Boolean> checkBoxColumn;

	@FXML
	private TableColumn<BaiduFile, String> fileNameColumn;

	@FXML
	private TableColumn<BaiduFile, String> modifyTimeColumn;

	@FXML
	private TableColumn<BaiduFile, Long> fileSizeColumn;

	private App app;

	private static ObjectMapper mapper = new ObjectMapper();

	public App getApp()
	{
		return app;
	}

	public void setApp(App app)
	{
		this.app = app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
//		System.out.println(RequestProxy.yunData);
//		userLabel.setText(RequestProxy.yunData.getString("username"));
//		userImage.setImage(new Image(RequestProxy.yunData.getString("photo")));

		try
		{
//			JSONObject quota = RequestProxy.getQuotaInfos();

//			quotaInfos.setProgress(quota.getDouble("used") / quota.getDouble("total"));

//			JSONObject json = RequestProxy.getDir("/", null, null);

//			File file = new File(this.getClass().getResource("/json/file.json").toURI());


			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

			List<BaiduFile> baiduFiles = mapper.readValue(this.getClass().getResource("/json/file.json"), javaType);


			/*JSONArray array = JSONArray.fromObject(FileUtils.readFileToString(file, Constant.CHARSET_UTF_8));

			array.forEach(o ->
			{
				JSONObject item = JSONObject.fromObject(o);
				item.put("checked", false);
			});*/

			checkBoxColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
			checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(index ->
			{
				BaiduFile baiduFile = fileTable.getItems().get(index);

				BooleanProperty booleanProperty = new SimpleBooleanProperty(baiduFile, "checked", baiduFile.getChecked());

				booleanProperty.addListener((observable, oldValue, newValue) ->
				{
					baiduFile.setChecked(newValue);

					int checkedSize = 0, size = fileTable.getItems().size();

					for (BaiduFile item : fileTable.getItems())
					{
						if (item.getChecked()) checkedSize++;
					}

					if (checkedSize == 0)
					{
						checkAllBox.setSelected(false);
						checkAllBox.setIndeterminate(false);
					} else if (checkedSize == size)
					{
						checkAllBox.setSelected(true);
						checkAllBox.setIndeterminate(false);
					} else
					{
						checkAllBox.setIndeterminate(true);
					}
				});

				return booleanProperty;
			}));


			fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			fileNameColumn.setCellFactory(column ->
			{
				TableCell<BaiduFile, String> tableCell = new TextFieldTableCell<BaiduFile, String>(new DefaultStringConverter())
				{
					@Override
					public void startEdit()
					{
						super.startEdit();
						TextField editor = (TextField) super.getGraphic();
						TableRow<BaiduFile> row = super.getTableRow();
						if (row.getItem().getIsDir().equals(0))
						{
							editor.selectRange(0, editor.getText().lastIndexOf('.'));
						}
					}

					@Override
					public void cancelEdit()
					{
						super.cancelEdit();
						fileTable.setEditable(false);
					}

					@Override
					public void commitEdit(String newValue)
					{
						if (StringUtils.isBlank(newValue))
						{
							super.cancelEdit();
						}
						super.commitEdit(newValue);
						fileTable.setEditable(false);
					}
				};
				return tableCell;
			});

			modifyTimeColumn.setCellValueFactory(new PropertyValueFactory<>("modifyTime"));

			fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
			fileSizeColumn.setCellFactory(column ->
			{
				TableCell<BaiduFile, Long> tableCell = new TableCell<BaiduFile, Long>()
				{
					@Override
					protected void updateItem(Long item, boolean empty)
					{
						int index = getIndex();

						if (index > -1 && index < fileTable.getItems().size())
						{
							BaiduFile baiduFile = fileTable.getItems().get(index);

							if (new Integer(1).equals(baiduFile.getIsDir()))
							{
								setText(null);
							} else if (!empty)
							{
								if (item < Constant.BYTE_MAX_SIZE)
								{
									setText(item + "B");
								} else if (item < Constant.k_BYTE_MAX_SIZE)
								{
									setText(item / Constant.BYTE_MAX_SIZE + "KB");
								} else if (item < Constant.M_BYTE_MAX_SIZE)
								{
									setText(new DecimalFormat("#.00").format(new Double(item) / Constant.k_BYTE_MAX_SIZE) + "MB");
								} else
								{
									setText(new DecimalFormat("#.00").format(new Double(item) / Constant.M_BYTE_MAX_SIZE) + "GB");
								}
							}
						}
					}
				};
				return tableCell;
			});


			fileTable.setRowFactory(tableView ->
			{
				TableRow<BaiduFile> row = new TableRow<>();

				row.onContextMenuRequestedProperty().set(event ->
				{
					fileTableMenu.setStyle("visibility:visible");
				});

				return row;
			});

			fileTable.setItems(FXCollections.observableArrayList(baiduFiles));

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 全选/反选
	 *
	 * @param event
	 * @throws Exception
	 */
	public void onClickToCheckAll(MouseEvent event) throws Exception
	{
		if (event.getButton().equals(MouseButton.PRIMARY))
		{
			fileTable.getItems().forEach(baiduFile ->
			{
				baiduFile.setChecked(checkAllBox.isSelected());
			});
		}
	}

	public void onTableMenuHidden()
	{
		fileTableMenu.setStyle("visibility:hidden");
	}

	public void onClickToOpenFile()
	{
	}

	public void onClickToRename()
	{
		fileTable.setEditable(true);
		fileTable.edit(fileTable.getSelectionModel().getSelectedIndex(), fileNameColumn);
	}
}