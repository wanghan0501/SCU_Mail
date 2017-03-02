package utils;

import frame.AboutUsFrame;
import frame.AddLinkManFrame;
import frame.HelpContentsFrame;
import frame.ReceiveFrame;
import frame.RecycleFrame;
import frame.SendFrame;
import frame.SendGroupMailFrame;
import frame.SendedFrame;

/**
 * 产生主界面上的各种类
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class FrameFactory {
	private static FrameFactory factory = new FrameFactory();

	private FrameFactory() {
	}

	public static FrameFactory getFrameFactory() {
		return factory;
	}

	// 收件箱对象

	public ReceiveFrame getReceiveFrame() {
		return new ReceiveFrame();
	}

	// 已发送邮件对象

	public SendedFrame getSendedFrame() {
		return new SendedFrame();
	}

	// 发送邮件对象

	public SendFrame getSendFrame() {
		return new SendFrame();
	}

	// 回收站对象
	public RecycleFrame getRecycleFrame() {
		return new RecycleFrame();
	}

	// 联系人列表
	public AddLinkManFrame getAddLinkManFrame() {
		return new AddLinkManFrame();
	}
	
	// 帮助文档对象
	public HelpContentsFrame getHelpContentsFrame(){
		return new HelpContentsFrame();
	}
	
	// 关于我们对象
	public AboutUsFrame getAboutUsFrame(){
		return new AboutUsFrame();
	}
	
	// 发送群邮件对象
	public SendGroupMailFrame getSendGroupMailFrame(){
		return new SendGroupMailFrame();
	}
}
