package asm.dt;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Triangle {
	private List<Point> p;

	public Triangle(Point one, Point two, Point three) {
		p = Arrays.asList(new Point[] { one, two, three });
		Collections.sort(p, new Point.SortHorizontallyThenVertically());
	}

	public List<Point> getPoints() {
		return p;
	}

	public boolean sharesSide(Triangle t) {
		List<Point> otherp = t.getPoints();
		int count = 0;
		for (Point pt : p) {
			if (otherp.contains(pt)) {
				count++;
				continue;
			}
		}

		if (count == 2) {
			return true;
		}
		// =0 -> no shared sides/points, =1 -> one shared point, no sides, =3 -> is same triangle
		else {
			return false;
		}
	}

	// within circumcircle identification code based off of an algorithm provided on Wikipedia: http://en.wikipedia.org/wiki/Delaunay_triangulation#Algorithms
	public boolean withinCircumcircle(Point pprime) {

		double det = (p.get(0).getX() - pprime.getX())
				* ((p.get(1).getY() - pprime.getY())
						* (p.get(2).getX() * p.get(2).getX() - pprime.getX() * pprime.getX() + p.get(2).getY() * p.get(2).getY() - pprime.getY()
								* pprime.getY()) - (p.get(1).getX() * p.get(1).getX() - pprime.getX() * pprime.getX() + p.get(1).getY() * p.get(1).getY() - pprime
						.getY() * pprime.getY())
						* (p.get(2).getY() - pprime.getY()))
				- (p.get(1).getX() - pprime.getX())
				* ((p.get(0).getY() - pprime.getY())
						* (p.get(2).getX() * p.get(2).getX() - pprime.getX() * pprime.getX() + p.get(2).getY() * p.get(2).getY() - pprime.getY()
								* pprime.getY()) - (p.get(0).getX() * p.get(0).getX() - pprime.getX() * pprime.getX() + p.get(0).getY() * p.get(0).getY() - pprime
						.getY() * pprime.getY())
						* (p.get(2).getY() - pprime.getY()))
				+ (p.get(2).getX() - pprime.getX())
				* ((p.get(0).getY() - pprime.getY())
						* (p.get(1).getX() * p.get(1).getX() - pprime.getX() * pprime.getX() + p.get(1).getY() * p.get(1).getY() - pprime.getY()
								* pprime.getY()) - (p.get(0).getX() * p.get(0).getX() - pprime.getX() * pprime.getX() + p.get(0).getY() * p.get(0).getY() - pprime
						.getY() * pprime.getY())
						* (p.get(1).getY() - pprime.getY()));

		if (det > 0) {
			return true;
		} else {
			return false;
		}
	}

	// within triangle identification code based off of an algorithm provided by @Kornel Kisielewicz on StackOverflow: answer:
	// http://stackoverflow.com/a/2049593/645647 question: http://stackoverflow.com/questions/2049582/how-to-determine-a-point-in-a-triangle
	public boolean withinTriangle(Point pt) {
		boolean b1, b2, b3;

		b1 = sign(pt, p.get(0), p.get(1)) < 0.0;
		b2 = sign(pt, p.get(1), p.get(2)) < 0.0;
		b3 = sign(pt, p.get(2), p.get(0)) < 0.0;

		return ((b1 == b2) && (b2 == b3));
	}

	private double sign(Point p1, Point p2, Point p3) {
		return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (!(o instanceof Triangle)) {
			return false;
		} else {
			Triangle other = (Triangle) o;
			if (p.equals(other.getPoints())) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return "Triangle(" + p.get(0) + "," + p.get(1) + "," + p.get(2) + ")";
	}

	public void draw(Graphics2D g) {
		g.setStroke(new BasicStroke(3));
		g.drawLine(p.get(0).getX(), p.get(0).getY(), p.get(1).getX(), p.get(1).getY());
		g.drawLine(p.get(0).getX(), p.get(0).getY(), p.get(2).getX(), p.get(2).getY());
		g.drawLine(p.get(1).getX(), p.get(1).getY(), p.get(2).getX(), p.get(2).getY());
	}
}
