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
		
		figure.addData(makeSwitch(0), Color.black);
		figure.addData(makeSwitch(1), Color.black);
		figure.addData(makePlatform(), Color.black);
		figure.addData(makeNullzones(0), Color.black);
		figure.addData(makeNullzones(1), Color.black);
		figure.addData(makeScale(), Color.black);
		figure.addData(makePortals(), Color.black);
		figure.addData(makeExchanges(0), Color.black);
		figure.addData(makeExchanges(1), Color.black);
		
		double xDelta = 10;
		double yDelta = 0;
		
		double stage2x1 = 5;
		double stage2y1 = 2;
		double stage2x2 = 10;
		double stage2y2 = 10;
		double stage2y3 = 15;
		double stage2y4 = 40;
		
		double[][] leftPath = new double[][]{
			{robotLength/2, height / 2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2+delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, height-distanceToSwitchFromWall-switchLength/2+yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta}
		};
		
		double[][] leftTurnBack = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x1, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y1},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y2},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y3},
		};
		
		double[][] leftGrabCube = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y3},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta-stage2y4}
		};
		
		double[][] leftReverseCube = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta-stage2y4},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y3}
		};
		
		double[][] leftTurnToSwitch = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y3},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y2},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x1, height-distanceToSwitchFromWall-switchLength/2+yDelta+stage2y1},
			{distanceToSwitchFromAlliance-robotLength/2, height-distanceToSwitchFromWall-switchLength/2+yDelta},
		};
		
		double[][] rightPath = new double[][]{
			{robotLength/2, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2-delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, distanceToSwitchFromWall+switchLength/2-yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, distanceToSwitchFromWall+switchLength/2-yDelta}
		};
		
		double[][] rightTurnBack = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2, distanceToSwitchFromWall+switchLength/2-yDelta},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x1, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y1},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y2},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y3},
		};
		
		double[][] rightGrabCube = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y3},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta+stage2y4}
		};
		
		double[][] rightReverseCube = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta+stage2y4},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y3},
		};
		
		double[][] rightTurnToSwitch = new double[][]{
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y3},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x2, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y2},
			{distanceToSwitchFromAlliance-robotLength/2-stage2x1, distanceToSwitchFromWall+switchLength/2-yDelta-stage2y1},
			{distanceToSwitchFromAlliance-robotLength/2, distanceToSwitchFromWall+switchLength/2-yDelta},
		};
		
		FastPathPlanner fpp = new FastPathPlanner(rightPath);
		FastPathPlanner stage2 = new FastPathPlanner(rightTurnBack); // This path should have left and right inverted and negative!
		FastPathPlanner grabCube = new FastPathPlanner(rightGrabCube);
		FastPathPlanner goBackFromCube = new FastPathPlanner(rightReverseCube);
		FastPathPlanner goBack = new FastPathPlanner(rightTurnToSwitch);
				
		fpp.calculate(3.2, 0.02, robotWidth);
		stage2.calculate(2, 0.02, robotWidth);
		grabCube.calculate(1, 0.02, robotWidth);
		goBackFromCube.calculate(1, 0.02, robotWidth);
		goBack.calculate(2, 0.02, robotWidth);
		
		reversePath(stage2);
		reversePath(goBackFromCube);
		
		addDataToGraph(fpp, figure);
		addDataToGraph(stage2, figure);
		addDataToGraph(grabCube, figure);
		addDataToGraph(goBackFromCube, figure);
		addDataToGraph(goBack, figure);
		
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
	public static void addDataToGraph(FastPathPlanner plannedPath, GoodGraphing fig) {
		fig.addData(plannedPath.nodeOnlyPath, Color.black);
		fig.addData(plannedPath.smoothPath, Color.red, Color.blue);
		fig.addData(plannedPath.leftPath, Color.red);
		fig.addData(plannedPath.rightPath, Color.green);
		
		GoodGraphing grabCubeFig = new GoodGraphing(plannedPath.smoothCenterVelocity, null, Color.blue);
		grabCubeFig.yGridOn();
		grabCubeFig.xGridOn();
		grabCubeFig.setYLabel("Velocity (in/s)");
		grabCubeFig.setXLabel("time (seconds)");
		grabCubeFig.setTitle("Velocity profile\nLeft = Red, Right = Green");
		grabCubeFig.addData(plannedPath.smoothLeftVelocity, Color.red);
		grabCubeFig.addData(plannedPath.smoothRightVelocity, Color.green);
	}
	
	public static void reversePath(FastPathPlanner path) {
		double[][] tmp = path.rightPath;
		path.rightPath = path.leftPath;
		path.leftPath = tmp;
		
		for (int i=0; i < path.smoothLeftVelocity.length; i++) {
			path.smoothRightVelocity[i][1] = -path.smoothRightVelocity[i][1];
			path.smoothLeftVelocity[i][1] = -path.smoothLeftVelocity[i][1];
			path.smoothCenterVelocity[i][1] = -path.smoothCenterVelocity[i][1];
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