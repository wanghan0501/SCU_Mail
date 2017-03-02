package frame;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * 进度条界面类 包括有、无模式两种进度条
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class JProgressBarFrame extends JDialog {
	private JLabel progressBarLabel = null;// 进度条上方的提示标签
	private JProgressBar progressBar = null;// 进度条对象

	public JProgressBarFrame() {
	}

	/**
	 * 进度条构造方法
	 * 
	 * @param title
	 *            进度条标题
	 * @param state
	 *            进度条状态 ，1 ：有模式的进度条，2：无模式的进度条
	 */
	public JProgressBarFrame(JFrame frame, String title, String message) {
		super(frame, title);
		init(frame, title, message);
	}

	// 界面的初始化
	private void initFrame() {
		this.setAlwaysOnTop(true);
		Toolkit tk = getToolkit();// 获得屏幕的宽和高
		Dimension dim = tk.getScreenSize();
		this.setResizable(false);// 设置窗口不可调整大小
		this.setBounds(dim.width / 2 - 110, dim.height / 2 - 40, 211, 90);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		validate();
	}

	/**
	 * @param message
	 *            进度条要显示的消息
	 * @param state
	 *            进度条的模式
	 */
	private void init(JFrame frame, String title, String message) {
		this.setLayout(null);// 设置布局为流布局

		progressBarLabel = new JLabel();
		progressBarLabel.setText(message);
		progressBarLabel.setBounds(2, 2, 200, 25);
		this.add(progressBarLabel);

		progressBar = new JProgressBar();
		progressBar.setBounds(2, 30, 200, 25);
		progressBar.setIndeterminate(true);
		this.add(progressBar);
		initFrame();
	}

}