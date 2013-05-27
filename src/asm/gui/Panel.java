package asm.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import asm.dt.Connection;
import asm.dt.DelaunayTriangulation;
import asm.dt.Point;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1187924516205302028L;

	Random random = new Random();

	public final int FRAMEX;
	public final int FRAMEY;

	BufferedImage givenImage;
	BufferedImage myImage;
	BufferedImage viewImage;
	Graphics viewBuffer;

	ArrayList<Point> points;
	HashSet<Connection> connections;

	boolean transformation = false;

	public Panel(String filename) throws IOException {
		givenImage = ImageIO.read(new File(filename));

		FRAMEX = givenImage.getWidth();
		FRAMEY = givenImage.getHeight();

		myImage = new BufferedImage(FRAMEX, FRAMEY, BufferedImage.TYPE_INT_ARGB);

		viewImage = new BufferedImage(FRAMEX, FRAMEY, BufferedImage.TYPE_INT_ARGB);
		viewBuffer = viewImage.getGraphics();

		points = new ArrayList<Point>();
		connections = new HashSet<Connection>();

		addMouseListener(new Mouse());
		addKeyListener(new Keyboard());

		Timer view = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		view.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (transformation) {
			viewBuffer.drawImage(myImage, 0, 0, viewImage.getWidth(), viewImage.getHeight(), 0, 0, myImage.getWidth(), myImage.getHeight(), null);
		} else {
			viewBuffer.drawImage(givenImage, 0, 0, viewImage.getWidth(), viewImage.getHeight(), 0, 0, givenImage.getWidth(), givenImage.getHeight(), null);

			for (Point p : points) {
				p.draw(viewBuffer);
			}
			for (Connection c : connections) {
				c.draw(viewBuffer);
			}
		}

		g.drawImage(viewImage, 0, 0, FRAMEX, FRAMEY, 0, 0, givenImage.getWidth(), givenImage.getHeight(), null);
	}

	private class Mouse extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			switch (e.getButton()) {
				case 1: {// left click
					points.add(new Point(e.getX(), e.getY()));
					break;
				}
				case 3: {// right click
					if (points.size() > 0) {
						points.remove(points.size() - 1);
					}
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
				case 'f': {
					for (int x = 0; x < 4; x++) {
						points.add(new Point(random.nextInt(FRAMEX), random.nextInt(FRAMEY)));
					}
					break;
				}
				case 'c': {
					points.clear();
					connections.clear();
					break;
				}
				case 't': {
					connections = DelaunayTriangulation.triangulate(points);
					System.out.println(connections);
					break;
				}
				default: {
					break;
				}
			}
		}
	}
}