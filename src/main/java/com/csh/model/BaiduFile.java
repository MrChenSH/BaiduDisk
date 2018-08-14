package com.csh.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiduFile implements Serializable {
	@JsonProperty("fs_id")
	private LongProperty id = new SimpleLongProperty();

	@JsonProperty("server_filename")
	private StringProperty fileName = new SimpleStringProperty();

	@JsonProperty("isdir")
	private BooleanProperty isDir = new SimpleBooleanProperty();

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
		this.fileName.set(fileName);
	}

	public Boolean getIsDir() {
		return isDir.get();
	}

	public void setIsDir(Boolean isDir) {
		this.isDir.set(isDir);
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
		this.modifyTime.set(DateUtil.formatDateTime(new Date(modifyTime * 1000L)));
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
