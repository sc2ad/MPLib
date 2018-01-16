package paths;

public class Point {
	public double x,y,vx,vy,ax,ay = 0;
	public Point(double x, double y, double vx, double vy, double ax, double ay) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.ax = ax;
		this.ay = ay;
	}
	public Point(double x, double y, double vOnS, double aOnS, double theta) {
		this.x = x;
		this.y = y;
		vx = vOnS * Math.cos(Math.toRadians(90 - theta));
		vy = vOnS * Math.sin(Math.toRadians(90 - theta));
		ax = aOnS * Math.cos(Math.toRadians(90 - theta));
		ay = aOnS * Math.sin(Math.toRadians(90 - theta));
	}
	public String toString() {
		return "("+x+", "+y+", "+vx+", "+vy+", "+ax+", "+ay+")";
	}
}
