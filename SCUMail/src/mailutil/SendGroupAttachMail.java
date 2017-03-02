package mailutil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

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
 * 
 * @author caesar
 *
 */
public class SendGroupAttachMail {
	private String SMTPHost = ""; // SMTP服务器
	private String user = ""; // 登录SMTP服务器的帐号
	private String password = ""; // 登录SMTP服务器的密码

	private String from = ""; // 发件人邮箱
	private String to = ""; // 收件人邮箱
	private String subject = ""; // 邮件标题
	private String content = ""; // 邮件内容
	private String priority = "3"; // 邮件发送优先级 1：紧急 3：普通 5：缓慢
	private String copy_to = null;// 抄送邮件到
	private ArrayList<String> filename = new ArrayList<String>(); // 附件文件名
	private Session mailSession = null;
	private Transport transport = null;

	public SendGroupAttachMail(String SMTPHost, String user, String password, String to, String subject, String content,
			String copy_to, ArrayList<String> filename) {
		this.user = user;
		this.SMTPHost = SMTPHost;
		this.password = password;
		this.from = user;
		this.to = to;
		this.subject = subject;
		this.content = content;
		this.copy_to = copy_to;
		this.filename = (ArrayList<String>) filename.clone();
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
			message.setHeader("X-Priority", this.priority);
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
