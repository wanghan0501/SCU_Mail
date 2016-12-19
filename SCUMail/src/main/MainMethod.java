package main;

import javax.swing.UIManager;

import frame.LoginFrame;

/**
 * 主函数
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class MainMethod {
	public static void main(String[] args) {
		// 设置界面为本地模式
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LoginFrame().setVisible(true);
			}
		});

	}

}
