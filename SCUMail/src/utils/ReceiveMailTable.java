package utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import mailutil.GetMail;
import frame.JProgressBarFrame;
import frame.MainFrame;

public class ReceiveMailTable extends Thread {
	private Vector mails = null;// 构造一个空向量，使其内部数据数组的大小为
	// 10，其标准容量增量为零。
	private static Vector<Vector<String>> mailListVector = null;// 收取邮件列表模型
	private static GetMail getmail = null;
	private RecycleMailTable recycleMail = null;
	private static JTable deleteMailTable = null;// 删除邮件表格
	private MailTableModel tableModel = null;
	public static LinkedList<String> list = new LinkedList<String>();
	private static ReceiveMailTable mail2Table = null;// 单例模式得到本类对象
	private static JProgressBarFrame progressBar = new JProgressBarFrame(
			MainFrame.MAINFRAME, "收件箱", "正在查收邮件，请稍后...");

	private ReceiveMailTable() {
		getmail = GetMail.getMailInstantiate();
	}

	public static ReceiveMailTable getMail2Table() {// 单例模式得到本类对象
		if (mail2Table == null) {
			progressBar.setVisible(true);
			mail2Table = new ReceiveMailTable();
			mail2Table.start();
		}
		return mail2Table;
	}

	public static boolean isInitMail2Table() {// 判断本类对象是否初始化
		boolean isInit = false;
		if (mail2Table != null)
			isInit = true;
		return isInit;
	}

	// 设置邮件列表表格
	public void setDeleteMailTable(JTable jTable) {
		deleteMailTable = jTable;
	}

	// 刷新邮件列表时用到
	public void startReceiveMail() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setVisible(true);
				MainFrame.addIFame(FrameFactory.getFrameFactory()
						.getReceiveFrame());// 将收件箱界面显示在主窗口中
			}
		});
		new Thread() {// 开启新的线程刷新邮件列表
			public void run() {
				mailToTable();
			}
		}.start();
	}

	@Override
	public void run() {
		mailToTable();
	}

	// 将邮件信息添加到表格中
	public void mailToTable() {
		try {
			Iterator it = getmail.getMailInfo(getmail.getAllMail()).iterator();
			Map map = null;
			// 将邮件信息列表显示在表格中
			if (deleteMailTable.getRowCount() > 0) {
				deleteMailTable.removeAll();// 移除表格中的所有内容
				mailListVector.removeAllElements();// 移除模型中的所有内容
				list.clear();// 从此列表中移除所有元素
			}
			while (it.hasNext()) {
				mails = new Vector<String>();
				map = (Map) it.next();
				mails.add("<html>" + map.get("sender")
						+ "</html>");
				mails.add("<html>" + map.get("subject")
						+ "</html>");
				mails.add("<html>" + map.get("senddate")
						+ "</html>");
				mails.add("<html><strong>" + map.get("hasAttach")
						+ "</strong></html>");
				list.add((String) map.get("ID"));
				mailListVector.add(mails);
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					deleteMailTable.validate();// 动态的更新表格
					deleteMailTable.repaint();
					progressBar.setVisible(false);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取邮件内容
	public static String readMail(LinkedList<String> linkedList, int id) {// id行号
		String message = null;
		String mailID = (String) linkedList.get(id);// 返回指定行对应的mailID号
		try {
			Map result = getmail.readMail(mailID);
			message = result.get("content").toString();// 得到邮件内容
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	// 将邮件移到回收站
	public void moveMail2Recycle(int id) {
		recycleMail = RecycleMailTable.getRecycleMail();// 初始化回收站
		recycleMail.getMailTableModel();// 得到回收站的表格模型
		recycleMail.addRecycleMail(mailListVector.get(id), list.get(id));// 将邮件移到回收站
	}

	// 删除邮件
	public void deleteMail(int id) {// id 选择的行号
		mailListVector.removeElementAt(id);// 删除表格中选中邮件对应的行
		list.remove(id);
		deleteMailTable.updateUI();// 动态的更新表格
		deleteMailTable.validate();// 动态的更新表格
		deleteMailTable.repaint();
	}

	// 恢复邮件
	public static void recoverMail(Vector<String> vector, String mailId) {
		mailListVector.add(vector);
		list.add(mailId);
		deleteMailTable.updateUI();// 动态的更新表格
		deleteMailTable.validate();// 动态的更新表格
		deleteMailTable.repaint();
	}

	// 彻底删除邮件
	public static boolean deleteMailForever(int[] selectMail, int id, List list) {
		boolean isDelete = false;
		String[] mailID = new String[id];
		for (int i = 0; i < id; i++) {
			mailID[i] = (String) list.get(selectMail[i]);// 返回指定行对应的mailID号
		}
		isDelete = getmail.deleteMail(mailID);// 彻底删除邮件
		return isDelete;
	}

	public MailTableModel getMailTableModel() {// 邮件表格模型
		if (tableModel == null)
			tableModel = new MailTableModel();
		mailListVector = tableModel.getVector();
		return tableModel;
	}

}
