package asm.vd;

import java.awt.Color;
import java.awt.Graphics;

public class Point {
	private static boolean displayCoordinates = false;

	private int x, y;

	public Point(int xPos, int yPos) {
		x = xPos;
		y = yPos;
	}

	public static void flipDisplay() {
		displayCoordinates = !displayCoordinates;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval(getX() - 5, getY() - 5, 10, 10);
		if (displayCoordinates) {
			g.drawString("(" + getX() + "," + getY() + ")", getX() + 3, getY());
		}
	}

	@Override
	public String toString() {
		return "Point:X " + getX() + " Y " + getY();
	}

	@Override
	public int hashCode() {
		return getX() * getY() + getY();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Point)) {
			return false;
		} else {
			Point other = (Point) o;
			if (other.getX() == this.getX() && other.getY() == this.getY()) {
				return true;
			} else {
				return false;
			}
		}
	}
}
