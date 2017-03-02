package utils.bickerTray;

import java.io.InputStream;
import java.net.URL;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * 类说明：播放音乐类
 */
public class MyAudioPlayer {
	private URL url = null;// 音乐文件的URl
	private AudioStream as = null;// 播放器

	public MyAudioPlayer() {
		try {
			url = MyAudioPlayer.class.getResource("/newmail.wav");// 获取音乐文件的url
			InputStream is = url.openStream();// 获得音乐文件的输入流
			as = new AudioStream(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 播放音乐
	public void play() {
		AudioPlayer.player.start(as);// 用AudioPlayer静态成员player.start播放音乐
	}
}
