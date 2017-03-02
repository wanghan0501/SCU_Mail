package mailutil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 附件的操作类
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class AttachFile {

	public AttachFile() {
	}

	// 邮件附件下载路径的选择
	public void choicePath(final String filename, final InputStream in) {
		File f = new File(".");// 得到当前user工作目录
		JFileChooser chooser = new JFileChooser(f);// 构造一个当前路径的文件选择器
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {// 如果选择确定键
			final File f1 = chooser.getSelectedFile();// 得到选择的文件
			new Thread() {// 开启新线程下载附件
				public void run() {
					downloadFile(f1.getPath() + "/" + filename, in);// 下载附件
				}
			}.start();
		}
	}

	// 邮件附件下载确定
	public boolean isDownload(String filename) {
		boolean download = false;
		int n = JOptionPane.showConfirmDialog(null, "该邮件具有附件  \"" + filename
				+ "\" 是否下载？", "询问", JOptionPane.YES_NO_OPTION);
		if (n == 0)
			download = true;
		return download;
	}

	// 邮件附件的下载
	public void downloadFile(String filename, InputStream in) {// filename
																// 文件名，in 输入流
		FileOutputStream out = null;// 输出流对象
		try {
			out = new FileOutputStream(new File(filename));
			byte[] content = new byte[1024];
			int read = 0;
			while ((read = in.read(content)) != -1) {
				out.write(content);
			}
			JOptionPane.showMessageDialog(null, "附件   \"" + filename
					+ "\"下载成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			System.out.println("GetMail类中downloadFile方法  下载文件错误！");
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();// 关闭输出流
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();// 关闭输入流
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
