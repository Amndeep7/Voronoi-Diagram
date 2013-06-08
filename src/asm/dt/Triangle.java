package asm.dt;

public class Triangle {
	private Point a, b, c;

	public Triangle(Point one, Point two, Point three) {
		a = one;
		b = two;
		c = three;
	}

	@Override
	public String toString() {
		return "Triangle(" + a + "," + b + "," + c + ")";
	}
}
