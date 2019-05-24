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

	private StringProperty quotaText = new SimpleStringProperty("0G/0G");

	private DoubleProperty quotaProgress = new SimpleDoubleProperty(1.0);

	private StringProperty status = new SimpleStringProperty("正在登录……");

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

	public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}

	public void setStatus(String status) {
		this.status.set(status);
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
		super.failed();
		this.setStatus("登录信息已过期，请重新登录！");
		MessageDialog.show("登录信息已过期，请重新登录！", this.getException());
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		this.setStatus("正在获取网盘信息……");
		this.setUsername(RequestProxy.YUN_DATA.getStr(Constant.NAME_KEY));
		this.setAvatar(new Image(RequestProxy.YUN_DATA.getStr(Constant.AVATAR_KEY)));

		JSONObject info = RequestProxy.getQuotaInfo();

		double used = info.getDouble("used");
		double total = info.getDouble("total");
		this.quotaProgress.set(used / total);
		this.quotaText.set(Math.round(used / Constant.M_BYTE_MAX_SIZE) + "G/" + Math.round(total / Constant.M_BYTE_MAX_SIZE) + "G");
	}
}
