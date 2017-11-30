public class LinearDerivativePath implements MotionPath {
	private double velStart,velEnd,accel,distance,totalTime;

	public LinearDerivativePath(double distance, double v0, double v, double a) {
		velStart = v0;
		velEnd = v;
		accel = a;
		this.distance = distance;
		totalTime = getTotalTime();
	}
	public LinearDerivativePath(double v0, double v, double a) {
		velStart = v0;
		velEnd = v;
		accel = a;
		if (accel == 0) {
			throw new IllegalArgumentException("This constructor requires an acceleration");
		}
		totalTime = getTotalTime();
	}
	public LinearDerivativePath(double distance, double v) {
		velStart = v;
		velEnd = v;
		accel = 0;
		this.distance = distance;
		totalTime = getTotalTime();
	}
	public double getTotalTime() {
		if (totalTime != 0) {
			return totalTime;
		}
		if (accel == 0) {
			totalTime = distance / velStart;
			return totalTime;
		}
		// 0.5 at^2 + v0t = dx
		// v^2 = v0^2 + 2ax
		// v = v0 + at
		totalTime = (velEnd - velStart) / accel;
		return totalTime;
	}
	public double getTotalDistance() {
		if (distance != 0) {
			return distance;
		}
		distance = getPositionOnPath(getTotalTime());
		return distance;
	}
	public double getSpeed(double time) {
		return velStart + accel * time;
	}
	public double getAccel(double time) {
		return accel;
	}
	public double getPositionOnPath(double time) {
		return velStart * time + 0.5 * getAccel(time) * time * time; // not needed because doing already
	}
	public boolean validate() {
		//TODO if x==predicted x, v==pred v, a==preda
		return true;
	}
}