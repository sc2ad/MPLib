package scadlib.paths;

public class SimplePoint {
	public double pos,vel,accel;
	public SimplePoint(double p, double v, double a) {
		pos = p;
		vel = v;
		accel = a;
	}
	public String toString() {
		return "("+pos+", "+vel+", "+accel+")";
	}
}
