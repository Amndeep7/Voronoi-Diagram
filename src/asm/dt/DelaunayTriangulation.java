package asm.dt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

//implemented as described at http://www.geom.uiuc.edu/~samuelp/del_project.html#algorithms
public class DelaunayTriangulation {

	public static HashSet<Connection> triangulate(List<Point> points) {
		Collections.sort(points);
		return dt(points);
	}

	private static HashSet<Connection> dt(List<Point> points) {
		HashSet<Connection> connections = new HashSet<Connection>();
		if (points.size() <= 3) {
			System.out.println("entered if");

			associate(points, connections);

			System.out.println("connections size after if " + connections.size());
		} else {
			System.out.println("entered else with size " + points.size());

			List<Point> left = points.subList(0, points.size() / 2 + points.size() % 2);
			List<Point> right = points.subList(points.size() / 2 + points.size() % 2, points.size());

			System.out.println("going left with size " + left.size());
			HashSet<Connection> leftConnections = dt(left);
			System.out.println("back from left\ngoing right with size " + right.size());
			HashSet<Connection> rightConnections = dt(right);
			System.out.println("Back from right");
			connections.addAll(leftConnections);
			connections.addAll(rightConnections);

			System.out.println("connections size after dt " + connections.size());

			merge(left, right, leftConnections, rightConnections, connections, null, null);

			System.out.println("connections size after merge " + connections.size());
		}

		System.out.println("connections size when leaving dt " + connections.size());
		return connections;
	}

	private static void merge(List<Point> left, List<Point> right, HashSet<Connection> leftConnections, HashSet<Connection> rightConnections,
			HashSet<Connection> connections, Point baseLeft, Point baseRight) {
		List<Point> left2 = new ArrayList<Point>(left);
		List<Point> right2 = new ArrayList<Point>(right);

		Collections.sort(left2, new Point.SortVerticallyThenHorizontally());
		Collections.sort(right2, new Point.SortVerticallyThenReverseHorizontally());

		System.out.println("Sorted left2 " + left2 + " right2 " + right2);

		// create base LR-edge if necessary
		if (baseLeft == null) {
			Point[] bases = createBaseLREdge(left2, right2, leftConnections, rightConnections, connections, baseLeft, baseRight);
			baseLeft = bases[0];
			baseRight = bases[1];
			System.out.println("made a base: bL " + baseLeft + " bR " + baseRight);
		}

		// make LR-edges

		List<Point> modifiableLeft = new ArrayList<Point>(left);
		List<Point> modifiableRight = new ArrayList<Point>(right);

		boolean makeEdges = true;
		while (makeEdges) {
			Point leftFC = createFinalCandidate(true, modifiableLeft, modifiableRight, baseLeft, baseRight, leftConnections, rightConnections, connections);
			Point rightFC = createFinalCandidate(false, modifiableLeft, modifiableRight, baseLeft, baseRight, leftConnections, rightConnections, connections);

			System.out.println("leftFC " + leftFC + " rightFC " + rightFC);

			if (leftFC == null) {
				if (rightFC == null) {
					makeEdges = false;
				} else {
					associate(baseLeft, rightFC, connections);
					modifiableRight.remove(baseRight);
					baseRight = rightFC;
				}
			} else {
				if (rightFC == null) {
					associate(baseRight, leftFC, connections);
					modifiableLeft.remove(baseLeft);
					baseLeft = leftFC;
				} else {
					if (withinCircumcircle(baseLeft, baseRight, rightFC, leftFC)) {
						associate(baseRight, leftFC, connections);
						modifiableLeft.remove(baseLeft);
						baseLeft = leftFC;
					} else {
						associate(baseLeft, rightFC, connections);
						modifiableRight.remove(baseRight);
						baseRight = rightFC;
					}
				}
			}
		}
	}

	// returns null if there is none
	private static Point createFinalCandidate(boolean isLeft, List<Point> left, List<Point> right, Point baseLeft, Point baseRight,
			HashSet<Connection> leftConnections, HashSet<Connection> rightConnections, HashSet<Connection> connections) {
		System.out.println("entered FC with isLeft = " + isLeft);
		System.out.println("bL " + baseLeft + " bR " + baseRight);

		List<Point> newList = new ArrayList<Point>(isLeft ? left : right);

		if (newList.size() == 1) {
			System.out.println("exited because too small");
			return null;
		}

		Point baseLine = isLeft ? new Point(baseRight.getX() - baseLeft.getX(), baseRight.getY() - baseLeft.getY()) : new Point(baseLeft.getX()
				- baseRight.getX(), baseLeft.getY() - baseRight.getY());

		SortByLeastAngle.setBaseLine(baseLine);
		SortByLeastAngle.setVertix(isLeft ? baseLeft : baseRight);

		Collections.sort(newList, new SortByLeastAngle());
		newList = new LinkedList<Point>(newList);
		for (Point p : newList) {
			System.out.println(p);
		}

		Point pc = newList.remove(isLeft ? 0 : newList.size() - 1);
		Point npc = newList.remove(isLeft ? 0 : newList.size() - 1);

		System.out.println("pc " + pc + " npc " + npc);
		System.out.println("current angle = " + Math.toDegrees(SortByLeastAngle.getAngle(pc)));
		while (isLeft ? SortByLeastAngle.getAngle(pc) < Math.PI : SortByLeastAngle.getAngle(pc) > Math.PI) {
			System.out.println("Did you even enter the while loop?");
			if (pc.equals(isLeft ? baseLeft : baseRight)) {
				System.out.println("pc equals a base");
				pc = npc;
				if (newList.size() == 0) {
					System.out.println("and out of new options with the angle being " + Math.toDegrees(SortByLeastAngle.getAngle(pc)));
					if (isLeft ? (SortByLeastAngle.getAngle(pc) + Math.PI) > Math.PI : (SortByLeastAngle.getAngle(pc) + Math.PI) < Math.PI) {
						System.out.println("so returning npc");
						return pc;
					} else {
						System.out.println("so returning null");
						break;
					}
				}
				System.out.println("but we still have spares");
				npc = newList.remove(isLeft ? 0 : newList.size() - 1);
			} else if (withinCircumcircle(baseLeft, baseRight, pc, npc)) {
				Connection badEdge = new Connection(baseRight, pc);
				System.out.print("npc is in the circumcircle, removing " + badEdge);

				if (isLeft) {
					System.out.print(" from the left\n");
					leftConnections.remove(badEdge);
				} else {
					System.out.print(" from the right\n");
					rightConnections.remove(badEdge);
				}
				connections.remove(badEdge);

				pc = npc;
				if (newList.size() == 0) {
					System.out.println("and out of options so returning null");
					break;
				}
				npc = newList.remove(isLeft ? 0 : newList.size() - 1);
			} else {
				System.out.println("everything worked so returning " + pc);
				return pc;
			}
		}
		System.out.println("returning null");
		return null;
	}

	// circumcircle identification code based off of Dave Watson and William Flis' responses from http://www.ics.uci.edu/~eppstein/junkyard/circumcenter.html
	public static boolean withinCircumcircle(Point baseLeft, Point baseRight, Point pc, Point npc) {
		double d = 2 * (baseLeft.getX() - pc.getX()) * (baseRight.getY() - pc.getY()) - (baseRight.getX() - pc.getX()) * (baseLeft.getY() - pc.getY());

		double centerX = (((baseLeft.getX() - pc.getX()) * (baseLeft.getX() + pc.getX()) + (baseLeft.getY() - pc.getY()) * (baseLeft.getY() + pc.getY()))
				* (baseRight.getY() - pc.getY()) - ((baseRight.getX() - pc.getX()) * (baseRight.getX() + pc.getX()) + (baseRight.getY() - pc.getY())
				* (baseRight.getY() + pc.getY()))
				* (baseLeft.getY() - pc.getY()))
				/ d;

		double centerY = (((baseRight.getX() - pc.getX()) * (baseRight.getX() + pc.getX()) + (baseRight.getY() - pc.getY()) * (baseRight.getY() + pc.getY()))
				* (baseLeft.getX() - pc.getX()) - ((baseLeft.getX() - pc.getX()) * (baseLeft.getX() + pc.getX()) + (baseLeft.getY() - pc.getY())
				* (baseLeft.getY() + pc.getY()))
				* (baseRight.getX() - pc.getX()))
				/ d;

		double squaredRadius = (pc.getX() - centerX) * (pc.getX() - centerX) + (pc.getY() - centerY) * (pc.getY() - centerY);

		if ((npc.getX() - centerX) * (npc.getX() - centerX) + (npc.getY() - centerY) * (npc.getY() - centerY) < squaredRadius)
			return true;
		else
			return false;
	}

	private static class SortByLeastAngle implements Comparator<Point> {
		public static Point baseLine;
		public static Point vertix;

		public static void setBaseLine(Point bl) {
			baseLine = bl;
		}

		public static void setVertix(Point v) {
			vertix = v;
		}

		public static double getAngle(Point p) {
			Point newLine = new Point(p.getX() - vertix.getX(), p.getY() - vertix.getY());
			double angle = Math.atan2(newLine.getY(), newLine.getX()) - Math.atan2(baseLine.getY(), baseLine.getX());
			while (angle < 0) {
				angle += 2 * Math.PI;
			}
			while (angle > 2 * Math.PI) {
				angle -= 2 * Math.PI;
			}
			return angle;
		}

		public int compare(Point o1, Point o2) {
			double angle1 = getAngle(o1);
			double angle2 = getAngle(o2);

			// margin of error of slightly less than a tenth of a degree
			return angle1 > (angle2 + 0.0017) ? 1 : (angle1 + 0.0017) < angle2 ? -1 : 0;
		}
	}

	private static Point[] createBaseLREdge(List<Point> left2, List<Point> right2, HashSet<Connection> leftConnections, HashSet<Connection> rightConnections,
			HashSet<Connection> connections, Point baseLeft, Point baseRight) {
		System.out.println("Making base edge");
		baseLeft = left2.get(left2.size() - 1);
		baseRight = right2.get(right2.size() - 1);
		System.out.println("original bL " + baseLeft + " bR " + baseRight);

		boolean hadIntersection = true;
		while (hadIntersection) {
			hadIntersection = false;
			System.out.println("bL's Y " + baseLeft.getY() + " bR's Y " + baseRight.getY());
			if (baseLeft.getY() >= baseRight.getY()) {
				for (Connection c : leftConnections) {
					if (areIntersecting(baseLeft, baseRight, c.getA(), c.getB())) {
						hadIntersection = true;
						System.out.println("L>=R bL " + baseLeft + " bR " + baseRight + " c " + c);
						break;
					}
				}
			} else {
				for (Connection c : rightConnections) {
					if (areIntersecting(baseLeft, baseRight, c.getA(), c.getB())) {
						hadIntersection = true;
						System.out.println("L<R bL " + baseLeft + " bR " + baseRight + " c " + c);
						break;
					}
				}
			}
			if (hadIntersection) {
				if (baseLeft.getY() >= baseRight.getY()) {
					baseLeft = left2.get(left2.indexOf(baseLeft) - 1);
					System.out.println("new bL " + baseLeft);
				} else {
					baseRight = right2.get(right2.indexOf(baseRight) - 1);
					System.out.println("new bR " + baseRight);
				}
			}
		}

		associate(baseLeft, baseRight, connections);

		Point[] ret = { baseLeft, baseRight };
		return ret;
	}

	// line intersection as described at
	// http://stackoverflow.com/a/565282/645647 but with modifications
	/*
	 * returns magnitude of 3-dimensional cross product (via augmenting with 1 in the kth dimension)
	 */
	private static int crossProduct(Point a, Point b) {
		return a.getX() * b.getY() - a.getY() * b.getX();
	}

	private static boolean areIntersecting(Point p, Point a, Point q, Point b) {
		if (p.equals(q) || p.equals(b) || q.equals(a)) {// an angle would be formed, not an intersection
			return false;
		}

		Point r = new Point(a.getX() - p.getX(), a.getY() - p.getY());
		Point s = new Point(b.getX() - q.getX(), b.getY() - q.getY());

		int denominator = crossProduct(r, s);
		Point qMinusP = new Point(q.getX() - p.getX(), q.getY() - p.getY());
		if (denominator == 0) {
			return crossProduct(qMinusP, r) == 0;
		} else {
			double t = crossProduct(qMinusP, s) / (double) (denominator);
			double u = crossProduct(qMinusP, r) / (double) (denominator);
			return 0 <= t && 0 <= u && t <= 1 && u <= 1;
		}
	}

	private static void associate(Point a, Point b, HashSet<Connection> connections) {
		connections.add(new Connection(a, b));
	}

	private static void associate(List<Point> points, HashSet<Connection> connections) {
		for (Point p : points) {
			for (Point pp : points) {
				if (p != pp) {
					associate(p, pp, connections);
				}
			}
		}
	}
}
