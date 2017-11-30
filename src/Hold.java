
public class Hold implements MotionPath {

	double time, value;
	
	public Hold(double time) {
		this.time = time;
		value = 0;
	}
	public Hold(double time, double value) {
		this.time = time;
		this.value = value;
	}
	
	public double getSpeed(double time) {
		return 0;
	}

	public double getAccel(double time) {
		return 0;
	}

	public double getPositionOnPath(double time) {
		return value; // 0 for a hold
	}

	public double getTotalTime() {
		return time;
	}

	public double getTotalDistance() {
		return 0; // 0 for a hold!
	}

	public boolean validate() {
		// TODO this prob needs no false validate tho
		return true;
	}

}
