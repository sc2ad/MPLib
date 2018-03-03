package scadlib.extra;

import scadlib.paths.FastPathPlanner;

public class PathData {
	public FastPathPlanner path;
	
	public PathData(double[][] path, double totalTime, double dt, double width) {
		this.path = new FastPathPlanner(path);
		this.path.calculate(totalTime, dt, width);
	}
	public PathData(double[][] path, double totalTime, double dt, double width, boolean reversed) {
		this.path = new FastPathPlanner(path);
		this.path.calculate(totalTime, dt, width);
		if (reversed) {
			reversePath(this.path);
		}
	}
	private void reversePath(FastPathPlanner path) {
		double[][] tmp = path.rightPath;
		path.rightPath = path.leftPath;
		path.leftPath = tmp;
		
		for (int i=0; i < path.smoothLeftVelocity.length; i++) {
			path.smoothRightVelocity[i][1] = -path.smoothRightVelocity[i][1];
			path.smoothLeftVelocity[i][1] = -path.smoothLeftVelocity[i][1];
			path.smoothCenterVelocity[i][1] = -path.smoothCenterVelocity[i][1];
		}
	}
}
