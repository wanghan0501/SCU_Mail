package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import utils.EditorUtils;
import utils.LinkmanListTabelModel;
import action.LinkmanEventAction;

/**
 * 增加联系人界面
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class AddLinkManFrame extends JInternalFrame implements ActionListener,
		DocumentListener {
	private JTextField emailAdressTF = null, nickNameTF = null, nameTF = null;
	private JLabel nameLabel = null, nickNameLabel = null,
			emailAdressLabel = null;
	private JButton add = null, delete = null, ok = null, cancel = null;
	private JScrollPane linkmanJSP;
	private JTable linkmanList = null;// 联系人列表
	private JPanel editorJPanel = null;// 编辑面板
	private JPanel linkManListJPanel = null;// 联系人列表面板
	private Box baseBox = null, boxV1 = null, boxV2 = null;
	private LinkmanEventAction linkmanEvent = null;

	public AddLinkManFrame() {
		super("添加联系人");
		this.setFrameIcon(EditorUtils.createIcon("addLinkman.jpg"));
		setClosable(true);
		this.setMaximizable(true);// 窗口最大化设置
		setIconifiable(true);
		setBounds(20, 10, 500, 385);
		getContentPane().setLayout(new BorderLayout());
		// 姓名
		nameLabel = new JLabel();
		nameLabel.setText("姓名:");
		getContentPane().add(nameLabel);
		nameTF = new JTextField(40);
		getContentPane().add(nameTF);

		// 昵称
		nickNameLabel = new JLabel();
		nickNameLabel.setText("昵称:");
		getContentPane().add(nickNameLabel);
		nickNameTF = new JTextField(40);
		getContentPane().add(nickNameTF);
		// 电子邮件
		emailAdressLabel = new JLabel();
		emailAdressLabel.setText("电子邮件:");
		getContentPane().add(emailAdressLabel);
		emailAdressTF = new JTextField(50);
		emailAdressTF.getDocument().addDocumentListener(this);

		linkmanList = new JTable();// 联系人列表
		linkmanList.setModel(new LinkmanListTabelModel());
		linkmanJSP = new JScrollPane(linkmanList);
		linkmanJSP.setPreferredSize(new Dimension(400, 150));

		add = new JButton("添加");
		add.addActionListener(this);
		add.setEnabled(false);
		delete = new JButton("删除");
		delete.addActionListener(this);
		ok = new JButton("确定");
		ok.addActionListener(this);
		cancel = new JButton("撤销");
		cancel.addActionListener(this);
		// 添加联系人面板
		editorJPanel = new JPanel();
		editorJPanel.setBorder(BorderFactory.createTitledBorder("添加联系人:"));
		editorJPanel.add(box());
		this.add(editorJPanel, BorderLayout.NORTH);

		// 添加删除联系人面板
		JPanel addAadDeletePanel = new JPanel();
		addAadDeletePanel.setPreferredSize(new Dimension(60, 150));
		addAadDeletePanel.add(add);
		addAadDeletePanel.add(new JLabel("   "));
		addAadDeletePanel.add(delete);
		// 联系人列表面板
		JPanel linkmanPanel = new JPanel();
		linkmanPanel.add(linkmanJSP);

		linkManListJPanel = new JPanel(new BorderLayout());
		linkManListJPanel.setBorder(BorderFactory.createTitledBorder("联系人列表:"));
		linkManListJPanel.add(linkmanPanel, BorderLayout.CENTER);
		linkManListJPanel.add(addAadDeletePanel, BorderLayout.EAST);
		this.add(linkManListJPanel, BorderLayout.CENTER);

		// 确定取消面板
		JPanel okAndCancelPanel = new JPanel();
		okAndCancelPanel.setPreferredSize(new Dimension(500, 35));
		okAndCancelPanel.add(ok);
		okAndCancelPanel.add(new JLabel("   "));
		okAndCancelPanel.add(cancel);
		this.add(okAndCancelPanel, BorderLayout.SOUTH);
		setVisible(true);
		linkmanEvent = new LinkmanEventAction(nameTF, nickNameTF,
				emailAdressTF, linkmanList);
	}

	private Box box() {
		// 创建标签box
		boxV1 = Box.createVerticalBox();
		boxV1.add(nameLabel);
		boxV1.add(Box.createVerticalStrut(10));
		boxV1.add(nickNameLabel);
		boxV1.add(Box.createVerticalStrut(14));
		boxV1.add(emailAdressLabel);
		boxV1.add(Box.createVerticalStrut(12));

		// 创建文本框box
		boxV2 = Box.createVerticalBox();
		boxV2.add(nameTF);
		boxV2.add(Box.createVerticalStrut(8));
		boxV2.add(nickNameTF);
		boxV2.add(Box.createVerticalStrut(8));
		boxV2.add(emailAdressTF);

		// 创建基本box
		baseBox = Box.createHorizontalBox();
		baseBox.add(boxV1);
		baseBox.add(Box.createHorizontalStrut(20));
		baseBox.add(boxV2);
		boxV2.add(Box.createVerticalStrut(8));
		return baseBox;
	}

	public void changedUpdate(DocumentEvent e) {
		checkInput();// 文本更新时调用
	}

	public void insertUpdate(DocumentEvent e) {
		checkInput();// 插入更新
	}

	public void removeUpdate(DocumentEvent e) {
		checkInput();// 移除更新
	}

	// 检测输入数据的有效性
	private void checkInput() {
		boolean checkEmail = EditorUtils.checkEmailAdress(emailAdressTF
				.getText().trim());
		if (checkEmail) {
			add.setEnabled(true);
		} else {
			add.setEnabled(false);
		}
	}

	// 事件的处理
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add) {
			linkmanEvent.addLinkman();// 添加联系人
		} else if (e.getSource() == delete) {
			linkmanEvent.deleteLinkman(linkmanList.getSelectedRow());// 删除联系人
		} else if (e.getSource() == ok) {
			linkmanEvent.ok();
		} else if (e.getSource() == cancel) {
			this.dispose();// 关闭窗口
		}
	}
}
