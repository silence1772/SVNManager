package application;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查看文件页面
 * 
 * @author wuzewei
 */
public class Page1 extends Group {
	/** 目录树 */
	private TreeView treeView;
	/** 文件名 */
	private Text text_filename;
	/** 文件行数 */
	private Text text_fileline;
	/** 文件大小 */
	private Text text_filesize;
	/** svn工具类 */
	private Svnkit svnkit;

	private int cnt = 0;
	private VBox vBox;
	private Text text_content;
	private ScrollPane scrollPane;

	/**
	 * 构造方法. 初始化svn工具类以及界面，设置监听刷新页面标识
	 * 
	 * @param svnkit
	 *            svn工具类
	 */
	Page1(Svnkit svnkit) {
		this.svnkit = svnkit;
		drawHint();
		Controller.freshPage1.addListener((ov, oldb, newb) -> {
			if (newb) {
				this.getChildren().clear();
				drawStaticUI();
				setRouter();
				Controller.freshPage1.setValue(false);
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
	 * 绘制界面
	 */
	protected void drawStaticUI() {
		// 标题
		Rectangle rectange_title = new Rectangle(248, 50, Color.rgb(255, 255, 255));
		Text text_title = new Text("查看文件");
		text_title.setFont(Font.loadFont(Page1.class.getResource("/resources/msyh.ttf").toExternalForm(), 24));
		text_title.setFill(Color.rgb(70, 186, 121));
		text_title.setX(26);
		text_title.setY(34);
		Rectangle rectangle_filetitle = new Rectangle(680, 50, Color.rgb(255, 255, 255));
		rectangle_filetitle.setX(248);

		// 分隔线
		Separator separator = new Separator(Orientation.VERTICAL);
		separator.setPrefHeight(20);
		separator.setLayoutX(820);
		separator.setLayoutY(16);

		Separator separator1 = new Separator(Orientation.HORIZONTAL);
		separator1.setPrefWidth(680);
		separator1.setLayoutX(248);
		separator1.setLayoutY(50);

		// 文件名
		text_filename = new Text();
		// text_filename.setFill(Color.rgb(77, 88, 99));
		text_filename.setLayoutX(280);
		text_filename.setLayoutY(30);
		text_filename.setFont(Font.font(16));

		// 目录树
		TreeItem<String> rootItem = new TreeItem<>("WorkSpace");
		rootItem.setExpanded(true);
		displayDir(rootItem, "");
		treeView = new TreeView();
		treeView.setRoot(rootItem);
		treeView.setLayoutY(50);
		treeView.setPrefHeight(610);

		// 文件内容
		text_content = new Text();
		text_content.setLineSpacing(2);
		text_content.setX(46);
		text_content.setY(22);
		// text_content.setFill(Color.rgb(106, 115, 125));

		// 文件行数
		text_fileline = new Text();
		text_fileline.setFill(Color.rgb(106, 115, 125));
		text_fileline.setLayoutX(750);
		text_fileline.setLayoutY(30);

		// 文件大小
		text_filesize = new Text();
		text_filesize.setFill(Color.rgb(106, 115, 125));
		text_filesize.setLayoutX(840);
		text_filesize.setLayoutY(30);

		vBox = new VBox(2);
		vBox.setPrefWidth(40);
		vBox.setPrefHeight(610);
		vBox.setAlignment(Pos.TOP_RIGHT);
		vBox.setPadding(new Insets(12, 0, 0, 0));

		Group group = new Group(vBox, text_content);
		scrollPane = new ScrollPane();
		scrollPane.setContent(group);
		scrollPane.setPrefWidth(680);
		scrollPane.setPrefHeight(610);
		scrollPane.setLayoutX(248);
		scrollPane.setLayoutY(50);

		this.getChildren().addAll(rectange_title, text_title, treeView, rectangle_filetitle, text_filename,
				text_fileline, text_filesize, scrollPane, separator, separator1);
	}

	/**
	 * 设置路由. 对目录树添加监听，当点击条目时，如果是文件则打开该文件
	 */
	protected void setRouter() {
		treeView.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<TreeItem>) (observable, oldValue, newValue) -> {
					if (newValue.isLeaf()) {
						String fileContent = svnkit.getContent(getFullPath(newValue));
						text_content.setText(fileContent);
						// 清空内容
						vBox.getChildren().clear();
						// 统计行数
						String regEx = "\n"; // 要匹配的子串，可以用正则表达式
						Pattern p = Pattern.compile(regEx);
						Matcher m = p.matcher(fileContent);
						cnt = 0;
						while (m.find()) {
							cnt++;
						}
						// 更新信息
						text_filename.setText(getProjectName(newValue) + "/" + newValue.getValue().toString());
						text_fileline.setText(String.valueOf(cnt) + " lines");
						text_filesize.setText(getNetFileSizeDescription(fileContent.getBytes().length));
						// 设置行号
						for (int i = 1; i <= cnt; i++) {
							Text text = new Text(String.valueOf(i));
							text.setFill(Color.rgb(27, 31, 35, 0.3));
							vBox.getChildren().add(text);
						}
					}
				});
	}

	/**
	 * 生成目录树. 递归方法生成目录树
	 * 
	 * @param treeItem
	 *            目录树根元素
	 * @param path
	 *            根路径
	 */
	protected void displayDir(TreeItem treeItem, String path) {
		List entries = null;
		try {
			entries = new ArrayList(svnkit.getDir(path));
		} catch (SVNException e) {
			e.printStackTrace();
		}
		Collections.sort(entries);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			TreeItem<String> item = new TreeItem<>(entry.getName());
			// item.setExpanded(true); //设置是否展开
			treeItem.getChildren().add(item);
			if (entry.getKind() == SVNNodeKind.DIR) {
				displayDir(item, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			}
		}
	}

	/**
	 * 递归获取当前选择项的完整路径
	 * 
	 * @param treeItem
	 *            当前选择的条目
	 * @return 完整路径
	 */
	protected String getFullPath(TreeItem treeItem) {
		if (treeItem.getParent() == null) {
			return "";
		} else {
			return getFullPath(treeItem.getParent()) + "/" + treeItem.getValue().toString();
		}
	}

	/**
	 * 递归获取当前选择项的根文件夹名称
	 * 
	 * @param treeItem
	 *            当前选择的条目
	 * @return 根文件夹名称
	 */
	protected String getProjectName(TreeItem treeItem) {
		if (treeItem.getParent().getParent() == null) {
			return treeItem.getValue().toString();
		} else {
			return getProjectName(treeItem.getParent());
		}
	}

	/**
	 * 计算文件大小. 将字节单位换算为合适的单位
	 * 
	 * @param size
	 *            字节大小
	 * @return 合适的大小表示
	 */
	protected String getNetFileSizeDescription(long size) {
		StringBuffer bytes = new StringBuffer();
		DecimalFormat format = new DecimalFormat("###.0");
		if (size >= 1024 * 1024 * 1024) {
			double i = (size / (1024.0 * 1024.0 * 1024.0));
			bytes.append(format.format(i)).append(" GB");
		} else if (size >= 1024 * 1024) {
			double i = (size / (1024.0 * 1024.0));
			bytes.append(format.format(i)).append(" MB");
		} else if (size >= 1024) {
			double i = (size / (1024.0));
			bytes.append(format.format(i)).append(" KB");
		} else if (size < 1024) {
			if (size <= 0) {
				bytes.append("0 B");
			} else {
				bytes.append((int) size).append(" B");
			}
		}
		return bytes.toString();
	}

}
