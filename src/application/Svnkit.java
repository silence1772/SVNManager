package application;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * svn工具类. 负责连接svn资源库并获取相关数据.
 *
 * @author wuzewei
 */
public class Svnkit {
	/**
	 * svn资源库
	 */
	private static SVNRepository repository = null;

	/**
	 * 连接到svn资源库
	 *
	 * @param url
	 *            资源库的地址
	 * @param name
	 *            用户名
	 * @param password
	 *            密码
	 * @return 连接的状态
	 */
	public int connectSVN(String url, String name, String password) {
		DAVRepositoryFactory.setup(); // 设置资源库工厂为http协议
		SVNRepository testRepository;
		try {
			testRepository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url)); // 创建资源库
		} catch (SVNException e) {
			System.err.println("请输入正确的svn资源库地址");
			return 1;
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password); // 连接资源库
		testRepository.setAuthenticationManager(authManager);
		// 检查路径是否存在
		try {
			SVNNodeKind nodeKind = testRepository.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("该路径不存在");
				return 2;
			} else if (nodeKind == SVNNodeKind.FILE) {
				System.err.println("该路径不是目录");
				return 3;
			}
		} catch (SVNException e) {
			System.err.println("资源库位置错误或用户名密码错误");
			return 4;
		}
		System.out.println("连接成功");
		this.repository = testRepository;
		return 0;
	}

	/**
	 * 获取指定路径下的文件目录
	 *
	 * @param path
	 *            所指定的路径
	 * @return 目录文件名列表
	 * @throws SVNException
	 *             svn异常
	 */
	public Collection getDir(String path) throws SVNException {
		Collection entries = null;
		entries = repository.getDir(path, -1, null, (Collection) null);
		return entries;
	}

	/**
	 * 获取文件内容
	 *
	 * @param filePath
	 *            文件路径
	 * @return 文件内容
	 */
	public String getContent(String filePath) {
		// 此变量用来存放要查看的文件的属性名/属性值列表。
		SVNProperties fileProperties = new SVNProperties();
		// 此输出流用来存放要查看的文件的内容。
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			// 获得版本库中文件的类型状态（是否存在、是目录还是文件），参数-1表示是版本库中的最新版本。
			SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("要查看的文件不存在.");
				return "";
			} else if (nodeKind == SVNNodeKind.DIR) {
				System.err.println("要查看对应版本的条目是一个目录.");
				return "";
			}
			// 获取要查看文件的内容和属性，结果保存在baos和fileProperties变量中。
			repository.getFile(filePath, -1, fileProperties, baos);
		} catch (SVNException svne) {
			System.err.println("在获取文件内容和属性时发生错误: " + svne.getMessage());
			return "";
		}

		// 获取文件的mime-type
		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
		// 判断此文件是否是文本文件
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);

		// 如果文件是文本类型，则把文件的内容返回。
		if (isTextType) {
			return baos.toString();
		} else {
			System.err.println("因为文件不是文本文件，无法显示！");
		}

		return "因为文件不是文本文件，无法显示！";
	}

	/**
	 * 获取提交历史
	 *
	 * @param path
	 *            目录路径
	 * @return 该目录下的提交历史
	 */
	public Collection getHistory(String[] path) {
		long startRevision = 0;
		long endRevision = -1; // HEAD (the latest) revision
		Collection logEntries = null;

		try {
			logEntries = repository.log(path, null, startRevision, endRevision, true, true);
			return logEntries;
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}
}
