package util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides Useful functions.
 * <p>SHOULD MOVE TO ITS OWN FILE
 * 
 * @author Sc2ad
 *
 */
public class Util {
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
	public static boolean fuzzyEquals(double a, double b) {
		return Math.abs(a - b) <= 0.00002;
	}
	public static double getAngle(double alpha, double dxdt, double dydt) {
		double angle = 0;
		if (dydt > 0 && dxdt > 0) {
			// Q1
			angle = alpha;
		} else if (dydt > 0 && dxdt < 0) {
			// Q2
			angle = 180 + alpha;
		} else if (dydt < 0 && dxdt < 0) {
			// Q3
			angle = 180 + alpha;
		} else if (dydt < 0 && dxdt > 0) {
			// Q4
			angle = alpha;
		}
		return 90 - angle;
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
	public static List<Double> getDoubleList(double[] list) {
		List<Double> out = new ArrayList<Double>();
		for (int i = 0; i < list.length; i++) {
			out.add(list[i]);
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
}