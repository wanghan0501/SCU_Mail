package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;

import utils.EditorUtils;
import utils.MailTableModel;
import utils.ReceiveMailTable;

/**
 * 接受邮件界面、回收站界面、已发送界面的父类
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class BaseReceiceFrame extends JInternalFrame implements MouseListener,
		ActionListener {
	public JTable table;
	private JScrollPane scrollPane_1;
	private JPanel panel;
	public JTextPane mailContent;// 邮件内容的显示
	public MailTableModel tableModel = null;;
	public ReceiveMailTable mail2Table = null;
	public JMenuItem itemPopupOne = null;
	public JMenuItem itemPopupTwo = null;
	public JMenuItem itemPopupThree = null;
	private String popupOne = "第一项", popupTwo = "第二项", popupThree = "第三项";
	private String icon1 = null, icon2 = null, icon3 = null;
	private JProgressBarFrame progressBar = null;// 进度条实例
	private boolean isThree = false;

	// 设置右键第一项名称
	public void setPopupOne(String popupOne, String name) {
		icon1 = name;
		this.popupOne = popupOne;
	}

	// 设置右键第二项名称
	public void setPopupTwo(String popupTwo, String name) {
		icon2 = name;
		this.popupTwo = popupTwo;
	}

	// 设置右键第三项名称
	public void setPopupThree(String popupThree, String name, boolean isThree) {
		icon3 = name;
		this.isThree = isThree;
		this.popupThree = popupThree;
	}

	public BaseReceiceFrame(String title) {
		this.setLayout(new BorderLayout());
		setTitle(title);
		setClosable(true);// 设置是否可以通过某个用户操作关闭此JInternalFrame
		setIconifiable(true);
		setMaximizable(true);// 窗口最大化设置
		setResizable(true);// 设置窗口课以调整大小
		setBounds(10, 10, 640, 600);// 设置界面的大小
		final JScrollPane scrollPane = new JScrollPane();// 创建一个空的（无视口的视图）JScrollPane，
		scrollPane.setPreferredSize(new Dimension(520, 155));
		table = new JTable();
		table.addMouseListener(this);
		scrollPane.setViewportView(table);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(520, 200));
		// getContentPane().add(panel, BorderLayout.CENTER);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setPreferredSize(new Dimension(520, 190));
		mailContent = new JTextPane();// 可以用以图形方式表示的属性来标记的文本组件
		HTMLEditorKit kit = new HTMLEditorKit();// 新建HTMLEditorKit
		mailContent.setEditorKit(kit);// 设置EditorKit为HTMLEditorKit
		mailContent.setContentType("text/html");
		scrollPane_1.setViewportView(mailContent);
		panel.add(scrollPane_1, BorderLayout.CENTER);

		// 添加一个分割窗口
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scrollPane, panel);
		splitPane.setOneTouchExpandable(true);// 在分隔条上提供一个 UI 小部件来快速展开/折叠分隔条
		splitPane.setDividerSize(10);// 设置分隔条的大小。
		getContentPane().add(splitPane, BorderLayout.CENTER);
		setVisible(true);
		// 添加超连接支持
		mailContent.setEditable(false);
		mailContent// HyperlinkListener 超链接监听器
				.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
					public void hyperlinkUpdate(
							javax.swing.event.HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							try {
								String command = "cmd /c start "
										+ e.getDescription();
								Runtime.getRuntime().exec(command);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				});

	}

	public void mouseClicked(MouseEvent e) {
		int selectRom = table.getSelectedRow();
		if (e.getClickCount() == 2) {// 双击时显示邮件
			if (selectRom != -1) {// 如果选择一行
				doubleClick(selectRom);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (selectRom >= 0 && selectRom < tableModel.getRowCount()) {// 判断选中行是否有效
				JPopupMenu popup = new JPopupMenu();
				itemPopupOne = new JMenuItem(popupOne);
				itemPopupOne.addActionListener(this);
				itemPopupOne.setIcon(EditorUtils.createIcon(icon1));

				itemPopupTwo = new JMenuItem(popupTwo);
				itemPopupTwo.addActionListener(this);
				itemPopupTwo.setIcon(EditorUtils.createIcon(icon2));

				popup.add(itemPopupOne);
				popup.add(itemPopupTwo);
				if (isThree) {// 是否有第三个选项
					itemPopupThree = new JMenuItem(popupThree);
					itemPopupThree.setIcon(EditorUtils.createIcon(icon3));
					itemPopupThree.addActionListener(this);
					popup.addSeparator();// 添加分割符
					popup.add(itemPopupThree);// 将第三项添加到菜单中
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * 双击事件的响应
	 * 
	 * @param selectRom
	 *            选择的行号
	 */
	public void doubleClick(int selectRom) {
	}

	// 删除邮件的事件处理
	@Override
	public void actionPerformed(ActionEvent e) {
		actionEvent(e);
	}

	/**
	 * 动作事件的处理
	 * 
	 * @param e
	 */
	public void actionEvent(ActionEvent e) {
		final int[] selectRoms = table.getSelectedRows();// 选中多行
		if (e.getSource() == this.itemPopupOne) {// 删除邮件
			delete(selectRoms, 1);
		} else if (e.getSource() == this.itemPopupTwo) {// 彻底删除邮件
			new Thread() {// 开启新的线程删除邮件
				public void run() {
					popupTwoisSelected(selectRoms, ReceiveMailTable.list);
				}
			}.start();
		} else if (e.getSource() == this.itemPopupThree) {// 第三项菜单
			popupThreeisSelected();
		}
	}

	// 右键第三个选项被选中
	private void popupThreeisSelected() {
		ReceiveMailTable.getMail2Table().startReceiveMail();// 右键刷新收件列表
	}

	/**
	 * 右键第二个选项被选中（ 彻底删除邮件）
	 * 
	 * @param selectRoms
	 */
	public void popupTwoisSelected(int[] selectRoms, List list) {
		String message = "";
		progressBar = new JProgressBarFrame(MainFrame.MAINFRAME, "删除邮件",
				"正在删除邮件...");
		progressBar.setVisible(true);
		int i = 0;
		for (i = 0; i < selectRoms.length// 循环删除每行
				&& selectRoms[i] < tableModel.getRowCount(); i++)
			;// 并判断每行的有效性
		if (ReceiveMailTable.deleteMailForever(selectRoms, i, list)) {// 彻底删除邮件
			delete(selectRoms, 2);
			message = "删除成功！";
		} else {
			message = "删除失败！";
		}
		progressBar.dispose();
		JOptionPane.showMessageDialog(this, message, "提示",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 有子类完成具体的删除功能
	public void delete(int[] selectRoms, int mailState) {// mailState 判断邮件的删除状态

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
