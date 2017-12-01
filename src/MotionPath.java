public interface MotionPath {
	public MotionPath copy();
	public double getSpeed(double time);
	public double getAccel(double time);
	public double getPosition(double time);
	public double getTotalTime();
	public double getTotalDistance(); // Basically the same for all paths
	public boolean validate();
}