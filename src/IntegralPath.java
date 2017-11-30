public class IntegralPath implements MotionPath {
	private MotionPath speedPath;
	private double position;
	private double lastTime;
	// This acts as an integral of the speed path provided
	
	public IntegralPath(MotionPath speedPath) {
		this.speedPath = speedPath;
		position = 0;
		lastTime = 0;
	}

	public double getSpeed(double time) {
		return speedPath.getPositionOnPath(time);
	}
	public double getAccel(double time) {
		return speedPath.getSpeed(time);
	}
	public double getPositionOnPath(double time) {
		// x + v0t + 1/2at^2
		position += getSpeed(time) * (time - lastTime);
		lastTime = time;
		return position;
//		return getSpeed(0) * time + getSpeed(time) * time;
//		return getSpeed(0) * time + getSpeed(time) * time + getAccel(time) * time; // redundant because it already happens
	}
	public double getTotalTime() {
		return speedPath.getTotalTime();
	}
	public double getTotalDistance() {
		return getPositionOnPath(getTotalTime());
	} // Basically the same for all paths

	public boolean validate() {
		return true;
	}
}