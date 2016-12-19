package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JTable;

import frame.SendFrame;

/**
 * 类说明：已发送邮件表格模板
 */
public class SendedMailTable {// 采用单例模式保证表格的唯一
	private static SendedMailTable sendedMail = new SendedMailTable();// 本类对象
	private JTable sendedMailTable = null;// 已发送邮件表格
	private MailTableModel tableModel = null;
	private Vector<Vector<String>> sendedMailVector = null;// 已发送邮件列表模型
	private Vector<Vector<Object>> resendMailVector = new Vector<Vector<Object>>();// 重新发送
	private LinkedList<String> sendedMailMessageList = new LinkedList<String>();// 已发送邮件内容列表

	private SendedMailTable() {
	}

	// 得到本类对象
	public static SendedMailTable getSendedMailTable() {
		return sendedMail;
	}

	// 设置表格模型
	public MailTableModel getMailTableModel() {
		if (tableModel == null) {
			tableModel = new MailTableModel();
			tableModel.setColumens(new String[] { "收件人", "主题", "发送时间", "附件" });
			sendedMailVector = tableModel.getVector();
		}
		return tableModel;
	}

	// 设置表格对象
	public void setSendedMailTable(JTable table) {
		sendedMailTable = table;
		sendedMailTable.updateUI();
	}

	// 添加邮件到已发送列表中
	public void addSendedMail(Vector<String> vector, String mailMessage) {
		sendedMailVector.add(vector);// 将要删除的邮件添加到回收站列表中
		sendedMailMessageList.add(mailMessage);
		if (sendedMailTable != null)
			sendedMailTable.updateUI();
	}

	// 删除已发送列表中的邮件
	public void deleteMail(int id) {// id 选择的行号
		sendedMailVector.removeElementAt(id);// 删除表格中选中邮件对应的行
		resendMailVector.removeElementAt(id);// 删除重新发送列表中的邮件信息
		sendedMailMessageList.remove(id);
		if (sendedMailTable != null)
			sendedMailTable.updateUI();// 动态的更新表格
	}

	// 读取已删除邮件的内容
	public String readMail(int id) {
		return sendedMailMessageList.get(id);
	}

	// 设置已发送邮件的各个属性
	public void setValues(String toMan, // 收件人
			String subject, // 已发送邮件的主题
			ArrayList<String> list,// 邮件是否有附件
			String text,// 已发送邮件的正文
			String copyto,// 抄送到
			String sendMan) {// 发送人
		getMailTableModel();// 初始化已发送邮件列表模型
		resendMail(toMan, subject, list, text, copyto, sendMan);// 将已发送邮件添加到重新发送列表中
		Vector<String> sendedMail = new Vector<String>();
		sendedMail.add("<html>" + toMan + "</html>");
		sendedMail.add("<html>" + subject + "</html>");
		sendedMail.add("<html>" + getTime() + "</html>");// 得到系统当前的时间
		if (!list.isEmpty())
			sendedMail.add("<html><strong>" + "☆" + "</strong></html>");
		else
			sendedMail.add("");// 没有附件
		addSendedMail(sendedMail, text);// 添加邮件到已发送列表中
	}

	// 得到系统当前的时间
	private String getTime() {
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss"); // 格式化当前系统日期
		return dateFm.format(new Date());
	}

	// 将邮件信息添加到重新发送列表中
	private void resendMail(String toMan, // 收件人
			String subject, // 已发送邮件的主题
			ArrayList<String> list,// 邮件是否有附件
			String text,// 已发送邮件的正文
			String copyto,// 抄送到
			String sendMan) {// 发件人
		Vector<Object> sendedMail = new Vector<Object>();
		sendedMail.add(toMan);
		sendedMail.add(subject);
		sendedMail.add(list);
		sendedMail.add(text);
		sendedMail.add(copyto);
		sendedMail.add(sendMan);
		resendMailVector.add(sendedMail);
	}

	// 重新发送邮件
	public void resend(int id) {
		Vector<Object> sendedMail = resendMailVector.get(id);
		sendFrame.sendMail((String) sendedMail.get(0),
				(String) sendedMail.get(1),
				(ArrayList<String>) sendedMail.get(2),
				(String) sendedMail.get(3), (String) sendedMail.get(4),
				(String) sendedMail.get(5));

	}

	// 发送邮件对象
	private SendFrame sendFrame = null;

	public void setSendFrame(SendFrame send) {
		sendFrame = send;
	}
}
