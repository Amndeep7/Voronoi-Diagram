package asm.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import asm.vd.Point;
import asm.vd.VoronoiDiagram;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1187924516205302028L;

	public final int FRAMEX;
	public final int FRAMEY;

	private final String filename;
	private final BufferedImage givenImage;
	private final BufferedImage viewImage;
	private final Graphics viewBuffer;

	private Timer view;

	private VoronoiDiagram vd;

	public Panel(String fn) throws IOException {
		filename = fn;
		givenImage = ImageIO.read(new File(filename));

		FRAMEX = givenImage.getWidth();
		FRAMEY = givenImage.getHeight();

		viewImage = new BufferedImage(FRAMEX, FRAMEY, BufferedImage.TYPE_INT_RGB);
		viewBuffer = viewImage.getGraphics();
		viewBuffer.setColor(Color.BLACK);
		viewBuffer.fillRect(0, 0, FRAMEX, FRAMEY);

		addMouseListener(new Mouse());
		addKeyListener(new Keyboard());

		view = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});

		vd = new VoronoiDiagram(givenImage);

		view.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		viewBuffer.setColor(Color.BLACK);
		viewBuffer.fillRect(0, 0, FRAMEX, FRAMEY);

		vd.draw(viewBuffer);

		g.drawImage(viewImage, 0, 0, FRAMEX, FRAMEY, 0, 0, viewImage.getWidth(), viewImage.getHeight(), null);
	}

	public static File makeImageFile(String filename, BufferedImage image) throws IOException {
		File file = new File(filename.substring(0, filename.lastIndexOf(".")) + System.nanoTime() + ".png");

		ImageIO.write(image, "png", file);

		return file;
	}

	private class Mouse extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			switch (e.getButton()) {
			case 1: {// left click
				vd.addSector(e.getX(), e.getY());
				break;
			}
			case 3: {// right click
				vd.removeSector(e.getX(), e.getY());
				break;
			}
			default: {
				// nothing cause something is seriously messed up if this case
				// comes about
			}
			}
		}
	}

	private class Keyboard extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			switch (e.getKeyChar()) {
			case 'q': {
				vd.addSectors(1);
				break;
			}
			case 'w': {
				vd.addSectors(5);
				break;
			}
			case 'e': {
				vd.addSectors(10);
				break;
			}
			case 'r': {
				vd.addSectors(100);
				break;
			}
			case 'c': {
				vd.removeAllSectors();
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			case 's': {
				vd.sectorize();
				System.out.println(System.nanoTime());
				break;
			}
			case 'd': {
				Point.flipDisplay();
				break;
			}
			case 'f': {
				VoronoiDiagram.flipPointDisplay();
				break;
			}
			case 'a': {
				view.stop();
				try {
					makeImageFile(filename, viewImage);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				view.start();
			}
			default: {
				break;
			}
			}
		}
	}
}