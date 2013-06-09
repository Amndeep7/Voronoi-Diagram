package asm.dt;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Point {
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

	public int compareTo(Point o, Comparator<Point> comparator) {
		return comparator.compare(this, o);
	}

	public static class SortHorizontallyThenVertically implements Comparator<Point> {
		public int compare(Point a, Point b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			int xDifference = a.getX() - b.getX();

			if (xDifference == 0) {
				return -(a.getY() - b.getY());
			} else {
				return -xDifference;
			}
		}
	}

	public static class SortReverseHorizontallyThenVertically implements Comparator<Point> {
		public int compare(Point a, Point b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			int xDifference = a.getX() - b.getX();

			if (xDifference == 0) {
				return -(a.getY() - b.getY());
			} else {
				return xDifference;
			}
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
		int numPoints = 5;
		for (int x = 0; x < numPoints; x++) {
			points.add(new Point((int) (Math.random() * 5), (int) (Math.random() * 5)));
		}
		System.out.print("Y\n|");
		for (int y = 4; y >= 0; y--) {
			for (int x = 0; x < 5; x++) {
				if (points.contains(new Point(x, y))) {
					System.out.print("#");
				} else {
					System.out.print("*");
				}
			}
			System.out.print("\n|");
		}
		System.out.print("-----X\n");

		System.out.println(points);
		Collections.sort(points, new SortVerticallyThenHorizontally());
		System.out.println(points);
		Collections.sort(points, new SortVerticallyThenReverseHorizontally());
		System.out.println(points);
		Collections.sort(points, new SortHorizontallyThenVertically());
		System.out.println(points);
		Collections.sort(points, new SortReverseHorizontallyThenVertically());
		System.out.println(points);
	}
}
