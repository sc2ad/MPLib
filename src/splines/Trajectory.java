package splines;

import util.Util;

public class Trajectory {
	int ARCLENGTH_SAMPLES = 10000;
	private Spline[] xySplines;
	private double arclength;
	private double leftArclength = 0;
	private double rightArclength = 0;
	private double lastHeading;
	private double lastTime = -1;
	public double seconds = 0;
	private double omega;
	public Trajectory(Samples s, Spline... xySplines) {
		if (xySplines.length != 2) {
			throw new IllegalArgumentException("Must have 2 splines!");
		}
		ARCLENGTH_SAMPLES = s.value;
		this.xySplines = xySplines;
		seconds = xySplines[0].seconds;
	}
	public double getDerivative(double time) {
		double dydt = xySplines[1].getDerivative(time);
		double dxdt = xySplines[0].getDerivative(time);
		return dydt / dxdt;
	}
	public double getHeading(double time) {
		double dydt = xySplines[1].getDerivative(time);
		double dxdt = xySplines[0].getDerivative(time);
//		double dydx = dydt / dxdt;
		return Math.atan2(dydt, dxdt); // Could be faster
	}
	public double getOmega(double t) {
		// THIS IS IN SPLINE SECONDS PLEASE PLEASE PLEASRE PLEASE OAJFP LEOJFAHKGH LKHJF CONVERT TO NORMAL UNITS OF MEASUREMENT LIKE SECONDS
		double heading = getHeading(t);
		if (lastTime == -1 || lastTime > t) {
			lastHeading = heading;
			lastTime = t;
			heading = getHeading(t+0.00125); // Magic number used here to depict correct omega at stat point
			return (heading - lastHeading) / (0.00125);
		}
		if (t == lastTime) {
			return omega;
		}
		double omega = (heading - lastHeading) / (t - lastTime);
		this.omega = omega;
		lastHeading = heading;
		lastTime = t;
		// Degrees per second
		return omega;
	}
	public double getY(double time) {
		return xySplines[1].get(time);
	}
	public double getX(double time) {
		return xySplines[0].get(time);
	}
	public double getXDerivative(double time) {
		return xySplines[0].getDerivative(time);
	}
	public double getYDerivative(double time) {
		return xySplines[1].getDerivative(time);
	}
	public double getArclength() {
		// Seems slightly off when compared to desmos (off by about 1)
		if (arclength == 0) {
			double i = 0;
			while (i < seconds * ARCLENGTH_SAMPLES) {
				double t = i / ARCLENGTH_SAMPLES;
				arclength += Math.sqrt(Math.pow(xySplines[0].getDerivative(t), 2) + Math.pow(xySplines[1].getDerivative(t), 2)) / ARCLENGTH_SAMPLES;
//				arclength += Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
				i++;
			}
		}
		return arclength;
	}
	public double getCurvature(double time) {
		// How to find the maximum of this numerically?
		return 0;
//		return (xySplines[0].getDerivative(time) * xySplines[1].getSecondDerivative(time) - xySplines[0].getSecondDerivative(time) * xySplines[1].getDerivative(time)) / Math.pow((Math.pow(xySplines[0].getDerivative(time), 2) + Math.pow(xySplines[1].getDerivative(time), 2)), 1.5);
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
				// Wait this is actually redundant and unneeded
				double dlydt = ly - lastLY;
				double dlxdt = lx - lastLX;
				double drydt = ry - lastRY;
				double drxdt = rx - lastRX;
				lHeading = Math.toDegrees(Math.atan(dlydt / dlxdt));
				rHeading = Math.toDegrees(Math.atan(drydt / drxdt));
				lHeading = Util.getAngle(lHeading, dlxdt, dlydt);
				rHeading = Util.getAngle(rHeading, drxdt, drydt);
			}
			leftArclength += Math.sqrt(Math.pow(lx - lastLX, 2) + Math.pow(ly - lastLY, 2));
			rightArclength += Math.sqrt(Math.pow(rx - lastRX, 2) + Math.pow(ry - lastRY, 2));
			
			left[(int)i] = new Position(lx, ly, lHeading);
			right[(int)i] = new Position(rx, ry, rHeading);
			i += 1;
			lastLX = lx;
			lastLY = ly;
			lastRX = rx;
			lastRY = ry;
		}
		return new Position[][]{left, right};
	}
	
	// Add all the important stuff
}
