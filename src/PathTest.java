import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import scadlib.extra.GoodGraphing;
import scadlib.paths.FastPathPlanner;
import scadlib.paths.MotionPath;
import scadlib.paths.Point;
import scadlib.paths.Position;
import scadlib.paths.Util;

/**
 * Overarching class to run various paths.
 * 
 * @author Sc2ad
 *
 */
public class PathTest {
	@SuppressWarnings("javadoc")
	public static void main(String[] args) {
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
		
		GoodGraphing figure = new GoodGraphing(new double[][]{{0,0}});
		figure.xGridOn();
		figure.yGridOn();
		figure.setYLabel("Y (inches)");
		figure.setXLabel("X (inches)");
		double width = 648;
		double height = 324.5;
		double robotWidth = 25.75;
		double robotLength = 37.25;
		double switchLength = 38.719;
		double distanceToSwitchFromWall = 85.25;
		double distanceToSwitchFromAlliance = 140;
		double delta = 10;
		double widthOfSwitch = 56;
		figure.setXTic(0, width, 12);
		figure.setYTic(0, height, 12);
		// Add the close switch
		// 38.719 is length of switch
		figure.addData(new double[][]{
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
		}, Color.black);
		
		double xDelta = 10;
		double yDelta = 0;
		
		double[][] leftPath = new double[][]{
			{robotLength/2, height / 2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2+delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, height-distanceToSwitchFromWall-switchLength/2+yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta}
		};
		
		double[][] rightPath = new double[][]{
			{robotLength/2, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2-delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, distanceToSwitchFromWall+switchLength/2-yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, distanceToSwitchFromWall+switchLength/2-yDelta}
		};
//		double[][] rightPath = new double[][]{
//			{11, 162.5},
//			{30, 162.5},
//			{47, 159},
//			{57, 145},
//			{70, 128},
//			{82.4, 111},
//			{105, 105},
//			{125.4, 105}
//		};
		
		FastPathPlanner fpp = new FastPathPlanner(leftPath);
		fpp.calculate(5, 0.04, robotWidth);
		
		figure.addData(fpp.nodeOnlyPath, Color.black);
		figure.addData(fpp.smoothPath, Color.red, Color.blue);
		figure.addData(fpp.leftPath, Color.red);
		figure.addData(fpp.rightPath, Color.green);
		
		GoodGraphing fig = new GoodGraphing(fpp.smoothCenterVelocity, null, Color.blue);
		fig.yGridOn();
		fig.xGridOn();
		fig.setYLabel("Velocity (in/s)");
		fig.setXLabel("time (seconds)");
		fig.setTitle("Velocity profile\nLeft = Red, Right = Green");
		fig.addData(fpp.smoothLeftVelocity, Color.red);
		fig.addData(fpp.smoothRightVelocity, Color.green);
		
		double[][] leftV = fpp.smoothLeftVelocity;
		double[][] rightV = fpp.smoothRightVelocity;
		
		for (int i = 0; i < leftV.length; i++) {
			System.out.print(leftV[i][0]);
			System.out.println(", "+leftV[i][1]);
		}
		
		GoodGraphing pos = new GoodGraphing(fpp.nodeOnlyPath, Color.blue, Color.green);
		pos.yGridOn();
		pos.xGridOn();
		pos.setYLabel("Y (inches)");
		pos.setXLabel("X (inches)");
		pos.setTitle("Top Down: (24x27 ft)");
//		pos.setXTic(0, 50, 5);
		pos.setXTic(0, 27*12, 12);
		pos.setYTic(0, 24*12, 12);
		pos.addData(fpp.smoothPath, Color.red, Color.blue);
		
		pos.addData(fpp.leftPath, Color.magenta);
		pos.addData(fpp.rightPath, Color.magenta);
		
		System.out.println("double[][] arr = new double[][]{");
		for (int i = 0; i < fpp.smoothPath.length-1; i++) {
			System.out.println("\t{"+fpp.smoothPath[i][0]+", "+fpp.smoothPath[i][1]+"},");
		}
		System.out.println("\t{"+fpp.smoothPath[fpp.smoothPath.length-1][0]+", "+fpp.smoothPath[fpp.smoothPath.length-1][1]+"}");
		System.out.println("};");
		System.out.println(fpp.getLeftArclength()[fpp.leftPath.length-1]);
		System.out.println(fpp.getRightArclength()[fpp.rightPath.length-1]);
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