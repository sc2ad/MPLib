package paths;
/*
1. You've generated a path using a spline to fit points. My one comment here is your path has a very sharp turn. Is this necessary for your mission? As we've talked about, with no means to directly sense vehicle position, skidding is your enemy, and tight turns equal skidding.

2. Assume your along-path velocity follows a trapezoidal profile. Also assume your time steps are small enough to approximate acceleration and heading as constant between time steps. If this is not the case then the analysis gets more complex, so try this first and see how it works.

I will use the following terminology:
k = time step index
dt = duration of a single time step
v = along-path velocity of vehicle centroid , where "centroid" is the midpoint between wheels.
s = distance centroid has traveled along path.
theta = angular position (i.e. heading) of vehicle
w = angular velocity of vehicle
alpha = angular acceleration of vehicle
L = distance between wheels
r = wheel radius
v_left, v_right = linear velocity of wheel center
x_left, y_left, x_right, y_right = coordinate of wheel center
w_left, w_right = angular velocity of wheel
alpha_left, alpha_right = angular acceleration of wheel
p_left, p_right = angular position of wheel

3. Integrate velocity profile to get along-path distance: s(k) = s(k-1) + v(k) * dt

4. Compute heading at current position: theta(k) = derivative_of_spline_at_point( s(k) ).

5. Compute angular velocity. It's possible to calculate given path velocity and curvature, but probably just approximate as w(k) = (theta(k) - theta(k-1))/dt

6. Wheel linear velocities are: v_left(k) = v(k) + w(k) * L/2 and v_right(k) = v(k) - w(k) * L/2

7. Wheel angular velocities are: w_left(k) = v_left(k) / r and w_right(k) =v_right(k) / r

8. Now your going to want to validate your path against the physical capabilities of your vehicle by checking the wheel accelerations at every point on the path. You can approximate the required angular acceleration of a wheel as: alpha_left(k) = (w_left(k) - w_left(k-1)) / dt. And this should be compared against an experimentally determined maximum wheel acceleration that is achievable on a fully loaded vehicle and does not slip the wheels.

8. Integrating wheel velocities to get wheel positions accumulates several earlier approximations, so errors will be greatest here. I think an acceptable result can be achieved with small enough dt.  Assuming you run PID wheel position control at each wheel, your reference angular position at any time step will be: p_left(k) = p_left(k-1) + w_left(k) * dt. This would be compared to encoder position from the left wheel encoder for the error input to the left wheel PID.

9. Finally you want to generate wheel paths to validate your algorithms. x_left(k) = x_left(k-1) + v_left(k) * sin(theta(k)),  y_left(k) = x_left(k-1) + v_left(k) * cos(theta(k))
 */
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
