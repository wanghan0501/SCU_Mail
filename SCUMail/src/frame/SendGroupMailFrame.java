package frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

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
import utils.CreateLoggerFile;
import mailutil.SendAttachMail;
import mailutil.SendGroupAttachMail;
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
	private JButton sendGroupButton = null;
	private JButton resetButton = null;
	private JProgressBarFrame progressBar = null;
	// 获得发件实例
	private SendAttachMail mail = SendAttachMail.getSendMailInstantiate();
	private int sendedEmailCount = 0;
	private int totalEmailCount = 0;
	private String selectDirPath = "";
	// 线程池
	private ExecutorService exec = Executors.newCachedThreadPool();
	// 群发时候最多只有10个线程
	private final Semaphore sendSemaphore = new Semaphore(10);
	// Log并发队列
	private ConcurrentLinkedQueue<String> successQueue = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> faileQueue = new ConcurrentLinkedQueue<String>();

	// 储存联系人信息
	Vector<Vector<String>> linkmanInfo = new Vector<Vector<String>>();
	// 储存是否有附件
	ArrayList<Boolean> hasAttachment = new ArrayList<Boolean>();

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
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "姓名", "联系地址", "邮件主题", "邮件正文", "附件" }));
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
				JOptionPane.showMessageDialog(SendGroupMailFrame.this, "通讯录为空，请载入" + "通讯录后再次尝试群发邮件！", "警告",
						JOptionPane.INFORMATION_MESSAGE);
		} else if (e.getSource() == resetButton) {
			resetButtonEvent();
		}
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return linkmanInfo.size() == 0;
	}

	public void resetButtonEvent() {
		// TODO Auto-generated method stub
		linkmanInfo.clear();
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "姓名", "联系地址", "邮件主题", "邮件正文", "附件" }));
	}

	public void openLinkmanInfoEvent() {
		// 构造一个当前路径的文件选择器
		JFileChooser chooser = new JFileChooser(new File("."));
		chooser.setOpaque(false);
		// 只搜索文件
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setAcceptAllFileFilterUsed(false);

		// xls文本过滤器
		chooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".xls") || f.isDirectory())
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
			selectDirPath = file.getParent();
			Icon icon = chooser.getIcon(file);
			if (file.isFile()) {
				// System.out.println("文件:" + file.getAbsolutePath());
				dispLinkmanInfo(file);
			}
			// System.out.println(file.getName());
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
						if (cell.getContents() == "")
							continue;
						else
							linkman.add(cell.getContents());
					}
					// 如果联系人信息正确
					if (linkman.size() == 5 || linkman.size() == 4) {
						linkmanInfo.add(linkman);
						// 数据模型更新
						model.addRow(linkman);
						// 表格更新
						table.updateUI();
					}
				}
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
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
		String sendMan = mail.getUser(); // 发件人
		File file = new File(selectDirPath);
		File[] files = file.listFiles();
		File successLogger = new File(selectDirPath + "/successLog.txt");
		File failelogger = new File(selectDirPath + "/faileLog.txt");
		FileOutputStream successOut = null;
		FileOutputStream faileOut = null;
		try {
			successOut = new FileOutputStream(successLogger);
			faileOut = new FileOutputStream(failelogger);
			for (int i = 0; i < linkmanInfo.size(); i++) {
				// 设置邮件主题
				subject = linkmanInfo.get(i).get(2);
				// 设置邮件收件人
				toMan = linkmanInfo.get(i).get(1);
				// 设置邮件内容
				content = linkmanInfo.get(i).get(3);
				// 设置抄送人
				copy_to = "";
				// 设置附件
				try {
					String[] temp = linkmanInfo.get(i).get(4).trim().split(";");
					for (int tempIndex = 0; tempIndex < temp.length; tempIndex++) {
						for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
							if (files[fileIndex].getName().equals(temp[tempIndex])) {
								list.add(files[fileIndex].getPath().replaceAll("\\\\", "/"));
							}
						}
					}
				} catch (Exception e) {
					list.clear();
				}
				// 发送邮件
				sendMail(toMan, subject, list, content, copy_to, sendMan);
				list.clear();
				// 成功发送邮件计数加1
				sendedEmailCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			new Thread(new CreateLoggerFile(successOut, successQueue)).start();
			new Thread(new CreateLoggerFile(faileOut, faileQueue)).start();
		}
	}

	public void sendMail(final String toMan, final String subject, final ArrayList<String> list, final String text,
			final String copy, final String sendMan) {
		// 群邮件对象
		final SendGroupAttachMail groupMails = new SendGroupAttachMail(mail.getSMTPHost(), mail.getUser(),
				mail.getPassword(), toMan, subject, text, copy, list);
		if (progressBar == null) {
			progressBar = new JProgressBarFrame(MainFrame.MAINFRAME, "发送群邮件", "群邮件正在发送中，请稍后...");
		}
		progressBar.setVisible(true);
		Runnable run = new Runnable() {// 开启一个新的线程发送邮件,线程最大数目10，超过最大数目后就等待
			public void run() {
				try {
					// 获得锁许可
					sendSemaphore.acquire();
					String message = "";
					if ("".equals(message = groupMails.send())) {
						SendedMailTable.getSendedMailTable().setValues(toMan, subject, list, text, copy, sendMan);// 将邮件添加到已发送
						message = "邮件已发送成功！";
						successQueue.add(new Date()+" 向"+toMan +"发送邮件成功!\n");
					} else {
						message = "邮件发送失败！ 失败原因：\n" + message;
						faileQueue.add(new Date() + " 向" + toMan + message+"\n");
						JOptionPane.showMessageDialog(SendGroupMailFrame.this, message, "提示",
								JOptionPane.INFORMATION_MESSAGE);
					}
					progressBar.dispose();
					// 访问完后，释放锁
					sendSemaphore.release();
					// System.out.println("-----------------"+sendSemaphore.availablePermits());
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		};
		exec.execute(run);
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
