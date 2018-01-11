package paths;
import java.util.ArrayList;
import java.util.List;

public class Spline {
	private static final int ARCLENGTH_SAMPLES = 100000;
	public static class Point {
		public double x,y,deriv = 0;
		public Point(double x, double y, double derivative, boolean bs) {
			this.x = x;
			this.y = y;
			this.deriv = derivative;
		}
		public Point(double x, double y, double bearingDegrees) {
			this.x = x;
			this.y = y;
			this.deriv = Math.tan(Math.toRadians(90-bearingDegrees));
			if (Double.isInfinite(deriv)) {
				deriv = 1000000;
			}
			//TODO POTENTIALLY A REALLY BIG ISSUE THAT IT CAN'T REALLY GO LEFT (PROBABLY)
		}
		public String toString() {
			return "("+x+", "+y+", "+deriv+")";
		}
	}
	private double a, b, c, d = 0;
	private double x0, x1;
	private double arclength;
	private Spline[] splines;
	public Spline(double a, double b, double c, double d, double x0, double x1) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.x0 = x0;
		this.x1 = x1;
		splines = new Spline[]{this};
	}
	public Spline(double x0, double x1, Spline... a) {
		splines = a;
		if (a.length == 1) {
			this.a = a[0].a;
			b = a[0].b;
			c = a[0].c;
			d = a[0].d;
		}
		this.x0 = x0;
		this.x1 = x1;
	}
	public double getY(double x) {
		// X Value is out of range of the splines.
		if (x < splines[0].x0) {
			// Too small of an x
			return splines[0].getY(splines[0].x0);
		}
		if (x > splines[splines.length-1].x1) {
			return splines[splines.length-1].getY(splines[splines.length-1].x1);
		}
		if (splines.length == 1) {
			return a + b * (x - x0) + c * Math.pow(x - x0, 2) + d * Math.pow(x - x0, 3);
		}
		for (int i = 0; i < splines.length; i++) {
			if (x >= splines[i].x0 && x <= splines[i].x1) {
				return splines[i].getY(x);
			}
		}
		
		return Double.NaN; // Never happens!
	}
	public double getDerivative(double x) {
		// X Value is out of range of the splines.
		if (x < splines[0].x0) {
			// Too small of an x
			return splines[0].getDerivative(splines[0].x0);
		}
		if (x > splines[splines.length-1].x1) {
			return splines[splines.length-1].getDerivative(splines[splines.length-1].x1);
		}
		if (splines.length == 1) {
			return b + (x - x0)*(2*c + 3*d*(x - x0));
		}
		for (int i = 0; i < splines.length; i++) {
			if (x >= splines[i].x0 && x <= splines[i].x1) {
				return splines[i].getDerivative(x);
			}
		}
		
		return Double.NaN; // Never happens!
	}
	public double getArclength() {
		if (arclength != 0) {
			return arclength;
		}
		if (splines.length == 1) {
			for (int i = 0; i < ARCLENGTH_SAMPLES; i++) {
				arclength += Math.sqrt(Math.pow(getDerivative(x0 + (x1-x0)*i / ARCLENGTH_SAMPLES), 2) + 1) * (x1-x0)/ARCLENGTH_SAMPLES;
			}
			return arclength;
		}
		for (Spline s : splines) {
			arclength += s.getArclength();
		}
		return arclength;
	}
	public String toString() {
		if (splines.length <= 1) {
			return "a: "+a+"\nb: "+b+"\nc: "+c+"\nd: "+d;
		}
		String out = "";
		for (Spline s : splines) {
			out+= "a: "+s.a+"\tb: "+s.b+"\tc: "+s.c+"\td: "+s.d+"\n";
		}
		return out;
	}
	public static Spline interpolate2(Point... points) throws Exception {
		
		if (points.length <= 1) {
			throw new Exception("you must have 2+ points!");
		}
		
		Spline[] outs = new Spline[points.length-1];
		
		for (int q = 0; q < points.length-1; q++) {
			// The following effectively forces the spline generation to ONLY occur with 2 points at a time
			List<Double> x = new ArrayList<Double>();
			List<Double> y = new ArrayList<Double>();
			x.add(points[q].x);
			x.add(points[q+1].x);
			y.add(points[q].y);
			y.add(points[q+1].y);
			double[] derivs = new double[]{points[q].deriv, points[q+1].deriv};
			
			int n = x.size()-1;
			
			double[] h = new double[n];
			double[] a = new double[n+1];
			double[] b = new double[n+1];
			double[] c = new double[n+1];
			double[] d = new double[n+1];
			double[] alpha = new double[n+1];
			
			double[] l = new double[n+1];
			double[] mew = new double[n];
			double[] z = new double[n];
			
			for (int i = 0; i < h.length; i++) {
				h[i] = x.get(i+1) - x.get(i);
				a[i] = y.get(i);
			}
			a[n] = y.get(n);
			
			alpha[0] = 3*(a[1] - a[0]) / h[0] - 3*derivs[0];
			for (int i = 1; i < n; i++) {
				alpha[i] = 3/h[i] * (a[i+1] - a[i]) - 3/h[i-1]*(a[i] - a[i-1]);
			}
			alpha[n] = 3*derivs[1]-3/h[n-1]*(a[n]-a[n-1]);
			
			l[0] = 2*h[0];
			mew[0] = 0.5;
			z[0] = alpha[0] / l[0];
			for (int i = 1; i < n; i++) {
				l[i] = 2*(x.get(i+1) - x.get(i-1))-h[i-1]*mew[i-1];
				mew[i] = h[i] / l[i];
				z[i] = (alpha[i] - h[i-1]*z[i-1]) / l[i];
			}
			l[n] = h[n-1]*(2-mew[n-1]);
			double zn = (alpha[n] - h[n-1]*z[n-1]) / l[n];
			c[n] = zn;
			
			for (int j = n-1; j>=0; j--) {
				c[j] = z[j] - mew[j] * c[j+1];
				b[j] = (a[j+1] - a[j]) / h[j] - (h[j] * (c[j+1] + 2*c[j]))/3;
				d[j] = (c[j+1] - c[j]) / (3*h[j]);
			}
			Spline[] splines = new Spline[n];
			for (int i = 0; i < n; i++) {
				splines[i] = new Spline(a[i], b[i], c[i], d[i], x.get(i), x.get(i+1)); 
			}
			
			outs[q] = new Spline(x.get(0), x.get(n), splines);
		}
		return new Spline(points[0].x, points[points.length-1].x, outs);
	}
	public static Spline interpolate(Point... points) throws Exception {
		if (points.length <= 1) {
			throw new Exception("you must have 2+ points!");
		}
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		for (int i = 0; i < points.length; i++) {
			x.add(points[i].x);
			y.add(points[i].y);
		}
		
		double[] derivs = new double[]{points[0].deriv, points[points.length-1].deriv};
		
		int n = x.size()-1;
		
		double[] h = new double[n];
		double[] a = new double[n+1];
		double[] b = new double[n+1];
		double[] c = new double[n+1];
		double[] d = new double[n+1];
		double[] alpha = new double[n+1];
		
		double[] l = new double[n+1];
		double[] mew = new double[n];
		double[] z = new double[n];
		
		for (int i = 0; i < h.length; i++) {
			h[i] = x.get(i+1) - x.get(i);
			a[i] = y.get(i);
		}
		a[n] = y.get(n);
		
		alpha[0] = 3*(a[1] - a[0]) / h[0] - 3*derivs[0];
		for (int i = 1; i < n; i++) {
			alpha[i] = 3/h[i] * (a[i+1] - a[i]) - 3/h[i-1]*(a[i] - a[i-1]);
		}
		alpha[n] = 3*derivs[1]-3/h[n-1]*(a[n]-a[n-1]);
		
		l[0] = 2*h[0];
		mew[0] = 0.5;
		z[0] = alpha[0] / l[0];
		for (int i = 1; i < n; i++) {
			l[i] = 2*(x.get(i+1) - x.get(i-1))-h[i-1]*mew[i-1];
			mew[i] = h[i] / l[i];
			z[i] = (alpha[i] - h[i-1]*z[i-1]) / l[i];
		}
		l[n] = h[n-1]*(2-mew[n-1]);
		double zn = (alpha[n] - h[n-1]*z[n-1]) / l[n];
		c[n] = zn;
		
		for (int j = n-1; j>=0; j--) {
			c[j] = z[j] - mew[j] * c[j+1];
			b[j] = (a[j+1] - a[j]) / h[j] - (h[j] * (c[j+1] + 2*c[j]))/3;
			d[j] = (c[j+1] - c[j]) / (3*h[j]);
		}
		Spline[] splines = new Spline[n];
		for (int i = 0; i < n; i++) {
			splines[i] = new Spline(a[i], b[i], c[i], d[i], x.get(i), x.get(i+1)); 
		}
		return new Spline(points[0].x, points[points.length-1].x, splines);
	}
}