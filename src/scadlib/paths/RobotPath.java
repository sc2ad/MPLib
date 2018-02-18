package scadlib.paths;

public class RobotPath {
	Trajectory traj;
	double[] headings, curvatures;
	double maxCurvature;
	MotionPath centralPath, leftPath, rightPath;
	public RobotPath(Trajectory t, double[] headings, double[] curvatures) {
		traj = t;
		this.headings = headings;
		this.curvatures = curvatures;
	}
	public double getMaxVelocity() {
		return getMainPath().getSpeed(getMainPath().getTotalTime() / 2.0);
	}
	public double getMaxCurvature() {
		if (maxCurvature == 0) {
			double max = curvatures[0];
			for (int i = 1; i < curvatures.length; i++) {
				if (Math.abs(curvatures[i]) > max) {
					max = Math.abs(curvatures[i]);
				}
			}
			maxCurvature = max;
		}
		return maxCurvature;
	}
	public MotionPath getMainPath() {
		return centralPath;
	}
	public double getSpeed(double time) {
		return centralPath.getSpeed(time);
	}
	public void constructMainPath(double vMax, double aMax, double omegaMax) {
		double vReal = vMax > omegaMax / getMaxCurvature() ? omegaMax / getMaxCurvature() : vMax; // Properly sets velocity due to path constraints (this limits entire path)
		centralPath = new CombinedPath.LongitudalTrapezoid(0, traj.getArclength(), vReal, aMax);
	}
	public MotionPath[] getLeftRightPaths(double vMax, double aMax, double omegaMax) {
		// TODO add unit conversion
		// Need to develop a path that can have the proper velocity only when omega does not work, instead of limiting the entire path's speed
		if (!(leftPath != null)) {
			double vReal = vMax > omegaMax / getMaxCurvature() ? omegaMax / getMaxCurvature() : vMax; // Properly sets velocity due to path constraints (this limits entire path)
			centralPath = new CombinedPath.LongitudalTrapezoid(0, traj.getArclength(), vReal, aMax);
			leftPath = new CombinedPath.LongitudalTrapezoid(0, traj.getLeftArclength(), vReal, aMax);
			rightPath = new CombinedPath.LongitudalTrapezoid(0, traj.getRightArclength(), vReal, aMax);
		}
		return new MotionPath[]{leftPath, rightPath};
	}
	public double getHeading(double time) {
		// Input should be MotionPath time, not spline path time
//		double s = getMainPath().getPosition(time);
//		return headings[(int)(s / traj.getArclength() * (headings.length-1))]; // Uses arclength to calculate which heading index should be returned
		return traj.getHeading(traj.seconds * time / getMainPath().getTotalTime());
	}
	public double getOmega(double time) {
		// Also converts to degrees / second
		return traj.getOmega(traj.seconds * time / getMainPath().getTotalTime()) * (traj.seconds / getMainPath().getTotalTime());
	}
}
