package asm.dt;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;

public class Connection {
	Point a, b;

	public Connection(Point one, Point two) {
		if (one.compareTo(two, new Point.SortHorizontallyThenVertically()) < 0) {
			a = one;
			b = two;
		} else {
			b = one;
			a = two;
		}
	}

	public Point getA() {
		return a;
	}

	public Point getB() {
		return b;
	}

	@Override
	public String toString() {
		return getA().toString() + "->" + getB().toString();
	}

	public void draw(Graphics viewBuffer) {
		((Graphics2D) viewBuffer).setStroke(new BasicStroke(3));
		viewBuffer.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Connection)) {
			return false;
		} else {
			Connection other = (Connection) o;
			return getA().equals(other.getA()) && getB().equals(other.getB());
		}
	}
	
	@Override
	public int hashCode(){
		return a.hashCode()*b.hashCode()+b.hashCode();
	}

	public static class SortByMin implements Comparator<Connection> {
		public int compare(Connection a, Connection b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			return a.getA().compareTo(b.getA(), new Point.SortHorizontallyThenVertically());
		}
	}

	public static class SortByMax implements Comparator<Connection> {
		public int compare(Connection a, Connection b) {
			if (a == null || b == null) {
				throw new NullPointerException();
			}

			return a.getB().compareTo(b.getB(), new Point.SortHorizontallyThenVertically());
		}
	}
}
