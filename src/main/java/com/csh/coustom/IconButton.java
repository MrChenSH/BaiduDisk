package com.csh.coustom;


import com.csh.utils.FontAwesome;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class IconButton extends Button {

	private FontAwesome icon;

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

	public IconButton() {
		super();
	}

	public IconButton(String text) {
		super(text);
	}

	public IconButton(FontAwesome icon) {
		this.setIcon(icon);
	}

	public IconButton(String text, FontAwesome icon) {
		super(text);
		this.setIcon(icon);
	}
}
