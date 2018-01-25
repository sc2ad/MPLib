import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import extra.RobotPathFollower;
import paths.CombinedPath;
import paths.Hold;
import paths.IntegralPath;
import paths.LinearDerivativePath;
import paths.MotionPath;
import paths.Point;
import paths.Position;
import paths.RobotPath;
import paths.Spline;
import paths.Trajectory;
import paths.Util;

/**
 * Overarching class to run various paths.
 * 
 * @author Sc2ad
 *
 */
public class PathTest {
	@SuppressWarnings("javadoc")
	public static void main(String[] args) {
		// ALL OF THESE LINES CAN HAVE A SIMPLIFIED TYPE OF MotionPath
		CombinedPath jerkAccelTrap = new CombinedPath.LongitudalTrapezoid(0, 5, 2, 2); // params: vel, accel, jerk
		IntegralPath jerkAccel = new IntegralPath(jerkAccelTrap);
		CombinedPath jerkDecelTrap = new CombinedPath.LongitudalTrapezoid(5, -5, -2, -2);
		IntegralPath jerkDecel = new IntegralPath(jerkDecelTrap);
		
		CombinedPath jerkAccelTrapDown = new CombinedPath.LongitudalTrapezoid(0, -5, -2, -2);
		IntegralPath jerkAccelDown = new IntegralPath(jerkAccelTrapDown);
		CombinedPath jerkDecelTrapDown = new CombinedPath.LongitudalTrapezoid(-5, 5, 2, 2);
		IntegralPath jerkDecelDown = new IntegralPath(jerkDecelTrapDown);
		
		CombinedPath pp = new CombinedPath(0, jerkAccel, new LinearDerivativePath(10,5), jerkDecel);
		CombinedPath pdown = new CombinedPath(0, jerkAccelDown, new LinearDerivativePath(-10,-5), jerkDecelDown);
		
		CombinedPath pap = new CombinedPath(0, pp, new Hold(5), pdown);
		CombinedPath pap2 = (CombinedPath) pap.copy();
		CombinedPath papDown = new CombinedPath(0, pdown.copy(), new Hold(5), pp.copy());
		CombinedPath pap2Down = (CombinedPath) papDown.copy();
		
		
		
		CombinedPath p = new CombinedPath(0, pap, new Hold(5), pap2, new Hold(5), papDown, new Hold(5), pap2Down);
		
		MotionPath trap = new CombinedPath.LongitudalTrapezoid(0, 100, 10, 1);
		MotionPath intTrap = new IntegralPath(0, trap);
		MotionPath coast = new IntegralPath(new Hold(5, 100));
		MotionPath trapDown = new CombinedPath.LongitudalTrapezoid(100, -100, -10, -1);
		MotionPath intDown = new IntegralPath(0, trapDown);
		
		CombinedPath triangleIntegral = new CombinedPath(0, intTrap, coast, intDown);
		
		// gyro test stuff?
		CombinedPath movement = new CombinedPath.LongitudalTrapezoid(0, 250, 10, 50);
		CombinedPath gyro = new CombinedPath.LongitudalTrapezoid(0, 30, 10, 50); // In this case, all units are in deg
//		CombinedPath gggg = new CombinedPath.LongitudalTrapezoid(90, 0, -90, -240);
//		run(gggg);
//		CombinedPath moveBack = new CombinedPath.LongitudalTrapezoid(100, -100, -10, -20);
//		CombinedPath gyroBack = new CombinedPath.LongitudalTrapezoid(30, -30, -10, -50);
//		OverlappingPath pfdsafdsa = new OverlappingPath(moveBack, gyroBack);
//		
//		GyroCombinedPath pasdf = new GyroCombinedPath(0, pasdfasdf, pfdsafdsa);
//		run(movement);
		
//		Point[] points = new Point[]{
//				new Point(-1, 0.86199480, 0.155362, false),
//				new Point(-0.5, 0.95802009, 0.232695, false), // Middle deriv is useless
//				new Point(0, 1.0986123, 0.333333, false),
//				new Point(0.5, 1.2943767, 0.451863, false)};
		Point[] frcPath = new Point[]{
				new Point(-23.6, -6.6, 3.5, 1.2, 3, -8.2),
				new Point(-8.65, 5.5, 23.4, 12.8, 2.1, 4.72),
				new Point(28.24, 4.3, -6.9, -3.3, 12.5, 13)
		};
		double vMax = 25;
		Point[] frcPath2 = new Point[]{
				new Point(0, 0, 0, 10, 0),
				new Point(0, 50, vMax, 0, 20),
				new Point(25, 90, vMax, 0, 100),
				new Point(50, 80, vMax, -10, 140),
				new Point(60, 50, 0, 0, 150)
		};
		Point[] frcPath3 = new Point[]{
				new Point(0,0,vMax,0,0),
				new Point(6.25, 12.5, vMax, 0, 90),
				new Point(12.5, 0, vMax, 0, 180),
				new Point(6.25, -12.5, vMax, 0, 270),
				new Point(0,0, vMax, 0, 360)
		};
		
		Spline[] xyspl = Spline.interpolateQuintic(frcPath);
		try {
			double t = 0;
			List<Double> time = new ArrayList<Double>();
			List<Double> x = new ArrayList<Double>();
			List<Double> y = new ArrayList<Double>();
			List<Double> derivX = new ArrayList<Double>();
			List<Double> derivY = new ArrayList<Double>();
			List<Double> derivYX = new ArrayList<Double>();
			while (t <= xyspl[0].seconds) {
				double xx = xyspl[0].get(t);
				double yy = xyspl[1].get(t);
				time.add(t);
				x.add(xx);
				y.add(yy);
				derivX.add(xyspl[0].getDerivative(t));
				derivY.add(xyspl[1].getDerivative(t));
				derivYX.add(xyspl[1].getDerivative(t)/xyspl[0].getDerivative(t));
				System.out.println("xHat: "+xx+"\tyHat: "+yy);
				t += 0.01;
			}
			System.out.println("X:\n"+xyspl[0]);
			System.out.println("Y:\n"+xyspl[1]);
			System.out.println(xyspl[0].getArclength());
			System.out.println(xyspl[1].getArclength());
			derivYX.remove(0);
			derivYX.add(derivYX.get(derivYX.size()-1));
			viewGraph(x,y,frcPath);
//			viewGraph(time,derivX);
//			viewGraph(time,derivY);
//			viewGraph(time,x);
//			viewGraph(time,y);
//			viewGraph(time,derivYX);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Trajectory traj = new Trajectory(xyspl);
		List<Double> heading = new ArrayList<Double>();
		List<Double> curvature = new ArrayList<Double>();
		Position[][] poses = traj.getLeftRightPositions(22);
		try {
			List<Double> time = new ArrayList<Double>();
			List<Double> x = new ArrayList<Double>();
			List<Double> y = new ArrayList<Double>();
			List<Double> derivX = new ArrayList<Double>();
			List<Double> derivY = new ArrayList<Double>();
			List<Double> omegas = new ArrayList<Double>();
			Position[] left = poses[0];
			Position[] right = poses[1];
			double t = 0;
			while (t <= xyspl[0].seconds) {
				double xx = traj.getX(t);
				double yy = traj.getY(t);
				time.add(t);
				x.add(xx);
				y.add(yy);
				derivX.add(xyspl[0].getDerivative(t));
				derivY.add(xyspl[1].getDerivative(t));
				heading.add(traj.getHeading(t));
				curvature.add(traj.getCurvature(t));
				omegas.add(traj.getOmega(t));
				System.out.println("xHat: "+xx+"\tyHat: "+yy);
				t += 0.01;
			}
			System.out.println(traj.getLeftArclength());
			System.out.println(traj.getRightArclength());
			System.out.println(traj.getArclength());
			viewGraph(x, y, left, right);
			viewGraph(time, heading);
//			viewGraph(time, curvature);
//			viewGraph(time, derivX);
//			viewGraph(time, derivY);
			viewGraph(time, omegas);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		RobotPath path = new RobotPath(traj, Util.getDoubleArr(heading), Util.getDoubleArr(curvature));
		System.out.println("Max abs(curvature): "+path.getMaxCurvature());
		double v = 10, a = 10, omega = 50;
		List<Double> time = new ArrayList<Double>();
		List<Double> head = new ArrayList<Double>();
		List<Double> omeg = new ArrayList<Double>();
		List<Double> left = new ArrayList<Double>();
		List<Double> right = new ArrayList<Double>();
		List<Double> leftV = new ArrayList<Double>();
		List<Double> rightV = new ArrayList<Double>();
		List<Double> leftA = new ArrayList<Double>();
		List<Double> rightA = new ArrayList<Double>();
		List<Double> leftF = new ArrayList<Double>();
		List<Double> rightF = new ArrayList<Double>();
		List<Double> leftP = new ArrayList<Double>();
		List<Double> rightP = new ArrayList<Double>();
		List<Double> middleP = new ArrayList<Double>();
		List<Double> mx = new ArrayList<Double>();
		List<Double> my = new ArrayList<Double>();
		List<Position> l = new ArrayList<Position>();
//		List<Double> ly = new ArrayList<Double>();
		List<Position> r = new ArrayList<Position>();
//		List<Double> ry = new ArrayList<Double>();
		double leftI = 0, rightI = 0, middleI = 0, middleX = frcPath[0].x, middleY = frcPath[0].y, leftX = 0, leftY = 0, rightX = 0, rightY = 0;
		MotionPath[] pafs = path.getLeftRightPaths(v, a, omega);
		double width = 22, wheelDiameter = 6, ticksPerRev = 1024;
		RobotPathFollower follower = new RobotPathFollower(width, wheelDiameter, ticksPerRev, new double[]{0,0,0,1.0/path.getMaxVelocity()}, new double[]{0,0,0,1.0/path.getMaxVelocity()});
		follower.setPath(path);
		follower.start();
		double t = 0;
		boolean setup = false;
		while (t <= path.getMainPath().getTotalTime()) {
			time.add(t);
			head.add(path.getHeading(t));
			left.add(pafs[0].getPosition(t));
			right.add(pafs[1].getPosition(t));
			leftV.add(pafs[0].getSpeed(t));
			rightV.add(pafs[1].getSpeed(t));
			leftA.add(pafs[0].getAccel(t));
			rightA.add(pafs[1].getAccel(t));
			omeg.add(path.getOmega(t));
			double lOut = follower.getLeftOut(t, follower.convertToTicks(path.getMainPath().getPosition(t)));
			double rOut = follower.getRightOut(t, follower.convertToTicks(path.getMainPath().getPosition(t)));
			System.out.println(lOut+", "+rOut+", "+follower.getDeltaV(t));
			leftF.add(lOut);
			rightF.add(rOut);
			leftP.add(leftI);
			rightP.add(rightI);
			middleP.add(middleI);
			leftI += lOut * 0.025;
			rightI += rOut * 0.025;
			middleI += (lOut + rOut) / 2 * 0.025;
			// Convert the following back into units instead of ticks
			if (!setup) {
				setup = true;
				// NEED TO FIX THIS ERROR CHECKING SHENANIGANS
				// setup initial points correctly
				leftX = traj.getX(t) - follower.convertToUnits(lOut) * width / 2 * Math.cos(Math.toRadians(path.getHeading(t)));
				leftY = traj.getY(t) + follower.convertToUnits(lOut) * width / 2 * Math.sin(Math.toRadians(path.getHeading(t)));
				rightX = traj.getX(t) + follower.convertToUnits(rOut) * width / 2 * Math.cos(Math.toRadians(path.getHeading(t)));
				rightY = traj.getY(t) - follower.convertToUnits(rOut) * width / 2 * Math.sin(Math.toRadians(path.getHeading(t)));
			} else {
				leftX += follower.convertToUnits(lOut) * 0.025 * Math.sin(Math.toRadians(path.getHeading(t)));
				leftY += follower.convertToUnits(lOut) * 0.025 * Math.cos(Math.toRadians(path.getHeading(t)));
				rightX += follower.convertToUnits(rOut) * 0.025 * Math.sin(Math.toRadians(path.getHeading(t)));
				rightY += follower.convertToUnits(rOut) * 0.025 * Math.cos(Math.toRadians(path.getHeading(t)));
			}
			mx.add(middleX);
			my.add(middleY);
			l.add(new Position(leftX, leftY, path.getHeading(t)));
//			ly.add(leftY);
			r.add(new Position(rightX, rightY, path.getHeading(t)));
//			ry.add(rightY);
			middleX += (follower.convertToUnits(lOut) + follower.convertToUnits(rOut)) / 2 * 0.025 * Math.sin(Math.toRadians(path.getHeading(t)));
			middleY += (follower.convertToUnits(lOut) + follower.convertToUnits(rOut)) / 2 * 0.025 * Math.cos(Math.toRadians(path.getHeading(t))); 
			t += 0.025;
		}
		viewGraph(time, head);
		viewGraph(time, omeg);
		viewGraph(time, left, leftV, leftA);
		viewGraph(time, right, rightV, rightA);
		viewGraph(time, leftF);
		viewGraph(time, rightF);
		viewGraph(time, leftP);
		viewGraph(time, rightP);
		viewGraph(time, middleP);
		viewGraph(mx, my, frcPath);
		Position[] tempL = new Position[l.size()];
		Position[] tempR = new Position[r.size()];
		for (int i = 0; i < tempL.length; i++) {
			tempL[i] = l.get(i);
			tempR[i] = r.get(i);
		}
		viewGraph(mx, my, tempL, tempR);
	}
	/**
	 * Displays various information about the path provided.
	 * It also displays a graph.
	 * 
	 * @param p
	 */
	public static void run(MotionPath p) {
		double t = 0;
		ArrayList<Double> times = new ArrayList<Double>();
		ArrayList<Double> pos = new ArrayList<Double>();
		ArrayList<Double> spd = new ArrayList<Double>();
		ArrayList<Double> accel = new ArrayList<Double>();
		
		while (Util.lessThan(t, p.getTotalTime()+1, 0.00002)) {
			System.out.println("Time: "+t+", Position: "+p.getPosition(t)+", Speed: "+p.getSpeed(t)+", Acceleration: "+p.getAccel(t));
			times.add(t);
			pos.add(p.getPosition(t));
			spd.add(p.getSpeed(t));
			accel.add(p.getAccel(t));
			t += 0.05;
		}
//		System.out.println(p.getTotalDistance());
		viewGraph(times,pos,spd,accel);
		Util.writeCSV(System.getProperty("user.dir")+"/out", times, spd, pos, accel);
	}
	/**
	 * Graphs information on the following data lists.
	 * 
	 * @param times the time {@link List} to visualize
	 * @param pos the positions {@link List} to visualize
	 * @param spd the speeds {@link List} to visualize
	 * @param accel the accelerations {@link List} to visualize
	 */
	public static void viewGraph(List<Double> times, List<Double> pos, List<Double> spd, List<Double> accel) {
		double[] timeSteps = Util.getDoubleArr(times);
		double[] posSteps = Util.getDoubleArr(pos);
		double[] speedSteps = Util.getDoubleArr(spd);
		double[] accelSteps = Util.getDoubleArr(accel);
		
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("position", timeSteps, posSteps);
		plot.addLinePlot("speed", timeSteps, speedSteps);
		plot.addLinePlot("accel", timeSteps, accelSteps);
		JFrame frame = new JFrame("the panel");
		frame.setContentPane(plot);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	public static void viewGraph(List<Double> x, List<Double> y) {
		double[] xReal = Util.getDoubleArr(x);
		double[] yReal = Util.getDoubleArr(y);
		
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("real space", xReal, yReal);
		JFrame frame = new JFrame("x-y panel");
		frame.setContentPane(plot);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	public static void viewGraph(List<Double> x, List<Double> y, Point[] points) {
		double[] xReal = Util.getDoubleArr(x);
		double[] yReal = Util.getDoubleArr(y);
		double[] xWp = new double[points.length];
		double[] yWp = new double[points.length];
		
		for (int i = 0; i < points.length; i++) {
			xWp[i] = points[i].x;
			yWp[i] = points[i].y;
		}
		
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("real space", xReal, yReal);
		plot.addScatterPlot("real waypoints", xWp, yWp);
		JFrame frame = new JFrame("x-y panel");
		frame.setContentPane(plot);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	public static void viewGraph(List<Double> x, List<Double> y, Position[] left, Position[] right) {
		double[] xReal = Util.getDoubleArr(x);
		double[] yReal = Util.getDoubleArr(y);
		
		double[] lx = new double[left.length];
		double[] ly = new double[left.length];
		double[] rx = new double[right.length];
		double[] ry = new double[right.length];
		for (int i = 0; i < left.length; i++) {
			lx[i] = left[i].x;
			ly[i] = left[i].y;
			rx[i] = right[i].x;
			ry[i] = right[i].y;
		}
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("real space", xReal, yReal);
		plot.addLinePlot("left", lx, ly);
		plot.addLinePlot("right", rx, ry);
		JFrame frame = new JFrame("triple curves");
		frame.setContentPane(plot);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}