package com.csh.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Pagination;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageController extends Pagination implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.setPageFactory(i -> new ImageView("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534313752786&di=8059839562bc56b71cafb0309e19f1b3&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201406%2F29%2F002350y098au8440ja80gm.jpg"));

	}
}
