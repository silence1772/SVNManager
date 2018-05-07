package application;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 保存学生各章完成情况数据的类
 *
 * @author wuzewei
 */
public class Data {
	/** svn工具类 */
	private Svnkit svnkit;
	/** 所有学生数据列表 */
	public ArrayList<StudentData> allStudentData;
	/** 类包名称格式 */
	public static String packageNameFormat;
	/** 满分题目数量 */
	public static int fullMarkNumber;
	/** 总章节数量 */
	public static int totalChapter;

	/**
	 * 构造方法. 初始化svn工具类以及监听刷新数据标识
	 *
	 * @param svnkit
	 */
	Data(Svnkit svnkit) {
		this.svnkit = svnkit;
		Controller.freshData.addListener((ov, oldb, newb) -> {
			if (newb) {
				getAllStudentData();
				Controller.freshData.setValue(false);
			}
		});

	}

	/**
	 * 获取所有学生数据
	 */
	protected void getAllStudentData() {
		allStudentData = new ArrayList<>();

		String path = "";
		List entries = null;
		try {
			entries = new ArrayList(svnkit.getDir(path)); // 获取根目录下所有条目
		} catch (SVNException e) {
			e.printStackTrace();
		}
		// Collections.sort(entries);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) { // 如果条目为学生文件夹，则获取数据并添加进列表里
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			StudentData student = getStudentData(entry.getName());
			if (student != null) {
				allStudentData.add(student);
			}
		}
	}

	/**
	 * 获取单个学生数据
	 *
	 * @param path
	 *            该学生路径
	 * @return 单个学生数据
	 */
	protected StudentData getStudentData(String path) {
		StudentData chapterCount;

		// 判断该路径是否为学生文件夹（201xxxxxxxxxYYY）
		if (!path.matches("^(\\d{12})([\\u4e00-\\u9fa5]{2,6})$")) {
			return null;
		} else {
			chapterCount = new StudentData(path.substring(0, 12), path.substring(12));
		}
		// 拼接出作业存放真实路径
		String realPath = path + "/src/" + packageNameFormat.replace('.', '/') + "/";
		Collection entries = null;
		try {
			entries = svnkit.getDir(realPath);
		} catch (SVNException e) {
			System.err.println("路径不存在");
			return null;
		}
		Iterator iterator = entries.iterator();
		if (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next(); // 跳过学生姓名文件夹
			List chapters = null;
			try {
				chapters = new ArrayList(svnkit.getDir(realPath + entry.getName()));
			} catch (SVNException e) {
				e.printStackTrace();
			}
			// 遍历每一章
			Collections.sort(chapters);
			Iterator chapterIterator = chapters.iterator();
			while (chapterIterator.hasNext()) {
				SVNDirEntry chapter = (SVNDirEntry) chapterIterator.next();

				Pattern patternChapter = Pattern.compile("^chapter(\\d{1,2})$", Pattern.CASE_INSENSITIVE);
				Matcher matcherChapter = patternChapter.matcher(chapter.getName());
				// 遍历每一题
				if (matcherChapter.matches()) {
					Collection answers = null;
					try {
						answers = svnkit.getDir(realPath + entry.getName() + "/" + chapter.getName());
					} catch (SVNException e) {
						e.printStackTrace();
					}
					Iterator answerIterator = answers.iterator();
					Pattern pattern = Pattern.compile("^Exercise(\\d{2})([\\w\\W]*)", Pattern.CASE_INSENSITIVE);
					int cnt = 0;
					while (answerIterator.hasNext()) {
						SVNDirEntry answer = (SVNDirEntry) answerIterator.next();
						Matcher matcher = pattern.matcher(answer.getName());
						if (matcher.matches()) {
							cnt++;
						}
					}
					// 将得到的数据添加
					chapterCount.addChapterCount(Integer.parseInt(matcherChapter.group(1)), cnt);
				}
			}
		}
		chapterCount.computeScore();
		return chapterCount;
	}

	/**
	 * 学生各章情况数据类
	 */
	public class StudentData implements Comparable<StudentData> {
		/**
		 * 学号
		 */
		public String id;
		/**
		 * 姓名
		 */
		public String name;
		/**
		 * 目录里存在的章节列表（即有做的）
		 */
		public ArrayList chapter = new ArrayList();
		/**
		 * 对应章节列表里每一章的做题数量
		 */
		public ArrayList count = new ArrayList();
		/**
		 * 得分
		 */
		public double score = 0;

		/**
		 * 构造方法. 初始化学号和姓名
		 *
		 * @param id
		 *            学号
		 * @param name
		 *            姓名
		 */
		StudentData(String id, String name) {
			this.id = id;
			this.name = name;
		}

		/**
		 * 添加章节及对应的做题数量
		 *
		 * @param chapter
		 *            章节
		 * @param count
		 *            题目数量
		 */
		public void addChapterCount(int chapter, int count) {
			this.chapter.add(chapter);
			this.count.add(count);
		}

		/**
		 * 计算分数
		 */
		public void computeScore() {
			double cnt = 0;

			Iterator iterator2 = this.count.iterator();
			// 计算有效题目量
			while (iterator2.hasNext()) {
				int tmp = Integer.parseInt(iterator2.next().toString());
				cnt += tmp > fullMarkNumber ? fullMarkNumber : tmp;
			}
			this.score = cnt / (fullMarkNumber * totalChapter) * 100;
		}

		/**
		 * 实现排序接口
		 *
		 * @param o
		 * @return
		 */
		@Override
		public int compareTo(StudentData o) {
			if ((int) this.score > (int) o.score) {
				return 1;
			} else if ((int) this.score < (int) o.score) {
				return -1;
			} else {
				if (this.name.compareTo(o.name) > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
}
