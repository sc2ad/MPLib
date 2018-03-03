import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import scadlib.extra.GoodGraphing;
import scadlib.extra.PathData;
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
		double width = 648;
		double height = 324.5;
		double robotWidth = 25.75;
		double robotLength = 37.25;
		double switchLength = 38.719;
		double distanceToSwitchFromWall = 85.25;
		double distanceToSwitchFromAlliance = 140;
		double distanceToScaleFromAlliance = 299.65;
		double distanceToScaleFromWall = 71.57;
		
		double scalePlatformLength = 36; 
		
		double delta = 10;
		double widthOfSwitch = 56;
		
		double xDelta = 10;
		double yDelta = 0;
		
		double stage2x1 = 5;
		double stage2y1 = 2;
		double stage2x2 = 10;
		double stage2y2 = 10;
		double stage2y3 = 15;
		double stage2y4 = 40;
		
		double portalY = 29.69;
		
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
		
		double[][] leftSwitchStart = new double[][]{
			{robotLength/2, height-portalY-robotWidth/2},
			{robotLength/2+10, height-portalY-robotWidth/2},
			{robotLength/2+80, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-15, height-distanceToSwitchFromWall+robotLength/2+23},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-7, height-distanceToSwitchFromWall+robotLength/2+18},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-2, height-distanceToSwitchFromWall+robotLength/2+10},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-distanceToSwitchFromWall+robotLength/2+3},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-distanceToSwitchFromWall+robotLength/2},
		};
		
		double[][] leftSwitchStartNew = new double[][]{
			{robotLength/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-distanceToSwitchFromWall+robotLength/2},
		};
		
		double[][] rightSwitchStart = new double[][]{
			{robotLength/2, portalY+robotWidth/2},
			{robotLength/2+10, portalY+robotWidth/2},
			{robotLength/2+80, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-15, distanceToSwitchFromWall-robotLength/2-23},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-7, distanceToSwitchFromWall-robotLength/2-18},
			{distanceToSwitchFromAlliance+widthOfSwitch/2-2, distanceToSwitchFromWall-robotLength/2-10},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, distanceToSwitchFromWall-robotLength/2-3},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, distanceToSwitchFromWall-robotLength/2},
		};
		
		double[][] rightSwitchStartNew = new double[][]{
			{robotLength/2, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, distanceToSwitchFromWall-robotLength/2},
		};
		
		double[][] leftScaleStart = new double[][]{
			{robotLength/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+switchLength+robotLength/2-40,height-portalY-robotWidth/2},
			{distanceToScaleFromAlliance-robotLength/2-40, height-portalY-robotWidth/2},
			{324-3, height-portalY-robotWidth/2+3},
			{324, height-portalY-robotWidth/2-5},
			{324, height-72-5+robotLength/2},
		};
		
		double[][] rightScaleStart = new double[][]{
			{robotLength/2, portalY+robotWidth/2},
			{324, portalY+robotWidth/2},
			{324, 72+5-robotLength/2},
		};
		
		double scaleYDelta = 8;
		
		double[][] leftScaleStartNew = new double[][]{
			{robotLength/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+switchLength+robotLength/2,height-portalY-robotWidth/2},
			{distanceToScaleFromAlliance-robotLength/2-40, height-distanceToScaleFromWall-scalePlatformLength/2+scaleYDelta},
			{distanceToScaleFromAlliance-robotLength/2+5, height-distanceToScaleFromWall-scalePlatformLength/2+scaleYDelta},
		};
		
		double[][] rightScaleStartNew = new double[][]{
			{robotLength/2, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+switchLength+robotLength/2, portalY+robotWidth/2},
			{distanceToScaleFromAlliance-robotLength/2-40, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta},
			{distanceToScaleFromAlliance-robotLength/2+5, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta},
		};
		
		PathData leftSwitchStartDataNew = new PathData(leftSwitchStartNew, 5, 0.02, robotWidth);
		PathData rightSwitchStartDataNew = new PathData(rightSwitchStartNew, 5, 0.02, robotWidth);
		
		PathData leftSwitchStartData = new PathData(leftSwitchStart, 5, 0.02, robotWidth);
		PathData rightSwitchStartData = new PathData(rightSwitchStart, 5, 0.02, robotWidth);
		
		PathData leftScaleStartData = new PathData(leftScaleStart, 6, 0.02, robotWidth);
		PathData rightScaleStartData = new PathData(rightScaleStart, 5, 0.02, robotWidth);
		
		PathData leftScaleStartDataNew = new PathData(leftScaleStartNew, 6, 0.02, robotWidth);
		PathData rightScaleStartDataNew = new PathData(rightScaleStartNew, 6, 0.02, robotWidth);
		
		PathData leftPathData = new PathData(leftPath, 3.2, 0.02, robotWidth);
		PathData rightPathData = new PathData(rightPath, 3.2, 0.02, robotWidth);
		
		PathData[] left2CubePathArrays = new PathData[]{
			new PathData(leftPath, 3.2, 0.02, robotWidth),
			new PathData(leftTurnBack, 2, 0.02, robotWidth, true),
			new PathData(leftGrabCube, 1, 0.02, robotWidth),
			new PathData(leftReverseCube, 1, 0.02, robotWidth, true),
			new PathData(leftTurnToSwitch, 2, 0.02, robotWidth)
		};
		
		PathData[] right2CubePathArrays = new PathData[]{
			new PathData(rightPath, 3.2, 0.02, robotWidth), 
			new PathData(rightTurnBack, 2, 0.02, robotWidth, true), 
			new PathData(rightGrabCube, 1, 0.02, robotWidth), 
			new PathData(rightReverseCube, 1, 0.02, robotWidth, true),
			new PathData(rightTurnToSwitch, 2, 0.02, robotWidth)
		};
//		makeGraph(width, height, true, getPaths(leftSwitchStartData));
//		makeGraph(width, height, true, getPaths(rightSwitchStartData));
//		// New data seems more reasonable in terms of speed, but has a negative velocity at some point
//		makeGraph(width, height, true, getPaths(leftSwitchStartDataNew));
//		makeGraph(width, height, true, getPaths(rightSwitchStartDataNew));
		
		makeGraph(width, height, true, getPaths(leftScaleStartData));
		makeGraph(width, height, true, getPaths(rightScaleStartData));
		
		makeGraph(width, height, true, getPaths(leftScaleStartDataNew));
		makeGraph(width, height, true, getPaths(rightScaleStartDataNew));
		
//		makeGraph(width, height, false, getPaths(right2CubePathArrays));
//		makeGraph(width, height, false, getPaths(left2CubePathArrays));
//		makeGraph(width, height, false, getPaths(rightPathData));
//		makeGraph(width, height, false, getPaths(leftPathData));
	}
	
	public static FastPathPlanner[] getPaths(PathData... pathData) {
		FastPathPlanner[] paths = new FastPathPlanner[pathData.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = pathData[i].path;
		}
		return paths;
	}
	
	public static void makeGraph(double width, double height, boolean showVelocity, FastPathPlanner... paths) {
		FastPathPlanner fpp = paths[0];
		GoodGraphing figure = new GoodGraphing(fpp.leftPath, fpp.smoothPath, fpp.rightPath, Color.red, Color.blue, Color.green);
		figure.setTitle("Field View");
		figure.xGridOn();
		figure.yGridOn();
		figure.setYLabel("Y (inches)");
		figure.setXLabel("X (inches)");
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
		
		for (FastPathPlanner p : paths) {
			addDataToGraph(p, figure, showVelocity);
		}
	}
	
	public static void addDataToGraph(FastPathPlanner plannedPath, GoodGraphing fig, boolean velocity) {
		fig.addData(plannedPath.nodeOnlyPath, Color.black);
		fig.addData(plannedPath.smoothPath, Color.red, Color.blue);
		fig.addData(plannedPath.leftPath, Color.red);
		fig.addData(plannedPath.rightPath, Color.green);
		if (velocity) {
			GoodGraphing grabCubeFig = new GoodGraphing(plannedPath.smoothCenterVelocity, null, Color.blue);
			grabCubeFig.yGridOn();
			grabCubeFig.xGridOn();
			grabCubeFig.setYLabel("Velocity (in/s)");
			grabCubeFig.setXLabel("time (seconds)");
			grabCubeFig.setTitle("Velocity profile\nLeft = Red, Right = Green");
			grabCubeFig.addData(plannedPath.smoothLeftVelocity, Color.red);
			grabCubeFig.addData(plannedPath.smoothRightVelocity, Color.green);
		}
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