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
		
		double[][] leftPath3Cube = new double[][]{
			{18.625, 162.25},
			{31.375, 162.25},
			{39.5013698630137, 165.37019230769232},
			{51.375, 172.25},
			{66.13150684931507, 190.33173076923077},
			{76.78356164383563, 203.70398351648353},
			{86.54794520547946, 217.07623626373626},
			{88.625, 219.8905},
			{99.86301369863014, 219.75068681318683},
			{121.375, 219.8905},
		};
		
		double[][] leftTurnBack = new double[][]{
			{94.53698630136986, 242.92925824175825},
			{100.75068493150685, 227.77403846153845},
			{106.96438356164384, 221.53365384615384},
			{114.95342465753426, 218.8592032967033},
			{121.375, 219.8905},
		};
		
		double[][] leftGrabCube = new double[][]{
			{94.53698630136986, 242.92925824175825},
			{96.31232876712329, 236.68887362637363},
			{96.31232876712329, 213.5103021978022},
			{100.75068493150685, 190.33173076923077},
			{103.41369863013699, 183.19986263736263},
		};
		
		double[][] leftReturnCube = new double[][]{
			{94.53698630136986, 242.92925824175825},
			{96.31232876712329, 226.88255494505495},
			{105.18904109589042, 214.40178571428572},
			{115.84109589041097, 209.0528846153846},
			{123.83013698630138, 210.83585164835165},
		};
		
		double[][] toLeftPortal = new double[][]{
			{123.83013698630138, 210.83585164835165},
			{110.5150684931507, 212.17307692307693},
			{103.41369863013699, 215.739010989011},
			{78.55890410958905, 231.78571428571428},
			{59.03013698630137, 251.39835164835165},
		};
		
		double[][] leftTurnBackA = new double[][]{
			{95.42465753424658, 242.03777472527474},
			{98.08767123287672, 232.23145604395606},
			{106.96438356164384, 225.0995879120879},
			{113.17808219178083, 222.42513736263737},
			{120.27945205479453, 222.42513736263737},
			{128.26849315068495, 225.99107142857144},
		};
		
		double[][] leftTurnBack2 = new double[][]{
			{102.52602739726028, 241.1462912087912},
			{104.3013698630137, 233.12293956043956},
			{111.4027397260274, 222.42513736263737},
			{119.39178082191782, 218.8592032967033},
			{128.26849315068495, 219.75068681318683},
		};
		
		double[][] leftGrabCube2 = new double[][]{
			{95.42465753424658, 225.99107142857144},
			{96.31232876712329, 221.53365384615384},
			{100.75068493150685, 206.37843406593407},
			{107.85205479452055, 190.33173076923077},
			{112.29041095890412, 182.30837912087912},
		};
		
		double[][] leftAfterGrabCube2 = new double[][]{
			{92.76164383561645, 245.6037087912088},
			{94.53698630136986, 234.0144230769231},
			{101.63835616438357, 209.0528846153846},
			{107.85205479452055, 190.33173076923077},
			{112.29041095890412, 182.30837912087912},
		};
		
		double[][] leftTurnBack2A = new double[][]{
			{102.52602739726028, 241.1462912087912},
			{104.3013698630137, 233.12293956043956},
			{111.4027397260274, 225.99107142857144},
			{118.50410958904111, 221.53365384615384},
			{123.83013698630138, 220.64217032967034},
			{130.93150684931507, 221.53365384615384},
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
		
		double[][] rightReturnCube = new double[][]{
			{122.4986301369863, 110.98969780219781},
			{116.28493150684933, 110.98969780219781},
			{107.4082191780822, 112.77266483516483},
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
			{108.73972602739727, 299.9842032967033},
			{146.0219178082192, 291.06936813186815},
			{158.44931506849318, 276.80563186813185},
			{163.77534246575345, 266.1078296703297},
			{163.77534246575345, 248.51854395604397},
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
			{284.9424657534247, 264.3248626373626},
		};
		
		double[][] rightScaleLeftStart = new double[][]{
			{18.625, 281.935},
			{34.0, 281.935},
			{50.15342465753425, 282.154532967033},
			{82.99726027397261, 281.26304945054943},
			{123.83013698630138, 281.26304945054943},
			{168.65753424657535, 285.72046703296706},
			{194.4, 281.26304945054943},
			{213.041095890411, 268.7822802197802},
			{223.69315068493154, 253.62706043956044},
			{229.01917808219181, 225.99107142857144},
			{229.46301369863016, 204.59546703296704},
			{229.46301369863016, 162.69574175824175},
			{229.01917808219181, 134.16826923076923},
			{228.1315068493151, 100.2918956043956},
			{232.56986301369867, 85.13667582417582},
			{245.88493150684934, 72.6559065934066},
			{261.86301369863014, 66.41552197802199},
			{289.38082191780825, 70.87293956043956},
		};
		
		double[][] leftScaleRightStart = new double[][]{
			{18.625, 42.565},
			{34.0, 42.565},
			{72.78904109589041, 42.34546703296701},
			{112.29041095890412, 43.23695054945057},
			{168.65753424657535, 38.779532967032935},
			{194.4, 43.23695054945057},
			{213.041095890411, 55.717719780219795},
			{223.69315068493154, 70.87293956043956},
			{229.01917808219181, 98.50892857142856},
			{232.56986301369867, 150.21497252747253},
			{229.01917808219181, 190.33173076923077},
			{228.1315068493151, 224.20810439560438},
			{232.56986301369867, 239.36332417582418},
			{245.88493150684934, 251.8440934065934},
			{261.86301369863014, 258.08447802197804},
			{289.38082191780825, 253.62706043956044},
		};
		
		double[][] leftScaleBackup = new double[][]{
			{284.9424657534247, 264.3248626373626},
			{272.0712328767124, 272.3482142857143},
			{258.75616438356167, 275.02266483516485},
			{245.441095890411, 275.91414835164835},
			{231.2383561643836, 285.72046703296706},
			{225.0246575342466, 295.5267857142857},
		};
		
		double[][] leftScaleSwitchBackup = new double[][]{
			{237.45205479452056, 266.1078296703297},
			{228.57534246575344, 259.86744505494505},
			{223.24931506849316, 246.49519230769232},
			{214.37260273972603, 236.68887362637363},
		};
		
		double[][] leftScaleGrabCube = new double[][]{
			{225.0246575342466, 295.5267857142857},
			{231.2383561643836, 285.72046703296706},
			{233.9013698630137, 271.4567307692308},
			{229.46301369863016, 259.86744505494505},
			{224.1369863013699, 249.16964285714286},
			{214.37260273972603, 236.68887362637363},
		};
		
		double[][] leftScaleSwitchPlace = new double[][]{
			{237.45205479452056, 266.1078296703297},
			{228.57534246575344, 259.86744505494505},
			{213.48493150684934, 255.41002747252747},
			{202.83287671232878, 246.49519230769232},
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
		
//		PathData leftSwitchStartDataNew = new PathData(leftSwitchStartNew, 5, 0.02, robotWidth);
//		PathData rightSwitchStartDataNew = new PathData(rightSwitchStartNew, 5, 0.02, robotWidth);
		
		PathData leftSwitchStartData = new PathData(leftSwitchStart, 3.8, 0.02, robotWidth);
		
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
		
		PathData rightScaleLeftStartData = new PathData(rightScaleLeftStart, 11, 0.02, robotWidth);
		PathData leftScaleRightStartData = new PathData(leftScaleRightStart, 11, 0.02, robotWidth);
		
		PathData[] left2CubeScaleStartData = new PathData[]{
			leftScaleStartDataNew,
			new PathData(leftScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftScaleGrabCube, 1, 0.02, robotWidth),
			new PathData(leftScaleGrabCube, 1, 0.02, robotWidth, PathData.PathParameter.REVERSE),
			new PathData(leftScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
		};
		
//		PathData[] right2CubeScaleStartData = new PathData[]{
//			rightScaleStartDataNew,
//			new PathData(rightScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
//			new PathData(rightScaleGrabCube, 1, 0.02, robotWidth),
//			new PathData(rightScaleGrabCube, 1, 0.02, robotWidth, PathData.PathParameter.REVERSE),
//			new PathData(rightScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
//		};
		
		PathData leftPathData = new PathData(leftPath, 3.2, 0.02, robotWidth);
		PathData rightPathData = new PathData(rightPath, 3.2, 0.02, robotWidth);
		
		PathData[] left2CubePathArrays = new PathData[]{
			new PathData(leftPath, 3.2, 0.02, robotWidth),
			new PathData(leftTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftGrabCube, 2, 0.02, robotWidth),
			new PathData(leftGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(leftReturnCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
			new PathData(toLeftPortal, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
		};
		
		PathData[] left3CubePathArrays = new PathData[]{
				new PathData(leftPath3Cube, 3.2, 0.02, robotWidth),
				new PathData(leftTurnBack, 1.5, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftGrabCube, 1.5, 0.02, robotWidth),
				new PathData(leftGrabCube, 1.5, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftTurnBackA, 1.5, 0.02, robotWidth),
				new PathData(leftTurnBack2, 1.5, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftGrabCube2, 1.2, 0.02, robotWidth),
				new PathData(leftAfterGrabCube2, 1.2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftTurnBack2A, 1.5, 0.02, robotWidth),
		};
		
		double[][] leftBackup2 = new double[][]{
			{70.56986301369864, 217.9677197802198},
			{74.12054794520549, 219.75068681318683},
			{86.54794520547946, 220.64217032967034},
			{106.07671232876713, 220.64217032967034},
			{118.06027397260274, 219.75068681318683},
			{121.375, 219.8905},
		};
		
		double[][] leftGrabby2 = new double[][]{
			{70.56986301369864, 217.9677197802198},
			{75.89589041095891, 221.53365384615384},
			{77.67123287671234, 226.88255494505495},
			{82.1095890410959, 229.5570054945055},
			{91.87397260273974, 233.12293956043956},
			{101.63835616438357, 234.0144230769231},
			{110.5150684931507, 228.66552197802199},
			{116.72876712328768, 217.07623626373626},
			{117.6164383561644, 204.59546703296704},
			{116.72876712328768, 195.68063186813188},
			{113.17808219178083, 177.85096153846155},
			{114.95342465753426, 173.39354395604397},
		};
		
		double[][] leftBackup2A = new double[][]{
			{93.64931506849317, 248.27815934065936},
			{93.64931506849317, 239.36332417582418},
			{98.08767123287672, 227.77403846153845},
			{106.07671232876713, 220.64217032967034},
			{118.06027397260274, 219.75068681318683},
			{121.375, 219.8905},
		};
		
		double[][] leftGrab2A = new double[][]{
			{93.64931506849317, 248.27815934065936},
			{93.64931506849317, 239.36332417582418},
			{97.2, 218.8592032967033},
			{102.52602739726028, 196.5721153846154},
			{106.96438356164384, 168.93612637362637},
			{106.07671232876713, 160.91277472527472},
		};
		
		PathData[] left3CubePath2 = new PathData[]{
				new PathData(leftPath3Cube, 3.2, 0.02, robotWidth),
				new PathData(leftBackup2, 1.1, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftGrabby2, 2, 0.02, robotWidth),
				new PathData(leftGrabby2, 2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftBackup2, 1.1, 0.02, robotWidth),
				new PathData(leftBackup2A, 1.5, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftGrab2A, 1.5, 0.02, robotWidth),
				new PathData(leftGrab2A, 1.5, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftBackup2A, 1.5, 0.02, robotWidth),
		};
		
//		makeGraph(width, height, true, left3CubePathArrays[2]);

//		makeGraph(width, height, true, left3CubePath2[1]);
//		makeGraph(width, height, true, left3CubePath2[6]);
		
		
		double[][] leftGrabby3 = new double[][]{
			{118.06027397260274, 218.8592032967033},
			{118.94794520547946, 211.72733516483518},
			{117.17260273972605, 188.54876373626374},
			{118.94794520547946, 179.63392857142858},
		};
		
		double[][] leftGrabby3A = new double[][]{
			{118.06027397260274, 218.8592032967033},
			{118.94794520547946, 211.72733516483518},
			{117.17260273972605, 188.54876373626374},
			{118.94794520547946, 176.63392857142858},
		};
		
		PathData[] left3CubePath3 = new PathData[]{
				new PathData(leftPath3Cube, 3.2, 0.02, robotWidth),
				new PathData(leftGrabby3, 1.3, 0.02, robotWidth),
				new PathData(leftGrabby3, 1.3, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftGrabby3A, 1.3, 0.02, robotWidth),
				new PathData(leftGrabby3A, 1.3, 0.02, robotWidth, PathData.PathParameter.REVERSE),
		};
		
//		makeGraph(width, height, true, getPaths(left3CubePath3));
		
//		makeGraph(width, height, true, left3CubePath3[4]);
		
		double[][] rightPath3Cube = new double[][]{
			{18.625, 162.25},
			{31.375, 162.25},
			{39.5013698630137, 159.12980769230768},
			{51.375, 152.25},
			{66.13150684931507, 134.16826923076923},
			{76.78356164383563, 120.79601648351647},
			{86.54794520547946, 107.42376373626374},
			{88.625, 104.6095},
			{99.86301369863014, 104.74931318681317},
			{121.375, 104.6095},
		};
		
		double[][] rightGrabby3 = new double[][]{
			{118.94794520547946, 144.86607142857142},
			{117.17260273972605, 135.95123626373626},
			{118.94794520547946, 112.77266483516482},
			{118.06027397260274, 105.6407967032967},
		};
		
		double[][] rightGrabby3A = new double[][]{
			{118.06027397260274, 105.6407967032967},
			{118.94794520547946, 112.77266483516482},
			{117.17260273972605, 135.95123626373626},
			{118.94794520547946, 147.86607142857142},
		};
		
		PathData[] right3CubePath3 = new PathData[]{
				new PathData(rightPath3Cube, 3.2, 0.02, robotWidth),
				new PathData(rightGrabby3, 1.3, 0.02, robotWidth, PathData.PathParameter.FLIP),
				new PathData(rightGrabby3, 1.3, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightGrabby3A, 1.3, 0.02, robotWidth),
				new PathData(rightGrabby3A, 1.3, 0.02, robotWidth, PathData.PathParameter.REVERSE),
		};
		
//		makeGraph(width, height, true, getPaths(right3CubePath3));
		
//		makeGraph(width, height, true, left3CubePath3[3]);
//		makeGraph(width, height, true, right3CubePath3[3]);
		
		PathData[] right2CubePathArrays = new PathData[]{
			new PathData(rightPath, 5, 0.02, robotWidth), 
			new PathData(rightTurnBack, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE), 
			new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP), 
			new PathData(rightGrabCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
			new PathData(rightReturnCube, 2, 0.02, robotWidth, PathData.PathParameter.FLIP),
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
		
		
		
		PathData[] leftStartScaleSwitch = new PathData[]{
				leftScaleStartDataNew,
				new PathData(leftScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(leftScaleGrabCube, 2, 0.02, robotWidth),
				new PathData(leftScaleSwitchBackup, 2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(leftScaleSwitchPlace, 2, 0.02, robotWidth),
		};
		
		double[][] rightScaleBackup = new double[][]{
			{284.9424657534247, 60.17513736263737},
			{272.0712328767124, 52.15178571428572},
			{258.75616438356167, 49.477335164835154},
			{245.441095890411, 48.58585164835165},
			{231.2383561643836, 38.779532967032935},
			{225.0246575342466, 28.973214285714278},
		};
		
		double[][] rightScaleGrabCube = new double[][]{
			{225.0246575342466, 28.973214285714278},
			{231.2383561643836, 38.779532967032935},
			{233.9013698630137, 53.043269230769226},
			{229.46301369863016, 64.63255494505495},
			{224.1369863013699, 75.33035714285714},
			{214.37260273972603, 87.81112637362637},
		};
		
		double[][] rightScaleSwitchBackup = new double[][]{
			{237.45205479452056, 58.39217032967031},
			{228.57534246575344, 64.63255494505495},
			{223.24931506849316, 78.00480769230768},
			{214.37260273972603, 87.81112637362637},
		};
		
		PathData[] rightStartScaleSwitch = new PathData[]{
				rightScaleStartDataNew,
				new PathData(rightScaleBackup, 2, 0.02, robotWidth, PathData.PathParameter.FLIPREVERSE),
				new PathData(rightScaleGrabCube, 2, 0.02, robotWidth),
				new PathData(rightScaleSwitchBackup, 2, 0.02, robotWidth, PathData.PathParameter.REVERSE),
				new PathData(rightScaleSwitchBackup, 2, 0.02, robotWidth),
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
		
//		makeGraph(width, height, true, leftStartScaleSwitch[1]);
		
//		makeGraph(width, height, true, rightScaleLeftStartData);
//		makeGraph(width, height, true, leftScaleRightStartData);
		
//		makeGraph(width, height, true, getPaths(left2CubeScaleStartData));
//		makeGraph(width, height, false, left2CubeScaleStartData[0]);
//		makeGraph(width, height, true, left2CubeScaleStartData[2]);
//		makeGraph(width, height, true, getPaths(right2CubeScaleStartData));
//		makeGraph(width, height, true, getPaths(right2CubeScaleStartData[1]));
		
//		makeGraph(width, height, true, getPaths(left3CubePathArrays));
//		makeGraph(width, height, true, getPaths(right3CubePathArrays));
		
//		makeGraph(width, height, true, left3CubePathArrays[1]);
//		makeGraph(width, height, true, left3CubePathArrays[2]);
//		makeGraph(width, height, true, left3CubePathArrays[5]);
//		makeGraph(width, height, true, left3CubePathArrays[6]);
		
//		makeGraph(width, height, false, getPaths(rightPathData));
//		makeGraph(width, height, false, getPaths(leftPathData));
		
		makeGraph(width, height, true, left2CubePathArrays[5]);
		
//		makeGraph(width, height, true, getPaths(left2CubePathArrays));
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