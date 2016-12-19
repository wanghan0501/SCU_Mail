package utils;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 类说明：读取通讯录列表信息
 */
public class ReadLinkmanXMl {
	private static Vector<Vector<String>> linkmans = LinkmanListTabelModel
			.getVector();

	// 读取联系人信息
	public void readXMl(String fileName, Vector<Vector<String>> linkmanVector) {
		try {
			String path = System.getProperty("user.dir");
			fileName = path + "/" + fileName;
			File f = new File(fileName);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);// 读取联系人列表
			Element root = doc.getRootElement();
			Element foo = null;
			for (Iterator i = root.elementIterator("linkman"); i.hasNext();) {// 遍历每一个结点
				foo = (Element) i.next();
				Vector<String> vector = new Vector<String>();
				vector.add(foo.elementText("name"));
				vector.add(foo.elementText("nickname"));
				vector.add(foo.elementText("emailadress"));
				linkmanVector.add(vector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static DefaultListModel dl = new DefaultListModel();// 联系人列表模型

	// 联系人列表 用于主页面显示
	public JList makeList() {
		JList linkmanList = null;

		try {
			linkmans.removeAllElements();
			readXMl("linkman.xml", linkmans);
			int linkmansCount = linkmans.size();
			if (linkmansCount != 0) {
				for (int i = 0; i < linkmansCount; i++) {
					String name = linkmans.get(i).get(0);// 得到第i个联系人的姓名
					if (name != null && !"".equals(name)) {
						dl.addElement(name);// 将联系人姓名添加到列表名模型中
					} else {
						String email = linkmans.get(i).get(2);// 得到联系人的email
						dl.addElement(email);
					}
				}
				linkmanList = new JList(dl);
			} else {
				dl.addElement("暂时没有联系人");
				linkmanList = new JList(dl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return linkmanList;// 返回联系人列表
	}

	// 根据姓名或邮箱地址查找联系人
	public String findLinkman(int index) {
		String linkman = "";
		String name = (String) dl.get(index);// 得到联系人模型中的联系人姓名
		int linkmansCount = linkmans.size();
		for (int i = 0; i < linkmansCount; i++) {
			String lkmname = linkmans.get(i).get(0);// 得到第i个联系人的姓名
			if (lkmname.equals(name)) {
				String email = linkmans.get(i).get(2);// 得到联系人的email
				linkman = name + " <" + email + ">;";
				break;
			}
		}
		return linkman;
	}
}
