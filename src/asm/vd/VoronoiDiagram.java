package asm.vd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

public class VoronoiDiagram {
	private static boolean displayCoordinates = false;

	private final BufferedImage givenImage;
	private final int FRAMEX, FRAMEY;

	private HashMap<Point, Sector> sectors;

	public VoronoiDiagram(BufferedImage gI) {
		givenImage = gI;
		FRAMEX = givenImage.getWidth();
		FRAMEY = givenImage.getHeight();

		sectors = new HashMap<Point, Sector>();
	}

	public boolean addSector(int x, int y) {
		Point p = new Point(x, y);
		if (sectors.containsKey(p)) {
			return false;
		} else {
			sectors.put(p, new Sector(new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256))));
			return true;
		}
	}

	public void addSectors(int num) {
		for (int x = 0; x < num; x++) {
			while (!addSector((int) (Math.random() * (FRAMEX - 2) + 1), (int) (Math.random() * (FRAMEY - 2) + 1))) {
			}
		}
	}

	public void removeSector(int x, int y) {
		sectors.remove(new Point(x, y));
	}

	public void removeAllSectors() {
		sectors.clear();
	}

	public static void flipPointDisplay() {
		displayCoordinates = !displayCoordinates;
	}

	public void draw(Graphics g) {
		for (Sector s : sectors.values()) {
			s.draw(g);
		}

		if (displayCoordinates) {
			for (Point p : sectors.keySet()) {
				p.draw(g);
			}
		}
	}

	public void clean() {
		for (Sector s : sectors.values()) {
			s.clear();
		}
	}

	public void average() {
		for (Sector s : sectors.values()) {
			s.average(givenImage);
		}
	}

	public void sectorize() {
		clean();

		Set<Point> points = sectors.keySet();
		for (int y = 0; y < FRAMEY; y++) {
			for (int x = 0; x < FRAMEX; x++) {
				Point closest = null;
				int distance = Integer.MAX_VALUE;
				for (Point p : points) {
					int newDistance = (p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y);
					if (newDistance < distance) {
						distance = newDistance;
						closest = p;
					}
				}
				sectors.get(closest).addPixel(x, y);
			}
		}

		average();
	}
}
