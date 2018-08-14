package com.csh.coustom.control;

import com.csh.utils.FontAwesome;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

public class PathLink extends Hyperlink {

	private String path;

	private FontAwesome icon;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FontAwesome getIcon() {
		return icon;
	}

	public void setIcon(FontAwesome icon) {
		this.icon = icon;
		if (this.icon == null) {
			this.setGraphic(null);
		} else {
			Label label = new Label(icon.value);
			label.getStyleClass().add("icon");
			this.setGraphic(label);
		}
	}

	public PathLink() {
	}

	public PathLink(String text) {
		super(text);
	}

	public PathLink(FontAwesome icon) {
		this.setIcon(icon);
	}

	public PathLink(String text, String path) {
		super(text);
		this.path = path;
	}

	public PathLink(String text, String path, FontAwesome icon) {
		super(text);
		this.path = path;
		this.setIcon(icon);
	}
}
