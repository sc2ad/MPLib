
public interface GyroMotionPath extends MotionPath {
	@Override
	public GyroMotionPath copy();
	public double getX(double time, int SAMPLES);
	public double getY(double time, int SAMPLES);
	public double getAngle(double time);
	public double getOmega(double time);
	public double getAlpha(double time);
}
