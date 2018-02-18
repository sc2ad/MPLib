package scadlib.paths;

public interface PotentialSpline {
	public double get(double t);
	public double getDerivative(double t);
	public double getArclength();
}
