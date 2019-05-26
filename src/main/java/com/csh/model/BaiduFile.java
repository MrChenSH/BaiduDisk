package com.csh.model;

import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import com.csh.utils.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduFile extends CloneSupport<BaiduFile> implements Serializable {
	@JsonProperty("fs_id")
	private LongProperty id = new SimpleLongProperty();

	@JsonProperty("server_filename")
	private StringProperty fileName = new SimpleStringProperty();

	@JsonProperty("isdir")
	private BooleanProperty isDir = new SimpleBooleanProperty();

	@JsonProperty("dir_empty")
	private BooleanProperty dirEmpty = new SimpleBooleanProperty();

	private IntegerProperty category = new SimpleIntegerProperty();

	private LongProperty size = new SimpleLongProperty();

	private StringProperty path = new SimpleStringProperty();

	@JsonProperty("server_mtime")
	private StringProperty modifyTime = new SimpleStringProperty();

	private BooleanProperty checked = new SimpleBooleanProperty();

	private JSONObject thumbs;

	public Long getId() {
		return id.get();
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(String fileName) {
		if (Objects.equals(this.getPath(), "/apps")) fileName = "我的应用数据";
		this.fileName.set(fileName);
	}

	public Boolean getIsDir() {
		return isDir.get();
	}

	public void setIsDir(Boolean isDir) {
		this.isDir.set(isDir);
	}

	public boolean isDirEmpty() {
		return dirEmpty.get();
	}

	public void setDirEmpty(boolean dirEmpty) {
		this.dirEmpty.set(dirEmpty);
	}

	public Integer getCategory() {
		return category.get();
	}

	public void setCategory(Integer category) {
		this.category.set(category);
	}

	public Long getSize() {
		return size.get();
	}

	public void setSize(Long size) {
		this.size.set(size);
	}

	public String getPath() {
		return path.get();
	}

	public void setPath(String path) {
		this.path.set(path);
	}

	public String getModifyTime() {
		return modifyTime.get();
	}

	public void setModifyTime(Long modifyTime) {
		if (modifyTime != null) this.modifyTime.set(new DateTime(modifyTime * 1000).toString());
	}

	public Boolean getChecked() {
		return checked.get();
	}

	public void setChecked(Boolean checked) {
		this.checked.set(checked);
	}

	public LongProperty idProperty() {
		return id;
	}

	public StringProperty fileNameProperty() {
		return fileName;
	}

	public BooleanProperty isDirProperty() {
		return isDir;
	}

	public BooleanProperty dirEmptyProperty() {
		return dirEmpty;
	}

	public IntegerProperty categoryProperty() {
		return category;
	}

	public LongProperty sizeProperty() {
		return size;
	}

	public StringProperty pathProperty() {
		return path;
	}

	public StringProperty modifyTimeProperty() {
		return modifyTime;
	}

	public BooleanProperty checkedProperty() {
		return checked;
	}

	public String getIcon() {
		if (this.getPath() != null && this.getPath().startsWith("/apps")) return "image/FileType/Middle/Apps.png";
		if (this.getIsDir()) return "image/FileType/Middle/FolderType.png";
		Set<Map.Entry<String, String>> entries = Constant.ICON_MAP.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			if (ReUtil.contains(entry.getKey(), this.getFileName().toLowerCase())) {
				return entry.getValue();
			}
		}
		return "image/FileType/Middle/OtherType.png";
	}

	public JSONObject getThumbs() {
		return thumbs;
	}

	public void setThumbs(JSONObject thumbs) {
		this.thumbs = thumbs;
	}

	@Override
	public String toString() {
		return "BaiduFile{" +
				"id=" + id +
				", fileName=" + fileName +
				", isDir=" + isDir +
				", category=" + category +
				", size=" + size +
				", path=" + path +
				", modifyTime=" + modifyTime +
				", checked=" + checked +
				", thumbs=" + thumbs +
				'}';
	}
}
