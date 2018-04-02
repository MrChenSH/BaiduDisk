package com.csh.controller;

import com.csh.app.App;
import com.csh.http.RequestProxy;
import com.csh.utils.Constant;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController extends AnchorPane implements Initializable
{

	@FXML
	private TextField userField;

	@FXML
	private PasswordField pwdField;

	@FXML
	private Pane verifyPane;

	@FXML
	private TextField verifyField;

	@FXML
	private ImageView verifyImage;

	@FXML
	private Hyperlink verifyLink;

	@FXML
	private Button loginBtn;

	@FXML
	private Label errorLabel;

	@FXML
	private WebView webView;

	private WebEngine engine;

	private App app;

	/**
	 * 验证码ID
	 */
	private static String verifyCodeString;

	public void setApp(App app)
	{
		this.app = app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		userField.focusedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (StringUtils.isNotBlank(userField.getText()) && !newValue)
			{
				if (verifyImage.getImage() == null)
				{
					new Thread(() ->
					{
						try
						{
							if (StringUtils.isBlank(RequestProxy.dv))
							{
								RequestProxy.dv = engine.executeScript("if(dv_Input)dv_Input.value;").toString();
							}

							verifyCodeString = RequestProxy.loginCheck(userField.getText());

							if (StringUtils.isNotBlank(verifyCodeString))
							{
								verifyPane.setVisible(true);
								verifyImage.setImage(new Image(Constant.PASS_VERIFY_CODE_URL + verifyCodeString));
							} else
							{
								verifyPane.setVisible(false);
							}

						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}).start();
				}
			}
		});

		final List<String> scripts = new ArrayList<>();
		try
		{
			File s = new File(App.class.getResource("/js/index.js").toURI());
			scripts.add(FileUtils.readFileToString(s, Constant.CHARSET_UTF_8));

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		this.engine = webView.getEngine();
		this.engine.setJavaScriptEnabled(true);
		this.engine.setUserAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Mobile Safari/537.36");

		this.engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) ->
		{
			try
			{
				//			this.engine.executeScript(scripts.get(0));
				if (newValue.equals(Worker.State.SUCCEEDED))
				{
					RequestProxy.dv = engine.executeScript("dv_Input.value;").toString();
					if (StringUtils.isBlank(RequestProxy.dv))
					{
						this.engine.reload();
					} else
					{
						app.getPrimaryStage().show();
					}
					System.out.println(RequestProxy.dv);
				}
			} catch (Exception e)
			{

			}
		});
		this.engine.load(Constant.BASE_URL);
	}

	public static void main(String[] args) throws Exception
	{
//		File s = new File(App.class.getResource("/js/index.js").toURI());
//		System.out.println(FileUtils.readFileToString(s, Constant.CHARSET_UTF_8));
	}

	/**
	 * 更换验证码
	 */
	public void onClickToRefreshVerifyCode()
	{
		new Thread(() ->
		{
			try
			{
				verifyCodeString = RequestProxy.loginCheck(userField.getText());

				if (verifyCodeString.isEmpty())
				{
					verifyPane.setVisible(false);
				} else
				{
					verifyImage.setImage(new Image(Constant.PASS_VERIFY_CODE_URL + verifyCodeString));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * 登录
	 */
	public void onClickToLogin()
	{
		String username = userField.getText();

		String password = pwdField.getText();

		String verifyCode = verifyField.getText();

		if (StringUtils.isBlank(username))
		{
			errorLabel.setVisible(true);
			errorLabel.setText("请输入用户名！");
			return;
		}
		if (password.isEmpty())
		{
			errorLabel.setVisible(true);
			errorLabel.setText("请输入密码！");
			return;
		}

		if (verifyPane.isVisible())
		{
			if (verifyCode.isEmpty())
			{

				errorLabel.setVisible(true);
				errorLabel.setText("请输入验证码！");
				return;
			} else
			{
				try
				{
					String message = RequestProxy.checkVerifyCode(verifyField.getText(), verifyCodeString);

					if (StringUtils.isNotBlank(message))
					{
						errorLabel.setVisible(true);
						errorLabel.setText(message);
						return;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		System.out.println(engine.executeScript("localStorage.FP_UID"));
		RequestProxy.fpInfo = engine.executeScript("window.PP_FP_INFO").toString();
		RequestProxy.fpUid = engine.executeScript("localStorage.FP_UID").toString();


		new Thread(() ->
		{
			try
			{
				JSONObject json = RequestProxy.login(username, password, verifyCode, verifyCodeString);

				if (json.isEmpty())
				{
					Platform.runLater(() ->
					{
						errorLabel.setVisible(true);
						errorLabel.setText("登录失败，请稍候再试！");
					});
				} else
				{
					RequestProxy.jumpHtml();

					int errorCode = json.getInt("err_no");

					if (errorCode == 0)
					{
						String html = RequestProxy.visitHome();

						Matcher matcher = Pattern.compile("var context=(.*);").matcher(html);

						if (matcher.find())
						{
							RequestProxy.yunData = JSONObject.fromObject(matcher.group(1));

							Platform.runLater(() ->
							{
								try
								{
									app.getPrimaryStage().hide();
									app.generateMainPanel();
									app.getPrimaryStage().show();
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							});
						}
					} else
					{
						Platform.runLater(() ->
						{
							errorLabel.setVisible(true);
							if (Constant.ERRORS.containsKey(errorCode))
							{
								errorLabel.setText(Constant.ERRORS.get(errorCode));
							} else
							{
								errorLabel.setText("系统错误，请稍候重试！");
							}

							String codeString = json.getString("codeString");

							if (StringUtils.isNotBlank(codeString))
							{
								verifyCodeString = codeString;
								verifyPane.setVisible(true);
								verifyImage.setImage(new Image(Constant.PASS_VERIFY_CODE_URL + codeString));
							}
						});
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public void onTouchPressed(TouchEvent event)
	{
		System.out.println(event);
	}

	public void onMouseClicked(MouseEvent event)
	{
		System.out.println(event);
	}
}
