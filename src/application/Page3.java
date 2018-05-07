package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

/**
 * 成绩分析页面
 *
 * @author wuzewei
 */
public class Page3 extends Group {
	/** 学生数据 */
	public ArrayList<Data.StudentData> students;
	/** 成绩排行按钮 */
	Group group1;
	Rectangle button1;
	/** 导出成绩按钮 */
	Group group2;
	Rectangle button2;
	/** 成绩排行页面 */
	Group page1;
	/** 导出成绩页面 */
	Group page2;

	/**
	 * 成绩排行
	 */
	TableView<StudentScore> tableView;
	public static ObservableList<StudentScore> data;
	XYChart.Series series;
	/**
	 * 数据类
	 */
	Data allData;
	/**
	 * 切换页面标识
	 */
	public static IntegerProperty router = new SimpleIntegerProperty(1);

	/**
	 * 构造方法. 初始化数据及相关逻辑
	 *
	 * @param data
	 */
	Page3(Data data) {
		this.allData = data;
		drawHint();
		Controller.freshPage3.addListener((ov, oldb, newb) -> {
			if (newb) {
				this.getChildren().clear();
				// 绘制界面
				drawStaticUI();
				drawStaticUI1();
				drawStaticUI2();
				// 更新数据
				setRouter();
				students = allData.allStudentData;
				getRank();
				getDistribution();
				Controller.freshPage3.setValue(false);
			}
		});
	}

	/**
	 * 绘制初始化提示界面
	 */
	protected void drawHint() {
		Text text = new Text("尚未连接到SVN资源库，请先连接");
		text.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 30));
		text.setLayoutX(250);
		text.setLayoutY(300);
		text.setFill(Color.rgb(130, 130, 135));
		this.getChildren().add(text);
	}

	/**
	 * 绘制标题及菜单界面
	 */
	protected void drawStaticUI() {
		Rectangle rectange = new Rectangle(250, 50, Color.rgb(255, 255, 255));
		Text text = new Text("成绩分析");
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

		Text text1 = new Text("分数排行");
		text1.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 16));
		text1.setFill(Color.rgb(255, 255, 255));
		text1.setLayoutX(26);
		text1.setLayoutY(80);

		Text text2 = new Text("成绩导出");
		text2.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 16));
		text2.setFill(Color.rgb(255, 255, 255));
		text2.setLayoutX(26);
		text2.setLayoutY(130);

		group1 = new Group(button1, text1);
		group2 = new Group(button2, text2);
		this.getChildren().addAll(rectange, text, rectangle1, group1, group2);
	}

	/**
	 * 绘制成绩排行页面
	 */
	protected void drawStaticUI1() {
		tableView = new TableView<>();
		tableView.setEditable(true);
		TableColumn IdCol = new TableColumn("学号");
		IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		TableColumn NameCol = new TableColumn("姓名");
		NameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn ScoreCol = new TableColumn("分数");
		ScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

		tableView.getColumns().addAll(IdCol, NameCol, ScoreCol);
		data = FXCollections.observableArrayList();
		tableView.setItems(data);
		tableView.setLayoutX(20);
		tableView.setLayoutY(10);
		tableView.setPrefWidth(625);
		tableView.setPrefHeight(250);

		final NumberAxis xAxis = new NumberAxis(0, 100, 1);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<>(xAxis, yAxis);
		ac.setTitle("分数段人数分布");
		series = new XYChart.Series();
		series.setName("分数-人数");
		ac.getData().add(series);
		ac.setLayoutX(10);
		ac.setLayoutY(260);
		ac.setPrefWidth(650);

		page1 = new Group(tableView, ac);
		page1.setLayoutX(250);
		this.getChildren().add(page1);
	}

	/**
	 * 绘制导出成绩页面
	 */
	protected void drawStaticUI2() {
		Rectangle background = new Rectangle(680, 660, Color.rgb(255, 255, 255));
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(50, 0, 0, 30));
		gridPane.setVgap(6);
		gridPane.setHgap(10);

		Text text = new Text("生成成绩表格：");
		GridPane.setConstraints(text, 0, 0);

		Button button = new Button("点击导出");
		button.getStyleClass().add("btn1");
		button.setPrefWidth(100);
		GridPane.setConstraints(button, 1, 0);
		button.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("保存文件");
			fileChooser.setInitialFileName("score");
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Excel表格文件", "*.csv"),
					new FileChooser.ExtensionFilter("TXT 文本文件", "*.txt"),
					new FileChooser.ExtensionFilter("All Files", "*.*"));
			Stage stage = new Stage();
			File file = fileChooser.showSaveDialog(stage);
			if (file != null) {
				try {
					creatCSV(file);
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		gridPane.getChildren().addAll(text, button);
		page2 = new Group(background, gridPane);
		page2.setLayoutX(250);
	}

	/**
	 * 获取成绩排行
	 */
	protected void getRank() {
		Collections.sort(students);
		for (Data.StudentData student : students) {
			data.add(new StudentScore(student.id, student.name, (int) student.score));
		}
	}

	/**
	 * 获取成绩分布
	 */
	protected void getDistribution() {
		int[] distribu = new int[101];
		for (int i = 0; i <= 100; i++) {
			distribu[i] = 0;
		}
		for (Data.StudentData student : students) {
			if ((int) student.score <= 100 && (int) student.score >= 0) {
				distribu[(int) student.score] += 1;
			}
		}
		for (int i = 0; i <= 100; i++) {
			series.getData().add(new XYChart.Data(i, distribu[i]));
		}
	}

	/**
	 * 导出成绩文件
	 *
	 * @param file
	 *            导出的文件
	 * @throws IOException
	 */
	protected void creatCSV(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
		ow.write("学号,姓名,分数");
		ow.write("\r\n");

		for (Data.StudentData student : students) {
			ow.write(student.id + "," + student.name + "," + (int) student.score);
			ow.write("\r\n");
		}
		ow.flush();
		ow.close();
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
	 * 排行榜所用学生成绩类
	 */
	public static class StudentScore {
		/**
		 * 学号
		 */
		private SimpleStringProperty id;
		/**
		 * 姓名
		 */
		private SimpleStringProperty name;
		/**
		 * 成绩
		 */
		private SimpleIntegerProperty score;

		private StudentScore(String id, String name, int score) {
			this.id = new SimpleStringProperty(id);
			this.name = new SimpleStringProperty(name);
			this.score = new SimpleIntegerProperty(score);
		}

		public String getId() {
			return id.get();
		}

		public SimpleStringProperty idProperty() {
			return id;
		}

		public void setId(String id) {
			this.id.set(id);
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

		public int getScore() {
			return score.get();
		}

		public SimpleIntegerProperty scoreProperty() {
			return score;
		}

		public void setScore(int score) {
			this.score.set(score);
		}
	}
}
