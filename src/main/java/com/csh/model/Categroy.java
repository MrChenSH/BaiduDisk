package com.csh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Categroy {

	private String url;

	private String text;

	private String icon;

	private Integer category;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Categroy{" +
				"url='" + url + '\'' +
				", text='" + text + '\'' +
				", icon='" + icon + '\'' +
				", category=" + category +
				'}';
	}
}
