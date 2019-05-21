package com.csh.service;

import cn.hutool.core.util.ObjectUtil;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.http.RequestProxy;
import com.csh.model.BaiduFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoadDataService extends Service<ObservableList<BaiduFile>> {

	private Query query;

	public class Query {

		private String url;

		private String path;

		private String text;

		private String serach;

		private Integer categroy;

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

		@Override
		public String toString() {
			return "Query{" +
					"url='" + url + '\'' +
					", path='" + path + '\'' +
					", text='" + text + '\'' +
					", serach='" + serach + '\'' +
					", categroy=" + categroy +
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
		MessageDialog.show("文件列表获取失败，请稍后重试！", getException());
	}

	public void load(Query query) {
		this.query = query;
		this.restart();
	}

	public Query getQuery() {
		return ObjectUtil.defaultIfNull(query, new Query());
	}
}
