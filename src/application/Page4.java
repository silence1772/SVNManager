package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.util.Properties;
import java.util.Set;

/**
 * 设置页面
 *
 * @author wuzewei
 */
public class Page4 extends Group {
	/** 设置页面按钮 */
	Group group1;
	Rectangle button1;
	/** 关于页面按钮 */
	Group group2;
	Rectangle button2;
	/** 设置页面 */
	Group page1;
	/** 关于页面 */
	Group page2;
	/** 资源库地址 */
	TextField url;
	/** 用户名 */
	TextField name;
	/** 密码 */
	TextField password;
	FlowPane flowPane;
	/** 满分题目数量 */
	TextField fullMarkNumber;
	/** 类包名称格式 */
	TextField packageNameFormat;
	/** 重置按钮 */
	Button resetButton;
	/** 连接按钮 */
	Button connectButton;
	/** 保存配置按钮1 */
	Button saveConfigButton1;
	/** 保存配置按钮2 */
	Button saveConfigButton2;
	/** 添加按钮 */
	Button addButton;
	/** 删除按钮 */
	Button deleteButton;
	/** 专业代码列表 */
	TableView<MajorCode> table;
	/** 新增专业名称 */
	TextField addName;
	/** 新增专业代码 */
	TextField addCode;
	/** 提示文本 */
	Text tip_text;
	/** 专业代码数据 */
	public static ObservableList<MajorCode> data;
	/** 提示框 */
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	/** 属性类 */
	Properties properties;
	/** 配置文件 */
	File file = new File(this.getClass().getResource("").getPath() + "/svn.properties");
	/** 切换页面标识 */
	public static IntegerProperty router = new SimpleIntegerProperty(1);
	/** svn工具类 */
	private Svnkit svnkit;

	/**
	 * 构造方法. 初始化svn工具类以及相关逻辑.
	 */
	Page4(Svnkit svnkit) {
		this.svnkit = svnkit;
		init();
	}

	/**
	 * 初始化界面及相关数据和逻辑
	 */
	protected void init() {
		// 确保配置文件存在
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 绘制界面
		drawStaticUI();
		drawStaticUI1();
		drawStaticUI2();
		// 设置相关逻辑
		setRouter();
		getInitialValue();
		handleButtonAction();
	}

	/**
	 * 绘制标题及菜单界面
	 */
	protected void drawStaticUI() {
		Rectangle rectange = new Rectangle(250, 50, Color.rgb(255, 255, 255));
		Text text = new Text("设置");
		text.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 24));
		text.setFill(Color.rgb(70, 186, 121));
		text.setX(26);
		text.setY(34);

		Rectangle rectangle1 = new Rectangle(250, 610, Color.rgb(62, 62, 62));
		rectangle1.setLayoutY(50);

		button1 = new Rectangle(251, 50, Color.rgb(70, 186, 121));
		button2 = new Rectangle(251, 50, Color.rgb(37, 174, 96));
		button1.setLayoutX(-1);
		button1.setLayoutY(50);
		button2.setLayoutX(-1);
		button2.setLayoutY(100);

		Text text1 = new Text("高级选项");
		text1.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 16));
		text1.setFill(Color.rgb(255, 255, 255));
		text1.setLayoutX(26);
		text1.setLayoutY(80);
		Text text2 = new Text("关于");
		text2.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 16));
		text2.setFill(Color.rgb(255, 255, 255));
		text2.setLayoutX(26);
		text2.setLayoutY(130);

		group1 = new Group(button1, text1);
		group2 = new Group(button2, text2);
		this.getChildren().addAll(rectange, text, rectangle1, group1, group2);
	}

	/**
	 * 绘制设置页面
	 */
	protected void drawStaticUI1() {
		Rectangle background = new Rectangle(680, 660, Color.rgb(255, 255, 255));
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(50, 0, 0, 25));
		gridPane.setVgap(6);
		gridPane.setHgap(10);

		Text text1 = new Text("SVN资源库URL：");
		GridPane.setConstraints(text1, 0, 0);
		Text text2 = new Text("用户名：");
		GridPane.setConstraints(text2, 0, 1);
		Text text3 = new Text("密码：");
		GridPane.setConstraints(text3, 0, 2);

		url = new TextField();
		url.setPrefWidth(500);
		GridPane.setConstraints(url, 1, 0);
		name = new TextField();
		GridPane.setConstraints(name, 1, 1);
		password = new TextField();
		GridPane.setConstraints(password, 1, 2);

		resetButton = new Button("复位");
		resetButton.setLayoutX(400);
		resetButton.setLayoutY(150);
		resetButton.getStyleClass().add("btn1");

		connectButton = new Button("连接");
		connectButton.setLayoutX(480);
		connectButton.setLayoutY(150);
		connectButton.getStyleClass().add("btn1");

		saveConfigButton1 = new Button("保存");
		saveConfigButton1.setLayoutX(560);
		saveConfigButton1.setLayoutY(150);
		saveConfigButton1.getStyleClass().add("btn1");

		GridPane gridPane1 = new GridPane();
		gridPane1.setPadding(new Insets(50, 0, 0, 25));
		gridPane1.setVgap(6);
		gridPane1.setHgap(20);

		flowPane = new FlowPane();
		flowPane.setHgap(10);
		flowPane.setVgap(5);
		flowPane.setPrefWidth(500);
		flowPane.setLayoutX(130);
		flowPane.setLayoutY(200);
		for (int i = 1; i <= 20; i++) {
			flowPane.getChildren().add(new RadioButton(String.valueOf(i)));
		}

		Text text7 = new Text("选择计分章节：");
		GridPane.setConstraints(text7, 0, 0);

		Text text = new Text("满分题目量：");
		GridPane.setConstraints(text, 0, 1);
		fullMarkNumber = new TextField();
		GridPane.setConstraints(fullMarkNumber, 1, 1);

		Text text4 = new Text("项目名称格式：");
		GridPane.setConstraints(text4, 0, 2);
		ChoiceBox choiceBox1 = new ChoiceBox(FXCollections.observableArrayList("学号+姓名"));
		choiceBox1.getSelectionModel().select(0);
		GridPane.setConstraints(choiceBox1, 1, 2);

		Text text5 = new Text("类包名称格式：");
		GridPane.setConstraints(text5, 0, 3);
		packageNameFormat = new TextField();
		packageNameFormat.setPrefWidth(200);
		GridPane.setConstraints(packageNameFormat, 1, 3);
		ChoiceBox choiceBox2 = new ChoiceBox(FXCollections.observableArrayList(".姓名拼音.chapter01.Exercise01"));
		choiceBox2.getSelectionModel().select(0);
		GridPane.setConstraints(choiceBox2, 2, 3);

		saveConfigButton2 = new Button("保存");
		saveConfigButton2.getStyleClass().add("btn1");
		saveConfigButton2.setLayoutX(495);
		saveConfigButton2.setLayoutY(330);

		Text text6 = new Text("学号专业代码：");
		GridPane.setConstraints(text6, 0, 9);

		table = new TableView<>();
		table.setEditable(true);
		data = FXCollections.observableArrayList();
		TableColumn NameCol = new TableColumn("专业名称");
		NameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		NameCol.setPrefWidth(130);
		TableColumn CodeCol = new TableColumn("专业代码");
		CodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
		CodeCol.setPrefWidth(68);
		table.setItems(data);
		table.getColumns().addAll(NameCol, CodeCol);
		table.setPrefWidth(200);
		table.setPrefHeight(206);
		GridPane.setConstraints(table, 1, 10);

		addName = new TextField();
		addName.setMaxWidth(NameCol.getPrefWidth());
		addName.setPromptText("专业名称");
		addName.setLayoutX(128);
		addName.setLayoutY(590);

		addCode = new TextField();
		addCode.setMaxWidth(CodeCol.getPrefWidth());
		addCode.setPromptText("专业代码");
		addCode.setLayoutX(260);
		addCode.setLayoutY(590);

		tip_text = new Text("");
		tip_text.setFill(Color.rgb(130, 130, 135));
		tip_text.setLayoutX(128);
		tip_text.setLayoutY(630);

		addButton = new Button("新添");
		addButton.getStyleClass().add("btn1");
		addButton.setLayoutX(340);
		addButton.setLayoutY(588);

		deleteButton = new Button("删除");
		deleteButton.getStyleClass().add("btn1");
		deleteButton.setLayoutX(420);
		deleteButton.setLayoutY(588);

		gridPane.getChildren().addAll(text1, text2, text3, url, name, password);
		gridPane1.getChildren().addAll(text7, text, fullMarkNumber, text4, choiceBox1, text5, packageNameFormat,
				choiceBox2, text6, table);
		gridPane1.setLayoutY(170);
		page1 = new Group(background, gridPane, gridPane1, resetButton, connectButton, saveConfigButton1,
				saveConfigButton2, addName, addCode, tip_text, addButton, deleteButton, flowPane);
		page1.setLayoutX(250);
		this.getChildren().addAll(page1);
	}

	/**
	 * 绘制关于页面
	 */
	protected void drawStaticUI2() {
		Rectangle background = new Rectangle(680, 660, Color.rgb(255, 255, 255));

		Image image = new Image(Page4.class.getResourceAsStream("/resources/logo_02.png"));
		ImageView imageView = new ImageView();
		imageView.setImage(image);
		imageView.setFitWidth(250);
		imageView.setFitHeight(130);
		imageView.setLayoutX(10);
		imageView.setLayoutY(50);

		Text text = new Text("v_1.0_180415");
		text.setLayoutX(500);
		text.setLayoutY(120);

		page2 = new Group(background, imageView, text);
		page2.setLayoutX(250);
	}

	/**
	 * 设置按钮逻辑
	 */
	protected void setRouter() {
		group1.setOnMouseEntered(e -> {
			button1.setFill(Color.rgb(70, 186, 121));
		});
		group2.setOnMouseEntered(e -> {
			button2.setFill(Color.rgb(70, 186, 121));
		});
		group1.setOnMouseExited(e -> {
			if (router.intValue() != 1) {
				button1.setFill(Color.rgb(37, 174, 96));
			}
		});
		group2.setOnMouseExited(e -> {
			if (router.intValue() != 2) {
				button2.setFill(Color.rgb(37, 174, 96));
			}
		});
		group1.setOnMouseClicked(e -> {
			if (router.intValue() != 1) {
				this.getChildren().add(page1);
				this.getChildren().remove(page2);
				router.setValue(1);
			}
			button1.setFill(Color.rgb(70, 186, 121));
			button2.setFill(Color.rgb(37, 174, 96));
		});
		group2.setOnMouseClicked(e -> {
			if (router.intValue() != 2) {
				this.getChildren().add(page2);
				this.getChildren().remove(page1);
				router.setValue(2);
			}
			button2.setFill(Color.rgb(70, 186, 121));
			button1.setFill(Color.rgb(37, 174, 96));
		});
	}

	/**
	 * 获取数据初始值
	 */
	protected void getInitialValue() {
		properties = new Properties();
		try {
			properties.load(Page4.class.getResourceAsStream("svn.properties"));
			url.setText(properties.getProperty("URL"));
			name.setText(properties.getProperty("NAME"));
			password.setText(properties.getProperty("PASSWORD"));
			fullMarkNumber.setText(properties.getProperty("FULL_MARK_NUMBER"));
			packageNameFormat.setText(properties.getProperty("PACKAGE_NAME_FORMAT"));
			Data.packageNameFormat = properties.getProperty("PACKAGE_NAME_FORMAT");
			Data.fullMarkNumber = Integer.parseInt(properties.getProperty("FULL_MARK_NUMBER"));
			Data.totalChapter = Integer.parseInt(properties.getProperty("TOTAL_CHAPTER"));
			Set<Object> keys = properties.keySet();
			for (Object key : keys) {
				if (key.toString().matches("^(\\d{4})$")) {
					data.add(new MajorCode(properties.getProperty(key.toString()), key.toString()));
				}
				if (key.toString().matches("^(\\d{1,2})$")) {
					if (properties.getProperty(key.toString()).equals("true")) {
						RadioButton radioButton = (RadioButton) flowPane.getChildren()
								.get(Integer.parseInt(key.toString()) - 1);
						radioButton.setSelected(true);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理按钮点击事件
	 */
	protected void handleButtonAction() {
		saveConfigButton1.setOnAction(event -> {
			if (url.getText().isEmpty() || name.getText().isEmpty() || password.getText().isEmpty()) {
				setAlert(Alert.AlertType.INFORMATION, "保存失败", "内容不能为空");
				url.setText(properties.getProperty("URL"));
				name.setText(properties.getProperty("NAME"));
				password.setText(properties.getProperty("PASSWORD"));
				return;
			}

			try {
				OutputStream fos = new FileOutputStream(file);
				properties.setProperty("URL", url.getText());
				properties.setProperty("NAME", name.getText());
				properties.setProperty("PASSWORD", password.getText());
				try {
					properties.store(fos, "update svn config");
					fos.close();
					setAlert(Alert.AlertType.INFORMATION, "保存成功", "配置已经更新");
				} catch (IOException e) {
					setAlert(Alert.AlertType.ERROR, "保存失败", "配置无法正常保存，请重试");
					return;
				}
			} catch (FileNotFoundException e) {
				setAlert(Alert.AlertType.ERROR, "保存失败", "配置无法正常保存，请重试");
				return;
			}
		});

		connectButton.setOnAction(event -> {
			if (url.getText().isEmpty() || name.getText().isEmpty() || password.getText().isEmpty()) {
				setAlert(Alert.AlertType.ERROR, "连接错误", "内容不能为空");
				url.setText(properties.getProperty("URL"));
				name.setText(properties.getProperty("NAME"));
				password.setText(properties.getProperty("PASSWORD"));
				return;
			}

			setAlert(Alert.AlertType.INFORMATION, "连接中...", "正在连接到资源库，请耐心等待");
			int flag = svnkit.connectSVN(url.getText(), name.getText(), password.getText());
			alert.close();
			switch (flag) {
			case 0:
				setAlert(Alert.AlertType.INFORMATION, "连接成功，正在更新中...", "正在从资源库拉取信息，请耐心等待");
				Controller.freshData.setValue(true);
				Controller.freshPage1.setValue(true);
				Controller.freshPage2.setValue(true);
				Controller.freshPage3.setValue(true);
				alert.close();
				setAlert(Alert.AlertType.INFORMATION, "更新完成", "已成功连接到资源库，请保存配置");
				break;
			case 1:
				setAlert(Alert.AlertType.ERROR, "连接错误", "请检查资源库地址是否正确");
				break;
			case 2:
				setAlert(Alert.AlertType.ERROR, "连接错误", "该资源库路径不存在");
				break;
			case 3:
				setAlert(Alert.AlertType.ERROR, "连接错误", "该资源库路径不是目录");
				break;
			case 4:
				setAlert(Alert.AlertType.ERROR, "连接错误", "请检查资源库地址或用户名密码是否正确");
				break;
			}

		});

		resetButton.setOnAction(event -> {
			url.setText(properties.getProperty("URL"));
			name.setText(properties.getProperty("NAME"));
			password.setText(properties.getProperty("PASSWORD"));
		});

		saveConfigButton2.setOnAction(event -> {
			if (!packageNameFormat.getText().matches("^[A-Za-z0-9][A-Za-z0-9.]{0,}[A-Za-z0-9]$")) {
				setAlert(Alert.AlertType.ERROR, "保存失败", "请输入正确格式的字符串");
				return;
			}
			if (!fullMarkNumber.getText().matches("^(\\d{1,2})$")) {
				setAlert(Alert.AlertType.ERROR, "保存失败", "请输入正确的题目数量");
				return;
			}

			try {
				OutputStream fos = new FileOutputStream(file);
				properties.setProperty("FULL_MARK_NUMBER", fullMarkNumber.getText());
				properties.setProperty("PACKAGE_NAME_FORMAT", packageNameFormat.getText());
				int totalChapter = 0;
				for (int i = 0; i < 20; i++) {
					RadioButton radioButton = (RadioButton) flowPane.getChildren().get(i);
					if (radioButton.selectedProperty().getValue()) {
						properties.setProperty(String.valueOf(i + 1), "true");
						totalChapter++;
					} else {
						properties.setProperty(String.valueOf(i + 1), "false");
					}
				}
				properties.setProperty("TOTAL_CHAPTER", String.valueOf(totalChapter));
				Data.packageNameFormat = properties.getProperty("PACKAGE_NAME_FORMAT");
				Data.fullMarkNumber = Integer.parseInt(properties.getProperty("FULL_MARK_NUMBER"));
				Data.totalChapter = Integer.parseInt(properties.getProperty("TOTAL_CHAPTER"));
				try {
					properties.store(fos, "update cpmpute config");
					fos.close();
					setAlert(Alert.AlertType.INFORMATION, "保存成功", "配置已经更新");
				} catch (IOException e) {
					setAlert(Alert.AlertType.ERROR, "保存失败", "配置无法正常保存，请重试");
					return;
				}
			} catch (FileNotFoundException e) {
				setAlert(Alert.AlertType.ERROR, "保存失败", "配置无法正常保存，请重试");
				return;
			}
		});

		addButton.setOnAction(event -> {
			if (addName.getText().isEmpty() || !addCode.getText().matches("^(\\d{4})$")) {
				tip_text.setText("请正确填写");
			} else if (properties.containsKey(addCode.getText())) {
				setAlert(Alert.AlertType.ERROR, "添加失败", "该代码已经存在，请删除后再添加");
			} else {
				try {
					OutputStream fos = new FileOutputStream(file);
					properties.setProperty(addCode.getText(), addName.getText());
					try {
						properties.store(fos, "update cpmpute config");
						fos.close();
						data.add(new MajorCode(addName.getText(), addCode.getText()));
						tip_text.setText("");
						addName.clear();
						addCode.clear();
						setAlert(Alert.AlertType.INFORMATION, "添加成功", "配置已经更新");
					} catch (IOException e) {
						setAlert(Alert.AlertType.ERROR, "添加失败", "配置无法正常保存，请重试");
						return;
					}
				} catch (FileNotFoundException e) {
					setAlert(Alert.AlertType.ERROR, "添加失败", "配置无法正常保存，请重试");
					return;
				}
			}
		});

		deleteButton.setOnAction(event -> {
			if (table.getSelectionModel().selectedIndexProperty().intValue() >= 0) {
				try {
					OutputStream fos = new FileOutputStream(file);
					properties.remove(table.getSelectionModel().getSelectedItem().code.getValue());
					try {
						properties.store(fos, "delete cpmpute config");
						fos.close();
						data.remove(table.getSelectionModel().selectedIndexProperty().intValue());
						tip_text.setText("");
						setAlert(Alert.AlertType.INFORMATION, "删除成功", "配置已经更新");
					} catch (IOException e) {
						setAlert(Alert.AlertType.ERROR, "删除失败", "配置无法正常保存，请重试");
						return;
					}
				} catch (FileNotFoundException e) {
					setAlert(Alert.AlertType.ERROR, "删除失败", "配置无法正常保存，请重试");
					return;
				}
			} else {
				tip_text.setText("请先选择要删除的条目");
			}
		});
	}

	/**
	 * 设置提示框内容
	 *
	 * @param alertType
	 * @param headerText
	 * @param contentText
	 */
	protected void setAlert(Alert.AlertType alertType, String headerText, String contentText) {
		alert.setAlertType(alertType);
		alert.setTitle("提示");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.show();
	}

	/**
	 * 列表所用保存专业名称和代码的类
	 */
	public static class MajorCode {
		/**
		 * 专业名称
		 */
		private SimpleStringProperty name;
		/**
		 * 专业代码
		 */
		private SimpleStringProperty code;

		private MajorCode(String name, String code) {
			this.name = new SimpleStringProperty(name);
			this.code = new SimpleStringProperty(code);
		}

		public String getName() {
			return name.get();
		}

		public SimpleStringProperty nameProperty() {
			return name;
		}

		public void setName(String name) {
			this.name.set(name);
		}

		public String getCode() {
			return code.get();
		}

		public SimpleStringProperty codeProperty() {
			return code;
		}

		public void setCode(String code) {
			this.code.set(code);
		}
	}
}
