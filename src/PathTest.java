import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

public class PathTest {
	public static void main(String[] args) {
//		LinearPath jerk = new LinearPath(0,1,1);
//		IntegralPath slope = new IntegralPath(jerk);
//		IntegralPath p = new IntegralPath(slope);
		
//		TrapezoidalMotionPath p = new TrapezoidalMotionPath(100, 5, 2);
		CombinedPath jerkAccelTrap = new CombinedPath.Trapezoid(5, 2, 2); // params: vel, accel, jerk
		IntegralPath jerkAccel = new IntegralPath(jerkAccelTrap);
		CombinedPath jerkDecelTrap = new CombinedPath.Trapezoid(5, 0, 0, -2, -2);
//		LinearPath jerkDecelL = new LinearPath(5, 0, -2);
		IntegralPath jerkDecel = new IntegralPath(jerkDecelTrap);
//		MotionPath cruise = new IntegralPath(new LinearPath(10, 5));
		
//		CombinedPath p = new CombinedPath(0, jerkAccel, new LinearPath(10,5,5,0), new IntegralPath(5, new LinearPath(0,-2,-2)), new IntegralPath(new LinearPath(-4,-2,-2,0)), new IntegralPath(new LinearPath(-2,0,2)));
		CombinedPath pp = new CombinedPath(0, jerkAccel, new LinearDerivativePath(10,5), jerkDecel);
		
		CombinedPath p = new CombinedPath(0, pp);
		
//		TrapezoidalMotionPath p = new TrapezoidalMotionPath(jerkAccel, cruise, jerkDecel);
		
		System.out.println(p.validate());
		double t = 0;
		ArrayList<Double> times = new ArrayList<Double>();
		ArrayList<Double> pos = new ArrayList<Double>();
		ArrayList<Double> spd = new ArrayList<Double>();
		ArrayList<Double> accel = new ArrayList<Double>();
		
		while (Util.lessThan(t, p.getTotalTime(), 0.00002)) {
			System.out.println("Time: "+t+", Position: "+p.getPositionOnPath(t)+", Speed: "+p.getSpeed(t)+", Acceleration: "+p.getAccel(t));
			times.add(t);
			pos.add(p.getPositionOnPath(t));
			spd.add(p.getSpeed(t));
			accel.add(p.getAccel(t));
			t += 0.05;
		}
		System.out.println(p.getTotalDistance());
		viewGraph(times,pos,spd,accel);
		Util.writeCSV(System.getProperty("user.dir")+"/out", times, spd, pos, accel);
	}
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
}
class Util {
	/*
	 * 
	 */
	public static boolean lessThan(double a, double b, double error) {
		return a < b - error || a < b + error;
	}
	/*
	 * 
	 */
	public static double[] getDoubleArr(List<Double> list) {
		double[] out = new double[list.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = list.get(i);
		}
		return out;
	}
	/*
	 * 
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
}