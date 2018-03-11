import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import extra.GoodGraphing;
import paths.CombinedPath;
import paths.Hold;
import paths.IntegralPath;
import paths.LinearDerivativePath;
import paths.MotionPath;
import splines.PathPlanner;
import splines.Point;
import splines.Position;
import splines.Samples;
import splines.Spline;
import splines.Trajectory;
import util.Util;

/**
 * Overarching class to run various paths.
 * 
 * @author Sc2ad
 *
 */
public class PathTest {
	static double fieldW = 648;
	static double fieldH = 324.5;
	private static double[][] makeSwitch(int switchNum) {
		double height = 324.5;
		double switchLength = 38.719;
		double distanceToSwitchFromWall = 85.25;
		double distanceToSwitchFromAlliance = 140;
		double widthOfSwitch = 56;
		if (switchNum <= 0) {
			return new double[][]{
				{distanceToSwitchFromAlliance, distanceToSwitchFromWall},
				{distanceToSwitchFromAlliance, height-distanceToSwitchFromWall},
				{distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall},
				{distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall-switchLength},
				{distanceToSwitchFromAlliance, height-distanceToSwitchFromWall-switchLength}, // 200.5
				{distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall-switchLength},
				{distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall+switchLength},
				{distanceToSwitchFromAlliance, distanceToSwitchFromWall+switchLength},
				{distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall+switchLength},
				{distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall},
				{distanceToSwitchFromAlliance, distanceToSwitchFromWall}
			};
		}
		return new double[][]{
			{fieldW-distanceToSwitchFromAlliance, distanceToSwitchFromWall},
			{fieldW-distanceToSwitchFromAlliance, height-distanceToSwitchFromWall},
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall},
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall-switchLength},
			{fieldW-distanceToSwitchFromAlliance, height-distanceToSwitchFromWall-switchLength}, // 200.5
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, height-distanceToSwitchFromWall-switchLength},
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall+switchLength},
			{fieldW-distanceToSwitchFromAlliance, distanceToSwitchFromWall+switchLength},
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall+switchLength},
			{fieldW-distanceToSwitchFromAlliance+widthOfSwitch, distanceToSwitchFromWall},
			{fieldW-distanceToSwitchFromAlliance, distanceToSwitchFromWall}
		};
	}
	private static double[][] makePlatform() {
		double distanceToEdgeofPlatform = 261.47;
		double distanceToPlatformFromWall = 95.25;
		return new double[][]{
			{distanceToEdgeofPlatform, distanceToPlatformFromWall},
			{distanceToEdgeofPlatform, fieldH-distanceToPlatformFromWall},
			{fieldW-distanceToEdgeofPlatform, fieldH-distanceToPlatformFromWall},
			{fieldW-distanceToEdgeofPlatform, distanceToPlatformFromWall},
			{distanceToEdgeofPlatform, distanceToPlatformFromWall}
		};
	}
	private static double[][] makeNullzones(int upperOrLower) {
		double distanceToEdgeX = 288;
		double distanceToEdgeY = 95.25;
		if (upperOrLower <= 0) {
			return new double[][]{
				{distanceToEdgeX, fieldH},
				{fieldW-distanceToEdgeX, fieldH},
				{fieldW-distanceToEdgeX, fieldH-distanceToEdgeY},
				{distanceToEdgeX, fieldH-distanceToEdgeY},
				{distanceToEdgeX, fieldH}
			};
		}
		return new double[][]{
			{distanceToEdgeX, distanceToEdgeY},
			{fieldW-distanceToEdgeX, distanceToEdgeY},
			{fieldW-distanceToEdgeX, 0},
			{distanceToEdgeX, 0},
			{distanceToEdgeX, distanceToEdgeY}
		};
	}
	private static double[][] makeScale() {
		double distanceX = 299.65;
		double distanceY = 71.57;
		double scalePlatformLength = 36;
		return new double[][]{
			{distanceX, distanceY},
			{distanceX, fieldH-distanceY},
			{fieldW-distanceX, fieldH-distanceY},
			{fieldW-distanceX, fieldH-distanceY-scalePlatformLength},
			{distanceX, fieldH-distanceY-scalePlatformLength},
			{fieldW-distanceX, fieldH-distanceY-scalePlatformLength},
			{fieldW-distanceX, distanceY+scalePlatformLength},
			{distanceX, distanceY+scalePlatformLength},
			{fieldW-distanceX, distanceY+scalePlatformLength},
			{fieldW-distanceX, distanceY},
			{distanceX, distanceY}
		};
	}
	private static double[][] makePortals() {
		double portalX = 36;
		double portalY = 29.69;
		return new double[][]{
			{0, fieldH-portalY},
			{portalX, fieldH},
			{fieldW-portalX, fieldH},
			{fieldW, fieldH-portalY},
			{fieldW, portalY},
			{fieldW-portalX, 0},
			{portalX, 0},
			{0, portalY}
		};
	}
	private static double[][] makeExchanges(int exchangeNumber) {
		double portalY = 29.69;
		double exchangeDistance = 6 * 12 + portalY;
		if (exchangeNumber == 0) {
			return new double[][]{
				{0, fieldH-exchangeDistance},
				{5, fieldH-exchangeDistance},
				{5, fieldH-exchangeDistance-4*12},
				{0, fieldH-exchangeDistance-4*12}
			};
		} else {
			return new double[][]{
				{fieldW, exchangeDistance},
				{fieldW-5, exchangeDistance},
				{fieldW-5, exchangeDistance+4*12},
				{fieldW, exchangeDistance+4*12}
			};
		}
	}
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
		double width = 648;
		double height = 324.5;
		double robotWidth = 25.75;
		double robotLength = 37.25;
		double switchLength = 38.719;
		double distanceToSwitchFromWall = 85.25;
		double distanceToSwitchFromAlliance = 140;
		double delta = 10;
		double widthOfSwitch = 56;
		double xDelta = 10;
		double yDelta = 0;
		
		double stage2x1 = 5;
		double stage2y1 = 2;
		double stage2x2 = 10;
		double stage2y2 = 10;
		double stage2y3 = 15;
		
		double[][] leftPathArr = new double[][]{
			{robotLength/2, height / 2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2+delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, height-distanceToSwitchFromWall-switchLength/2+yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta}
		};
		double displacementV = 10;
		Point[] leftPath = new Point[]{
			new Point(robotLength/2, height/2, displacementV, 0, 2, 0),
			new Point((distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2+5, 10, 5, 5, 2),
			new Point(distanceToSwitchFromAlliance-robotLength/2-delta, height-distanceToSwitchFromWall-switchLength/2+yDelta-5, displacementV, 5, 5, 2),
			new Point(distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta, displacementV, 0, 0, 0),
		};
		
		Point[] points0 = new Point[]{
				new Point(robotLength/2, height/2, 10, 10, 90),
				new Point((distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta-20, height/2+5, 10, 1, 60),	
		};
		
		Point[] points = new Point[]{
//			new Point(robotLength/2, height/2, 10, 10, 90),
			new Point((distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta-20, height/2+5, 10, 1, 60),
			new Point(distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta, 100, 0, 90),
		};
		Spline[] x0spl = Spline.interpolateQuintic(points0);
		Spline[] xyspl = Spline.interpolateQuintic(points);
		
		
		Trajectory frcPath0Traj = new Trajectory(Samples.LOW, x0spl);
		Trajectory frcPathTraj = new Trajectory(Samples.LOW, xyspl);
		Trajectory frcPathT2 = new Trajectory(Samples.LOW, frcPath0Traj, frcPathTraj);
//		Trajectory frcPathT2 = new Trajectory(Samples.LOW, new Spline[]{new Spline(x0spl[0].x0,xyspl[0].x1,new Spline[]{x0spl[0], xyspl[0]}),new Spline(x0spl[1].x0,xyspl[1].x1,new Spline[]{x0spl[1], xyspl[1]})});
		PathPlanner p0 = new PathPlanner(frcPath0Traj, robotWidth);
		PathPlanner pather = new PathPlanner(frcPathTraj, robotWidth);
		PathPlanner combined = new PathPlanner(frcPathT2, robotWidth);
		
		p0.calculateSmoothVelocities(100, 200, 0.02);
		pather.calculateSmoothVelocities(100, 200, 0.02);
		
		combined.calculateSmoothVelocities(1, 200, 0.02);
		
		display(p0);
		display(pather);
		display(combined);
		
//		System.out.println(pather.getLeftArclength());
//		System.out.println(pather.getRightArclength());

		GoodGraphing figure = makeField(width, height);
		
		addData(figure, p0, points0);
		addData(figure, pather, points);
		
		GoodGraphing f2 = makeField(width, height);
		
		addData(f2, combined, points0);
		
		GoodGraphing velFigure = new GoodGraphing(pather.getCenterVelocities(), Color.blue, Color.blue);
		velFigure.setTitle("Velocity profile\nCenter = blue\nLeft = red\nRight = green");
		velFigure.setXLabel("Time (s)");
		velFigure.setYLabel("Magnitude (inches / second)");
		velFigure.xGridOn();
		velFigure.yGridOn();
		velFigure.addData(pather.getLeftSmoothVelocities(), Color.red);
		velFigure.addData(pather.getRightSmoothVelocities(), Color.green);
		
		GoodGraphing heading = new GoodGraphing(new double[][]{{0,0}}, Color.black, Color.black);
		heading.setTitle("Headings and Omegas\nHeadings = red\nOmegas = black");
		heading.setXLabel("Time (s)");
		heading.setYLabel("Radians or Radians / second");
		heading.xGridOn();
		heading.yGridOn();
		addHeadings(heading, p0, pather);
		
		GoodGraphing heading2 = new GoodGraphing(new double[][]{{0,0}}, Color.black, Color.black);
		heading.setTitle("Headings and Omegas\nHeadings = red\nOmegas = black");
		heading.setXLabel("Time (s)");
		heading.setYLabel("Radians or Radians / second");
		heading.xGridOn();
		heading.yGridOn();
		addHeadings(heading2, combined);
	}
	public static void display(PathPlanner pather) {
		int maxIndex = 0;
		for (int i = 0; i < pather.getOmegas().length; i++) {
			if (pather.getOmegas()[i][1] > pather.getOmegas()[maxIndex][1]) {
				maxIndex = i;
			}
		}
		
		System.out.println();
		System.out.println("X and Y of point at max velocity: ("+pather.getCenterPath()[maxIndex][0]+", "+pather.getCenterPath()[maxIndex][1]+")");
	}
	public static GoodGraphing makeField(double width, double height) {
		GoodGraphing figure = new GoodGraphing(makeSwitch(0), Color.black, null);
		figure.addData(makeScale(), Color.black);
		figure.addData(makeExchanges(0), Color.black);
		figure.addData(makePlatform(), Color.black);
		figure.addData(makePortals(), Color.black);
		figure.addData(makeSwitch(1), Color.black);
		figure.addData(makeExchanges(1), Color.black);
		figure.setXTic(0, width, 10);
		figure.setYTic(0, height, 10);
		figure.setXLabel("Field width (inches)");
		figure.setYLabel("Field width (inches)");
		figure.setTitle("Field with left and right paths\nCenter = blue\nLeft = red\nRight = green");
		figure.xGridOn();
		figure.yGridOn();
		return figure;
	}
	public static void addData(GoodGraphing figure, PathPlanner pather, Point[] points) {
		double[][] temp = new double[points.length][2];
		for (int i = 0; i < points.length; i++) {
			temp[i][0] = points[i].x;
			temp[i][1] = points[i].y;
		}
		figure.addData(temp, Color.black, Color.black);
		
		figure.addData(pather.getCenterPath(), Color.blue, Color.blue);
		figure.addData(pather.getLeftPath(), Color.red);
		figure.addData(pather.getRightPath(), Color.green);
	}
	public static void addHeadings(GoodGraphing figure, PathPlanner... pathers) {
		
		for (int i = 0; i < pathers.length; i++) {
			PathPlanner pather = pathers[i];
			double[][] tempOmegas = pather.getOmegas();
			double[][] tempHeadings = pather.getHeadings();
			if (i != 0) {
				for (int j = 0; j < tempOmegas.length; j++) {
					tempOmegas[j][0] += pathers[i-1].getOmegas()[pathers[i-1].getOmegas().length-1][0];
					tempHeadings[j][0] += pathers[i-1].getHeadings()[pathers[i-1].getHeadings().length-1][0];					
				}
			}
			figure.addData(tempOmegas, Color.black, Color.black);
			figure.addData(tempHeadings, Color.red);
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
	public static void viewGraph(List<Double> guessX, List<Double> guessY, List<Double> x, List<Double> y, boolean s) {
		double[] gx = Util.getDoubleArr(guessX);
		double[] gy = Util.getDoubleArr(guessY);
		double[] rx = Util.getDoubleArr(x);
		double[] ry = Util.getDoubleArr(y);
		
		Plot2DPanel plot = new Plot2DPanel();
		plot.addLinePlot("integrated", gx, gy);
		plot.addLinePlot("real x-y", rx, ry);
		JFrame frame = new JFrame("integrals vs errors");
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