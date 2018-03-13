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
			{67.71235955056179, 198.78416912487708},
			{75.72134831460673, 199.1032448377581},
			{88.4629213483146, 206.1229105211406},
			{105.20898876404493, 217.2905604719764},
			{119.0, 219.8905},
			{121.375, 219.8905},
		};
		
		double[][] leftGrabCube = new double[][]{
			{113.58202247191011, 178.36332350049165},
			{106.66516853932583, 185.70206489675516},
			{96.10786516853932, 191.76450344149458},
			{85.18651685393257, 196.2315634218289},
			{75.72134831460673, 199.1032448377581},
			{67.71235955056179, 198.78416912487708},
		};
		
		double[][] leftTurnBack2 = new double[][]{
			{66.57534246575344, 195.68063186813188},
			{71.9013698630137, 196.5721153846154},
			{82.1095890410959, 201.9210164835165},
			{93.64931506849317, 209.94436813186815},
			{105.20898876404493, 217.2905604719764},
			{119.0, 219.8905},
			{121.375, 219.8905},
		};
		
		double[][] leftGrabCube2 = new double[][]{
			{66.57534246575344, 195.68063186813188},
			{71.9013698630137, 196.5721153846154},
			{81.66575342465754, 193.89766483516485},
			{87.43561643835618, 188.54876373626374},
			{96.31232876712329, 181.41689560439562},
			{100.75068493150685, 176.0679945054945},
		};
		
		double[][] rightPath = new double[][]{
			{robotLength/2, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2-delta-xDelta, height/2},
			{(distanceToSwitchFromAlliance-robotLength)/2+delta-xDelta, height/2-delta},
			{(distanceToSwitchFromAlliance+robotLength)/2+delta-xDelta, distanceToSwitchFromWall+switchLength/2-yDelta},
			{distanceToSwitchFromAlliance-robotLength/2, distanceToSwitchFromWall+switchLength/2-yDelta}
		};
		
		double[][] rightTurnBack = new double[][]{
			{121.375, 104.6095},
			{119.0, 104.6095},
			{105.20898876404493, 107.20943952802361},
			{88.4629213483146, 118.3770894788594},
			{75.72134831460673, 125.3967551622419},
			{67.71235955056179, 125.71583087512292},
		};
		
		double[][] rightGrabCube = new double[][]{
			{103.41369863013699, 139.07142857142858},
			{96.10786516853932, 132.73549655850542},
			{85.18651685393257, 128.2684365781711},
			{75.72134831460673, 125.3967551622419},
			{67.71235955056179, 125.71583087512292},
		};
		
		double[][] leftSwitchStart = new double[][]{
			{18.625, 281.935},
			{28.625, 281.935},
			{75.0082191780822, 294.6353021978022},
			{105.18904109589042, 296.4182692307692},
			{141.58356164383562, 290.17788461538464},
			{159.33698630136988, 275.91414835164835},
			{168.0, 260.875},
			{168.0, 257.875},
		};
		
		double[][] leftSwitchStartBackup = new double[][]{
			{168.0, 257.875},
			{168.0, 260.875},
			{169.98904109589043, 274.5769230769231},
			{176.2027397260274, 284.38324175824175},
			{198.39452054794523, 292.4065934065934},
			{203.7205479452055, 296.864010989011},
			{207.27123287671236, 305.77884615384613},
		};
		
		double[][] leftSwitchStartGrabCube = new double[][]{
			{207.27123287671236, 305.77884615384613},
			{203.7205479452055, 296.864010989011},
			{206.38356164383563, 289.7321428571429},
			{206.38356164383563, 285.27472527472526},
			{209.93424657534248, 275.4684065934066},
			{217.0356164383562, 266.55357142857144},
			{209.0465753424658, 244.26648351648353},
		};
		
		double[][] leftSwitchStartInvert = new double[][]{
			{207.27123287671236, 305.77884615384613},
			{203.7205479452055, 296.864010989011},
			{198.39452054794523, 292.4065934065934},
			{176.2027397260274, 284.38324175824175},
			{169.98904109589043, 274.5769230769231},
			{168.0, 260.875},
			{168.0, 257.875},
		};
		
		double[][] rightTurnBack2 = new double[][]{
			{121.375, 104.6095},
			{119.0, 104.6095},
			{105.20898876404493, 107.20943952802361},
			{88.4629213483146, 118.3770894788594},
			{71.9013698630137, 127.92788461538461},
			{66.57534246575344, 128.81936813186812},
		};
		
		double[][] rightGrabCube2 = new double[][]{
			{66.57534246575344, 128.81936813186812},
			{71.9013698630137, 127.92788461538461},
			{81.66575342465754, 130.60233516483515},
			{104.3013698630137, 148.87774725274727},
		};
		
		double[][] leftSwitchStartNew = new double[][]{
			{robotLength/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-portalY-robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, height-distanceToSwitchFromWall+robotLength/2},
		};
		
		double[][] rightSwitchStart = new double[][]{
			{18.625, 42.565},
			{28.625, 42.565},
			{75.0082191780822, 29.86469780219778},
			{105.18904109589042, 28.081730769230774},
			{141.58356164383562, 34.32211538461536},
			{159.33698630136988, 48.58585164835165},
			{168.0, 63.625},
			{168.0, 66.625},
		};
		
		double[][] rightSwitchStartBackup = new double[][]{
			{168.0, 66.625},
			{168.0, 63.625},
			{169.98904109589043, 49.923076923076906},
			{176.2027397260274, 40.11675824175825},
			{198.39452054794523, 32.0934065934066},
			{203.7205479452055, 27.63598901098902},
			{207.27123287671236, 18.721153846153868},
		};
		
		double[][] rightSwitchStartGrabCube = new double[][]{
			{207.27123287671236, 18.721153846153868},
			{203.7205479452055, 27.63598901098902},
			{206.38356164383563, 34.76785714285711},
			{206.38356164383563, 39.225274725274744},
			{209.93424657534248, 49.0315934065934},
			{217.0356164383562, 57.946428571428555},
			{209.0465753424658, 80.23351648351647},
		};
		
		double[][] rightSwitchStartInvert = new double[][]{
			{207.27123287671236, 18.721153846153868},
			{203.7205479452055, 27.63598901098902},
			{198.39452054794523, 32.0934065934066},
			{176.2027397260274, 40.11675824175825},
			{169.98904109589043, 49.923076923076906},
			{168.0, 63.625},
			{168.0, 66.625},
		};
		
		double[][] rightSwitchStartNew = new double[][]{
			{robotLength/2, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, portalY+robotWidth/2},
			{distanceToSwitchFromAlliance+widthOfSwitch/2, distanceToSwitchFromWall-robotLength/2},
		};
		
		double[][] leftScaleStart = new double[][]{
			{18.625, 281.935},
			{34.0, 281.935},
			{131.8191780821918, 287.5034340659341},
			{225.0246575342466, 290.17788461538464},
			{299.1452054794521, 289.2864010989011},
			{312.46027397260275, 282.154532967033},
			{323.1123287671233, 266.1078296703297},
			{324.0, 260.125},
		};
		
		double[][] rightScaleStart = new double[][]{
			{18.625, 42.565},
			{40.0, 42.565},
			{121.16712328767125, 33.43063186813187},
			{221.47397260273974, 29.864697802197803},
			{307.5780821917808, 30.756181318681318},
			{318.2301369863014, 39.67101648351648},
			{324.0, 52.0},
			{324.0, 58.375},
		};
		
		double scaleYDelta = 8;
		
		double[][] leftScaleStartNew = new double[][]{
			{18.625, 281.935},
			{34.0, 281.935},
			{103.41369863013699, 287.5034340659341},
			{160.2246575342466, 289.2864010989011},
			{222.36164383561646, 286.61195054945057},
			{255.20547945205482, 279.03434065934067},
			{272.0712328767124, 272.79395604395603},
			{289.8246575342466, 260.3131868131868},
		};
		
		double[][] rightScaleLeftStart = new double[][]{
			{18.625, 281.935},
			{34.0, 281.935},
			{112.29041095890412, 281.26304945054943},
			{169.98904109589043, 282.154532967033},
			{194.84383561643838, 275.91414835164835},
			{210.8219178082192, 262.5418956043956},
			{221.47397260273974, 230.89423076923077},
			{225.9123287671233, 134.61401098901098},
			{225.0246575342466, 102.96634615384616},
			{231.2383561643836, 85.13667582417582},
			{242.77808219178084, 77.11332417582418},
			{262.3068493150685, 74.43887362637363},
			{283.6109589041096, 78.89629120879121},
		};
		
		double[][] leftScaleRightStart = new double[][]{
			{18.625, 42.565},
			{34.0, 42.565},
			{112.29041095890412, 43.23695054945057},
			{169.98904109589043, 42.345467032967036},
			{194.84383561643838, 48.58585164835165},
			{210.8219178082192, 61.958104395604394},
			{221.47397260273974, 93.60576923076923},
			{225.9123287671233, 189.88598901098902},
			{225.0246575342466, 221.53365384615384},
			{231.2383561643836, 239.36332417582418},
			{242.77808219178084, 247.38667582417582},
			{262.3068493150685, 250.06112637362637},
			{283.6109589041096, 245.6037087912088},
		};
		
		double backUpTurnPoint = 55;
		// Need to fix velocity being negative here
		double[][] leftScaleBackup = new double[][]{
			{289.8246575342466, 260.3131868131868},
			{272.0712328767124, 272.79395604395603},
			{259.64383561643837, 273.2396978021978},
			{242.77808219178084, 275.91414835164835},
			{229.46301369863016, 283.0460164835165},
			{226.8, 294.6353021978022},
		};
		
		
		
		double xDistanceToCube = 17;
		double cubeYdisp = 20;
		
		double[][] leftScaleGrabCube = new double[][]{
			{226.8, 294.6353021978022},
			{229.46301369863016, 283.9375},
			{225.0246575342466, 264.3248626373626},
			{210.8219178082192, 242.92925824175825},
		};
		
		double[][] rightScaleStartNew = new double[][]{
			{18.625, 42.565},
			{28.849315068493155, 42.791208791208796},
			{113.17808219178083, 39.67101648351648},
			{194.84383561643838, 41.45398351648352},
			{242.77808219178084, 48.58585164835165},
			{264.0821917808219, 54.82623626373626},
			{283.6109589041096, 61.958104395604394},
			{291.6, 68.19848901098901},
		};
		
		double[][] rightScaleBackup = new double[][]{
			{distanceToScaleFromAlliance-robotLength/2+5, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta},
			{distanceToScaleFromAlliance-robotLength/2-10, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta},
			{distanceToScaleFromAlliance-robotLength/2-50, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-3},
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-18},
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-22},
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-25},			
		};
		
		double[][] rightScaleGrabCube = new double[][]{
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-25},
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta-22},
			{distanceToScaleFromAlliance-robotLength/2-backUpTurnPoint-xDistanceToCube, distanceToScaleFromWall+scalePlatformLength/2-scaleYDelta+cubeYdisp},
		};
		
		PathData leftSwitchStartDataNew = new PathData(leftSwitchStartNew, 5, 0.02, robotWidth);
		PathData rightSwitchStartDataNew = new PathData(rightSwitchStartNew, 5, 0.02, robotWidth);
		
		PathData leftSwitchStartData = new PathData(leftSwitchStart, 5, 0.02, robotWidth);
		
		PathData[] leftSwitchGrabCube = new PathData[]{
				leftSwitchStartData,
				new PathData(leftSwitchStartBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftSwitchStartGrabCube, 2, 0.02, robotWidth),
				new PathData(leftSwitchStartGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftSwitchStartInvert, 2, 0.02, robotWidth),
		};
		
		PathData rightSwitchStartData = new PathData(rightSwitchStart, 5, 0.02, robotWidth);
		
		PathData[] rightSwitchGrabCube = new PathData[]{
				rightSwitchStartData,
				new PathData(rightSwitchStartBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightSwitchStartGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
				new PathData(rightSwitchStartGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightSwitchStartInvert, 2, 0.02, robotWidth),
		};
		
		PathData leftScaleStartData = new PathData(leftScaleStart, 8, 0.02, robotWidth);
		PathData rightScaleStartData = new PathData(rightScaleStart, 8, 0.02, robotWidth);
		
		PathData leftScaleStartDataNew = new PathData(leftScaleStartNew, 8, 0.02, robotWidth);
		PathData rightScaleStartDataNew = new PathData(rightScaleStartNew, 8, 0.02, robotWidth);
		
		PathData rightScaleLeftStartData = new PathData(rightScaleLeftStart, 13, 0.02, robotWidth);
		PathData leftScaleRightStartData = new PathData(leftScaleRightStart, 13, 0.02, robotWidth);
		
		PathData[] left2CubeScaleStartData = new PathData[]{
			leftScaleStartDataNew,
			new PathData(leftScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftScaleGrabCube, 1, 0.02, robotWidth),
			new PathData(leftScaleGrabCube, 1, 0.02, robotWidth, PathData.PathParameter.REVERSE),
			new PathData(leftScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
		};
		
		PathData[] right2CubeScaleStartData = new PathData[]{
			rightScaleStartDataNew,
			new PathData(rightScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(rightScaleGrabCube, 1, 0.02, robotWidth),
			new PathData(rightScaleGrabCube, 1, 0.02, robotWidth, PathData.PathParameter.REVERSE),
			new PathData(rightScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
		};
		
		PathData leftPathData = new PathData(leftPath, 3.2, 0.02, robotWidth);
		PathData rightPathData = new PathData(rightPath, 3.2, 0.02, robotWidth);
		
		PathData[] left2CubePathArrays = new PathData[]{
			new PathData(leftPath, 3.2, 0.02, robotWidth),
			new PathData(leftTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftGrabCube, 2, 0.02, robotWidth),
			new PathData(leftGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIP)
		};
		
		PathData[] left3CubePathArrays = new PathData[]{
				new PathData(leftPath, 3.2, 0.02, robotWidth),
				new PathData(leftTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftGrabCube, 1.5, 0.02, robotWidth, PathData.PathParameter.FLIP),
				new PathData(leftGrabCube, 1.5, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftTurnBack, 2, 0.02, robotWidth),
				new PathData(leftTurnBack2, 1.5, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftGrabCube2, 1.2, 0.02, robotWidth),
				new PathData(leftGrabCube2, 1.2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftTurnBack2, 1.5, 0.02, robotWidth),
		};
		
		PathData[] right2CubePathArrays = new PathData[]{
			new PathData(rightPath, 3.2, 0.02, robotWidth), 
			new PathData(rightTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE), 
			new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP), 
			new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(rightTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIP)
		};
		
		PathData[] right3CubePathArrays = new PathData[]{
				new PathData(rightPath, 3.2, 0.02, robotWidth), 
				new PathData(rightTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE), 
				new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP), 
				new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
				new PathData(rightTurnBack2, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightGrabCube2, 1.2, 0.02, robotWidth),
				new PathData(rightGrabCube2, 1.2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(rightTurnBack2, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
		};
//		makeGraph(width, height, true, getPaths(leftSwitchGrabCube));
//		makeGraph(width, height, true, getPaths(rightSwitchGrabCube));
//		makeGraph(width, height, true, leftSwitchGrabCube[0]);
//		makeGraph(width, height, true, rightSwitchGrabCube[0]);
		
//		makeGraph(width, height, true, leftSwitchGrabCube[2]);
//		makeGraph(width, height, true, rightSwitchStartData);
		// New data seems more reasonable in terms of speed, but has a negative velocity at some point
//		makeGraph(width, height, true, getPaths(leftSwitchStartDataNew));
//		makeGraph(width, height, true, getPaths(rightSwitchStartDataNew));
		
//		makeGraph(width, height, true, leftScaleStartData);
//		makeGraph(width, height, true, rightScaleStartData);
		
//		makeGraph(width, height, true, leftScaleStartDataNew);
//		makeGraph(width, height, true, rightScaleStartDataNew);
		
//		makeGraph(width, height, true, rightScaleLeftStartData);
//		makeGraph(width, height, true, leftScaleRightStartData);
		
//		makeGraph(width, height, true, getPaths(left2CubeScaleStartData));
//		makeGraph(width, height, false, left2CubeScaleStartData[0]);
//		makeGraph(width, height, true, left2CubeScaleStartData[2]);
//		makeGraph(width, height, true, getPaths(right2CubeScaleStartData));
//		makeGraph(width, height, true, getPaths(right2CubeScaleStartData[1]));
		
//		makeGraph(width, height, true, getPaths(left3CubePathArrays));
//		makeGraph(width, height, true, getPaths(right3CubePathArrays));
		
		makeGraph(width, height, true, right3CubePathArrays[2]);
		makeGraph(width, height, true, right3CubePathArrays[3]);
		makeGraph(width, height, true, right3CubePathArrays[5]);
		makeGraph(width, height, true, right3CubePathArrays[6]);
		
//		makeGraph(width, height, false, getPaths(rightPathData));
//		makeGraph(width, height, false, getPaths(leftPathData));
		
//		makeGraph(width, height, true, right2CubePathArrays[2]);
//		makeGraph(width, height, true, leftSwitchStartData);
		
//		makeGraph(width, height, true, leftScaleStartData);
//		makeGraph(width, height, true, right2CubeScaleStartData[1]);
		
	}
	
	public static FastPathPlanner[] getPaths(PathData... pathData) {
		FastPathPlanner[] paths = new FastPathPlanner[pathData.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = pathData[i].path;
		}
		return paths;
	}
	
	public static void makeGraph(double width, double height, boolean showVelocity, PathData data) {
		// Makes a single graph
		GoodGraphing figure = new GoodGraphing(data);
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
	
		if (showVelocity) {
			GoodGraphing velocity = new GoodGraphing(data.path.smoothCenterVelocity, null, Color.blue);
			velocity.yGridOn();
			velocity.xGridOn();
			velocity.setYLabel("Velocity (in/s)");
			velocity.setXLabel("time (seconds)");
			velocity.setTitle("Velocity profile\nLeft = Red, Right = Green");
			velocity.addData(data.path.smoothLeftVelocity, Color.red);
			velocity.addData(data.path.smoothRightVelocity, Color.green);
		}
	}
	
	public static void makeGraph(double width, double height, boolean showVelocity, FastPathPlanner... paths) {
		FastPathPlanner fpp = paths[0];
		GoodGraphing figure = new GoodGraphing(fpp.leftPath, fpp.smoothPath, fpp.rightPath, fpp.nodeOnlyPath, Color.red, Color.blue, Color.green);
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