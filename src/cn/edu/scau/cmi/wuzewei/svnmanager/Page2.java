package cn.edu.scau.cmi.wuzewei.svnmanager;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 个人报告页面
 */
public class Page2 extends Group {
	/** 姓名列表 */
	private ListView<String> list;
	/** 姓名 */
	private Text name;
	/** 学号 */
	private Text id;
	/** 班级 */
	private Text classes;
	/** 成绩 */
	private Text score;
	/** 饼图 */
	private ObservableList<PieChart.Data> pieChartData;
	/** 条形图数据 */
	private XYChart.Series series;
	/** 提交记录 */
	private VBox recordBox;
	/** 活跃图 */
	private HBox contribution;
	/** 提示 */
	private Group tip = new Group();
	/** 字体 */
	private Font font = Font.font ("", 20);
	/** svn工具类 */
	private Svnkit svnkit;
	/** 类包名称格式 */
	// public static String packageNameFormat;
	/** 学生数据类 */
	private Data data;
	/** 所有学生数据 */
	private ArrayList<Data.StudentData> allStudentData;
	/** 单个学生数据 */
	private Data.StudentData studentData;

	/**
	 * 构造方法. 初始化svn工具类和数据类及相关逻辑
	 * 
	 * @param svnkit
	 *            svn工具类
	 * @param data
	 *            数据类
	 */
	Page2(Svnkit svnkit, Data data) {
		this.svnkit = svnkit;
		this.data = data;
		drawHint();
		Controller.freshPage2.addListener((ov, oldb, newb) -> {
			if (newb) {
				this.getChildren().clear();
				// 绘制界面
				drawStaticUI();
				drawStaticUI1();
				drawStaticUI2();
				drawStaticUI3();
				drawStaticUI4();
				drawStaticUI5();
				// 获取数据
				allStudentData = data.allStudentData;
				getList();
				Controller.freshPage2.setValue(false);
			}
		});
	}

	/**
	 * 获取名单列表
	 */
	protected void getList() {
		ObservableList<String> items = FXCollections.observableArrayList();
		list.setItems(items);
		// 获取根目录条目
		String path = "";
		List entries = null;
		try {
			entries = new ArrayList(svnkit.getDir(path));
		} catch (SVNException e) {
			e.printStackTrace();
		}
		// 添加进列表
		Collections.sort(entries);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			items.add(entry.getName());
		}
		// 设置监听器
		list.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.equals(oldValue)) {
						drawContribution(new String[] { newValue });

						studentData = getStudentData(newValue);
						getInfo(newValue);
						getPieChart();
						getScore();
						getBarChart();
						getCommitRecord(new String[] { newValue });
					}
				});
	}

	/**
	 * 绘制活跃图
	 *
	 * @param path
	 *            文件夹路径
	 */
	protected void drawContribution(String[] path) {
		// 清空原图形
		contribution.getChildren().clear();
		// 获取提交历史
		Collection logHistory = svnkit.getHistory(path);
		Iterator log = logHistory.iterator();
		SVNLogEntry logEntry = (SVNLogEntry) log.next();
		// 获取当前时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		calendar.setTime(logEntry.getDate());
		long preday = calendar.getTimeInMillis();
		long today = System.currentTimeMillis();
		int gap = (int) (today / (1000 * 60 * 60 * 24) - preday / (1000 * 60 * 60 * 24));

		int ithweek = 53 - (gap + (7 - dayOfWeek)) / 7;
		int ithday = calendar.get(Calendar.DAY_OF_WEEK);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String recordDate = simpleDateFormat.format(calendar.getTime());
		int flag = 0; // 取值-1 0 1 0表示ith还不可以作为输出 -1表示预备输出 1表示可以输出
		int cnt = 0; // 表示当天的提交次数

		for (int i = 0; i < 53; i++) {
			while (log.hasNext() && flag == 0 || flag == -1) {
				if (flag != -1) {
					logEntry = (SVNLogEntry) log.next();
				}

				calendar.setTime(logEntry.getDate());
				long tmp_preday = calendar.getTimeInMillis();
				long tmp_today = System.currentTimeMillis();
				int tmp_gap = (int) (tmp_today / (1000 * 60 * 60 * 24) - tmp_preday / (1000 * 60 * 60 * 24));

				if (flag == -1) {
					ithweek = 53 - (tmp_gap + (7 - dayOfWeek)) / 7;
					ithday = calendar.get(Calendar.DAY_OF_WEEK);
					recordDate = simpleDateFormat.format(calendar.getTime());
					flag = 0;
					cnt++;
				} else {
					if (ithweek != 53 - (tmp_gap + (7 - dayOfWeek)) / 7
							|| ithday != calendar.get(Calendar.DAY_OF_WEEK)) {
						flag = 1;
					} else {
						cnt++;
					}
				}
			}

			VBox vBox = new VBox();
			vBox.setSpacing(2);

			int d = 7;
			if (i >= 52) {
				d = dayOfWeek;
			}

			for (int j = 0; j < d; j++) {
				Rectangle rectangle = new Rectangle(10, 10);
				Text text = new Text();
				text.setFill(Color.rgb(255, 255, 255));
				text.setLayoutX(4);
				text.setLayoutY(22);

				if (i == ithweek - 1 && j == ithday - 1) {
					text.setText(String.valueOf(cnt) + " contributions on " + recordDate);
					switch (cnt) {
					case 1:
						rectangle.setFill(Color.rgb(198, 228, 139));
						break;
					case 2:
						rectangle.setFill(Color.rgb(123, 201, 111));
						break;
					case 3:
						rectangle.setFill(Color.rgb(35, 154, 59));
						break;
					case 4:
						rectangle.setFill(Color.rgb(25, 97, 39));
						break;
					default:
						rectangle.setFill(Color.rgb(25, 97, 39));
					}
					cnt = 0;
					flag = -1;
				} else {
					text.setText("No contributions");
					rectangle.setFill(Color.rgb(235, 237, 240));
				}

				rectangle.setOnMouseEntered(e -> {
					//tip.setLayoutX(e.getSceneX() - 50 - 190); //适应win7
					tip.setLayoutX(e.getSceneX() - 50 - 250); //适应win10
					tip.setLayoutY(e.getSceneY() - 36);
					tip.getChildren().remove(1);
					tip.getChildren().add(text);
					this.getChildren().add(tip);
				});
				rectangle.setOnMouseExited(e -> {
					this.getChildren().remove(tip);
				});
				vBox.getChildren().add(rectangle);
			}
			contribution.getChildren().add(vBox);
		}
	}

	/**
	 * 绘制初始化提示界面
	 */
	protected void drawHint() {
		Text text = new Text("尚未连接到SVN资源库，请先连接");
		text.setFont(Font.font ("", 30));
		text.setLayoutX(250);
		text.setLayoutY(300);
		text.setFill(Color.rgb(130, 130, 135));
		this.getChildren().add(text);
	}

	/**
	 * 绘制标题栏及姓名列表
	 */
	protected void drawStaticUI() {
		Rectangle rectange_title = new Rectangle(240, 50, Color.rgb(255, 255, 255));

		Text text_title = new Text("个人报告");
		text_title.setFont(Font.font ("", 24));
		text_title.setFill(Color.rgb(70, 186, 121));
		text_title.setX(26);
		text_title.setY(34);

		list = new ListView<>();
		list.setLayoutY(50);
		list.setPrefWidth(240);
		list.setPrefHeight(610);

		this.getChildren().addAll(rectange_title, text_title, list);
	}

	/**
	 * 绘制个人信息
	 */
	protected void drawStaticUI1() {
		int width = 260;
		int height = 150;

		Rectangle brackground = new Rectangle(width, height, Color.rgb(255, 255, 255));
		Line line1 = new Line(0, 0, 0, height);
		Line line2 = new Line(0, 0, width, 0);
		Line line3 = new Line(width, height, 0, height);
		Line line4 = new Line(width, height, width, 0);
		line1.setStroke(Color.rgb(209, 213, 218));
		line2.setStroke(Color.rgb(209, 213, 218));
		line3.setStroke(Color.rgb(209, 213, 218));
		line4.setStroke(Color.rgb(209, 213, 218));

		Text text1 = new Text("基本信息");
		Text text2 = new Text("姓名：");
		Text text3 = new Text("学号：");
		Text text4 = new Text(("班级："));
		text1.setFont(font);
		text1.setLayoutX(10);
		text1.setLayoutY(30);
		text2.setLayoutX(10);
		text2.setLayoutY(60);
		text3.setLayoutX(10);
		text3.setLayoutY(85);
		text4.setLayoutX(10);
		text4.setLayoutY(110);

		name = new Text();
		id = new Text();
		classes = new Text();
		name.setLayoutX(50);
		name.setLayoutY(60);
		id.setLayoutX(50);
		id.setLayoutY(85);
		classes.setLayoutX(50);
		classes.setLayoutY(110);

		Group graph = new Group(brackground, line1, line2, line3, line4, text1, text2, text3, text4, name, id, classes);
		graph.setLayoutX(250);
		graph.setLayoutY(20);

		this.getChildren().add(graph);
	}

	/**
	 * 绘制总览
	 */
	protected void drawStaticUI2() {
		int width = 400;
		int height = 150;

		Rectangle brackground = new Rectangle(width, height, Color.rgb(255, 255, 255));
		Line line1 = new Line(0, 0, 0, height);
		Line line2 = new Line(0, 0, width, 0);
		Line line3 = new Line(width, height, 0, height);
		Line line4 = new Line(width, height, width, 0);
		line1.setStroke(Color.rgb(209, 213, 218));
		line2.setStroke(Color.rgb(209, 213, 218));
		line3.setStroke(Color.rgb(209, 213, 218));
		line4.setStroke(Color.rgb(209, 213, 218));

		Text text1 = new Text("总览");
		Text text2 = new Text("得分");
		text1.setFont(font);
		text1.setLayoutX(10);
		text1.setLayoutY(30);
		text2.setFont(font);
		text2.setLayoutX(260);
		text2.setLayoutY(30);

		pieChartData = FXCollections.observableArrayList();
		PieChart chart = new PieChart(pieChartData);
		chart.setLabelsVisible(false);
		chart.setLegendSide(Side.LEFT);
		chart.setPrefWidth(280);
		chart.setPrefHeight(160);
		chart.setLayoutX(0);
		chart.setLayoutY(0);

		score = new Text();
		score.setFont(Font.font ("", 50));
		score.setLayoutX(300);
		score.setLayoutY(100);

		Group graph = new Group(brackground, line1, line2, line3, line4, text1, text2, chart, score);
		graph.setLayoutX(520);
		graph.setLayoutY(20);

		this.getChildren().add(graph);
	}

	/**
	 * 绘制做题情况条形图
	 */
	protected void drawStaticUI3() {
		int width = 420;
		int height = 310;

		Rectangle brackground = new Rectangle(width, height, Color.rgb(255, 255, 255));
		Line line1 = new Line(0, 0, 0, height);
		Line line2 = new Line(0, 0, width, 0);
		Line line3 = new Line(width, height, 0, height);
		Line line4 = new Line(width, height, width, 0);
		line1.setStroke(Color.rgb(209, 213, 218));
		line2.setStroke(Color.rgb(209, 213, 218));
		line3.setStroke(Color.rgb(209, 213, 218));
		line4.setStroke(Color.rgb(209, 213, 218));

		Text text1 = new Text("统计情况");
		text1.setFont(font);
		text1.setLayoutX(10);
		text1.setLayoutY(30);

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
		bc.setPrefWidth(420);
		bc.setPrefHeight(300);
		bc.setLayoutY(20);
		bc.setLegendVisible(false);
		bc.setTitle("各章完成情况");

		series = new XYChart.Series();
		bc.getData().addAll(series);

		Group graph = new Group(brackground, line1, line2, line3, line4, text1, bc);
		graph.setLayoutX(250);
		graph.setLayoutY(180);

		this.getChildren().add(graph);
	}

	/**
	 * 绘制最近提交记录
	 */
	protected void drawStaticUI4() {
		int width = 240;
		int height = 310;

		Rectangle brackground = new Rectangle(width, height, Color.rgb(255, 255, 255));
		Line line1 = new Line(0, 0, 0, height);
		Line line2 = new Line(0, 0, width, 0);
		Line line3 = new Line(width, height, 0, height);
		Line line4 = new Line(width, height, width, 0);
		line1.setStroke(Color.rgb(209, 213, 218));
		line2.setStroke(Color.rgb(209, 213, 218));
		line3.setStroke(Color.rgb(209, 213, 218));
		line4.setStroke(Color.rgb(209, 213, 218));

		Text text1 = new Text("最近提交");
		text1.setFont(font);
		text1.setLayoutX(10);
		text1.setLayoutY(30);

		recordBox = new VBox();
		recordBox.setMaxWidth(220);
		recordBox.setMaxHeight(260);
		recordBox.setLayoutX(10);
		recordBox.setLayoutY(40);

		Group graph = new Group(brackground, line1, line2, line3, line4, text1, recordBox);
		graph.setLayoutX(680);
		graph.setLayoutY(180);

		this.getChildren().add(graph);
	}

	/**
	 * 绘制活跃统计图
	 */
	protected void drawStaticUI5() {
		//Rectangle tip1 = new Rectangle(190, 36, Color.rgb(0, 0, 0, 0.7)); //适应win7
		Rectangle tip1 = new Rectangle(250, 36, Color.rgb(0, 0, 0, 0.7)); //适应win10
		tip1.setArcHeight(15);
		tip1.setArcWidth(15);
		Text tip2 = new Text("No contributions on ");
		tip.getChildren().addAll(tip1, tip2);

		Rectangle con_brackground = new Rectangle(670, 150, Color.rgb(255, 255, 255));

		Line line1 = new Line(0, 0, 0, 150);
		Line line2 = new Line(0, 0, 670, 0);
		Line line3 = new Line(670, 150, 0, 150);
		Line line4 = new Line(670, 150, 670, 0);
		line1.setStroke(Color.rgb(209, 213, 218));
		line2.setStroke(Color.rgb(209, 213, 218));
		line3.setStroke(Color.rgb(209, 213, 218));
		line4.setStroke(Color.rgb(209, 213, 218));

		Text date1 = new Text("Mon");
		Text date2 = new Text("Wed");
		Text date3 = new Text("Fri");
		date1.setFill(Color.rgb(165, 165, 170));
		date2.setFill(Color.rgb(165, 165, 170));
		date3.setFill(Color.rgb(165, 165, 170));
		date1.setLayoutX(4);
		date1.setLayoutY(60);
		date2.setLayoutX(4);
		date2.setLayoutY(84);
		date3.setLayoutX(4);
		date3.setLayoutY(110);

		Text less = new Text("Less");
		Text more = new Text("More");
		Rectangle rect1 = new Rectangle(10, 10, Color.rgb(235, 237, 240));
		Rectangle rect2 = new Rectangle(10, 10, Color.rgb(198, 228, 139));
		Rectangle rect3 = new Rectangle(10, 10, Color.rgb(123, 201, 111));
		Rectangle rect4 = new Rectangle(10, 10, Color.rgb(35, 154, 59));
		Rectangle rect5 = new Rectangle(10, 10, Color.rgb(25, 97, 39));
		less.setLayoutX(496);
		less.setLayoutY(140);
		more.setLayoutX(596);
		more.setLayoutY(140);
		rect1.setLayoutX(530);
		rect1.setLayoutY(130);
		rect2.setLayoutX(542);
		rect2.setLayoutY(130);
		rect3.setLayoutX(554);
		rect3.setLayoutY(130);
		rect4.setLayoutX(566);
		rect4.setLayoutY(130);
		rect5.setLayoutX(578);
		rect5.setLayoutY(130);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int bias = 0;
		if (dayOfMonth > 7) {
			monthOfYear += 1;
			monthOfYear %= 12;
			bias = 52 - dayOfMonth / 7 * 10;
		}
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		Group monthsText = new Group();
		monthsText.setLayoutX(30);
		monthsText.setLayoutY(30);
		for (int i = 0; i < 12; i++) {
			Text month = new Text(months[monthOfYear]);
			month.setLayoutX(i * 52 + bias);
			monthsText.getChildren().add(month);
			monthOfYear += 1;
			monthOfYear %= 12;
		}

		contribution = new HBox(2);
		contribution.setLayoutX(30);
		contribution.setLayoutY(40);
		Group graph = new Group(con_brackground, contribution, date1, date2, date3, line1, line2, line3, line4, less,
				more, rect1, rect2, rect3, rect4, rect5, monthsText);
		graph.setLayoutX(250);
		graph.setLayoutY(500);

		this.getChildren().add(graph);
	}

	/**
	 * 获取指定学生数据
	 *
	 * @param path
	 *            指定学生路径
	 * @return 指定学生数据
	 */
	protected Data.StudentData getStudentData(String path) {
		for (Data.StudentData student : allStudentData) {
			if (path.equals(student.id + student.name)) {
				return student;
			}
		}
		return null;
	}

	/**
	 * 获取学生信息
	 *
	 * @param path
	 *            学生路径
	 */
	protected void getInfo(String path) {
		Pattern pattern = Pattern.compile("^(\\d{4})(\\d{4})(\\d{2})(\\d{2})([\\u4e00-\\u9fa5]{2,6})$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(path);
		if (matcher.matches()) {
			id.setText(matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4));
			name.setText(matcher.group(5));
			Iterator iterator = Page4.data.iterator();
			boolean flag = false;
			while (iterator.hasNext()) {
				Page4.MajorCode item = (Page4.MajorCode) iterator.next();
				if (item.getCode().equals(matcher.group(2))) {
					classes.setText(matcher.group(1) + "级" + item.getName() + matcher.group(3) + "班");
					flag = true;
					break;
				}
			}
			if (!flag) {
				classes.setText("转专业/重修");
			}
		} else {
			id.setText("");
			name.setText("未能识别");
			classes.setText("");
		}
	}

	/**
	 * 获取学生分数
	 */
	protected void getScore() {
		score.setText("");
		if (studentData == null) {
			return;
		}
		score.setText(String.valueOf((int) studentData.score));
	}

	/**
	 * 获取学生完成概况
	 */
	protected void getPieChart() {
		pieChartData.clear();
		if (studentData == null) {
			return;
		}
		pieChartData.addAll(new PieChart.Data("已完成", (int) studentData.score),
				new PieChart.Data("剩余", 100 - (int) studentData.score));
	}

	/**
	 * 获取学生完成情况条形图
	 */
	protected void getBarChart() {
		series.getData().clear();
		if (studentData == null) {
			return;
		}
		Iterator iterator1 = studentData.chapter.iterator();
		Iterator iterator2 = studentData.count.iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			series.getData().add(new XYChart.Data("第" + iterator1.next().toString() + "章",
					Integer.parseInt(iterator2.next().toString())));
		}
	}

	/**
	 * 获取学生最近提交记录
	 * 
	 * @param path
	 *            学生路径
	 */
	protected void getCommitRecord(String[] path) {
		List logHistory = new ArrayList(svnkit.getHistory(path));
		ListIterator log = logHistory.listIterator(logHistory.size());
		recordBox.getChildren().clear();
		int cnt = 0;
		while (log.hasPrevious() && cnt < 8) {
			cnt++;
			SVNLogEntry logEntry = (SVNLogEntry) log.previous();
			Label label1 = new Label(logEntry.getMessage());
			label1.setWrapText(true);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(logEntry.getDate());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String recordDate = simpleDateFormat.format(calendar.getTime());
			Label label2 = new Label("commit in " + recordDate);
			label2.setTextFill(Color.rgb(130, 130, 135));
			label2.setPrefWidth(220);
			label2.setAlignment(Pos.CENTER_RIGHT);
			VBox record = new VBox();
			Separator separator = new Separator(Orientation.HORIZONTAL);
			record.getChildren().addAll(label1, label2, separator);
			recordBox.getChildren().add(record);
		}
	}
}
