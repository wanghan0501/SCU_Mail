package utils;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.swing.ImageIcon;

import frame.MainFrame;

/**
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class EditorUtils {
	public static boolean checkEmailAdress(String input){
		Pattern pattern = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	
	public static ImageIcon createIcon(String ImageName) {
		ImageIcon icon = null;
		try {
			if (ImageName.indexOf(":") == -1) {
			URL IconUrl = MainFrame.class.getResource("/" + ImageName);
			icon = new ImageIcon(IconUrl);
			} else {
				icon = new ImageIcon(ImageName);
			}
		} catch (Exception e) {
			System.out.println("Õº∆¨   " + ImageName + "  º”‘ÿ ß∞‹£°");
			e.printStackTrace();
		}
		return icon;
	}
}
