package asm.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import asm.dt.DelaunayTriangulation;
import asm.dt.Point;
import asm.dt.Triangle;

public class Panel extends JPanel {
	private static final long serialVersionUID = 1187924516205302028L;

	public final int FRAMEX;
	public final int FRAMEY;

	BufferedImage givenImage;
	BufferedImage myImage;
	BufferedImage viewImage;
	Graphics viewBuffer;

	ArrayList<Point> points;
	HashMap<Triangle, ArrayList<Triangle>> triangles;

	boolean transformation = false;

	public Panel(String filename) throws IOException {
		givenImage = ImageIO.read(new File(filename));

		FRAMEX = givenImage.getWidth();
		FRAMEY = givenImage.getHeight();

		System.out.println("FRAMEX = " + FRAMEX);
		System.out.println("FRAMEY = " + FRAMEY);

		myImage = new BufferedImage(FRAMEX, FRAMEY, BufferedImage.TYPE_INT_ARGB);

		viewImage = new BufferedImage(FRAMEX, FRAMEY, BufferedImage.TYPE_INT_ARGB);
		viewBuffer = viewImage.getGraphics();

		points = new ArrayList<Point>();
		triangles = new HashMap<Triangle, ArrayList<Triangle>>();

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
			for (Triangle t : triangles.keySet()) {
				t.draw((Graphics2D) viewBuffer);
			}
		}

		g.drawImage(viewImage, 0, 0, FRAMEX, FRAMEY, 0, 0, givenImage.getWidth(), givenImage.getHeight(), null);
	}

	private class Mouse extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			switch (e.getButton()) {
			case 1: {// left click
				points.add(new Point(e.getX() == 0 ? e.getX() + 1 : e.getX() == FRAMEX - 1 ? e.getX() - 1 : e.getX(), e.getY() == 0 ? e.getY() + 1
						: e.getY() == FRAMEX - 1 ? e.getY() - 1 : e.getY()));
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
		private int numAlreadySorted = 0;

		public void keyTyped(KeyEvent e) {
			switch (e.getKeyChar()) {
			case 'f': {
				Point upperLeft = new Point(0, 0);
				Point upperRight = new Point(FRAMEX - 1, 0);
				Point lowerLeft = new Point(0, FRAMEY - 1);
				Point lowerRight = new Point(FRAMEX - 1, FRAMEY - 1);

				Triangle one = new Triangle(upperLeft, upperRight, lowerLeft);
				Triangle two = new Triangle(lowerLeft, lowerRight, upperRight);

				points.add(upperLeft);
				points.add(upperRight);
				points.add(lowerLeft);
				points.add(lowerRight);
				triangles.put(one, new ArrayList<Triangle>());
				triangles.put(two, new ArrayList<Triangle>());
				triangles.get(one).add(two);
				triangles.get(two).add(one);

				numAlreadySorted = 4;
				break;
			}
			case 'd': {

				for (int x = 0; x < 1; x++) {
					points.add(new Point((int) (Math.random() * (FRAMEX - 2) + 1), (int) (Math.random() * (FRAMEY - 2) + 1)));
				}
				break;
			}
			case 's': {

				for (int x = 0; x < 5; x++) {
					points.add(new Point((int) (Math.random() * (FRAMEX - 2) + 1), (int) (Math.random() * (FRAMEY - 2) + 1)));
				}
				break;
			}
			case 'a': {

				for (int x = 0; x < 10; x++) {
					points.add(new Point((int) (Math.random() * (FRAMEX - 2) + 1), (int) (Math.random() * (FRAMEY - 2) + 1)));
				}
				break;
			}
			case 'c': {
				points.clear();
				triangles.clear();
				System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
				break;
			}
			case 't': {
				try {
					DelaunayTriangulation.triangulate(triangles, points.subList(numAlreadySorted, points.size()));
					numAlreadySorted = points.size();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				System.out.println(triangles);
				break;
			}
			default: {
				break;
			}
			}
		}
	}
}