package frame;

import utils.EditorUtils;
import utils.ReceiveMailTable;

/**
 * 收件界面
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class ReceiveFrame extends BaseReceiceFrame {

	public ReceiveFrame() {
		super("收件箱");
		this.setFrameIcon(EditorUtils.createIcon("receive.png"));
		mail2Table = ReceiveMailTable.getMail2Table();// 开启新的线程显示邮件列表
		mail2Table.setDeleteMailTable(this.table);
		tableModel = mail2Table.getMailTableModel();
		table.setModel(tableModel);
		this.setPopupOne("删除", "delete.png");
		this.setPopupTwo("彻底删除", "forverdelete.png");
		this.setPopupThree("刷新收件箱", "refresh.jpg", true);
	}

	public void doubleClick(int selectRom) {// 双击事件的处理
		mailContent.setText(ReceiveMailTable.readMail(ReceiveMailTable.list,
				selectRom));
	}

	// 删除邮件
	public void delete(int[] selectRoms, int mailState) {// mailState 判断邮件的删除状态
		for (int i = 0; i < selectRoms.length// 循环删除每行
				&& selectRoms[i] < tableModel.getRowCount(); i++) {// 并判断每行的有效性
			if (mailState == 1)
				mail2Table.moveMail2Recycle(selectRoms[i]);// 将邮件放入回收站
			mail2Table.deleteMail(selectRoms[i]);// 删除邮件列表表格中的邮件
			for (int j = i + 1; j < selectRoms.length; j++) {// 修改选中邮件以后的每封邮件的行号
				selectRoms[j]--;
			}
		}
	}

	// 右键第三个选项被选中刷新收件箱
	private void popupThreeisSelected() {

	}
}
