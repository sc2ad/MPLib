package extra;

import splines.RobotPath;

public class RobotPathFollower {
	private double width,wheelDiameter,ticksPerRevolution;
	private RobotPath path;
	public PIDF left,right;
	// Wheel diameter and width must be same units as the RobotPath!
	public RobotPathFollower(double width, double wheelDiameter, double ticksPerRevolution, double[]... pidfLR) {
		this.width = width;
		this.wheelDiameter = wheelDiameter;
		this.ticksPerRevolution = ticksPerRevolution;
		left = new PIDF(pidfLR[0][0], pidfLR[0][1], pidfLR[0][2], pidfLR[0][3]);
		right = new PIDF(pidfLR[1][0], pidfLR[1][1], pidfLR[1][2], pidfLR[1][3]);
	}
	public void setPath(RobotPath p) {
		path = p;
	}
	public double getDeltaV(double time) {
		// deg / s * rot / deg * units / rot
		double omega = path.getOmega(time);
		return (Math.toRadians(omega) * width / 2);
//		return omega / 360.0 * width * Math.PI / 2; // This / 2 seems to actually do the trick...
//		double vL = (v + omega * WIDTH / 2) / RADIUS_OF_WHEEL;
//		double vR = (v - omega * WIDTH / 2) / RADIUS_OF_WHEEL;
	}
	public double getLeftVelocity(double time) {
		return (path.getMainPath().getSpeed(time) + getDeltaV(time)) / (wheelDiameter / 2);
	}
	public double getRightVelocity(double time) {
		return (path.getMainPath().getSpeed(time) - getDeltaV(time)) / (wheelDiameter / 2);
	}
	public double getLeftPosition(double time) {
		// I don't know how to determine this (this is arclength of the left position)
		return path.getMainPath().getPosition(time);
	}
	public double getRightPosition(double time) {
		// I don't know how to determine this (this is arclength of the right position)
		return path.getMainPath().getPosition(time);
	}
	public double convertToTicks(double value) {
		// units * rot / units * ticks / rot
		return value / (wheelDiameter * Math.PI) * ticksPerRevolution;
	}
	public double convertToUnits(double ticks) {
		return ticks * (wheelDiameter * Math.PI) / ticksPerRevolution;
	}
	public void start() {
		left.reset();
		right.reset();
	}
	public double getLeftOut(double time, double currentEncPosition) {
		double leftPos = convertToTicks(getLeftPosition(time));
		double leftV = convertToTicks(getLeftVelocity(time));
		left.setTarget(leftPos, leftV);
		return left.getOut(time, currentEncPosition);
	}
	public double getRightOut(double time, double currentEncPosition) {
		double rightPos = convertToTicks(getRightPosition(time));
		double rightV = convertToTicks(getRightVelocity(time));
		right.setTarget(rightPos, rightV);
		return right.getOut(time, currentEncPosition);
	}
}
