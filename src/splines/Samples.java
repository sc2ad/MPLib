package splines;

public enum Samples {
	HIGH(100000), MIDDLE(50000), LOW(20000), FAST(10000);
	int value;
	private Samples(int v) {
		value = v;
	}
}
