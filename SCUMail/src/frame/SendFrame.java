package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import mailutil.SendAttachMail;
import utils.EditorPopupMenu;
import utils.EditorUtils;
import utils.SendedMailTable;

/**
 * 发送邮件界面
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class SendFrame extends JInternalFrame implements ActionListener,
		MouseListener, MouseMotionListener, FocusListener {
	private JComboBox fontSizeCB;// 字体大小列表
	private JComboBox fontCB;// 字体列表
	private JTextPane sendCotent;// 发送内容面板
	private JTextField subjectTF;// 邮件主题文本框
	private JTextField copy_to;// 抄送
	private JTextField to_mail;// 收件人
	private JList attachmentList = null;// 附件列表，最多能添加三个附件
	private JScrollPane scrollPane = null;// 正文编辑窗口
	private JScrollPane jsp = null;// 用于显示附件
	private DefaultListModel listmodel = null;// 附件列表模型
	private JLabel adjunctL = null;// 附件标签
	private JLabel to_mailLabel = null, copy_toLabel = null,
			subject_Label = null;
	private JButton sendButton = null;// 发送按钮
	private JButton resetButton = null;// 重置
	private JButton attachmentButton = null;// 插入附件按钮
	private JButton selectColorButton = null;// 颜色选择按钮
	private Box baseBox = null, boxV1 = null, boxV2 = null;
	private ArrayList<String> attachArrayList = new ArrayList<String>();// 用于存储附件路径的链表
	private Color color = Color.black;
	// 属性定义
	private Action boldAction = new StyledEditorKit.BoldAction();// 添加加粗侦听器
	private Action underlineAction = new StyledEditorKit.UnderlineAction(); // 添加加下划线侦听器
	private Action italicAction = new StyledEditorKit.ItalicAction(); // 添加倾斜侦听器
	private HTMLDocument document = null;// 声明一个网页文档对象变量
	private SendAttachMail sendMail = null;
	private EditorPopupMenu rightMouseButton = null;
	private JProgressBarFrame progressBar = null;// 进度条实例

	public SendFrame() {
		super("新邮件");
		this.setFrameIcon(EditorUtils.createIcon("newMail.gif"));
		// 初始化基本项
		getContentPane().setLayout(new BorderLayout());// 设置空布局
		setIconifiable(true);// 是否使 JInternalFrame 变成一个图标
		setClosable(true);// 是否关闭
		setMaximizable(true);// 窗口最大化设置
		setResizable(true);// 设置窗口课以调整大小
		setBounds(10, 10, 640, 600);// 设置界面的大小
		setVisible(true);

		// 设置收件人标签
		to_mailLabel = new JLabel();
		to_mailLabel.setText("收件人:");
		// 抄送标签
		copy_toLabel = new JLabel();
		copy_toLabel.setText("抄送:");
		// 主题标签
		subject_Label = new JLabel();
		subject_Label.setText("主题:");
		// 收件人文本框
		to_mail = new JTextField(40);
		to_mail.addFocusListener(this);
		to_mail.setToolTipText("收件人地址以英文逗号分隔");
		// 抄送文本框
		copy_to = new JTextField(40);
		copy_to.addFocusListener(this);
		// 主题文本框
		subjectTF = new JTextField(40);
		JPanel setPanel = new JPanel();// 上半部
		setPanel.add(box());
		scrollPane = new JScrollPane();

		sendCotent = new JTextPane();
		sendCotent.setContentType("text/html");
		HTMLEditorKit editorKit = new HTMLEditorKit();
		document = (HTMLDocument) editorKit.createDefaultDocument();// 创建默认文档指向网页引用document
		sendCotent.setEditorKit(editorKit);// 设置为html格式的编辑器
		sendCotent.setDocument(document);
		sendCotent.addMouseListener(this);
		scrollPane.setViewportView(sendCotent);
		// 工具条
		final JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar);
		sendButton = new JButton("发送", EditorUtils.createIcon("newsend.gif"));
		sendButton.addActionListener(this);
		toolBar.add(sendButton);
		resetButton = new JButton("重写", EditorUtils.createIcon("rewrite.gif"));
		resetButton.addActionListener(this);
		toolBar.add(resetButton);
		// 附件列表
		listmodel = new DefaultListModel();
		adjunctL = new JLabel("附件：");
		jsp = new JScrollPane();// 用于显示JList
		jsp.setPreferredSize(new Dimension(350, 20));
		attachmentList = new JList(listmodel);
		attachmentList.addMouseListener(this);// 为邮件列表添加鼠标事件
		jsp.setViewportView(attachmentList);// 设置JScrollPanel的视图为JList
		attachmentList
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		attachmentList.setVisibleRowCount(1);
		attachmentList.setLayoutOrientation(JList.VERTICAL_WRAP);
		// 插入附件按钮
		attachmentButton = new JButton("插入附件",
				EditorUtils.createIcon("attach.png"));
		attachmentButton.addActionListener(this);
		toolBar.add(attachmentButton);
		// 斜体按钮
		JButton italicButton = new JButton(italicAction);
		italicButton.setIcon(EditorUtils.createIcon("italic.gif"));
		italicButton.setText("");
		italicButton.setPreferredSize(new Dimension(22, 22));
		// 粗体按钮
		JButton blodButton = new JButton(boldAction);
		blodButton.setIcon(EditorUtils.createIcon("blod.gif"));
		blodButton.setText("");
		blodButton.setPreferredSize(new Dimension(22, 22));
		// 下划线按钮
		JButton underlineButton = new JButton(underlineAction);
		underlineButton.setIcon(EditorUtils.createIcon("underline.gif"));
		underlineButton.setText("");
		underlineButton.setPreferredSize(new Dimension(22, 22));

		// 字体
		final JLabel fontLabel = new JLabel("字体");
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();// 获得本地 计算机上字体可用的名称
		String font[] = ge.getAvailableFontFamilyNames();
		fontCB = new JComboBox(font);
		fontCB.addActionListener(this);
		// 字号列表
		final JLabel fontSizeLabel = new JLabel("字号");
		String fontSize[] = { "10", "11", "12", "13", "14", "16", "18", "20",
				"22", "24", "26", "28", "36", "48" };
		fontSizeCB = new JComboBox(fontSize);
		fontSizeCB.addActionListener(this);
		fontSizeCB.setPreferredSize(new Dimension(50, 23));

		// 颜色
		final JLabel colorLabel = new JLabel("颜色");
		selectColorButton = new JButton("选色");
		selectColorButton.addActionListener(this);

		JPanel editorToolBarPanel = new JPanel();// 编辑区工具条
		editorToolBarPanel.add(italicButton);
		editorToolBarPanel.add(blodButton);
		editorToolBarPanel.add(underlineButton);
		editorToolBarPanel.add(new JLabel("   "));
		editorToolBarPanel.add(fontLabel);
		editorToolBarPanel.add(fontCB);
		editorToolBarPanel.add(new JLabel("   "));
		editorToolBarPanel.add(fontSizeLabel);
		editorToolBarPanel.add(fontSizeCB);
		editorToolBarPanel.add(new JLabel("   "));
		editorToolBarPanel.add(colorLabel);
		editorToolBarPanel.add(selectColorButton);
		// 编辑区面板
		JPanel editorPanel = new JPanel(new BorderLayout());// 编辑区
		editorPanel.add(editorToolBarPanel, BorderLayout.NORTH);
		editorPanel.add(scrollPane, BorderLayout.CENTER);
		// 添加一个分割窗口
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				setPanel, editorPanel);
		splitPane.setOneTouchExpandable(true);// 在分隔条上提供一个 UI 小部件来快速展开/折叠分隔条
		splitPane.setDividerSize(10);// 设置分隔条的大小。

		// 整个界面编辑区
		JPanel framePanel = new JPanel(new BorderLayout());// 编辑区
		framePanel.add(splitPane, BorderLayout.CENTER);
		this.add(framePanel, BorderLayout.CENTER);
		this.add(toolBar, BorderLayout.NORTH);
		rightMouseButton = new EditorPopupMenu(sendCotent);
	}

	private Box box() {
		// 创建标签box
		boxV1 = Box.createVerticalBox();
		boxV1.add(to_mailLabel);
		boxV1.add(Box.createVerticalStrut(10));
		boxV1.add(copy_toLabel);
		boxV1.add(Box.createVerticalStrut(14));
		boxV1.add(subject_Label);
		boxV1.add(Box.createVerticalStrut(12));

		// 创建文本框box
		boxV2 = Box.createVerticalBox();
		boxV2.add(to_mail);
		boxV2.add(Box.createVerticalStrut(8));
		boxV2.add(copy_to);
		boxV2.add(Box.createVerticalStrut(8));
		boxV2.add(subjectTF);

		// 创建基本box
		baseBox = Box.createHorizontalBox();
		baseBox.add(boxV1);
		baseBox.add(Box.createHorizontalStrut(20));
		baseBox.add(boxV2);
		boxV2.add(Box.createVerticalStrut(8));
		return baseBox;
	}

	// 按钮事件的处理
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selectColorButton) {// 选择颜色
			color = JColorChooser.showDialog(this, "请选择颜色", Color.black);
			Action colorAction = new StyledEditorKit.ForegroundAction(
					"set-foreground-", color);// 添加颜色侦听器
			if (color != null)
				colorAction.actionPerformed(new ActionEvent(color, 0,
						sendCotent.getSelectedText()));
		} else if (e.getSource() == fontCB) {// 字体设置
			String font = (String) fontCB.getSelectedItem();
			Action fontAction = new StyledEditorKit.FontFamilyAction(font, font);
			fontAction.actionPerformed(new ActionEvent(fontAction, 0,
					sendCotent.getSelectedText()));
		} else if (e.getSource() == fontSizeCB) {// 字体大小设置
			String fontsize = (String) fontSizeCB.getSelectedItem();
			Action fontSizeAction = new StyledEditorKit.FontSizeAction(
					fontsize, Integer.parseInt(fontsize));
			fontSizeAction.actionPerformed(new ActionEvent(fontSizeAction, 0,
					sendCotent.getSelectedText()));
		} else if (e.getSource() == resetButton) {// 重置按钮事件
			reset();
		} else if (e.getSource() == attachmentButton) {// 插入附件
			addAttachment();// 插入附件
		} else if (e.getSource() == sendButton) {// 发送邮件
			getSendMailInfo();// 发送邮件
		}
	}

	// 鼠标事件处理
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == attachmentList && e.getButton() == 3) {// 鼠标按键getButton()方法返回1表示按了左键盘，2表示按了中键盘，3表示按了右键盘
			deleteAttachment(e);// 删除附件
		} else if (e.getSource() == sendCotent && e.getButton() == 3) {// 鼠标按键getButton()方法返回1表示按了左键盘，2表示按了中键盘，3表示按了右键盘
			rightMouseButton.rightMouseButton(e);// 鼠标右键响应
		}

	}

	// 鼠标拖动
	public void mouseDragged(MouseEvent mouseevent) {

	}

	public void mouseMoved(MouseEvent mouseevent) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
	}

	// 添加附件
	private void addAttachment() {
		if (listmodel.getSize() >= 4) {
			JOptionPane.showMessageDialog(this, "最多只能添加4个附件");
			return;
		}
		File f = new File(".");// 得到当前目录
		JFileChooser chooser = new JFileChooser(f);// 构造一个当前路径的文件选择器
		if (chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {// 如果选择确定键
			File file = chooser.getSelectedFile();
			Icon icon = chooser.getIcon(file);
			attachmentList.setCellRenderer(new CellRender(icon));
			listmodel.addElement(file.getName());// 将附件添加到JLIST中
			attachArrayList.add(file.getPath());// 将附件的路径添加到附件列表中
		}
		if (listmodel.getSize() <= 1) {
			boxV1.add(adjunctL);
			boxV2.add(jsp);
		}
		validate();
		repaint();
	}

	// 删除附件
	private void deleteAttachment(MouseEvent e) {
		final JPopupMenu popup = new JPopupMenu();
		JMenuItem itemdel = new JMenuItem("删除");
		itemdel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (attachmentList.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(SendFrame.this,
							"请您选择列表中需要删除的附件");
					return;
				}
				int attachmentIndex = attachmentList.getSelectedIndex();// 得到选择附件的索引号
				attachArrayList.remove(attachmentIndex);// 将附件路径链表中的对应值删除
				listmodel.remove(attachmentIndex);// 将列表模型中的附件删除
			}
		});
		popup.add(itemdel);
		popup.show(e.getComponent(), e.getX(), e.getY());// 显示弹出菜单
	}

	// 得到发送邮件信息
	public void getSendMailInfo() {
		sendMail = SendAttachMail.getSendMailInstantiate();// 初始化发送邮件对象
		String text = sendCotent.getText().trim();// 正文
		String sendMan = sendMail.getUser();// 发件人
		String subject = subjectTF.getText().trim();// 主题
		String toMan = to_mail.getText().trim();// 收件人
		String copy = copy_to.getText().trim();// 抄送到
		SendedMailTable.getSendedMailTable().setSendFrame(this);// 重新发送邮件时调用
		sendMail(toMan, subject, attachArrayList, text, copy, sendMan);// 发送邮件
	}

	/**
	 * 发送邮件
	 * 
	 * @param text
	 *            正文内容
	 * @param sendMan
	 *            发件人
	 * @param subject
	 *            主题
	 * @param toMan
	 *            收件人
	 */

	public void sendMail(final String toMan, final String subject,
			ArrayList<String> list, final String text, final String copy,
			final String sendMan) {
		sendMail.setContent(text);// 设置邮件正文
		sendMail.setFilename(list);// 设置邮件附件名称
		sendMail.setFrom(sendMan);// 设置发件人
		sendMail.setSubject(subject);// 设置邮件主题
		sendMail.setTo(toMan);// 设置收件人
		sendMail.setCopy_to(copy);// 设置抄送人

		if (progressBar == null) {
			progressBar = new JProgressBarFrame(MainFrame.MAINFRAME, "发送邮件",
					"正在发送邮件，请稍后...");
		}
		progressBar.setVisible(true);
		new Thread() {// 开启一个新的线程发送邮件
			public void run() {
				String message = "";
				if ("".equals(message = sendMail.send())) {
					SendedMailTable.getSendedMailTable().setValues(toMan,
							subject, attachArrayList, text, copy, sendMan);// 将邮件添加到已发送
					message = "邮件已发送成功！";
				} else {
					message = "<html><h4>邮件发送失败！ 失败原因：</h4></html>\n" + message;
				}
				progressBar.dispose();
				JOptionPane.showMessageDialog(SendFrame.this, message, "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}.start();
	}

	// 清空各种属性值
	private void reset() {
		sendCotent.setText("");
		subjectTF.setText("");
		copy_to.setText("");
		to_mail.setText("");
		attachArrayList.clear();
		listmodel.clear();
	}

	// 添加联系人到收件人
	public void addLinkman(String linkman) {
		if (focusStatic == 2) {// 判断抄送文本框是否得到焦点
			setJTextFieldString(copy_to, linkman);
			copy_to.requestFocus();// 抄送人文本框得到焦点
		} else {
			to_mail.requestFocus();// 收件人文本框得到焦点
			setJTextFieldString(to_mail, linkman);
		}
	}

	// 设置文本框中的字符串
	private void setJTextFieldString(JTextField jt, String linkman) {
		String copy_toString = jt.getText();
		if (!copy_toString.endsWith(";") && !copy_toString.equals(""))
			copy_toString += ";";
		copy_toString += linkman;
		jt.setText(copy_toString);
	}

	private int focusStatic = 1;// 1 代表收件人得到焦点，2代表抄送人得到焦点

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == to_mail)
			focusStatic = 1;
		else
			focusStatic = 2;
	}

	@Override
	public void focusLost(FocusEvent e) {

	}
}
