
public class OverlappingPath implements GyroMotionPath {

	MotionPath longPath;
	MotionPath gyroPath;
	private double x;
	private double y;
	private double lastXTime;
	private double lastYTime;
	double gyroDeltaTime;
	
	public OverlappingPath(MotionPath longitudalPath, MotionPath gyroPath) {
		longPath = longitudalPath;
		this.gyroPath = gyroPath;
		gyroDeltaTime = (longPath.getTotalTime() - gyroPath.getTotalTime()) / 2;
	}
	
	@Override
	public double getX(double time, int SAMPLES) {
		// lazy integrals
//		double dt = time - lastXTime;
//		x += getSpeed(time) * Math.sin(Util.d2r(getAngle(time))) * dt;
//		lastXTime = time;
//		return x;
		return getX(0, time, SAMPLES);
	}
	
	public double getX(double tstart, double tend, int SAMPLES) {
		double dt = tend - tstart;
		double dx = 0;
		for (int n = 0; n < SAMPLES; n++) {
			dx += getSpeed(n*dt/SAMPLES + tstart) * dt / SAMPLES * Math.sin(Util.d2r(getAngle(n*dt/SAMPLES + tstart)));
		}
		return dx;
	}
	
	@Override
	public double getY(double time, int SAMPLES) {
		// lazy integrals
//		double dt = time - lastYTime;
//		y += getSpeed(time) * Math.cos(Util.d2r(getAngle(time))) * dt;
//		lastYTime = time;
//		return y;
		return getY(0, time, SAMPLES);
	}
	
	public double getY(double tstart, double tend, int SAMPLES) {
		double dt = tend - tstart;
		double dy = 0;
		for (int n = 0; n < SAMPLES; n++) {
			dy += getSpeed(n*dt/SAMPLES + tstart) * dt / SAMPLES * Math.cos(Util.d2r(getAngle(n*dt/SAMPLES + tstart)));
		}
		return dy;
	}
	
	private double retTimeCheck(double actualTime, double start, double end, double middle) {
		if (actualTime < 0) {
			return start;
		}
		if (actualTime > gyroPath.getTotalTime()) {
			return end;
		}
		return middle;
	}
	
	@Override
	public double getAngle(double time) {
//		return time <= gyroPath.getTotalTime() ? time - gyroDeltaTime > 0 ? gyroPath.getPosition(time) : 0 : gyroPath.getTotalDistance();
		return retTimeCheck(time - gyroDeltaTime, gyroPath.getPosition(0), gyroPath.getTotalDistance(), gyroPath.getPosition(time - gyroDeltaTime));
	}
	
	@Override
	public double getOmega(double time) {
//		return time <= gyroPath.getTotalTime() ? gyroPath.getSpeed(time) : 0;
		return retTimeCheck(time - gyroDeltaTime, gyroPath.getSpeed(0), gyroPath.getSpeed(gyroPath.getTotalTime()), gyroPath.getSpeed(time - gyroDeltaTime));
	}
	
	@Override
	public double getAlpha(double time) {
//		return time <= gyroPath.getTotalTime() ? gyroPath.getAccel(time) : 0;
		return retTimeCheck(time - gyroDeltaTime, gyroPath.getAccel(0), gyroPath.getAccel(gyroPath.getTotalTime()), gyroPath.getAccel(time - gyroDeltaTime));
	}
	
	@Override
	public GyroMotionPath copy() {
		return new OverlappingPath(longPath, gyroPath);
	}

	@Override
	public double getAccel(double time) {
		return longPath.getAccel(time);
	}

	@Override
	public double getSpeed(double time) {
		return longPath.getSpeed(time);
	}

	@Override
	public double getPosition(double time) {
		return longPath.getPosition(time);
	}

	@Override
	public double getTotalTime() {
		return longPath.getTotalTime();
	}

	@Override
	public double getTotalDistance() {
		return longPath.getTotalDistance();
	}

	@Override
	public boolean validate() {
		return true;
	}

}
