package scadlib.extra;

public class PIDF {
	private double p,i,d,f;
	private double target,velTarget;
	private double totalError, lastError, lastTime;
	public PIDF(double p, double i, double d, double f) {
		configPIDF(p,i,d,f);
	}
	public void configPIDF(double p, double i, double d, double f) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		reset();
	}
	public void reset() {
		totalError = 0;
		lastError = 0;
		lastTime = -1;
	}
	public double getTarget() {
		return target;
	}
	public double getVelTarget() {
		return velTarget;
	}
	public void setTarget(double target) {
		this.target = target;
		this.velTarget = 0;
//		reset();
	}
	public void setTarget(double target, double velTarget) {
		this.target = target;
		this.velTarget = velTarget;
//		reset();
	}
	public double getError(double current) {
		return target - current;
	}
	public double getTotalError(double current) {
		totalError += getError(current);
		return totalError;
	}
	public double getDeltaError(double current, double time) {
		double error = getError(current);
		if (lastTime == -1 || time < lastTime) {
			lastTime = time;
			lastError = error;
			return 0;
		}
		double out = (error - lastError) / (time - lastTime);
		lastTime = time;
		lastError = error;
		return out;
	}
	public double getPStep(double current) {
		return p * getError(current);
	}
	public double getIStep(double current) {
		return i * getTotalError(current);
	}
	public double getDStep(double time, double current) {
		return d * getDeltaError(current, time);
	}
	public double getFStep() {
		return f * velTarget;
	}
	public double getOut(double time, double current) {
		return getPStep(current) + getIStep(current) + getDStep(time, current) + getFStep();
	}
}
