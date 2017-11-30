
public class CombinedPath implements MotionPath {
	/*
	 * ENUM CLASSES
	 */
	public static class Trapezoid extends CombinedPath {

		public Trapezoid(double distance, double maxV, double a) {
			super(0.0);
			MotionPath[] p = new MotionPath[3];
			p[0] = new LinearPath(0, maxV, a);
			p[2] = new LinearPath(maxV, 0, -a);
			// The cruise one is the one we know the LEAST about, need to use positions of others
			p[1] = new LinearPath(distance - 2 * p[0].getTotalDistance(), maxV);
			setPath(p);
		}
		public Trapezoid(double start, double end, double startV, double maxV, double a) {
			super(start);
			double dist = end-start;
			MotionPath[] p = new MotionPath[3];
			p[0] = new LinearPath(startV, maxV, a);
			p[2] = new LinearPath(maxV, 0, -a);
			// The cruise one is the one we know the LEAST about, need to use positions of others
			p[1] = new LinearPath(dist - 2 * p[0].getTotalDistance(), maxV);
			setPath(p);
		}
		
	}
	
	
	private MotionPath[] paths;
	private double travelledPathDistance, start, distance, totTime;
	
	public CombinedPath(double start) {
		this.start = start;
	}
	public CombinedPath(double start, MotionPath... p) {
		paths = p;
		this.start = start;
	}
	private MotionPath getCurve(double time) {
		travelledPathDistance = 0;
		if (time <= paths[0].getTotalTime()) {
			return paths[0];
		}
		for (int i = 1; i < paths.length; i++) {
			double sum = paths[i].getTotalTime();
			double dsum = 0;
			for (int j=i-1; j >= 0; j--) {
				sum += paths[j].getTotalTime();
				dsum += paths[j].getTotalDistance();
			}
			if (time <= sum) {
				travelledPathDistance = dsum;
				return paths[i];
			}
		}
		return paths[paths.length-1]; // should never happen
	}
	
	private double getDeltaTime(double time) {
		if (time <= paths[0].getTotalTime()) {
			return time;
		}
		for (int i = 1; i < paths.length; i++) {
			double sum = 0;
			for (int j=i-1; j >= 0; j--) {
				sum += paths[j].getTotalTime();
			}
			if (time <= sum + paths[i].getTotalTime()) {
				return time - sum;
			}
		}
		return time - getTotalTime(); // should never happen
	}
	
	public void setPath(MotionPath[] p) {
		paths = p;
	}
	
	public double getSpeed(double time) {
		double dt = getDeltaTime(time);
		if (time >= getTotalTime()) {
			return 0;
		}
		return getCurve(time).getSpeed(dt);
	}
	
	public double getAccel(double time) {
		double dt = getDeltaTime(time);
		if (time >= getTotalTime()) {
			return 0;
		}
		return getCurve(time).getAccel(dt);
	}

	public double getPositionOnPath(double time) {
		double dt = getDeltaTime(time);
		if (time >= getTotalTime()) {
			return start+distance;
		}
		return start+getCurve(time).getPositionOnPath(dt) + travelledPathDistance;
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
	
	public boolean validate() {
		//TODO ADD ERROR CHECKS
		return true;
	}
}
