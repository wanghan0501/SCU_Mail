package action;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;


import utils.LinkmanListTabelModel;
import utils.SaveLinkmans2XML;

/**
 * 类说明：处理添加删除联系人事件
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class LinkmanEventAction {
	Vector<Vector<String>> linkmanVectors = LinkmanListTabelModel.getVector();
	private JTextField name = null, nickName = null, emailAdress = null;
	private JTable linkmanList = null;

	public LinkmanEventAction(JTextField nameTF, JTextField nickNameTF,
			JTextField emailAdressTF, JTable linkmanList) {
		name = nameTF;// 名称
		nickName = nickNameTF;// 昵称
		emailAdress = emailAdressTF;// 邮箱地址
		this.linkmanList = linkmanList;
	}

	// 添加联系人
	public void addLinkman() {
		if (!checkRepeatEmail(emailAdress.getText().trim()))// 如果email地址不重复
			add();// 添加
		else {
			JOptionPane.showMessageDialog(null, "你添加的邮箱地址已存在，请勿重复添加！", "警告",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	// 删除联系人
	public void deleteLinkman(int selectRow) {
		if (selectRow < linkmanVectors.size() && selectRow != -1) {// 选中一行删除
			linkmanVectors.remove(selectRow);
			linkmanList.updateUI();
		} else {
			JOptionPane.showMessageDialog(null, "你没有选中任何一行不能删除！", "警告",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	// 确定修改联系人并将联系人保存为xml格式的文档
	public void ok() {
		SaveLinkmans2XML saveLinkmansXML = new SaveLinkmans2XML();
		saveLinkmansXML.saveLinkmanXml("linkman.xml", linkmanVectors);
		JOptionPane.showMessageDialog(null, "通讯录修改成功，文件名是 linkman.xml", "提示",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// 添加联系人
	private void add() {
		Vector<String> linkmanVector = new Vector<String>();
		linkmanVector.add(name.getText().trim());
		linkmanVector.add(nickName.getText().trim());
		linkmanVector.add(emailAdress.getText().trim());
		linkmanVectors.add(linkmanVector);
		linkmanList.updateUI();
	}

	// 检测邮箱是否重复
	private boolean checkRepeatEmail(String email) {
		boolean isRepeate = true;
		Vector<String> v = null;
		int count = linkmanVectors.size();// 得到联系人个数
		if (count > 0) {// 如果有联系人就比较
			for (int i = 0; i < count; i++) {
				v = linkmanVectors.get(i);// 得到联系人邮箱地址
				if (v.get(2).equals(email))// 新加的和已有的地址是否重复
					isRepeate = true;
				else
					isRepeate = false;
			}
		} else
			// 如果没有就直接添加
			isRepeate = false;
		return isRepeate;
	}
}
