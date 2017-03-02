package utils;

/**
 * 表格模型
 * @author caesar
 */
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MailTableModel extends AbstractTableModel {
	private String[] columns = new String[] { "发件人", "主题", "接收时间", "附件" };

	private Vector<Vector<String>> v = new Vector<Vector<String>>();

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return v.size();
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		return ((Vector) (v.get(row))).get(column);
	}

	// 得到Vector<Vector<String>> 对象
	public Vector<Vector<String>> getVector() {
		return v;
	}

	// 设置列的显示名称
	public void setColumens(String[] value) {
		columns = value;
	}
}