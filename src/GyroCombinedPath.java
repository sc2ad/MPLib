
/**
 * Provides handling for multiple MotionPaths combined together.
 * DOES NOT INTEGRATE ANY OF THE PROVIDED PATHS
 * It uses the positions from each path to construct the overall curve
 * Constructs each curve offset from the previous (maybe?)
 * 
 * @author Sc2ad
 * 
 */
public class GyroCombinedPath implements GyroMotionPath {
	
	@SuppressWarnings("javadoc")
	private GyroMotionPath[] paths;
	@SuppressWarnings("javadoc")
	private double travelledPathDistance, start, distance, totTime;
	private double x,y,lastXTime,lastYTime;
	private double travelledThetaDistance;
	
	/**
	 * Construct a CombinedPath with simply a start.
	 * Must be used in conjunction with {@link #setPath(GyroMotionPath[] p) setPath}, otherwise NullPointerExceptions will occur.
	 * 
	 * @param start the offset this path has (typically 0)
	 */
	public GyroCombinedPath(double start) {
		this.start = start;
	}
	/**
	 * Standard construction.
	 * 
	 * @param start the offset this path has (typically 0)
	 * @param p the paths that make up this CombinedPath
	 */
	public GyroCombinedPath(double start, GyroMotionPath... p) {
		paths = p;
		this.start = start;
	}
	/**
	 * Returns the current {@link MotionPath} at the given time.
	 * 
	 * @param time time since this class started
	 * @return the {@link MotionPath} at the current time
	 */
	private GyroMotionPath getCurve(double time) {
		travelledPathDistance = 0;
		travelledThetaDistance = 0;
		if (time <= paths[0].getTotalTime()) {
			return paths[0];
		}
		for (int i = 1; i < paths.length; i++) {
			double sum = paths[i].getTotalTime();
			double dsum = 0;
			double tsum = 0;
			for (int j=i-1; j >= 0; j--) {
				sum += paths[j].getTotalTime();
				dsum += paths[j].getTotalDistance();
				tsum += paths[j].getAngle(paths[j].getTotalTime());
			}
			if (time <= sum) {
				travelledPathDistance = dsum;
				travelledThetaDistance = tsum;
				return paths[i];
			}
		}
		return paths[paths.length-1]; // should never happen
	}
	
	/**
	 * Returns the time since the current {@link MotionPath} has begun.
	 * 
	 * @param time time since this class started
	 * @return returns time since the last {@link MotionPath} has begun running
	 */
	private double getDeltaTime(double time) {
		double maxSum = 0;
		if (time <= paths[0].getTotalTime()) {
			return time;
		}
		for (int i = 1; i < paths.length; i++) {
			double sum = 0;
			for (int j=i-1; j >= 0; j--) {
				sum += paths[j].getTotalTime();
			}
			maxSum = sum + paths[i].getTotalTime();
			if (time <= maxSum) {
				return time - sum;
			}
		}
		return time - maxSum; // should never happen
	}
	
	/**
	 * Used to set the {@link MotionPath} array of this object.
	 * @see #paths
	 * 
	 * @param p the path array to set {@link #paths} to
	 */
	public void setPath(GyroMotionPath[] p) {
		paths = p;
	}
	
	public GyroMotionPath copy() {
		GyroMotionPath[] paf = new GyroMotionPath[paths.length];
		for (int i = 0; i < paf.length; i++) {
			paf[i] = paths[i].copy();
		}
		return new GyroCombinedPath(start, paf);
	}
	
	public double getSpeed(double time) {
		double dt = getDeltaTime(time);
		if (time > getTotalTime()) {
			return getCurve(time).getSpeed(dt + dt < getCurve(time).getTotalTime() ? getCurve(time).getTotalTime() : 0);
		}
		return getCurve(time).getSpeed(dt);
	}
	
	public double getAccel(double time) {
		double dt = getDeltaTime(time);
		return getCurve(time).getAccel(dt);
	}

	public double getPosition(double time) {
		double dt = getDeltaTime(time);
		if (time >= getTotalTime()) {
			return start+getTotalDistance();
		}
		return start+getCurve(time).getPosition(dt) + travelledPathDistance;
	}

	public double getTotalTime() {
		if (totTime != 0) {
			return totTime;
		}
		double sum = 0;
		for (MotionPath p : paths) {
			sum += p.getTotalTime();
		}
		totTime = sum;
		return sum;
	}

	@Override
	public double getTotalDistance() {
		if (distance != 0) {
			return distance;
		}
		double sum = 0;
		for (MotionPath p : paths) {
			sum += p.getTotalDistance();
		}
		distance = sum;
		return sum;
	}
	
	@Override
	public boolean validate() {
		for (GyroMotionPath p : paths) {
			if (!p.validate()) return false;
		}
		return true;
	}
	
	@Override
	public double getX(double time) {
		double dt = time - lastXTime;
		x += getSpeed(time) * Math.sin(Util.d2r(getAngle(time))) * dt;
		lastXTime = time;
		return x;
	}
	
	@Override
	public double getY(double time) {
		double dt = time - lastYTime;
		y += getSpeed(time) * Math.cos(Util.d2r(getAngle(time))) * dt;
		lastYTime = time;
		return y;
	}
	
	@Override
	public double getAngle(double time) {
		double dt = getDeltaTime(time);
		if (time >= getTotalTime()) {
			return getCurve(time).getAngle(getTotalTime());
		}
		return getCurve(time).getAngle(dt) + travelledThetaDistance;
	}
	
	@Override
	public double getOmega(double time) {
		double dt = getDeltaTime(time);
		if (time > getTotalTime()) {
			return getCurve(time).getOmega(dt + dt < getCurve(time).getTotalTime() ? getCurve(time).getTotalTime() : 0);
		}
		return getCurve(time).getOmega(dt);
	}
	
	@Override
	public double getAlpha(double time) {
		double dt = getDeltaTime(time);
		return getCurve(time).getAlpha(dt);
	}
}
