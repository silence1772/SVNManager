package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * 控制程序的元素以及主要运行逻辑
 * 
 * @author wuzewei
 */
public class Controller extends Group {
	/** 背景矩形 */
	private Rectangle rectangle = new Rectangle(980, 660, Color.rgb(240, 240, 240));
	/** 左边的菜单 */
	private LeftMenu leftMenu = new LeftMenu();
	/** 连接svn资源库的工具类 */
	private Svnkit svnkit = new Svnkit();
	/** 保存相关成绩数据的类 */
	private Data data = new Data(svnkit);
	/** 查看文件页面 */
	private Page1 page1 = new Page1(svnkit);
	/** 个人报告页面 */
	private Page2 page2 = new Page2(svnkit, data);
	/** 成绩分析页面 */
	private Page3 page3 = new Page3(data);
	/** 设置页面 */
	private Page4 page = new Page4(svnkit);

	/** 当前页编号 */
	public static IntegerProperty router = new SimpleIntegerProperty(1);
	/** 更新数据标识 */
	public static BooleanProperty freshData = new SimpleBooleanProperty(false);
	/** 更新查看文件页面标识 */
	public static BooleanProperty freshPage1 = new SimpleBooleanProperty(false);
	/** 更新个人报告页面标识 */
	public static BooleanProperty freshPage2 = new SimpleBooleanProperty(false);
	/** 更新成绩分析页面标识 */
	public static BooleanProperty freshPage3 = new SimpleBooleanProperty(false);

	/**
	 * 构造方法. 初始化各节点的位置以及切换页面的逻辑
	 */
	Controller() {
		leftMenu.setLayoutX(0);
		leftMenu.setLayoutY(0);

		page1.setLayoutX(50);
		page2.setLayoutX(50);
		page3.setLayoutX(50);
		page.setLayoutX(50);

		this.getChildren().addAll(rectangle, leftMenu, page1);

		/* 监听router变化，以切换页面 */
		router.addListener((ov, oldb, newb) -> {
			if (newb.intValue() == 1) {
				this.getChildren().remove(2); // 页面的索引始终为2，即第3个
				this.getChildren().add(page1);
			} else if (newb.intValue() == 2) {
				this.getChildren().remove(2);
				this.getChildren().add(page2);
			} else if (newb.intValue() == 3) {
				this.getChildren().remove(2);
				this.getChildren().add(page3);
			} else if (newb.intValue() == 4) {
				this.getChildren().remove(2);
				this.getChildren().add(page);
			}
		});
	}
}
