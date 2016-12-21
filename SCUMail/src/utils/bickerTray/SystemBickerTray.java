package utils.bickerTray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import mailutil.CheckNewMialUtil;
import utils.EditorUtils;
import utils.ReceiveMailTable;

/**
 * 类说明：系统闪动托盘类
 */
public class SystemBickerTray extends Thread {
	private SystemTray sysTray = null;// 当前操作系统的托盘对象
	private TrayIcon trayIcon;// 当前对象的托盘
	private ImageIcon icon = null;// 托盘图标
	private ImageIcon nullIcon = null;// 空的图盘图标
	private boolean flag = false; // 是否有新邮件
	private PopupMenu popupMenu = null;// 弹出菜单
	private Image nullimage = null;
	private Image iconImage = null;

	public SystemBickerTray() {
		sysTray = SystemTray.getSystemTray();// 获得当前操作系统的托盘对象
		icon = EditorUtils.createIcon("e.png");// 托盘图标
		nullIcon = new ImageIcon("");// 空图片对象
		nullimage = nullIcon.getImage();
		iconImage = icon.getImage();
		createPopupMenu();
		trayIcon = new TrayIcon(icon.getImage(), "SCU邮件客户端", popupMenu);
		trayIcon.addMouseListener(new MouseAdapter() {// 系统托盘添加鼠标事件
			public void mouseClicked(MouseEvent e) {
				mouseAction(e);// 鼠标事件的处理
			}
		});
		addTrayIcon();// 将托盘添加到操作系统的托盘
	}

	// 设置新邮件总数
	public void setCount(int count) {
		trayIcon.setToolTip("你有 " + count + " 封新邮件，请查收！");
	}

	/**
	 * 添加托盘的方法
	 */
	public void addTrayIcon() {
		try {
			sysTray.add(trayIcon);// 将托盘添加到操作系统的托盘
			this.start();// 开启线程发出提示新和图片闪动
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 创建弹出菜单
	 */
	private void createPopupMenu() {
		popupMenu = new PopupMenu();// 弹出菜单
		MenuItem refresh = new MenuItem("刷新收件箱");
		MenuItem cancle = new MenuItem("取消闪烁");
		MenuItem exit = new MenuItem("退出系统");
		popupMenu.add(refresh);
		popupMenu.add(cancle);
		popupMenu.add(exit);
		refresh.addActionListener(new ActionListener() {// 刷新收件箱
			public void actionPerformed(ActionEvent e) {
				refreshInbox(e);// 刷新收件箱
			}
		});
		cancle.addActionListener(new ActionListener() {// 取消闪烁
			public void actionPerformed(ActionEvent e) {
				cancleBicker(e);// 取消闪烁
			}
		});
		exit.addActionListener(new ActionListener() {// 退出程序
			public void actionPerformed(ActionEvent e) {
				System.exit(0);// 退出系统
			}
		});
	}

	// 运行线程
	public void run() {
		while (true) {
			if (flag) { // 有邮件
				try {
					trayIcon.setImage(nullimage);
					sleep(500);
					// 闪动消息的空白时间
					trayIcon.setImage(iconImage);
					sleep(500);
					// 闪动消息的提示图片
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (trayIcon.getImage().equals(nullimage))
					trayIcon.setImage(iconImage);
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// 刷新邮箱事件处理
	private void refreshInbox(ActionEvent e) {
		ReceiveMailTable.getMail2Table().startReceiveMail();// 右键刷新收件列表
		setCount(0);// 设置显示新邮件个数
		CheckNewMialUtil.isCheck = true;// 设置新邮件标记
		flag = false;
	}

	// 取消 闪烁事件处理
	private void cancleBicker(ActionEvent e) {
		flag = false;
		CheckNewMialUtil.isCheck = true;// 设置新邮件标记
	}

	// 鼠标事件的处理
	private void mouseAction(MouseEvent e) {
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		if (flag)// 如果有新邮件 播放声音
			new MyAudioPlayer().play();
		this.flag = flag;
	}

	public static void main(String[] args) throws InterruptedException {
		SystemBickerTray bickerTray = new SystemBickerTray();
		for (int i = 0; i < 10; i++) {
			bickerTray.setFlag(true);
			Thread.sleep(3000);

		}
	}
}
