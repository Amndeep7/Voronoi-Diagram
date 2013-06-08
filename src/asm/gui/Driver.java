package asm.gui;

import java.awt.FileDialog;
import javax.swing.JFrame;

public class Driver {

	public static void main(String[] args) throws Exception {
		FileDialog fd = new FileDialog(new JFrame("Frame"), "Picture to use for Delaunay Triangulation", FileDialog.LOAD);
		fd.setMultipleMode(false);
		fd.setVisible(true);
		Panel p = new Panel(fd.getDirectory() + fd.getFile());

		JFrame frame = new JFrame("Delaunay Triangulation");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(p);

		p.requestFocusInWindow();
		frame.pack();
		frame.validate();
		frame.setVisible(true);

		String OS = System.getProperty("os.name");
		if (OS.contains("Windows")) {
			frame.setSize(p.FRAMEX + frame.getInsets().left + frame.getInsets().right, p.FRAMEY + frame.getInsets().top + frame.getInsets().bottom);
		} else {// if OS.contains("Linux")
			frame.setSize(p.FRAMEX, p.FRAMEY);
		}
		frame.setLocationRelativeTo(null);
		p.requestFocusInWindow();
	}
}