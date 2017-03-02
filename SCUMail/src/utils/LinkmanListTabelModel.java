package utils;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * 联系人列表表格模型
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 * 
 */
public class LinkmanListTabelModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private final String[] COLUMNS = new String[] { "名称", "昵称", "电子邮箱地址" };
	private static Vector<Vector<String>> v = new Vector<Vector<String>>();

	public LinkmanListTabelModel() {
	}

	@Override
	public int getColumnCount() {// 得到总行数
		return COLUMNS.length;
	}

	@Override
	public int getRowCount() {// 得到列数
		return v.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {// 得到某行列的值
		return ((Vector<String>) (v.get(rowIndex))).get(columnIndex);
	}

	public String getColumnName(int column) {// 得到行名
		return COLUMNS[column];
	}

	// 得到Vector<Vector<String>> 对象
	public static Vector<Vector<String>> getVector() {
		return v;
	}

}
