package utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * 
 * @author caesar
 * @version Copyright(C) SCU. 2016
 */
public class EditorPopupMenu {

	/** 添加剪切侦听器 */
	private Action cutAction = new DefaultEditorKit.CutAction();
	/** 添加复制侦听器 */
	private Action copyAction = new DefaultEditorKit.CopyAction();
	/** 添加粘贴侦听器 */
	private Action pasteAction = new DefaultEditorKit.PasteAction();
	/** 添加撤消管理器 */
	protected UndoManager undoMenager = new UndoManager();
	/** 添加撤消侦听器 */
	private UndoAction undoAction = new UndoAction();
	/** 添加恢复侦听器 */
	private RedoAction redoAction = new RedoAction();
	/** 侦听在当前文档上的编辑器 */
	protected UndoableEditListener undoHandler = new UndoHandler();
	private JPopupMenu popup = null;
	private JMenuItem copy = null;
	private JMenuItem paste = null;
	private JMenuItem cut = null;
	private JMenuItem undo = null;
	private JMenuItem redo = null;
	private JMenuItem insertIcon = null;

	public EditorPopupMenu(final JTextPane textPane) {
		textPane.getDocument().addUndoableEditListener(undoHandler);
		popup = new JPopupMenu();
		copy = new JMenuItem(copyAction);
		copy.setText("复制");
		copy.setIcon(EditorUtils.createIcon("copy.png"));
		paste = new JMenuItem(pasteAction);
		paste.setText("粘贴");
		paste.setIcon(EditorUtils.createIcon("paste.png"));
		cut = new JMenuItem(cutAction);
		cut.setText("剪切");
		cut.setIcon(EditorUtils.createIcon("cut.png"));
		undo = new JMenuItem(undoAction);
		undo.setText("撤销");
		undo.setIcon(EditorUtils.createIcon("undo.png"));
		redo = new JMenuItem(redoAction);
		redo.setText("重做");
		redo.setIcon(EditorUtils.createIcon("redo.png"));
		insertIcon = new JMenuItem();
		insertIcon.setText("插入图片");
		insertIcon.setIcon(EditorUtils.createIcon("image.png"));
		insertIcon.addActionListener(new ActionListener() {// 插入图片事件
					public void actionPerformed(ActionEvent arg0) {
						File f = new File(".");// 得到当前目录
						JFileChooser chooser = new JFileChooser(f);// 构造一个当前路径的文件选择器
						if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {// 如果选择确定键
							File file = chooser.getSelectedFile();
							textPane.insertIcon(EditorUtils.createIcon(file
									.getAbsolutePath()));
						}
					}
				});
		popup.add(copy);
		popup.add(paste);
		popup.add(cut);
		popup.addSeparator();// 插入分隔符
		popup.add(undo);
		popup.add(redo);
		popup.addSeparator();// 插入分隔符
		popup.add(insertIcon);
	}

	// 编辑区鼠标右键的响应
	public void rightMouseButton(MouseEvent e) {
		popup.show(e.getComponent(), e.getX(), e.getY());// 显示弹出菜单
	}

	// 撤销
	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoMenager.undo();
			} catch (CannotUndoException ex) {
				System.out.println("Unable to undo: " + ex);
				ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if (undoMenager.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undoMenager.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "撤销");
			}
		}
	}

	// 重做
	class RedoAction extends AbstractAction {

		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoMenager.redo();
			} catch (CannotRedoException ex) {
				System.err.println("Unable to redo: " + ex);
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update() {
			if (undoMenager.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undoMenager.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "重做");
			}
		}
	}

	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undoMenager.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
		}
	}
}
