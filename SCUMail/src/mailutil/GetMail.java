package mailutil;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;

/**
 * 利用POP3接收邮件
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class GetMail {

	private String POP3Host = ""; // POP3服务器
	private String user = ""; // 登录POP3服务器的帐号
	private String password = ""; // 登录POP3服务器的密码

	private Session session = null;
	private Folder folder = null;
	private Store store = null;
	private Message[] msg = null;// 邮件信息
	private static final GetMail getMail = new GetMail();
	private AttachFile attachFile = new AttachFile();

	// 无参数的构造函数
	private GetMail() {
	}

	// 返回GetMail的对象
	public static GetMail getMailInstantiate() {
		return getMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPOP3Host() {
		return POP3Host;
	}

	public void setPOP3Host(String host) {
		POP3Host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	// 连接邮件服务器
	public void connect() throws Exception {
		// 创建一个授权验证对象
		SmtpPop3Auth auth = new SmtpPop3Auth();
		auth.setAccount(user, password);

		// 取得一个Session对象
		Properties prop = new Properties();
		prop.put("mail.pop3.host", POP3Host);
		session = Session.getDefaultInstance(prop, auth);
		//session.setDebug(true);
		// 取得一个Store对象
		store = session.getStore("pop3");
		store.connect(POP3Host, user, password);
	}

	// 关闭连接
	public void closeConnect() {
		try {
			if (folder != null)
				folder.close(true);// 关闭连接时是否删除邮件，true删除邮件
		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
			try {
				if (store != null)
					store.close();// 关闭收件箱连接
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	// 获得所有邮件的列表
	public Message[] getAllMail() throws Exception {
		// 建立POP3连接
		connect();// 连接邮件服务器

		// 取得一个Folder对象
		folder = store.getDefaultFolder().getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		// 取得所有的Message对象
		msg = folder.getMessages();
		FetchProfile profile = new FetchProfile();
		profile.add(FetchProfile.Item.ENVELOPE);
		folder.fetch(msg, profile);
		closeConnect();// 关闭连接邮件服务器
		return msg;
	}

	// 取得邮件列表的信息
	public List getMailInfo(Message[] msg) throws Exception {
		List result = new ArrayList();
		Map map = null;
		Multipart mp = null;
		BodyPart part = null;
		String disp = null;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
		Enumeration enumMail = null;
		// 取出每个邮件的信息
		for (int i = 0; i < msg.length; i++) {
			map = new HashMap();
			// 读取邮件ID
			enumMail = msg[i].getAllHeaders();
			Header h = null;
			while (enumMail.hasMoreElements()) {
				h = (Header) enumMail.nextElement();
				if (h.getName().equals("Message-ID")
						|| h.getName().equals("Message-Id")) {
					map.put("ID", h.getValue());
				}
			}
			// 读取邮件标题
			map.put("subject", msg[i].getSubject());
			// 读取发件人
			map.put("sender",
					MimeUtility.decodeText(msg[i].getFrom()[0].toString()));
			// 读取邮件发送日期
			map.put("senddate", fmt.format(msg[i].getSentDate()));
			// 读取邮件大小
			map.put("size", new Integer(msg[i].getSize()));
			map.put("hasAttach", "&nbsp;");
			// 判断是否有附件
			if (msg[i].isMimeType("multipart/*")) {
				mp = (Multipart) msg[i].getContent();
				// 遍历每个Miltipart对象
				for (int j = 0; j < mp.getCount(); j++) {
					part = mp.getBodyPart(j);
					disp = part.getDisposition();
					// 如果有附件
					if (disp != null
							&& (disp.equals(Part.ATTACHMENT) || disp
									.equals(Part.INLINE))) {
						// 设置有附件的特征值
						map.put("hasAttach", "☆");
					}
				}
			}
			result.add(map);
		}
		return result;
	}

	// 查找指定邮件
	public Message findMail(Message[] msg, String id) throws Exception {
		Enumeration enumMail = null;
		Header h = null;
		for (int i = 0; i < msg.length; i++) {
			enumMail = msg[i].getAllHeaders();
			// 查找邮件头中的Message-ID项
			while (enumMail.hasMoreElements()) {
				h = (Header) enumMail.nextElement();
				// 根据传入的message-id来查找目标邮件
				boolean messageId = (h.getName().equals("Message-ID"))
						|| (h.getName().equals("Message-Id"));
				if (messageId && (h.getValue().equals(id))) {
					return msg[i];
				}
			}
		}
		return null;
	}

	// 删除邮件
	public boolean deleteMail(String[] id) {
		boolean isDelete = false;
		try {
			connect();// 连接邮件服务器
			// 取得一个Folder对象
			folder = store.getDefaultFolder().getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			Message[] deletemsg = folder.getMessages();
			Message mes = null;
			for (int i = 0; i < id.length; i++) {
				mes = findMail(deletemsg, id[i]);// 查找指定邮件
				mes.setFlag(Flags.Flag.DELETED, true);// 把邮件标记为删除
			}
			closeConnect();// 关闭邮件服务器的连接并删除邮件
			CheckNewMialUtil.isCheck = true;// 设置新邮件标记
			isDelete = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDelete;
	}

	// 读取邮件内容
	public Map readMail(String id) throws Exception {
		Map map = new HashMap();
		// 找到目标邮件
		Message readmsg = findMail(msg, id);
		// 读取邮件标题
		map.put("subject", readmsg.getSubject());
		// 读取发件人
		map.put("sender",
				MimeUtility.decodeText(readmsg.getFrom()[0].toString()));
		map.put("attach", "");
		// 取得邮件内容
		if (readmsg.isMimeType("text/*")) {
			map.put("content", readmsg.getContent().toString());
		} else {
			Multipart mp = (Multipart) readmsg.getContent();
			BodyPart part = null;
			String disp = null;
			StringBuffer result = new StringBuffer();
			// 遍历每个Miltipart对象
			for (int j = 0; j < mp.getCount(); j++) {
				part = mp.getBodyPart(j);
				disp = part.getDisposition();
				// 如果有附件
				if (disp != null
						&& (disp.equals(Part.ATTACHMENT) || disp
								.equals(Part.INLINE))) {
					// 取得附件文件名
					String filename = MimeUtility
							.decodeText(part.getFileName());// 解决中文附件名的问题
					map.put("attach", filename);
					// 下载附件
					InputStream in = part.getInputStream();// 附件输入流
					if (attachFile.isDownload(filename))
						attachFile.choicePath(filename, in);// // 下载附件
				} else {
					// 显示复杂邮件正文内容
					result.append(getPart(part, j, 1));
				}
			}
			map.put("content", result.toString());
		}
		return map;
	}

	// x参数来确定是以html 1 格式显示还是以plain 2
	// 调用时getPart（part，i，1）;
	// 显示复杂邮件的正文内容
	public String getPart(Part part, int partNum, int x) throws

	MessagingException, IOException {
		String s = "";
		String s1 = "";
		String s2 = "";
		String s5 = "";
		String sct = part.getContentType();
		if (sct == null) {
			s = "part 无效";
			return s;
		}
		ContentType ct = new ContentType(sct);
		if (ct.match("text/html") || ct.match("text/plain")) {
			// display text/plain inline
			s1 = "" + (String) part.getContent() + "";
		} else if (partNum != 0) {
			String temp = "";
			if ((temp = part.getFileName()) != null) {
				s2 = "Filename: " + temp + "";
			}
		}
		if (part.isMimeType("multipart/alternative")) {
			String s6 = "";
			String s7 = "";
			Multipart mp = (Multipart) part.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				if (mp.getBodyPart(i).isMimeType("text/plain"))
					s7 = getPart(mp.getBodyPart(i), i, 2);
				else if (mp.getBodyPart(i).isMimeType("text/html"))
					s6 = getPart(mp.getBodyPart(i), i, 1);
			}
			if (x == 1) {// html格式的字符串
				s5 = s6;
			}
			if (x == 2) {// paint类型的字符串
				s5 = s7;
			}
			return s5;
		}
		s = s1 + s2;
		return s;
	}

}
