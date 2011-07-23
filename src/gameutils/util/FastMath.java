package gameutils.util;

public class FastMath {
	public static final float PI = (float)Math.PI;
	public static final float E = (float)Math.E;
	
	private static final float RAD,DEG;
	private static final float radToIndex, degToIndex;
	private static final int SIN_MASK = ~(-1 << 12);
	private static final float[] sin, cos;
	
	private static final int ATAN2_COUNT = (~(-1 << (7 << 1))) + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	private static final float[] atan2 = new float[ATAN2_COUNT];
	
	static {
		RAD = (float)(Math.PI/180.0);
		DEG = (float)(180.0/Math.PI);
		
		final int SIN_COUNT = SIN_MASK + 1;
		
		final float radFull    = PI * 2.0f;
		radToIndex = SIN_COUNT/radFull;
		degToIndex = SIN_COUNT/360.0f;
		
		sin = new float[SIN_COUNT];
		cos = new float[SIN_COUNT];
		
		for(int i = 0; i < SIN_COUNT; i++) {
			sin[i] = (float)Math.sin((i + 0.5)/SIN_COUNT * radFull);
			cos[i] = (float)Math.cos((i + 0.5)/SIN_COUNT * radFull);
		}
		
		for(int i = 0; i < 360; i += 90) {
			sin[(int)(i * degToIndex) & SIN_MASK] = (float)Math.sin(i * Math.PI/180.0);
			cos[(int)(i * degToIndex) & SIN_MASK] = (float)Math.cos(i * Math.PI/180.0);
		}
		
		for(int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				float x0 = (float)i/ATAN2_DIM;
				float y0 = (float)j/ATAN2_DIM;
				
				atan2[j * ATAN2_DIM + i] = (float)Math.atan2(y0, x0);
			}
		}
	}
	
	public static final int min(int a, int b) {
		return Math.min(a, b);
	}
	
	public static final float min(float a, float b) {
		return Math.min(a,b);
	}
	
	public static final double min(double a, double b) {
		return Math.min(a,b);
	}
	
	public static final int max(int a, int b) {
		return Math.max(a, b);
	}
	
	public static final float max(float a, float b) {
		return Math.max(a,b);
	}
	
	public static final double max(double a, double b) {
		return Math.max(a,b);
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
		return (int)Math.ceil(x);
	}
	
	public static final int ceil(double x) {
		return (int)Math.ceil(x);
	}
	
	public static final int floor(float x) {
		return (int)Math.floor(x);
	}
	
	public static final int floor(double x) {
		return (int)Math.floor(x);
	}
	
	public static final int round(float x) {
		return Math.round(x);
	}
	
	public static final int round(double x) {
		return (int)Math.round(x);
	}
	
	public static final float toDegrees(float rad) {
		return rad*DEG;
	}
	
	public static final double toDegrees(double rad) {
		return rad*DEG;
	}
	
	public static final float toRadians(float deg) {
		return deg*RAD;
	}
	
	public static final double toRadians(double deg) {
		return deg*RAD;
	}
	
	public static final float sin(float rad) {
		return sin[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final float cos(float rad) {
		return cos[(int)(rad * radToIndex) & SIN_MASK];
	}
	
	public static final float sinDeg(float deg) {
		return sin[(int)(deg * degToIndex) & SIN_MASK];
	}
	
	public static final float cosDeg(float deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}
	
	public static final float atan2Deg(float y, float x) {
		return atan2(y, x) * DEG;
	}
	
	public static final float atan2(float y, float x) {
		float add, mul;
		
		if (x < 0.0f) {
			if (y < 0.0f) {
				x = -x;
				y = -y;
				
				mul = 1.0f;
			}
			else {
				x = -x;
				mul = -1.0f;
			}
			
			add = -PI;
		}
		else {
			if (y < 0.0f) {
				y = -y;
				mul = -1.0f;
			}
			else
				mul = 1.0f;
			
			add = 0.0f;
		}
		
		float invDiv = (ATAN2_DIM-1) / ((x < y) ? y : x);
		
		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}
}