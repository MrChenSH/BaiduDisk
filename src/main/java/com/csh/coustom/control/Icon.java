package com.csh.coustom.control;

import com.csh.utils.FontIcon;
import javafx.scene.control.Label;


public class Icon extends Label {


	private FontIcon icon;

	public FontIcon getIcon() {
		return icon;
	}

	public void setIcon(FontIcon icon) {
		this.icon = icon;
		if (icon != null) {
			this.setText(icon.value);
		}
	}

	public Icon() {
		super();
	}

	public Icon(String text) {
		super(text);
	}

	public Icon(FontIcon icon) {
		this.setIcon(icon);
	}

	public Icon(FontIcon icon, String... className) {
		this.setIcon(icon);
		this.getStyleClass().addAll(className);
	}

	;
}
