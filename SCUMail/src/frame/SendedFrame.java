package frame;

import java.util.List;

import utils.EditorUtils;
import utils.SendedMailTable;

/**
 * 已发送邮件界面
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class SendedFrame extends BaseReceiceFrame {
	private SendedMailTable sendedMail = null;// 已发送邮件列表对象

	public SendedFrame() {
		super("发件箱");
		this.setFrameIcon(EditorUtils.createIcon("sended.png"));
		this.setPopupOne("删除邮件", "delete.png");
		this.setPopupTwo("重新发送", "send.png");
		sendedMail = SendedMailTable.getSendedMailTable();
		tableModel = sendedMail.getMailTableModel();
		sendedMail.setSendedMailTable(table);
		table.setModel(tableModel);
		mailContent.setText("");
	}

	public void doubleClick(int selectRom) {// 双击事件的处理
		mailContent.setText(sendedMail.readMail(selectRom));// 读取已发送的邮件内容
	}

	public void delete(int[] selectRoms, int mailState) {
		for (int i = 0; i < selectRoms.length// 循环删除每行
				&& selectRoms[i] < tableModel.getRowCount(); i++) {// 并判断每行的有效性
			sendedMail.deleteMail(selectRoms[i]);// 删除邮件列表表格中的邮件
			for (int j = i + 1; j < selectRoms.length; j++) {// 修改选中邮件以后的每封邮件的行号
				selectRoms[j]--;
			}
		}
	}

	/* 右键第二个选项被选中 */
	@Override
	public void popupTwoisSelected(int[] selectRoms, List list) {
		for (int i = 0; i < selectRoms.length// 循环删除每行
				&& selectRoms[i] < tableModel.getRowCount(); i++) {// 并判断每行的有效性
			sendedMail.resend(selectRoms[i]);// 重新发送邮件
			for (int j = i + 1; j < selectRoms.length; j++) {// 修改选中邮件以后的每封邮件的行号
				selectRoms[j]--;
			}
		}
	}
}
