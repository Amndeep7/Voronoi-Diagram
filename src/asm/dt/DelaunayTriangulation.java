package asm.dt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//implemented as described in Rich Vuduc's lecture notes from the class "Algorithms in the Real World" at CMU
public class DelaunayTriangulation {
	private static Triangle getContainingTriangle(HashMap<Triangle, ArrayList<Triangle>> triangles, Point p) throws Exception {
		for (Triangle t : triangles.keySet()) {
			if (t.withinTriangle(p)) {
				return t;
			}
		}
		throw new Exception("Algorithm broken: point is not contained by any triangle - " + p);
	}

	private static Triangle[] split(HashMap<Triangle, ArrayList<Triangle>> triangles, Triangle t, Point p) {
		List<Point> points = t.getPoints();

		ArrayList<Triangle> subs = new ArrayList<Triangle>();
		subs.add(new Triangle(points.get(0), points.get(1), p));
		subs.add(new Triangle(points.get(0), points.get(2), p));
		subs.add(new Triangle(points.get(1), points.get(2), p));

		ArrayList<Triangle> neighbors = new ArrayList<Triangle>(triangles.get(t));
		Triangle[] pairs = new Triangle[3];
		for (int x = 0; x < 3; x++) {
			pairs[x] = null;
		}
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < neighbors.size(); y++) {
				if (subs.get(x).sharesSide(neighbors.get(y))) {
					pairs[x] = neighbors.get(y);
				}
			}
		}

		for (int x = 0; x < 3; x++) {
			if (pairs[x] != null) {
				triangles.get(pairs[x]).remove(t);
				triangles.get(pairs[x]).add(subs.get(x));
				ArrayList<Triangle> share = new ArrayList<Triangle>();
				share.add(pairs[x]);
				triangles.put(subs.get(x), share);
			} else {
				triangles.put(subs.get(x), new ArrayList<Triangle>());
			}
		}

		triangles.get(subs.get(0)).add(subs.get(1));
		triangles.get(subs.get(0)).add(subs.get(2));
		triangles.get(subs.get(1)).add(subs.get(0));
		triangles.get(subs.get(1)).add(subs.get(2));
		triangles.get(subs.get(2)).add(subs.get(0));
		triangles.get(subs.get(2)).add(subs.get(1));

		triangles.remove(t);

		return new Triangle[] { subs.get(0), subs.get(1), subs.get(2) };
	}

	private static void patch(Triangle t, Point p) {

	}

	private static void insert(HashMap<Triangle, ArrayList<Triangle>> triangles, Point p) throws Exception {
		Triangle surround = getContainingTriangle(triangles, p);
		Triangle[] subs = split(triangles, surround, p);
		for (int x = 0; x < 3; x++) {
			patch(subs[x], p);
		}
	}

	private static void incrementaldt(HashMap<Triangle, ArrayList<Triangle>> triangles, List<Point> points) throws Exception {
		for (Point p : points) {
			insert(triangles, p);
		}
	}

	public static void triangulate(HashMap<Triangle, ArrayList<Triangle>> triangles, List<Point> points) throws Exception {
		Collections.sort(points, new Point.SortHorizontallyThenVertically());
		incrementaldt(triangles, points);
	}
}
