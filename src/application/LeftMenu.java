package application;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * 左边菜单及逻辑类
 *
 * @author wuzewei
 */
public class LeftMenu extends Group {

	/**
	 * 构造方法. 生成菜单及其控制逻辑
	 */
	LeftMenu() {
		VBox vBox = new VBox();
		Button button1 = new Button(); // 查看文件页面按钮
		Button button2 = new Button(); // 个人报告页面按钮
		Button button3 = new Button(); // 成绩分析页面按钮
		Button button4 = new Button(); // 设置页面按钮
		StackPane stackPane = new StackPane();

		// 设置按钮样式及点击事件
		button1.getStyleClass().addAll("menu-btn", "icon1");
		button1.setOnAction(event -> Controller.router.setValue(1));
		button2.getStyleClass().addAll("menu-btn", "icon2");
		button2.setOnAction(event -> Controller.router.setValue(2));
		button3.getStyleClass().addAll("menu-btn", "icon3");
		button3.setOnAction(event -> Controller.router.setValue(3));
		button4.getStyleClass().addAll("menu-btn", "icon4");
		button4.setOnAction(event -> Controller.router.setValue(4));

		// 设置页面按钮单独置于一个面板底部
		stackPane.getChildren().add(button4);
		stackPane.setAlignment(Pos.BOTTOM_CENTER);

		// 将元素置于垂直盒子并添加进此类的节点中
		vBox.getStyleClass().addAll("left-menu");
		vBox.getChildren().addAll(button1, button2, button3, stackPane);
		vBox.setVgrow(stackPane, Priority.ALWAYS);
		this.getChildren().add(vBox);
	}
}
