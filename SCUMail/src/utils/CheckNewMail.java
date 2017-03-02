package utils;

import mailutil.CheckNewMialUtil;
import utils.bickerTray.SystemBickerTray;

/**
 * 类说明：检测新邮件类
 * @author caesar
 */
public class CheckNewMail extends Thread {
	private static int MailCount = 0;// 新邮件总数计数器
	private SystemBickerTray bickerTray = null;// 系统托盘图标
	private int num = 0;// 得到邮箱中新邮件的个数
	private CheckNewMialUtil check = null;

	public CheckNewMail() {
		bickerTray = new SystemBickerTray();// 系统托盘图标
		check = new CheckNewMialUtil();
	}

	public void run() {
		while (true) {
			try {
				num = check.checkNewMail();
				if (num > MailCount) {
					if (!bickerTray.isFlag()) {
						bickerTray.setFlag(true);
					}
					bickerTray.setCount(num - MailCount);// 设置显示新邮件个数
				}
				sleep(2500);// 暂停3秒
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 设置显示新邮件个数
	public static void setNewMailCount(int count) {
		MailCount = count;
	}
}
