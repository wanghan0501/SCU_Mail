package frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import utils.EditorUtils;
import utils.SendedMailTable;
import mailutil.SendAttachMail;
import frame.JProgressBarFrame;
/**
 * 发送群邮件界面
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class SendGroupMailFrame extends JInternalFrame implements MouseListener, ActionListener {
	private JPanel upperPanel = null;
	private JButton linkmanInfoButton = null;
	private JTable table = null;
	private JButton sendGroupButton= null;
	private JButton resetButton = null;
	private JProgressBarFrame progressBar = null;
	// 获得发件实例
	private SendAttachMail groupMails = SendAttachMail.getSendMailInstantiate();
	private int sendedEmailCount =0 ;
	private int totalEmailCount = 0;
	
	
	// 储存联系人信息
	Vector<Vector<String>> linkmanInfo = new Vector<Vector<String>>();
	
			
	public SendGroupMailFrame() {
		super("群邮件");
		// 设置对话框图标
		this.setFrameIcon(EditorUtils.createIcon("newGroup.jpg"));
		this.setClosable(true);
		this.setMaximizable(true);// 窗口最大化设置
		this.setIconifiable(true);
		this.setBounds(10, 10, 640, 600);// 设置界面的大小
		this.getContentPane().setLayout(new BorderLayout());
		this.setVisible(true);

		// 顶部面板
		upperPanel = new JPanel();
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BorderLayout(0, 0));
		// 功能条
		final JToolBar toolBar = new JToolBar();
		toolBar.setEnabled(false);
		upperPanel.add(toolBar);
		// 通讯录功能按键
		linkmanInfoButton = new JButton("通讯录", EditorUtils.createIcon("addressBook.png"));
		linkmanInfoButton.addActionListener(this);
		toolBar.add(linkmanInfoButton, JPanel.LEFT_ALIGNMENT);
		// 群发功能按键
		sendGroupButton = new JButton("群发", EditorUtils.createIcon("sendGroup.png"));
		sendGroupButton.addActionListener(this);
		toolBar.add(sendGroupButton, JPanel.LEFT_ALIGNMENT);
		// 重置功能按键
		resetButton = new JButton("重置", EditorUtils.createIcon("reset.png"));
		resetButton.addActionListener(this);
		toolBar.add(resetButton, JPanel.LEFT_ALIGNMENT);
		
		// 滚动面板
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		// 通讯录表
		table = new JTable();
		table.setFillsViewportHeight(true);
		table.setEnabled(false);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "姓名", "联系地址", "邮件主题", "邮件正文" }));
		table.setRowHeight(25);
		scrollPane.setViewportView(table);
	}

	// 监听事件处理
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == linkmanInfoButton) {
			openLinkmanInfoEvent();
		} else if (e.getSource() == sendGroupButton) {
			if (!isEmpty())
				sendGroupMail(linkmanInfo);
			else
				JOptionPane.showMessageDialog(SendGroupMailFrame.this, "通讯录为空，请载入"
						+ "通讯录后再次尝试群发邮件！", "警告",
						JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == resetButton) {
			resetButtonEvent();
		}
	}

	public boolean isEmpty(){
		// TODO Auto-generated method stub
		return linkmanInfo.size()==0;
	}
	
	public void resetButtonEvent() {
		// TODO Auto-generated method stub
		linkmanInfo.clear();
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "姓名", "联系地址", "邮件主题", "邮件正文" }));
	}

	public void openLinkmanInfoEvent() {
		// 构造一个当前路径的文件选择器
		JFileChooser chooser = new JFileChooser(new File("."));
		chooser.setOpaque(false);
		// 只搜索文件
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setAcceptAllFileFilterUsed(false);

		// xls文本过滤器
		chooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".xls"))
					// TODO Auto-generated method stub
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "xls文件(*.xls)";
			}

		});

		// 允许所有文件
		chooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "所有文件(*.*)";
			}

		});

		if (chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {// 如果选择确定键
			File file = chooser.getSelectedFile();
			Icon icon = chooser.getIcon(file);
			if (file.isFile()) {
				System.out.println("文件:" + file.getAbsolutePath());
				dispLinkmanInfo(file);
			}
			System.out.println(file.getName());
			// 发送群邮件
			validate();
			repaint();
		}
	}

	public void dispLinkmanInfo(File file) {
		// 储存联系人
		Vector<String> linkman = null;
		// 表格数据模型
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		try {
			FileInputStream fis = new FileInputStream(file);
			jxl.Workbook rwb = Workbook.getWorkbook(fis);
			Sheet[] sheet = rwb.getSheets();
			for (int i = 0; i < sheet.length; i++) {
				Sheet rs = rwb.getSheet(i);
				for (int j = 1; j < rs.getRows(); ++j) {
					linkman = new Vector<String>();
					for (int k = 0; k < rs.getColumns(); ++k) {
						Cell cell = rs.getCell(k, j);
						linkman.add(cell.getContents());
					}
					linkmanInfo.add(linkman);
					// 数据模型更新
					model.addRow(linkman);
					// 表格更新
					table.updateUI();
				}
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMail(final String toMan, final String subject, final ArrayList<String> list, final String text,
			final String copy, final String sendMan) {
		groupMails.setContent(text);// 设置邮件正文
		groupMails.setFilename(list);// 设置邮件附件名称
		groupMails.setFrom(sendMan);// 设置发件人
		groupMails.setSubject(subject);// 设置邮件主题
		groupMails.setTo(toMan);// 设置收件人
		groupMails.setCopy_to(copy);// 设置抄送人

		if (progressBar == null) {
			progressBar = new JProgressBarFrame(MainFrame.MAINFRAME, "发送群邮件",
					"正在发送群邮件, 请稍后...");

		}
		progressBar.setVisible(true);
		new Thread() {// 开启一个新的线程发送邮件
			public void run() {
				String message = "";
				if ("".equals(message = groupMails.send())) {
					SendedMailTable.getSendedMailTable().setValues(toMan,
							subject, list, text, copy, sendMan);// 将邮件添加到已发送
					//message = "邮件已发送成功！";
				} else {
					message = "<html><h4>邮件发送失败！ 失败原因：</h4></html>\n" + message;
					JOptionPane.showMessageDialog(SendGroupMailFrame.this, message, "提示",
							JOptionPane.INFORMATION_MESSAGE);
				}
				progressBar.dispose();
			}
		}.start();

		if ((getSendedEmailCount() == getTotalEmailCount())
				&& (getTotalEmailCount()!=0)) {
			JOptionPane.showMessageDialog(SendGroupMailFrame.this, "群邮件发送已完成!", "提示",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	

	public int getSendedEmailCount() {
		return this.sendedEmailCount;
	}

	public int getTotalEmailCount() {
		return this.totalEmailCount;
	}

	public void sendGroupMail(Vector<Vector<String>> linkmanInfo) {
		// 初始化群邮件发送状态
		sendedEmailCount = 0;
		totalEmailCount = linkmanInfo.size();
		// 待发送群邮件总数
		String toMan = ""; // 收件人
		String subject = ""; // 主题
		ArrayList<String> list = new ArrayList<String>(); // 附件列表
		String content = ""; // 邮件正文内容
		String copy_to = ""; // 抄送人
		String sendMan = groupMails.getUser(); // 发件人

		try {
			// System.out.println(linkmanInfo.toString());
			for (int i = 0; i < linkmanInfo.size(); i++) {
				// 设置邮件主题
				subject = linkmanInfo.get(i).get(2);
				// 设置邮件收件人
				toMan = linkmanInfo.get(i).get(1);
				// 设置邮件内容
				content = linkmanInfo.get(i).get(3);
				// 设置抄送人
				groupMails.setCopy_to("");
				// 发送邮件，其实应该用多线程改写，稍后再写
				//System.out.println(toMan + subject + list.toString() + content + copy_to + sendMan);
				sendMail(toMan, subject, list, content, copy_to, sendMan);// 发送邮件
				// 成功发送邮件计数加1
				sendedEmailCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("成功发送" + sendedEmailCount + "封");
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
