package mailutil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 发送附件
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class SendAttachMail {

	private String SMTPHost = ""; // SMTP服务器
	private String user = ""; // 登录SMTP服务器的帐号
	private String password = ""; // 登录SMTP服务器的密码

	private String from = ""; // 发件人邮箱
	private Address[] to = null; // 收件人邮箱
	private String subject = ""; // 邮件标题
	private String content = ""; // 邮件内容
	private String priority ="3"; // 邮件发送优先级 1：紧急 3：普通 5：缓慢
	private Address[] copy_to = null;// 抄送邮件到
	private Session mailSession = null;
	private Transport transport = null;
	private ArrayList<String> filename = new ArrayList<String>(); // 附件文件名
	private static SendAttachMail sendMail = new SendAttachMail();

	// 无参数构造方法
	private SendAttachMail() {
	}

	// 返回本类对象的实例
	public static SendAttachMail getSendMailInstantiate() {
		return sendMail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		try {
			// 解决内容的中文问题
			content = new String(content.getBytes("ISO8859-1"), "gbk");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.content = content;
	}

	public String getPriority(){
		return priority;
	}
	
	public void SetPriority(String priority) {
		this.priority = priority;
	}
	
	public ArrayList<String> getFilename() {
		return filename;
	}

	public void setFilename(ArrayList<String> filename) {
		Iterator<String> iterator = filename.iterator();
		ArrayList<String> attachArrayList = new ArrayList<String>();
		while (iterator.hasNext()) {
			String attachment = iterator.next();
			try {
				// 解决文件名的中文问题
				attachment = MimeUtility.decodeText(attachment);
				// 将文件路径中的'\'替换成'/'
				attachment = attachment.replaceAll("\\\\", "/");
				attachArrayList.add(attachment);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		this.filename = attachArrayList;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSMTPHost() {
		return SMTPHost;
	}

	public void setSMTPHost(String host) {
		SMTPHost = host;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		try {
			// 解决标题的中文问题
			subject = MimeUtility.encodeText(subject);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.subject = subject;
	}

	public Address[] getTo() {
		return to;
	}

	public void setTo(String toto) {
		int i = 0;
		StringTokenizer tokenizer = new StringTokenizer(toto, ";");
		to = new Address[tokenizer.countTokens()];// 动态的决定数组的长度
		while (tokenizer.hasMoreTokens()) {
			String d = tokenizer.nextToken();
			try {
				d = MimeUtility.encodeText(d);
				to[i] = new InternetAddress(d);// 将字符串转换为整型
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Address[] getCopy_to() {
		return copy_to;
	}

	// 设置抄送
	public void setCopy_to(String copyTo) {
		int i = 0;
		StringTokenizer tokenizer = new StringTokenizer(copyTo, ";");
		copy_to = new Address[tokenizer.countTokens()];// 动态的决定数组的长度
		while (tokenizer.hasMoreTokens()) {
			String d = tokenizer.nextToken();
			try {
				d = MimeUtility.encodeText(d);
				copy_to[i] = new InternetAddress(d);// 将字符串转换为整型
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	public void connect() throws Exception {

		// 创建一个属性对象
		Properties props = new Properties();
		// 指定SMTP服务器
		props.put("mail.smtp.host", SMTPHost);
		// 指定是否需要SMTP验证
		props.put("mail.smtp.auth", "true");
		// 创建一个授权验证对象
		SmtpPop3Auth auth = new SmtpPop3Auth();
		auth.setAccount(user, password);
		// 创建一个Session对象
		mailSession = Session.getDefaultInstance(props, auth);
		// 设置是否调试
		mailSession.setDebug(false);
		if (transport != null)
			transport.close();// 关闭连接
		// 创建一个Transport对象
		transport = mailSession.getTransport("smtp");
		// 连接SMTP服务器
		transport.connect(SMTPHost, user, password);
	}

	// 发送邮件
	public String send() {
		String issend = "";
		try {// 连接smtp服务器
			connect();
			// 创建一个MimeMessage 对象
			MimeMessage message = new MimeMessage(mailSession);

			// 指定发件人邮箱
			message.setFrom(new InternetAddress(from));
			// 指定收件人邮箱
			message.addRecipients(Message.RecipientType.TO, to);
			if (!"".equals(copy_to))
				// 指定抄送人邮箱
				message.addRecipients(Message.RecipientType.CC, copy_to);
			// 指定邮件主题
			message.setSubject(subject);
			// 指定邮件发送日期
			message.setSentDate(new Date());
			// 指定邮件优先级 1：紧急 3：普通 5：缓慢
			message.setHeader("X-Priority",this.priority);
			message.saveChanges();
			// 判断附件是否为空
			if (!filename.isEmpty()) {
				// 新建一个MimeMultipart对象用来存放多个BodyPart对象
				Multipart container = new MimeMultipart();
				// 新建一个存放信件内容的BodyPart对象
				BodyPart textBodyPart = new MimeBodyPart();
				// 给BodyPart对象设置内容和格式/编码方式
				textBodyPart.setContent(content, "text/html;charset=gbk");
				// 将含有信件内容的BodyPart加入到MimeMultipart对象中
				container.addBodyPart(textBodyPart);
				Iterator<String> fileIterator = filename.iterator();
				while (fileIterator.hasNext()) {// 迭代所有附件
					String attachmentString = fileIterator.next();
					// 新建一个存放信件附件的BodyPart对象
					BodyPart fileBodyPart = new MimeBodyPart();
					// 将本地文件作为附件
					FileDataSource fds = new FileDataSource(attachmentString);
					fileBodyPart.setDataHandler(new DataHandler(fds));
					// 处理邮件中附件文件名的中文问题
					String attachName = fds.getName();
					attachName = MimeUtility.encodeText(attachName);
					// 设定附件文件名
					fileBodyPart.setFileName(attachName);
					// 将附件的BodyPart对象加入到container中
					container.addBodyPart(fileBodyPart);
				}
				// 将container作为消息对象的内容
				message.setContent(container);
			} else {// 没有附件的情况
				message.setContent(content, "text/html;charset=gbk");
			}
			// 发送邮件
			Transport.send(message, message.getAllRecipients());
			if (transport != null)
				transport.close();
		} catch (Exception ex) {
			issend = ex.getMessage();
		}
		return issend;
	}
}
