package paths;

public class Trajectory {
	private static final int ARCLENGTH_SAMPLES = 10000;
	private Spline[] xySplines;
	private double arclength;
	private double leftArclength = 0;
	private double rightArclength = 0;
	private double lastHeading;
	private double lastTime = -1;
	public double seconds = 0;
	private double omega;
	public Trajectory(Spline... xySplines) {
		if (xySplines.length != 2) {
			throw new IllegalArgumentException("Must have 2 splines!");
		}
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
		return 90 - Math.toDegrees(Math.atan2(dydt, dxdt)); // Could be faster
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
	@Deprecated
	public double getDeprecatedOmega(double t) {
		// At this link: https://www.wolframalpha.com/input/?i=d%2Fdt+arctan(((1+-+4*t+%2B+3*t*t)*v10+%2B+t*(-2+%2B+3*t)*v11+%2B+6*(-1+%2B+t)*t*(y0+-+y1))%2F((1+-+4*t+%2B+3*t*t)*v00+%2B+t*(-2+%2B+3*t)*v01+%2B+6*(-1+%2B+t)*t*(x0+-+x1)))
		// Please don't let this be wrong! I will be so sad!
		int index = (int)t;
		if (Util.fuzzyEquals(t, index)) {
			return 0;
		}
		// Index is the index for the spline (for xySplines[0].splines and xySplines[1].splines)
		double v00 = xySplines[0].getSpline(index).v0;
		double v01 = xySplines[0].getSpline(index).v1;
		double v10 = xySplines[1].getSpline(index).v0;
		double v11 = xySplines[1].getSpline(index).v1;
		double x0 = xySplines[0].getSpline(index).x0;
		double x1 = xySplines[0].getSpline(index).x1;
		double y0 = xySplines[1].getSpline(index).x0;
		double y1 = xySplines[1].getSpline(index).x1;
//		double part1 = (6*(t-1)*v00*(y0-y1)) / (3*t*t-4*t+1);
//		double part2 = (6*t*v00*(y0-y1)) / (3*t*t-4*t+1);
//		double part3 = (6*(t-1)*t*(6*t-4)*v00*(y0-y1)) / Math.pow(3*t*t-4*t+1,2);
//		double part4 = 3*t*v01 + (3*t-2)*v01 + (6*t-4)*v10 + 3*t*v11;
//		double part5 = (3*t-2)*v11 + 6*(t-1)*(x0-x1) + 6*t*(x0-x1);
//		double part6 = (6*(t-1)*t*v00*(y0-y1)) / (3*t*t-4*t+1);
//		double part7 = (3*t*t-4*t+1)*v10 + t*(3*t-2)*v01;
//		double part8 = t*(3*t-2)*v11+6*(t-1)*t*(x0-x1);
		
		double part1 = (-2*(3*t*t-3*t+1)*v00*v11);
		double part2 = 2*(3*t*t-3*t+1)*v01*v10;
		double part3 = 6*t*t*v01*(y0-y1);
		double part4 = 6*(x0-x1)*(Math.pow(t-1, 2)*v10-t*t*v11);
		double part5 = 6*Math.pow(t-1, 2)*v00*(y0-y1);
		double part6 = (3*t*t-3*t+1)*v00 + t*(3*t-2)*v01 + 6*(t-1)*t*(x0-x1);
		double part7 = (3*t*t-3*t+1)*v10 + t*(3*t-2)*v11 + 6*(t-1)*t*(y0-y1);
		double part8 = (3*t*t-3*t+1)*v00 + t*(3*t-2)*v01 + 6*(t-1)*t*(x0-x1);

		return (part1 + part2 + part3 + part4 - part5) / (Math.pow(part6, 2) * Math.pow(part7, 2) / Math.pow(part8, 2) + 1);
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
