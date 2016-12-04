package handwriting.learners.som;

public class SOMPoint {
	private int x, y;
	
	public SOMPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {return x;}
	public int y() {return y;}
	
	public double distanceTo(SOMPoint other) {
		return distanceTo(other.x, other.y);
	}
	
	public double distanceTo(int x, int y) {
		return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
	}
	
	public int hashCode() {return x * 10000 + y;}
	public String toString() {return String.format("(%d,%d)", x, y);}
	public boolean equals(Object other) {
		if (other instanceof SOMPoint) {
			SOMPoint that = (SOMPoint)other;
			return this.x == that.x && this.y == that.y;
		} else {
			return false;
		}
	}
}
