package com.csh.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoadDataService extends Service<ObservableList<BaiduFile>> {

	private Query query;

	private StringProperty status = new SimpleStringProperty("正在获取文件列表……");

	public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public class Query {

		private String url;

		private String path;

		private String text;

		private String serach;

		private Integer categroy;

		/**
		 * 查询额外参数
		 */
		private JSONObject extra;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getSerach() {
			return serach;
		}

		public void setSerach(String serach) {
			this.serach = serach;
		}

		public Integer getCategroy() {
			return categroy;
		}

		public void setCategroy(Integer categroy) {
			this.categroy = categroy;
		}

		public JSONObject getExtra() {
			return ObjectUtil.defaultIfNull(extra, extra = new JSONObject());
		}

		public void setExtra(JSONObject extra) {
			this.extra = extra;
		}

		@Override
		public String toString() {
			return "Query{" +
					"url='" + url + '\'' +
					", path='" + path + '\'' +
					", text='" + text + '\'' +
					", serach='" + serach + '\'' +
					", categroy=" + categroy +
					", extra=" + extra +
					'}';
		}
	}

	@Override
	protected Task<ObservableList<BaiduFile>> createTask() {
		return new Task<ObservableList<BaiduFile>>() {
			@Override
			protected ObservableList<BaiduFile> call() {
				return FXCollections.observableArrayList(RequestProxy.loadFiles(query));
			}
		};
	}

	@Override
	protected void failed() {
		super.failed();
		this.setStatus("文件列表获取失败，请稍后重试！");
		MessageDialog.show(this.getStatus(), this.getException());
	}

	@Override
	protected void running() {
		super.running();
		this.setStatus("正在获取文件列表……");
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		this.setStatus("加载完成，共" + this.getValue().size() + "项");
	}

	public void load(Query query) {
		this.query = query;
		this.restart();
	}

	public Query getQuery() {
		return ObjectUtil.defaultIfNull(query, new Query());
	}
}
