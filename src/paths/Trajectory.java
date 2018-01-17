package paths;

public class Trajectory {
	private static final int ARCLENGTH_SAMPLES = 10000;
	private Spline[] xySplines;
	private double arclength;
	private double leftArclength = 0;
	private double rightArclength = 0;
	public Trajectory(Spline... xySplines) {
		if (xySplines.length != 2) {
			throw new IllegalArgumentException("Must have 2 splines!");
		}
		this.xySplines = xySplines;
	}
	public double getDerivative(double time) {
		double dydt = xySplines[1].getDerivative(time);
		double dxdt = xySplines[0].getDerivative(time);
		return dydt / dxdt;
	}
	public double getHeading(double time) {
		double dydt = xySplines[1].getDerivative(time);
		double dxdt = xySplines[0].getDerivative(time);
		double dydx = dydt / dxdt;
		double alpha = Math.toDegrees(Math.atan(dydx)); // Could be faster
		return Util.getAngle(alpha, dxdt, dydt);
	}
	public double getY(double time) {
		return xySplines[1].get(time);
	}
	public double getX(double time) {
		return xySplines[0].get(time);
	}
	public double getArclength() {
		if (arclength == 0) {
			for (int i = 0; i < ARCLENGTH_SAMPLES; i++) {
				arclength += Math.sqrt(Math.pow(getDerivative(i / ARCLENGTH_SAMPLES), 2) + 1) * 1/ARCLENGTH_SAMPLES;
			}
		}
		return arclength;
	}
	public double getCurvature(double time) {
		// How to find the maximum of this numerically?
		return (xySplines[0].getDerivative(time) * xySplines[1].getSecondDerivative(time) - xySplines[0].getSecondDerivative(time) * xySplines[1].getDerivative(time)) / Math.pow((Math.pow(xySplines[0].getDerivative(time), 2) + Math.pow(xySplines[1].getDerivative(time), 2)), 1.5);
	}
	public double getMaxCurvature(double time) {
		// TODO THIS NEEDS TO HAPPEN!
		return 0;
	}
	public double getLeftArclength() {
		return leftArclength;
	}
	public double getRightArclength() {
		return rightArclength;
	}
	public Position[][] getLeftRightPositions(double width) {
		Position[] left = new Position[(int)xySplines[0].seconds * ARCLENGTH_SAMPLES];
		Position[] right = new Position[(int)xySplines[1].seconds * ARCLENGTH_SAMPLES];
		double i = 0;
		double lastLX = Double.NaN, lastRX = Double.NaN, lastLY = Double.NaN, lastRY = Double.NaN;
		leftArclength = 0;
		rightArclength = 0;
		while (i < left.length) {
			double t = i / ARCLENGTH_SAMPLES;
			double lx = getX(t) - width / 2 * xySplines[1].getDerivative(t) / (Math.sqrt(Math.pow(xySplines[0].getDerivative(t), 2) + Math.pow(xySplines[1].getDerivative(t), 2)));
			double ly = getY(t) + width / 2 * xySplines[0].getDerivative(t) / (Math.sqrt(Math.pow(xySplines[0].getDerivative(t), 2) + Math.pow(xySplines[1].getDerivative(t), 2)));
			double rx = getX(t) + width / 2 * xySplines[1].getDerivative(t) / (Math.sqrt(Math.pow(xySplines[0].getDerivative(t), 2) + Math.pow(xySplines[1].getDerivative(t), 2)));
			double ry = getY(t) - width / 2 * xySplines[0].getDerivative(t) / (Math.sqrt(Math.pow(xySplines[0].getDerivative(t), 2) + Math.pow(xySplines[1].getDerivative(t), 2)));
			
			double lHeading = 0;
			double rHeading = 0;
			if (Double.isNaN(lastLX)) {
				lastLX = lx;
				lastLY = ly;
				lastRX = rx;
				lastRY = ry;
				lHeading = getHeading(t);
				rHeading = getHeading(t);
			} else {
				double dlydt = ly - lastLY;
				double dlxdt = lx - lastLX;
				double drydt = ry - lastRY;
				double drxdt = rx - lastRX;
				lHeading = Math.toDegrees(Math.atan(dlydt / dlxdt));
				rHeading = Math.toDegrees(Math.atan(drydt / drxdt));
				lHeading = Util.getAngle(lHeading, dlxdt, dlydt);
				rHeading = Util.getAngle(rHeading, drxdt, drydt);
			}
			leftArclength += Math.sqrt(Math.pow(lx - lastLX, 2) + Math.pow(ly - lastLY, 2)) / ARCLENGTH_SAMPLES;
			rightArclength += Math.sqrt(Math.pow(rx - lastRX, 2) + Math.pow(ry - lastRY, 2)) / ARCLENGTH_SAMPLES;
			
			left[(int)i] = new Position(lx, ly, lHeading);
			right[(int)i] = new Position(rx, ry, rHeading);
			i += 1;
		}
		return new Position[][]{left, right};
	}
	
	// Add all the important stuff
}
