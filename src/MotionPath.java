public interface MotionPath {
	public double getSpeed(double time);
	public double getAccel(double time);
	public double getPositionOnPath(double time);
	public double getTotalTime();
	public double getTotalDistance(); // Basically the same for all paths
	public boolean validate();
}