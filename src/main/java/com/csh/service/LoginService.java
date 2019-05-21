package com.csh.service;

import cn.hutool.json.JSONObject;
import com.csh.coustom.dialog.MessageDialog;
import com.csh.http.RequestProxy;
import com.csh.utils.Constant;
import com.csh.utils.CookieUtil;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

public class LoginService extends Service<BooleanProperty> {

	private StringProperty username = new SimpleStringProperty("百度网盘");

	private ObjectProperty<Image> avatar = new SimpleObjectProperty<>(new Image("image/logo.png"));

	private StringProperty quotaText = new SimpleStringProperty("0 GB / 0 GB");

	private DoubleProperty quotaProgress = new SimpleDoubleProperty(1.0);

	public String getUsername() {
		return username.get();
	}

	public StringProperty usernameProperty() {
		return username;
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}

	public void setAvatar(Image avatar) {
		this.avatar.set(avatar);
	}

	public String getQuotaText() {
		return quotaText.get();
	}

	public StringProperty quotaTextProperty() {
		return quotaText;
	}

	public void setQuotaText(String quotaText) {
		this.quotaText.set(quotaText);
	}

	public double getQuotaProgress() {
		return quotaProgress.get();
	}

	public DoubleProperty quotaProgressProperty() {
		return quotaProgress;
	}

	public void setQuotaProgress(double quotaProgress) {
		this.quotaProgress.set(quotaProgress);
	}

	@Override
	protected Task<BooleanProperty> createTask() {
		return new Task<BooleanProperty>() {
			@Override
			protected BooleanProperty call() {
				if (CookieUtil.COOKIES.isEmpty()) throw new RuntimeException("请登录百度网盘账号");
				return new SimpleBooleanProperty(!RequestProxy.getYunData().isEmpty());
			}
		};
	}

	@Override
	protected void failed() {
		MessageDialog.show("网盘信息获取失败，请稍后重试！", this.getException());
	}

	@Override
	protected void succeeded() {
		this.setUsername(RequestProxy.YUN_DATA.getStr(Constant.NAME_KEY));
		this.setAvatar(new Image(RequestProxy.YUN_DATA.getStr(Constant.AVATAR_KEY)));

		JSONObject info = RequestProxy.getQuotaInfo();

		double used = info.getDouble("used");
		double total = info.getDouble("total");
		this.quotaProgress.set(used / total);
		this.quotaText.set(Math.round(used / Constant.M_BYTE_MAX_SIZE) + " GB / " + Math.round(total / Constant.M_BYTE_MAX_SIZE) + " GB");

	}
}
