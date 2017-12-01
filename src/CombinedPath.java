
public class CombinedPath implements MotionPath {
	/*
	 * ENUM CLASSES
	 */
	public static class LongitudalTrapezoid extends CombinedPath {

		public LongitudalTrapezoid(double start, double distance, double maxV, double a) {
			super(start);
			MotionPath[] p = new MotionPath[3];
			p[0] = new LinearDerivativePath(0, maxV, a);
			p[2] = new LinearDerivativePath(maxV, 0, -a);
			// The cruise one is the one we know the LEAST about, need to use positions of others
			p[1] = new LinearDerivativePath(distance - 2 * p[0].getTotalDistance(), maxV);
			if (p[0].getTotalDistance() > distance / 2) {
				// Need to setup a triangle instead
				// 1/2at^2 = distance/2
				double newTime = Math.sqrt(distance / a);
				p[0] = new LinearDerivativePath(0, newTime * a, a); // don't question jankness
				p[1] = new Hold(0); // Legit, nothing
				p[2] = new LinearDerivativePath(newTime * a, 0, -a);
			}
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
	
	public void setPath(MotionPath[] p) {
		paths = p;
	}
	
	public MotionPath copy() {
		MotionPath[] paf = new MotionPath[paths.length];
		for (int i = 0; i < paf.length; i++) {
			paf[i] = paths[i].copy();
		}
		return new CombinedPath(start, paf);
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
		//TODO ADD ERROR CHECKS
		return true;
	}
}
