import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import paths.CombinedPath;
import paths.Hold;
import paths.IntegralPath;
import paths.LinearDerivativePath;
import paths.MotionPath;
import paths.Spline;
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
//		CombinedPath moveBack = new CombinedPath.LongitudalTrapezoid(100, -100, -10, -20);
//		CombinedPath gyroBack = new CombinedPath.LongitudalTrapezoid(30, -30, -10, -50);
//		OverlappingPath pfdsafdsa = new OverlappingPath(moveBack, gyroBack);
//		
//		GyroCombinedPath pasdf = new GyroCombinedPath(0, pasdfasdf, pfdsafdsa);
		run(pasdfasdf);
//		run(movement);
		Spline.Point[] points = new Spline.Point[]{
				new Spline.Point(-1, 0.86199480, 0.155362),
				new Spline.Point(-0.5, 0.95802009, 0), // Middle deriv is useless
				// TODO IDEA: INCORPORATE HAVING AN INTEGRAL FOR A PATH THAT DOES X VS DERIVATIVE (SPLINE FITS THAT INSTEAD)
				new Spline.Point(0, 1.0986123, 0),
				new Spline.Point(0.5, 1.2943767, 0.451863)};
		try {
			Spline s = Spline.interpolate(points);
			double nx = -1.1;
			List<Double> x = new ArrayList<Double>();
			List<Double> y = new ArrayList<Double>();
			while (nx <= 1.04) {
				x.add(nx);
				y.add(s.getY(nx));
				System.out.println("x: "+nx+"\tyHat: "+s.getY(nx));
				nx += 0.05;
			}
			System.out.println();
			System.out.println(s);
			viewGraph(x,y,points);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		while (Util.lessThan(t, p.getTotalTime(), 0.00002)) {
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
	// This entire method is kinda a big BAD BAD BAD BAD *BAD* repeat
	public static void run(GyroMotionPath p) {
		double t = 0;
		double sx=0, sy=0, lx=0, ly=0, ex=0, ey=0, tstart=0, tend=0;
		
		ArrayList<Double> times = new ArrayList<Double>();
		ArrayList<Double> pos = new ArrayList<Double>();
		ArrayList<Double> spd = new ArrayList<Double>();
		ArrayList<Double> accel = new ArrayList<Double>();
		ArrayList<Double> theta = new ArrayList<Double>();
		ArrayList<Double> omega = new ArrayList<Double>();
		ArrayList<Double> alpha = new ArrayList<Double>();
		
		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		
		while (Util.lessThan(t, p.getTotalTime(), 0.00002)) {
			System.out.println("Time: "+t+", Position: "+p.getPosition(t)+", Speed: "+p.getSpeed(t)+", Acceleration: "+p.getAccel(t)+", Angle: "+p.getAngle(t)+", Omega: "+p.getOmega(t)+", Alpha: "+p.getAlpha(t)+", (X,Y): ("+p.getX(t)+","+p.getY(t)+")");
			times.add(t);
			pos.add(p.getPosition(t));
			spd.add(p.getSpeed(t));
			accel.add(p.getAccel(t));
			theta.add(p.getAngle(t));
			omega.add(p.getOmega(t));
			alpha.add(p.getAlpha(t));
			if (p.getAlpha(t) == 0) {
				// start!
				if (sx == 0 && sy == 0) {
					sx = p.getX(t);
					sy = p.getY(t);
					tstart = t;
				}
				lx = p.getX(t);
				ly = p.getY(t);
			} else {
				ex = lx;
				ey = ly;
				if (tend == 0 && tstart != 0) {
					tend = t;
				}
			}
			x.add(p.getX(t));
			y.add(p.getY(t));
			t += 0.05;
		}
//		System.out.println(p.getTotalDistance());
		System.out.println(sx);
		System.out.println(sy);
		System.out.println(ex);
		System.out.println(ey);
		System.out.println("\ndeltax: "+(ex-sx));
		System.out.println("deltay: "+(ey-sy));
		System.out.println("dt: "+(tend-tstart));
		if (!Util.equals(p.getSpeed(tstart), p.getSpeed(tend), 0.00002) || !Util.equals(p.getOmega(tstart), p.getOmega(tend), 0.00002)) {
			// validation check
			System.out.println(p.getSpeed(tstart)+" != "+p.getSpeed(tend));
			System.out.println(p.getOmega(tstart)+" != "+p.getOmega(tend));
			throw new IllegalArgumentException("Accelerated during turn! Change params");
		}
		
		System.out.println("dx: "+p.getX(tstart, tend, 20000));
		System.out.println("dy: "+dy);
		
		viewGraph(times,pos,spd,accel);
		viewGraph(times,theta,omega,alpha); // visualize theta curve at the same time
		viewGraph(x,y);
		Util.writeCSV(System.getProperty("user.dir")+"/out", times, spd, pos, accel, theta, omega, alpha, x, y);
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
	public static void viewGraph(List<Double> x, List<Double> y, Spline.Point[] points) {
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
}