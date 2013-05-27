package asm.dt;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Point implements Comparable<Point> {
	int x, y;

	public Point(int xPos, int yPos) {
		x = xPos;
		y = yPos;
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
		g.drawString("(" + getX() + "," + getY() + ")", getX() + 3, getY());
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

	// default is horizontal sort
	public int compareTo(Point o) {
		if (o == null) {
			throw new NullPointerException();
		}

		int xDifference = o.getX() - this.getX();

		if (xDifference == 0) {
			return -(o.getY() - this.getY());
		} else {
			return -xDifference;
		}
	}

	public static class SortVerticallyThenHorizontally implements Comparator<Point> {
		public int compare(Point a, Point b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			int yDifference = a.getY() - b.getY();

			if (yDifference == 0) {
				return -(a.getX() - b.getX());
			} else {
				return -yDifference;
			}
		}
	}

	public static class SortVerticallyThenReverseHorizontally implements Comparator<Point> {
		public int compare(Point a, Point b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			int yDifference = a.getY() - b.getY();

			if (yDifference == 0) {
				return (a.getX() - b.getX());
			} else {
				return -yDifference;
			}
		}
	}

	public static void main(String[] args) {
		List<Point> points = new ArrayList<Point>();
		for (int x = 0; x < 5; x++) {
			points.add(new Point((int) (Math.random() * 5), (int) (Math.random() * 5)));
		}
		System.out.println(points);
		Collections.sort(points, new SortVerticallyThenHorizontally());
		System.out.println(points);
		Collections.sort(points, new SortVerticallyThenReverseHorizontally());
		System.out.println(points);
	}
}
