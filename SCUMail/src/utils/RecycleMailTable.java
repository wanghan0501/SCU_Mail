package utils;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JTable;

/**
 * 类说明：恢复邮件类
 * 
 * @author 作者:user
 * @version 创建时间：2011-2-22 下午06:37:03
 */
public class RecycleMailTable {
	private static RecycleMailTable recycleMail = new RecycleMailTable();;
	private Vector<Vector<String>> recycleMailVector = null;// 恢复邮件列表模型
	private JTable recycleMailTable = null;// 恢复邮件表格
	private MailTableModel tableModel = null;
	public static LinkedList<String> listCopy = new LinkedList<String>();

	private RecycleMailTable() {
	}

	public static RecycleMailTable getRecycleMail() {
		return recycleMail;
	}

	// 设置恢复邮件表格
	public void setRecycleMailTable(JTable table) {
		recycleMailTable = table;
		recycleMailTable.updateUI();
	}

	// 添加删除邮件信息到恢复列表模型中
	public void addRecycleMail(Vector<String> vector, String mailID) {
		recycleMailVector.addElement(vector);// 将要删除的邮件添加到回收站列表中
		listCopy.add(mailID);
		if (recycleMailTable != null)
			recycleMailTable.updateUI();
	}

	// 恢复邮件
	public void recycleMail(int id) {
		ReceiveMailTable.recoverMail(recycleMailVector.get(id), listCopy.get(id));
	}

	// 删除邮件
	public void deleteMail(int id) {// id 选择的行号
		recycleMailVector.removeElementAt(id);// 删除表格中选中邮件对应的行
		listCopy.remove(id);
		if (recycleMailTable != null)
			recycleMailTable.updateUI();// 动态的更新表格
	}

	// 邮件表格模型
	public MailTableModel getMailTableModel() {// 邮件表格模型
		if (tableModel == null)
			tableModel = new MailTableModel();
		recycleMailVector = tableModel.getVector();
		return tableModel;
	}

}
