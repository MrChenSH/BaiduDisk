package com.csh.coustom;


import com.csh.utils.FontAwesome;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;


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
}
