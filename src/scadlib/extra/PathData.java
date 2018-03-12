package scadlib.extra;

import java.awt.Color;

import scadlib.paths.FastPathPlanner;

public class PathData {
	public enum PathParameter {
		NORMAL, REVERSE, FLIPREVERSE, FLIP
	}

	public FastPathPlanner path;
	
	public Color lColor;
	public Color cColor;
	public Color rColor;
	
	public double[][] waypoints;
	private double totalTime;
	private double dt;
	private double width;
	private PathParameter p;
	
	public PathData(double[][] path, double totalTime, double dt, double width) {
		this.totalTime = totalTime;
		this.dt = dt;
		this.width = width;
		p = PathParameter.NORMAL;
		waypoints = path;
		this.path = new FastPathPlanner(path);
		this.path.calculate(totalTime, dt, width);
		this.lColor = Color.red;
		this.cColor = Color.blue;
		this.rColor = Color.green;
	}
	public PathData(double[][] path, double totalTime, double dt, double width, PathParameter p) {
		this(path, totalTime, dt, width, p, Color.red, Color.blue, Color.green);
	}
	public PathData(double[][] path, double totalTime, double dt, double width, PathParameter p, Color lColor, Color cColor, Color rColor) {
		this.totalTime = totalTime;
		this.dt = dt;
		this.width = width;
		this.p = p;
		waypoints = path;
		this.path = new FastPathPlanner(path);
		recalculate();
		this.lColor = lColor;
		this.cColor = cColor;
		this.rColor = rColor;
	}
	public void recalculate() {
		this.path = new FastPathPlanner(waypoints);
		this.path.calculate(totalTime, dt, width);
		switch (p) {
		case REVERSE:
			System.out.println("Reversing path!");
			reversePath(this.path);
			break;
		case FLIPREVERSE:
			System.out.println("Flipping path and reversing path!");
			flipPath(this.path);
			reversePath(this.path);
			break;
		case FLIP:
			System.out.println("Flipping path!");
			flipPath(this.path);
		case NORMAL:
		default:
			break;
		}
	}
	public void updateTime(double time) {
		this.totalTime = time;
	}
	public double getTime() {
		return totalTime;
	}
	private void flipPath(FastPathPlanner path) {
		double[][] tmp = path.rightPath;
		path.rightPath = path.leftPath;
		path.leftPath = tmp;
		double[][] temp = path.smoothLeftVelocity;
		path.smoothLeftVelocity = path.smoothRightVelocity;
		path.smoothRightVelocity = temp;
//		reversePath(path);
	}
	private void reversePath(FastPathPlanner path) {
		for (int i=0; i < path.smoothLeftVelocity.length; i++) {
			path.smoothRightVelocity[i][1] = -path.smoothRightVelocity[i][1];
			path.smoothLeftVelocity[i][1] = -path.smoothLeftVelocity[i][1];
			path.smoothCenterVelocity[i][1] = -path.smoothCenterVelocity[i][1];
		}
	}
}
