package splines;

import paths.CombinedPath;
import paths.MotionPath;
import paths.CombinedPath.LongitudalTrapezoid;

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
	double maxCurvature, T_STEP;
	double vMax;
	double width;
	double wheelRadius;
	double leftTotal, rightTotal, lx, ly, rx, ry;
	MotionPath centralPath, leftPath, rightPath;
	
	public RobotPath(Trajectory t, double[] headings, double[] curvatures, double timeStep) {
		traj = t;
		this.headings = headings;
		this.curvatures = curvatures;
		T_STEP = timeStep;
	}
	public double getMaxVelocity() {
//		return getMainPath().getSpeed(getMainPath().getTotalTime() / 2.0);
		if (vMax == 0) {
			return Double.NaN;
		}
		return vMax;
	}
//	public double getMaxCurvature() {
//		if (maxCurvature == 0) {
//			double max = curvatures[0];
//			for (int i = 1; i < curvatures.length; i++) {
//				if (Math.abs(curvatures[i]) > max) {
//					max = Math.abs(curvatures[i]);
//				}
//			}
//			maxCurvature = max;
//		}
//		return maxCurvature;
//	}
	public MotionPath getMainPath() {
		return centralPath;
	}
	public double getTotalTime() {
		return getMainPath().getTotalTime();
	}
	public double getPosition(double time) {
		return getMainPath().getPosition(time);
	}
	public double getPosition(int tIndex) {
		return getMainPath().getPosition(tIndex * T_STEP);
	}
	public double getSpeed(double time) {
		return getMainPath().getSpeed(time);
	}
	public double getSpeed(int tIndex) {
		return getMainPath().getSpeed(tIndex * T_STEP);
	}
	
	public void constructMainPath(double vMax, double aMax, double omegaMax) {
//		double vReal = vMax > omegaMax / getMaxCurvature() ? omegaMax / getMaxCurvature() : vMax; // Properly sets velocity due to path constraints (this limits entire path)
		double vReal = vMax;
		this.vMax = vMax;
		centralPath = new CombinedPath.LongitudalTrapezoid(0, traj.getArclength(), vReal, aMax);
	}

	public MotionPath[] getLeftRightPaths(double vMax, double aMax, double omegaMax) {

		// TODO add unit conversion
		// Need to develop a path that can have the proper velocity only when
		// omega does not work, instead of limiting the entire path's speed
		if (!(leftPath != null)) {
			constructMainPath(vMax, aMax, omegaMax);
			leftPath = new CombinedPath.LongitudalTrapezoid(0, traj.getLeftArclength(), vMax, aMax);
			rightPath = new CombinedPath.LongitudalTrapezoid(0, traj.getRightArclength(), vMax, aMax);
		}
		return new MotionPath[]{leftPath, rightPath};
	}
  
	public double getHeadingT(double time) {
		// Input should be MotionPath time, not spline path time
//		double s = getMainPath().getPosition(time);
//		return headings[(int)(s / traj.getArclength() * (headings.length-1))]; // Uses arclength to calculate which heading index should be returned
//		return traj.getHeading(traj.seconds * time / getMainPath().getTotalTime());
		return traj.getHeading(traj.seconds * time / getMainPath().getTotalTime());
	}
	public double getHeadingT(int tIndex) {
		return traj.getHeading(traj.seconds * (tIndex * T_STEP) / getTotalTime());
	}
//	public double getHeading(double arclength) {
////		1/2at^2 + vt = s
////		(1/2at^2 - s)/t = v
////		v^2 = v0^2 + 2ax
//		double maxv = getMainPath().getSpeed(getMainPath().getTotalTime()/2);
//		double maxa = getMainPath().getAccel(0);
//		double t0 = maxv / maxa;
//		double dx = getMainPath().getPosition(t0);
//		double t1 = getMainPath().getTotalTime() - t0;
//		double v = maxv;
//		double time = 0;
//		if (arclength > getMainPath().getTotalDistance() - dx) {
//			time = t1;
//			arclength -= getMainPath().getTotalDistance() - dx;
//		}
//		if (arclength < dx) {
//			v = Math.sqrt(2 * maxa * arclength); // because trapezoids r the best
//			time += v / maxa;
//		} else {
//			if (time == t1) {
//				// This is a BIG problem! You are asking for a time that is out of bounds
//				time = getMainPath().getTotalTime();
//			} else {
//				arclength -= dx;
//				time = t0 + arclength / maxv;
//			}
//		}
//		return getHeadingT(time);
//	}
	public double getOmega(double time) {
		// Also converts to degrees / second
//		return traj.getOmega(traj.seconds * time / getMainPath().getTotalTime()) * (traj.seconds / getMainPath().getTotalTime());
		return 0;
	}
	public double getOmega(int tIndex) {
		return (getHeadingT(tIndex * T_STEP) - getHeadingT((tIndex - 1) * T_STEP)) / (T_STEP);
	}
	public void configWidth(double width) {
		// Width must be the same unit as everything else
		this.width = width;
	}
	public void configWheelRadius(double radius) {
		// Radius must be the same unit as everything else
		wheelRadius = radius;
	}
	public double getLeftVelocity(int tIndex) {
		return getSpeed(tIndex) + getOmega(tIndex) * width / 2;
	}
	public double getRightVelocity(int tIndex) {
		return getSpeed(tIndex) - getOmega(tIndex) * width / 2;
	}
	public double getLeftWheelOmega(int tIndex) {
		return getLeftVelocity(tIndex) / wheelRadius;
	}
	public double getRightWheelOmega(int tIndex) {
		return getRightVelocity(tIndex) / wheelRadius;
	}
	public double getLeftAlpha(int tIndex) {
		return (getLeftWheelOmega(tIndex) - getLeftWheelOmega(tIndex - 1)) / T_STEP;
	}
	public double getRightAlpha(int tIndex) {
		return (getRightWheelOmega(tIndex) - getRightWheelOmega(tIndex - 1)) / T_STEP;
	}
	public void resetAccumulation() {
		leftTotal = 0;
		rightTotal = 0;
		lx = traj.getX(0) - width / 2 * traj.getYDerivative(0) / (Math.sqrt(Math.pow(traj.getXDerivative(0), 2) + Math.pow(traj.getYDerivative(0), 2)));
		ly = traj.getY(0) + width / 2 * traj.getXDerivative(0) / (Math.sqrt(Math.pow(traj.getXDerivative(0), 2) + Math.pow(traj.getYDerivative(0), 2)));
		rx = traj.getX(0) + width / 2 * traj.getYDerivative(0) / (Math.sqrt(Math.pow(traj.getXDerivative(0), 2) + Math.pow(traj.getYDerivative(0), 2)));
		ry = traj.getY(0) - width / 2 * traj.getXDerivative(0) / (Math.sqrt(Math.pow(traj.getXDerivative(0), 2) + Math.pow(traj.getYDerivative(0), 2)));
	}
	public double getLeftArclength(int tIndex) {
		leftTotal += getLeftWheelOmega(tIndex) * T_STEP;
		return leftTotal;
	}
	public double getRightArclength(int tIndex) {
		rightTotal += getRightWheelOmega(tIndex) * T_STEP;
		return rightTotal;
	}
	// Error checkers
	// x_left(k) = x_left(k-1) + v_left(k) * sin(theta(k))
	public double getLX(int tIndex) {
		lx += getLeftVelocity(tIndex) * Math.sin(getHeadingT(tIndex)) * T_STEP;
		return lx;
	}
	public double getRX(int tIndex) {
		rx += getRightVelocity(tIndex) * Math.sin(getHeadingT(tIndex)) * T_STEP;
		return rx;
	}
	public double getLY(int tIndex) {
		ly += getLeftVelocity(tIndex) * Math.cos(getHeadingT(tIndex)) * T_STEP;
		return ly;
	}
	public double getRY(int tIndex) {
		ry += getRightVelocity(tIndex) * Math.cos(getHeadingT(tIndex)) * T_STEP;
		return ry;
	}
}
