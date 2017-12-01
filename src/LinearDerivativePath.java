public class LinearDerivativePath implements MotionPath {
	private double velStart,velEnd,accel,distance,totalTime;
	
	/*
	 * TODO SIMPLIFY THIS FILE TO USE INTEGRALPATH WITH HOLD
	 */

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
	@Override
	public MotionPath copy() {
		return new LinearDerivativePath(distance, velStart, velEnd, accel);
	}
	@Override
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
	@Override
	public double getTotalDistance() {
		if (distance != 0) {
			return distance;
		}
		distance = getPosition(getTotalTime());
		return distance;
	}
	@Override
	public double getSpeed(double time) {
		return velStart + accel * time;
	}
	@Override
	public double getAccel(double time) {
		return accel;
	}
	@Override
	public double getPosition(double time) {
		return velStart * time + 0.5 * getAccel(time) * time * time; // not needed because doing already
	}
	@Override
	public boolean validate() {
		//TODO if x==predicted x, v==pred v, a==preda
		return true;
	}
}