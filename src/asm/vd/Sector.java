package asm.vd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sector {
	private ArrayList<Integer> pixelX;
	private ArrayList<Integer> pixelY;
	private Color color;

	public Sector() {
		pixelX = new ArrayList<Integer>();
		pixelY = new ArrayList<Integer>();
		color = Color.BLUE;
	}

	public Sector(Color c) {
		this();
		color = c;
	}

	public void draw(Graphics g) {
		g.setColor(color);
		for (int i = 0; i < pixelX.size(); i++) {
			int x = pixelX.get(i);
			int y = pixelY.get(i);
			g.drawLine(x, y, x, y);
		}
	}

	public void addPixel(int x, int y) {
		pixelX.add(x);
		pixelY.add(y);
	}

	public void clear() {
		pixelX.clear();
		pixelY.clear();
	}

	// Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	public static double[] RGBToXYZ(int[] colors) {
		double[] rgb = { colors[0] / 255.0, colors[1] / 255.0, colors[2] / 255.0 };

		for (int i = 0; i < 3; i++) {
			double c = rgb[i];
			if (c > 0.04045) {
				c = Math.pow((c + 0.055) / 1.055, 2.4);
			} else {
				c /= 12.92;
			}

			c *= 100.0;

			rgb[i] = c;
		}

		double[] xyz = new double[3];
		xyz[0] = rgb[0] * 0.4124 + rgb[1] * 0.3576 + rgb[2] * 0.1805;
		xyz[1] = rgb[0] * 0.2126 + rgb[1] * 0.7152 + rgb[2] * 0.0722;
		xyz[2] = rgb[0] * 0.0193 + rgb[1] * 0.1192 + rgb[2] * 0.9505;

		return xyz;
	}

	// Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	public static int[] XYZToRGB(double[] colors) {
		double[] xyz = { colors[0] / 100.0, colors[1] / 100.0, colors[2] / 100.0 };

		double[] rgb = new double[3];
		rgb[0] = xyz[0] * 3.2406 + xyz[1] * -1.5372 + xyz[2] * -0.4986;
		rgb[1] = xyz[0] * -0.9689 + xyz[1] * 1.8758 + xyz[2] * 0.0415;
		rgb[2] = xyz[0] * 0.0557 + xyz[1] * -0.2040 + xyz[2] * 1.0570;

		for (int i = 0; i < 3; i++) {
			double c = rgb[i];
			if (c > 0.0031308) {
				c = 1.055 * Math.pow(c, 1.0 / 2.4) - 0.055;
			} else {
				c *= 12.92;
			}
			rgb[i] = c;
		}

		int[] RGB = { (int) (rgb[0] * 255), (int) (rgb[1] * 255), (int) (rgb[2] * 255) };

		return RGB;
	}

	// Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	public static double[] XYZToCIEL_a_b(double[] colors) {
		double refX = 95.047;
		double refY = 100.000;
		double refZ = 108.883;

		double xyz[] = new double[3];
		xyz[0] = colors[0] / refX;
		xyz[1] = colors[1] / refY;
		xyz[2] = colors[2] / refZ;

		for (int i = 0; i < 3; i++) {
			double c = xyz[i];
			if (c > 0.008856) {
				c = Math.pow(c, (1.0 / 3.0));
			} else {
				c = (7.787 * c) + (16.0 / 116.0);
			}
			xyz[i] = c;
		}

		double lab[] = new double[3];
		lab[0] = (116 * xyz[0]) - 16;
		lab[1] = 500 * (xyz[0] - xyz[1]);
		lab[2] = 200 * (xyz[1] - xyz[2]);

		return lab;
	}

	// Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	public static double[] CIEL_a_bToXYZ(double[] colors) {
		double refX = 95.047;
		double refY = 100.000;
		double refZ = 108.883;

		double xyz[] = new double[3];
		xyz[1] = (colors[0] + 16.0) / 116.0;
		xyz[0] = colors[1] / 500.0 + xyz[1];
		xyz[2] = xyz[1] - colors[2] / 200.0;

		for (int i = 0; i < 3; i++) {
			double c = xyz[i];
			if (c * c * c > 0.008856) {
				c = c * c * c;
			} else {
				c = (c - 16 / 116.0) / 7.787;
			}
			xyz[i] = c;
		}

		double XYZ[] = new double[3];
		XYZ[0] = refX * xyz[0];
		XYZ[1] = refY * xyz[1];
		XYZ[2] = refZ * xyz[2];

		return XYZ;
	}

	public void average(BufferedImage givenImage) {
		double[] l = new double[pixelX.size()];
		double[] a = new double[pixelX.size()];
		double[] b = new double[pixelX.size()];

		for (int i = 0; i < pixelX.size(); i++) {
			//System.out.println("index " + i);
			int rgbval = givenImage.getRGB(pixelX.get(i), pixelY.get(i));
			int[] rgbvals = { (rgbval >> 16) & 0x000000FF, (rgbval >> 8) & 0x000000FF, rgbval & 0x000000FF };
			//System.out.println("rgbvals R " + rgbvals[0] + " G " + rgbvals[1] + " B " + rgbvals[2]);
			double[] xyzvals = RGBToXYZ(rgbvals);
			//System.out.println("xyzvals X " + xyzvals[0] + " Y " + xyzvals[1] + " Z " + xyzvals[2]);
			double[] labvals = XYZToCIEL_a_b(xyzvals);
			//System.out.println("labvals L " + labvals[0] + " a " + labvals[1] + " b " + labvals[2]);
			l[i] = labvals[0];
			a[i] = labvals[1];
			b[i] = labvals[2];
		}

		double[] aveLAB = { 0.0, 0.0, 0.0 };
		for (int i = 0; i < pixelX.size(); i++) {
			aveLAB[0] += l[i];
			aveLAB[1] += a[i];
			aveLAB[2] += b[i];
		}
		for (int i = 0; i < 3; i++) {
			aveLAB[i] /= (double) pixelX.size();
		}
		//System.out.println("aveLab L " + aveLAB[0] + " a " + aveLAB[1] + " b " + aveLAB[2]);

		double[] aveXYZ = CIEL_a_bToXYZ(aveLAB);
		//System.out.println("aveXYZ X " + aveXYZ[0] + " Y " + aveXYZ[1] + " Z " + aveXYZ[2]);
		int[] aveRGB = XYZToRGB(aveXYZ);
		//System.out.println("aveRGB R " + aveRGB[0] + " G " + aveRGB[1] + " B " + aveRGB[2]);
		color = new Color(Math.max(0, Math.min(255, aveRGB[0])), Math.max(0, Math.min(255, aveRGB[1])), Math.max(0, Math.min(255, aveRGB[2])));
		
	}
}
