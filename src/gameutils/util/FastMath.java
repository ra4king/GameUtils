package gameutils.util;

public class FastMath {
	public static final double PI = Math.PI;
	public static final double E = Math.E;
	
	private static final double RAD,DEG;
	private static final float RADf, DEGf;
	private static final double radToIndex, degToIndex;
	private static final int SIN_MASK = ~(-1 << 12);
	private static final double[] sin, cos;
	private static final int largeInt   = 16 * 1024;
	
	private static final int ATAN2_COUNT = (~(-1 << (7 << 1))) + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	private static final double[] atan2 = new double[ATAN2_COUNT];
	
	static {
		RAD = Math.PI/180.0;
		DEG = 180.0/Math.PI;
		
		RADf = (float)(Math.PI/180.0);
		DEGf = (float)(180.0/Math.PI);
		
		final int SIN_COUNT = SIN_MASK + 1;
		
		final double radFull    = Math.PI * 2.0;
		radToIndex = SIN_COUNT/radFull;
		degToIndex = SIN_COUNT/360.0;
		
		sin = new double[SIN_COUNT];
		cos = new double[SIN_COUNT];
		
		for(int i = 0; i < SIN_COUNT; i++) {
			sin[i] = Math.sin((i + 0.5)/SIN_COUNT * radFull);
			cos[i] = Math.cos((i + 0.5)/SIN_COUNT * radFull);
		}
		
		for(int i = 0; i < 360; i += 90) {
			sin[(int)(i * degToIndex) & SIN_MASK] = Math.sin(i * Math.PI/180.0);
			cos[(int)(i * degToIndex) & SIN_MASK] = Math.cos(i * Math.PI/180.0);
		}
		
		for(int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				double x0 = i/ATAN2_DIM;
				double y0 = j/ATAN2_DIM;
				
				atan2[j * ATAN2_DIM + i] = Math.atan2(y0, x0);
			}
		}
	}
	
	public static final int abs(int a) {
		return Math.abs(a);
	}
	
	public static final long abs(long a) {
		return Math.abs(a);
	}
	
	public static final float abs(float a) {
		return Math.abs(a);
	}
	
	public static final double abs(double a) {
		return Math.abs(a);
	}
	
	public static final int ceil(float x) {
		return largeInt - (int)(largeInt - x);
	}
	
	public static final int ceil(double x) {
		return largeInt - (int)(largeInt - x);
	}
	
	public static final int floor(float x) {
		return (int)(x + largeInt) - largeInt;
	}
	
	public static final int floor(double x) {
		return (int)(x + largeInt) - largeInt;
	}
	
	public static final int round(float x) {
		return (int)(x + largeInt + 0.5f) - largeInt;
	}
	
	public static final int round(double x) {
		return (int)(x + largeInt + 0.5) - largeInt;
	}
	
	public static final float toDegrees(float rad) {
		return rad*DEGf;
	}
	
	public static final double toDegrees(double rad) {
		return rad*DEG;
	}
	
	public static final float toRadians(float deg) {
		return deg*RADf;
	}
	
	public static final double toRadians(double deg) {
		return deg*RAD;
	}
	
	public static final float sin(float rad) {
		return (float)sin(rad);
	}
	
	public static final double sin(double rad) {
		return sin[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final float cos(float rad) {
		return (float)cos(rad);
	}
	
	public static final double cos(double rad) {
		return cos[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final float sinDeg(float deg) {
		return (float)sinDeg(deg);
	}
	
	public static final double sinDeg(double deg) {
		return sin[(int)(deg * degToIndex) & SIN_MASK];
	}
	
	public static final float cosDeg(float deg) {
		return (float)cosDeg(deg);
	}
	
	public static final double cosDeg(double deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}
	
	public static final float atan2Deg(float y, float x) {
		return (float)atan2Deg(y,x);
	}
	
	public static final double atan2Deg(double y, double x) {
		return atan2(y, x) * DEG;
	}
	
	public static final float atan2(float y, float x) {
		return (float)atan2(y,x);
	}
	
	public static final double atan2(double y, double x) {
		double add, mul;
		
		if(x < 0.0) {
			if(y < 0.0) {
				x = -x;
				y = -y;
				
				mul = 1.0;
			}
			else {
				x = -x;
				mul = -1.0;
			}
			
			add = -3.141592653;
		}
		else {
			if(y < 0.0) {
				y = -y;
				mul = -1.0;
			}
			else {
				mul = 1.0;
			}
			
			add = 0.0;
		}
		
		double invDiv = (ATAN2_DIM-1) / ((x < y) ? y : x);
		
		int xi = (int)(x * invDiv);
		int yi = (int)(y * invDiv);
		
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}
}