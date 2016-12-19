package frame;

import java.util.List;

import utils.EditorUtils;
import utils.ReceiveMailTable;
import utils.RecycleMailTable;

/**
 * 回收站 删除邮件的恢复和彻底删除
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016 
 */
public class RecycleFrame extends BaseReceiceFrame {
	private RecycleMailTable recycleMail = null;

	public RecycleFrame() {
		super("回收站");
		this.setFrameIcon(EditorUtils.createIcon("deleted.png"));
		recycleMail = RecycleMailTable.getRecycleMail();
		tableModel = recycleMail.getMailTableModel();
		recycleMail.setRecycleMailTable(table);
		table.setModel(tableModel);
		this.setPopupOne("恢复邮件", "undo.png");
		this.setPopupTwo("彻底删除", "forverdelete.png");
	}

	public void doubleClick(int selectRom) {// 双击事件的处理
		mailContent.setText(ReceiveMailTable.readMail(
				RecycleMailTable.listCopy, selectRom));
	}

	/**
	 * 右键第二个选项被选中
	 * 
	 * @param selectRoms
	 */
	public void popupTwoisSelected(int[] selectRoms, List list) {
		super.popupTwoisSelected(selectRoms, RecycleMailTable.listCopy);
	}

	// 恢复邮件
	public void delete(int[] selectRoms, int mailState) {
		for (int i = 0; i < selectRoms.length// 循环删除每行
				&& selectRoms[i] < tableModel.getRowCount(); i++) {// 并判断每行的有效性
			if (mailState == 1)
				recycleMail.recycleMail(selectRoms[i]);
			recycleMail.deleteMail(selectRoms[i]);// 删除邮件列表表格中的邮件
			for (int j = i + 1; j < selectRoms.length; j++) {// 修改选中邮件以后的每封邮件的行号
				selectRoms[j]--;
			}
		}
	}
}
