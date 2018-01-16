package paths;
import java.util.ArrayList;
import java.util.List;

public class Spline implements PotentialSpline {
	private static final int ARCLENGTH_SAMPLES = 100000;
	private double x0, x1, v0, v1, a0, a1;
	private double arclength;
	private Spline[] splines;
	public double seconds;
	public Spline(double x0, double x1, Spline... a) {
		splines = a;
		if (a.length == 1) {
			v0 = a[0].v0;
			v1 = a[a.length-1].v1;
			a0 = a[0].a0;
			a1 = a[a.length-1].a1;
		}
		this.x0 = x0;
		this.x1 = x1;
		seconds = a.length;
	}
	public Spline(double x0, double x1, double v0, double v1, double a0, double a1) {
		this.x0 = x0;
		this.x1 = x1;
		this.v0 = v0;
		this.v1 = v1;
		this.a0 = a0;
		this.a1 = a1;
		splines = new Spline[]{this};
		seconds = 1;
	}
	public double get(double t) {
		// X Value is out of range of the splines.
		if (t < 0) {
			// Too small of an x
			return splines[0].get(0);
		}
		if (t > seconds) {
			return splines[splines.length-1].get(1);
		}
		if (splines.length == 1) {
			return (1 - 3*t*t + 2*t*t*t)*x0 + (t - 2*t*t + t*t*t)*v0 + (-t*t + t*t*t)*v1 + (3*t*t - 2*t*t*t)*x1;
		}
		for (int i = 0; i < splines.length; i++) {
			if (t >= i && t <= i+1) {
				return splines[i].get(t - i);
			}
		}
		
		return Double.NaN; // Never happens!
	}
	public double getDerivative(double t) {
		// X Value is out of range of the splines.
		if (t < 0) {
			// Too small of an x
			return splines[0].getDerivative(0);
		}
		if (t > seconds) {
			return splines[splines.length-1].getDerivative(1);
		}
		if (splines.length == 1) {
			return (3*t*t - 4*t + 1)*v0 + t*(3*t - 2)*v1 + 6*(t - 1)*(x0 - x1);
		}
		for (int i = 0; i < splines.length; i++) {
			if (t >= i && t <= i+1) {
				return splines[i].getDerivative(t - i);
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
				arclength += Math.sqrt(Math.pow(getDerivative(i / ARCLENGTH_SAMPLES), 2) + 1) * 1/ARCLENGTH_SAMPLES;
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
			return "x0: "+x0+"\nx1: "+x1+"\nv0: "+v0+"\nv1: "+v1+"\na0: "+a0+"\na1: "+a1;
		}
		String out = "";
		for (Spline s : splines) {
			out+= "x0: "+s.x0+"\tv0: "+s.v0+"\ta0: "+s.a0+"\tx1: "+s.x1+"\tv1: "+s.v1+"\ta1: "+s.a1+"\n";
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
			double[] derivs = new double[]{points[q].vx, points[q+1].vx};
			
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
		
		double[] derivs = new double[]{points[0].vx, points[points.length-1].vx};
		
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
	public static Spline[] interpolateQuintic(Point... points) {
		if (points.length <= 1) {
			throw new IllegalArgumentException("There must be at least two points!");
		}
		SimplePoint[] xsimples = new SimplePoint[points.length];
		SimplePoint[] ysimples = new SimplePoint[points.length];
		for (int i = 0; i < points.length; i++) {
			xsimples[i] = new SimplePoint(points[i].x, points[i].vx, points[i].ax);
			ysimples[i] = new SimplePoint(points[i].y, points[i].vy, points[i].ay);
		}
		Spline xSpline = getSpline(xsimples);
		Spline ySpline = getSpline(ysimples);
		
		return new Spline[]{xSpline, ySpline};
	}
	private static Spline getSpline(SimplePoint... simples) {
		Spline[] spl = new Spline[simples.length - 1];
		for (int i = 0; i < spl.length; i++) {
			spl[i] = new Spline(simples[i].pos, simples[i+1].pos, simples[i].vel, simples[i+1].vel, simples[i].accel, simples[i+1].accel);
		}
		return new Spline(simples[0].pos, simples[simples.length-1].pos, spl);
	}
}