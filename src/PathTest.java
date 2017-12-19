import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

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
		OverlappingPath pasdfasdf = new OverlappingPath(movement, gyro);
//		CombinedPath moveBack = new CombinedPath.LongitudalTrapezoid(100, -100, -10, -20);
//		CombinedPath gyroBack = new CombinedPath.LongitudalTrapezoid(30, -30, -10, -50);
//		OverlappingPath pfdsafdsa = new OverlappingPath(moveBack, gyroBack);
//		
//		GyroCombinedPath pasdf = new GyroCombinedPath(0, pasdfasdf, pfdsafdsa);
		run(pasdfasdf);
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
}
/**
 * Provides Useful functions.
 * <p>SHOULD MOVE TO ITS OWN FILE
 * 
 * @author Sc2ad
 *
 */
class Util {
	/**
	 * Determines if a < b with an error for b.
	 * 
	 * @param a the a value in a < b
	 * @param b the b value in a < b
	 * @param error the error to be added or subtracted from b
	 * @return if a < b with error
	 */
	public static boolean lessThan(double a, double b, double error) {
		return a < b - error || a < b + error;
	}
	
	public static boolean equals(double a, double b, double error) {
		return (a + error > b - error && a - error < b + error);
	}

	public static double[] getDoubleArr(List<Double> list) {
		double[] out = new double[list.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = list.get(i);
		}
		return out;
	}
	/**
	 * Writes the following {@link List}s to a CSV file.
	 * 
	 * @param location the file path to the csv file (without datetime)
	 * @param times the times {@link List} to write
	 * @param speeds the speeds {@link List} to write
	 * @param positions the positions {@link List} to write
	 * @param accelerations the accelerations {@link List} to write
	 */
	public static void writeCSV(String location, List<Double> times, List<Double> speeds, List<Double> positions, List<Double> accelerations) {
        BufferedWriter os;
        Date date = new Date(System.currentTimeMillis());
        String timestamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(date);
        try {
            File dir = new File(location+"_" + timestamp + ".csv");
            dir.createNewFile();
            os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir)));
            os.write("times,speeds,positions,accelerations\n");
//            os.write(String.format("%s,%s,%s,%s\n", times.get(0), targets.get(0), positions.get(0)));
            for (int i=0; i < times.size(); i++) {
                os.write(String.format("%s,%s,%s,%s\n", times.get(i), speeds.get(i), positions.get(i), accelerations.get(i)));
            }
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	// This entire method is kinda a big BAD BAD BAD BAD *BAD* repeat
	public static void writeCSV(String location, List<Double> times, List<Double> speeds, List<Double> positions, List<Double> accelerations, List<Double> thetas, List<Double> omegas, List<Double> alphas, List<Double> x, List<Double> y) {
		BufferedWriter os;
        Date date = new Date(System.currentTimeMillis());
        String timestamp = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(date);
        try {
            File dir = new File(location+"_" + timestamp + ".csv");
            dir.createNewFile();
            os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir)));
            os.write("times,speeds,positions,accelerations,thetas,omegas,alphas,x,y\n");
//            os.write(String.format("%s,%s,%s,%s\n", times.get(0), targets.get(0), positions.get(0)));
            for (int i=0; i < times.size(); i++) {
                os.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", times.get(i), speeds.get(i), positions.get(i), accelerations.get(i), thetas.get(i), omegas.get(i), alphas.get(i), x.get(i), y.get(i)));
            }
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static double d2r(double angle) {
		return Math.PI * angle / 180.0;
	}
}