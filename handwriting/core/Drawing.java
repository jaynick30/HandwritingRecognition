package handwriting.core;

import java.util.BitSet;

public class Drawing {
	private BitSet bits;
	private int width, height;
	
	private void init(int width, int height) {
		this.width = width;
		this.height = height;
		bits = new BitSet(width * height);
	}
	
	public Drawing(int width, int height) {
		init(width, height);
	}
	
	public Drawing(String encoded) {
		String[] tokens = encoded.split("\\|");
		int width = Integer.parseInt(tokens[0]);
		int height = Integer.parseInt(tokens[1]);
		init(width, height);
		

		for (int t = 2, x = 0; t < tokens.length; ++t, ++x) {
			String line = tokens[t];
			for (int y = 0; y < line.length(); ++y) {
				set(x, y, line.charAt(y) == 'X');
			}
		}
	}
	
	public Drawing(Drawing other) {
		this(other.width, other.height);
		for (int i = 0; i < other.bits.size(); i++) {
			bits.set(i, other.bits.get(i));
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean inBounds(int x, int y) {
		return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
	}
	
	private int bitFor(int x, int y) {
		return y * width + x;
	}
	
	public void set(int x, int y, boolean on) {
		if (inBounds(x, y)) {
			bits.set(bitFor(x, y), on);
		}
	}
	
	public boolean isSet(int x, int y) {
		return bits.get(bitFor(x, y));
	}
	
	public void clear() {
		for (int x = 0; x < getWidth(); ++x) {
			for (int y = 0; y < getHeight(); ++y) {
				set(x, y, false);
			}
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getWidth());
		result.append("|");
		result.append(getHeight());
		result.append("|");
		for (int x = 0; x < getWidth(); ++x) {
			for (int y = 0; y < getHeight(); ++y) {
				result.append(bits.get(bitFor(x, y)) ? 'X' : 'O');
			}
			result.append("|");
		}
		return result.toString();
	}

	public int hashCode() {return bits.hashCode();}
	
	public boolean equals(Object other) {
		if (other instanceof Drawing) {
			return bits.equals(((Drawing)other).bits);
		} else {
			return false;
		}
	}
}
