package splines;

import paths.CombinedPath;
import paths.MotionPath;

public class PathPlanner {
	Trajectory path;
	double width;
	private MotionPath centerProfile;
	
	private double[][] smoothVelocities;
	private double[][] leftSmoothVelocities;
	private double[][] rightSmoothVelocities;
	private double[][] centerVelocities;
	private double[][] centerPath;
	private double[][] leftPath;
	private double[][] rightPath;
	private double[] headings;
	private double[] omegas;
	private double[] leftArclength;
	private double[] rightArclength;
	
	public PathPlanner(Trajectory p, double w) {
		path=p;
		width = w;
		// This dt is in spline time!
	}
	public double[] getOmegas(double dt) {
		// This is all in spline time
		double[] omegas = new double[(int)(path.seconds / dt)];
		for (int i = 0; i < omegas.length; i++) {
			omegas[i] = path.getOmega((double)i * dt);
		}
		return omegas;
	}
	public double[][] calculateOmegaVelocities(double[] omegas) {
		// Returns {{L0, R0}, {L1, R1}, ...}
		double[][] velocities = new double[omegas.length][2];
		for (int i = 0; i < velocities.length; i++) {
			velocities[i][0] = omegas[i] * width / 2;
			velocities[i][1] = -omegas[i] * width / 2;
		}
		return velocities;
	}
	public MotionPath getCenterProfile(double maxV, double maxA) {
		if (centerProfile == null) {
			centerProfile = new CombinedPath.LongitudalTrapezoid(0, path.getArclength(), maxV, maxA);
		}
		return centerProfile;
	}
	public double[][] getLeftRightSmoothVelocities(MotionPath centerProfile, double[][] omegaVelocities) {
		// This method has many problems if the path's velocity + the omega's velocity exceeds the physical max velocity
		double totalTime = centerProfile.getTotalTime();
		
		double dt = totalTime / (double)(omegaVelocities.length);
//		double[][] smoothV = new double[][];
//		for (int i = 0; i < )
		return null;
	}
	public double[][] calculateSmoothVelocities(double maxV, double maxA, double dt) {
		if (smoothVelocities == null) {
			getCenterProfile(maxV, maxA);
			double splineDT = path.seconds * dt / centerProfile.getTotalTime();
			int length = (int)(path.seconds / splineDT)+1;
			smoothVelocities = new double[length][2];
			leftSmoothVelocities = new double[length][2];
			rightSmoothVelocities = new double[length][2];
			centerVelocities = new double[length][2];
			leftPath = new double[length][2];
			rightPath = new double[length][2];
			centerPath = new double[length][2];
			headings = new double[length];
			omegas = new double[length];
			
			double centerX = path.getX(0), centerY = path.getY(0);
			double heading = path.getHeading(0);
			double leftX = centerX - width / 2 * Math.sin(heading), leftY = centerY + width / 2 * Math.cos(heading);
			double rightX = centerX + width / 2 * Math.sin(heading), rightY = centerY - width / 2 * Math.cos(heading);
			
			for (int i = 0; i < length; i++) {
				leftPath[i][0] = leftX;
				leftPath[i][1] = leftY;
				rightPath[i][0] = rightX;
				rightPath[i][1] = rightY;
				centerPath[i][0] = centerX;
				centerPath[i][1] = centerY;
				headings[i] = heading;
				omegas[i] = path.getOmega(splineDT * i);
				
				double left = centerProfile.getSpeed(i * dt) + path.getOmega(splineDT * i) * width / 2;
				double right = centerProfile.getSpeed(i * dt) - path.getOmega(splineDT * i) * width / 2;
				
				double seconds = i * dt;
				
				centerVelocities[i][0] = seconds;
				centerVelocities[i][1] = centerProfile.getSpeed(i * dt);
				smoothVelocities[i][0] = left;
				smoothVelocities[i][1] = right;
				leftSmoothVelocities[i][0] = seconds;
				leftSmoothVelocities[i][1] = left;
				rightSmoothVelocities[i][0] = seconds;
				rightSmoothVelocities[i][1] = right;
				
				heading = path.getHeading(splineDT * i);
//				leftX += left * Math.cos(heading) * dt;
//				leftY += left * Math.sin(heading) * dt;
//				rightX += right * Math.cos(heading) * dt;
//				rightY += right * Math.sin(heading) * dt;
				centerX += centerProfile.getSpeed(i * dt) * Math.cos(heading) * dt;
				centerY += centerProfile.getSpeed(i * dt) * Math.sin(heading) * dt;
				leftX = centerX - width / 2 * Math.sin(heading);
				leftY = centerY + width / 2 * Math.cos(heading);
				rightX = centerX + width / 2 * Math.sin(heading);
				rightY = centerY - width / 2 * Math.cos(heading);
			}
		}
		return smoothVelocities;
	}
	public double[][] getCenterVelocities() {
		return centerVelocities;
	}
	public double[][] getLeftSmoothVelocities() {
		return leftSmoothVelocities;
	}
	public double[][] getRightSmoothVelocities() {
		return rightSmoothVelocities;
	}
	public double[][] getCenterPath() {
		return centerPath;
	}
	public double[][] getLeftPath() {
		return leftPath;
	}
	public double[][] getRightPath() {
		return rightPath;
	}
	public double[] getHeadings() {
		return headings;
	}
	public double[] getOmegas() {
		return omegas;
	}
	public double[] getLeftArclength() {
		if (leftArclength == null) {
			double leftArclengthSum = 0;
			leftArclength = new double[leftPath.length];
			leftArclength[0] = 0;
			for (int i = 1; i < leftPath.length; i++) {
				leftArclengthSum += Math.sqrt(Math.pow(leftPath[i][1] - leftPath[i-1][1], 2) + Math.pow(leftPath[i][0] - leftPath[i-1][0], 2));
				leftArclength[i] = leftArclengthSum;
			}
			return leftArclength;
		}
		return leftArclength;
	}
	public double[] getRightArclength() {
		if (rightArclength == null) {
			double rightArclengthSum = 0;
			rightArclength = new double[rightPath.length];
			rightArclength[0] = 0;
			for (int i = 1; i < rightPath.length; i++) {
				rightArclengthSum += Math.sqrt(Math.pow(rightPath[i][1] - rightPath[i-1][1], 2) + Math.pow(rightPath[i][0] - rightPath[i-1][0], 2));
				rightArclength[i] = rightArclengthSum;
			}
			return rightArclength;
		}
		return rightArclength;
	}
}
