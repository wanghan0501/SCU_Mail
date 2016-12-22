package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import utils.ClassNameTreeCellRenderer;
import utils.EditorUtils;
import utils.FrameFactory;
import utils.ReadLinkmanXMl;
import utils.ReceiveMailTable;

/**
 * 主界面
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class MainFrame extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private static JDesktopPane desktopPane = null;// 用于创建多文档界面或虚拟桌面的容器
	public static MainFrame MAINFRAME;
	private JTree tree;// 树形图
	private JList jl;// 联系人列表
	private JPanel panel, panelframe;// panelframe左半部界面
	private JLabel labelbackground;
	private JScrollPane scrollPane;
	private JMenuItem exitMI = null, newMailMI = null, sendedMI = null,
			receiveMI = null, recycleMI = null, refreshMI = null, groupMailMI = null,
			helpMI = null,aboutMI = null;
	private JButton addLinkmanButton = null;// 添加联系人按钮
	private JMenu fileMenu = null;
	private JMenu mailMenu = null;
	private JMenu aboutMenu = null;
	private ReadLinkmanXMl readLinkman = null;

	// 初始化界面配置
	public void jFrameValidate() {
		Toolkit tk = getToolkit();// 获得屏幕的宽和高
		Dimension dim = tk.getScreenSize();
		this.setBounds(dim.width / 2 - 420, dim.height / 2 - 350, 850, 678);
		validate();
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public MainFrame() {
		super("SCU邮件客户端");
		MAINFRAME = this;
		this.setIconImage(EditorUtils.createIcon("email.png").getImage());
		desktopPane = new JDesktopPane();
		jFrameValidate();// 初始化界面
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		fileMenu = new JMenu("文件(F)");
		mailMenu = new JMenu("邮件(M)");
		aboutMenu = new JMenu("帮助(H)");
		menuBar.add(fileMenu);
		menuBar.add(mailMenu);
		menuBar.add(aboutMenu);

		exitMI = addMenuItem(fileMenu, "退出", "exit.gif");// 退出菜单项的初始化
		newMailMI = addMenuItem(mailMenu, "新建邮件", "newMail.gif");// 新建邮件菜单项的初始化
		
		groupMailMI = addMenuItem(mailMenu,"新建群邮件","newGroup.jpg"); //发送群邮件菜单项初始化
		
		sendedMI = addMenuItem(mailMenu, "发件箱", "sended.png");// 已发送邮件菜单项的初始化
		receiveMI = addMenuItem(mailMenu, "收件箱", "receive.png");// 收件箱邮件菜单项的初始化
		recycleMI = addMenuItem(mailMenu, "回收站", "deleted.png");// 已删除邮件菜单项的初始化
		refreshMI = addMenuItem(mailMenu, "刷新收件箱", "refresh.jpg");// 已删除邮件菜单项的初始化
		helpMI = addMenuItem(aboutMenu,"帮助文档","help.png"); //帮助文档菜单项初始化
		aboutMI = addMenuItem(aboutMenu,"关于我们","aboutUs.png"); // 关于我们菜单项的初始化		
		
		// 构建树形节点
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("SCU邮件客户端");
		DefaultMutableTreeNode send = new DefaultMutableTreeNode("新建邮件");
		DefaultMutableTreeNode sendGroup = new DefaultMutableTreeNode("新建群邮件");
		DefaultMutableTreeNode addressee = new DefaultMutableTreeNode("收件箱");
		DefaultMutableTreeNode AlreadySend = new DefaultMutableTreeNode("发件箱");
		DefaultMutableTreeNode delete = new DefaultMutableTreeNode("回收站");
		root.add(send);
		root.add(sendGroup);
		root.add(addressee);
		root.add(AlreadySend);
		root.add(delete);

		tree = new JTree(root);
		tree.addMouseListener(this);// 为树形节点注册鼠标事件
		tree.setPreferredSize(new Dimension(160, 650));
		//tree.setBackground(Color.GRAY);
		// 重新渲染树形节点
		ClassNameTreeCellRenderer render = new ClassNameTreeCellRenderer();
		tree.setCellRenderer(render);
		// 联系人面板
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(160, 300));
		// 界面左半部面板
		panelframe = new JPanel();
		panelframe.setLayout(new BorderLayout());
		panelframe.add(panel, BorderLayout.CENTER);
		panelframe.add(tree, BorderLayout.NORTH);

		addLinkmanButton = new JButton();
		addLinkmanButton.setText("联系人");
		addLinkmanButton.setIcon(EditorUtils.createIcon("linkman.gif"));
		panel.add(addLinkmanButton, BorderLayout.NORTH);
		addLinkmanButton.addActionListener(this);// 注册添加联系人事件
		readLinkman = new ReadLinkmanXMl();
		jl = readLinkman.makeList();// 返回联系人列表
		jl.addMouseListener(this);// 添加联系人列表双击事件
		scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(jl);// 在滚动面板中添加联系人
		validate();

		labelbackground = new JLabel();
		labelbackground.setIcon(null); // 窗体背景
		desktopPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				Dimension size = e.getComponent().getSize();
				labelbackground.setSize(e.getComponent().getSize());
				// 设置窗体背景图
				labelbackground.setText("<html><img width=" + size.width
						+ " height=" + size.height + " src='"
						+ this.getClass().getResource("/main.jpg")
						+ "'></html>");
			}
		});
		desktopPane.add(labelbackground, new Integer(Integer.MIN_VALUE));

		// 添加一个分割窗口
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				panelframe, desktopPane);
		splitPane.setOneTouchExpandable(true);// 在分隔条上提供一个 UI 小部件来快速展开/折叠分隔条
		splitPane.setDividerSize(10);// 设置分隔条的大小
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	// 返回新建菜单项
	private JMenuItem addMenuItem(JMenu menu, String name, String icon) {
		// 新建邮件菜单项的初始化
		JMenuItem menuItem = new JMenuItem(name, EditorUtils.createIcon(icon));
		menuItem.addActionListener(this);// 监听退出菜单项事件
		menu.add(menuItem);
		return menuItem;
	}

	// 添加子窗体的方法
	public static void addIFame(JInternalFrame iframe) {
		JInternalFrame[] frames = desktopPane.getAllFrames();
		try {
			for (JInternalFrame ifm : frames) {
				if (ifm.getTitle().equals(iframe.getTitle())) {
					desktopPane.selectFrame(true);
					ifm.toFront();
					ifm.setSelected(true);
					return;
				}
			}
			desktopPane.add(iframe);
			iframe.setSelected(true);
			iframe.toFront();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	// action事件的处理
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitMI) {
			System.exit(0);// 退出系统
		} else if (e.getSource() == addLinkmanButton) {
			addIFame(FrameFactory.getFrameFactory().getAddLinkManFrame());// 联系人列表
		} else if (e.getSource() == newMailMI) {// 新建邮件
			addIFame(FrameFactory.getFrameFactory().getSendFrame());// 发件夹
		} else if (e.getSource() == groupMailMI) { // 新建群邮件
			addIFame(FrameFactory.getFrameFactory().getSendGroupMailFrame()); //新建群邮件
		} else if (e.getSource() == itemPopupOne || e.getSource() == refreshMI) {// 右键刷新收件列表
			ReceiveMailTable.getMail2Table().startReceiveMail();// 右键刷新收件列表
		} else if (e.getSource() == sendedMI) {// 已发送
			addIFame(FrameFactory.getFrameFactory().getSendedFrame());// 已发送
		} else if (e.getSource() == receiveMI) {// 收邮件
			addIFame(FrameFactory.getFrameFactory().getReceiveFrame());// 收邮件
		} else if (e.getSource() == recycleMI) {// 已删除
			addIFame(FrameFactory.getFrameFactory().getRecycleFrame());// 收邮件
		} else if (e.getSource() == helpMI) { //帮助文档
			addIFame(FrameFactory.getFrameFactory().getHelpContentsFrame()); // 帮助文档
		} else if (e.getSource() == aboutMI) { //关于我们
			addIFame (FrameFactory.getFrameFactory().getAboutUsFrame()); //关于我们
		}

	}

	private SendFrame sendFrame = null;// 发送邮件对象
	public JMenuItem itemPopupOne = null;// 鼠标右键第一个选项

	@Override
	public void mouseClicked(MouseEvent e) {
		// 树形节点中的单击事件
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (e.getSource() == tree && e.getButton() != 3 && e.getButton() != 2) {
			if (selectedNode == null)
				return;
			else if (selectedNode.toString().equals("新建邮件")) {
				sendFrame = FrameFactory.getFrameFactory().getSendFrame();
				addIFame(sendFrame);// 发件夹
			} else if (selectedNode.toString().equals("新建群邮件")) {
				addIFame(FrameFactory.getFrameFactory().getSendGroupMailFrame()); //新建群邮件
			}
			else if (selectedNode.toString().equals("收件箱")) {
				addIFame(FrameFactory.getFrameFactory().getReceiveFrame());// 收件夹
			} else if (selectedNode.toString().equals("发件箱")) {
				addIFame(FrameFactory.getFrameFactory().getSendedFrame());// 已发送邮件
			} else if (selectedNode.toString().equals("回收站")) {
				addIFame(FrameFactory.getFrameFactory().getRecycleFrame());// 已删除邮件
			}
		} else if (e.getSource() == jl && e.getClickCount() == 2) {// 双击联系人事件
			int index = jl.getSelectedIndex();
			if (sendFrame != null && sendFrame.isSelected()) {// 如果发送邮件界面被初始化并且被激活
				sendFrame.addLinkman(readLinkman.findLinkman(index));
			}
		} else if (e.getButton() == MouseEvent.BUTTON3 && e.getSource() == tree) {// 收件箱右键刷新
			if (selectedNode == null)
				return;
			else if ("收件箱".equals(selectedNode.toString())) {
				JPopupMenu popup = new JPopupMenu();
				itemPopupOne = new JMenuItem("刷新收件箱",
						EditorUtils.createIcon("refresh.jpg"));
				itemPopupOne.addActionListener(this);
				popup.add(itemPopupOne);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
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
