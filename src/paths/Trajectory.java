package paths;

public class Trajectory {
	private Spline[] xySplines;
	public Trajectory(Spline... xySplines) {
		if (xySplines.length != 2) {
			throw new IllegalArgumentException("Must have 2 splines!");
		}
		this.xySplines = xySplines;
	}
	public double getHeading(double time) {
		double dydt = xySplines[1].getDerivative(time);
		double dxdt = xySplines[0].getDerivative(time);
		double dydx = dydt / dxdt;
		double alpha = Math.atan(dydx); // Could be faster
		double angle = 0;
		if (dydt > 0 && dxdt > 0) {
			// Q1
			angle = alpha;
		} else if (dydt > 0 && dxdt < 0) {
			// Q2
			angle = 180 + alpha;
		} else if (dydt < 0 && dxdt < 0) {
			// Q3
			angle = 180 + alpha;
		} else if (dydt < 0 && dxdt > 0) {
			// Q4
			angle = alpha;
		}
		return 90 - angle;
	}
	// Add all the important stuff
}
