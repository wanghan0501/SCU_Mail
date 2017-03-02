package mailutil;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import utils.CheckNewMail;

public class CheckNewMialUtil {
	private String POP3Host = ""; // POP3服务器
	private String user = ""; // 登录POP3服务器的帐号
	private String password = ""; // 登录POP3服务器的密码

	private Session session = null;
	private Folder folder = null;
	private Store store = null;
	private GetMail getMail = GetMail.getMailInstantiate();

	public CheckNewMialUtil() {
		POP3Host = getMail.getPOP3Host();
		user = getMail.getUser();
		password = getMail.getPassword();
	}

	// 连接邮件服务器
	public void connect() {
		// 创建一个授权验证对象
		SmtpPop3Auth auth = new SmtpPop3Auth();
		auth.setAccount(user, password);

		// 取得一个Session对象
		Properties prop = new Properties();
		prop.put("mail.pop3.host", POP3Host);
		session = Session.getDefaultInstance(prop, auth);

		// 取得一个Store对象
		try {
			store = session.getStore("pop3");
			store.connect(POP3Host, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public static boolean isCheck = true;

	// 检测新邮件
	public int checkNewMail() {
		int count = 0;
		connect();
		try {
			folder = store.getDefaultFolder().getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			count = folder.getMessageCount();
			if (isCheck) {
				CheckNewMail.setNewMailCount(count);
				isCheck = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnect();
		}
		return count;
	}
}
