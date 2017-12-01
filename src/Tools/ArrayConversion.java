package Tools;


public abstract class ArrayConversion {
	
	/**
	 * The out array must have at least half the size of the in-array
	 * The in Array must have a size divisible by two, as shorts need two bytes
	 * @param in: the byte array with the input values
	 * @param out: the short array the input values are written into
	 */
	public static void reinterpByteToShort(byte[] in, short[] out) {
		for (int i = 0; i < in.length; i += 2) {
			out[i / 2] = (short)((in[i + 1] << 8) | (in[i] & 0xff));
			System.out.printf("%x : %x, %x\n", out[i / 2], in[i], in[i + 1]);
		}
	}
	
	/**
	 * @param b1 the Low byte, e.g. in Lower Endian the byte at the lower index
	 * @param b2 the High byte, e.g. in Lower Endian the byte at the higher index
	 * @return b1, b2 reinterpreted as short
	 */
	public static short reinterpByteToShort(byte b1, byte b2) {
		return (short)((b2 << 8) | (b1 & 0xff));
	}
	
	/**
	 * The out array must be at least double the size of the in array
	 * @param in: the short array to be reinterpreted
	 * @param out: the byte array to be written into
	 */
	public static void reinterpShortToByte(short[] in, byte[] out) {
		for (int i = 0; i < in.length; ++i) {
			out[i * 2] = (byte) in[i];
			out[i * 2 + 1] = (byte) (in[i] >> 8);
		}
	}
	
	public static void reinterpShortToByte(short in, byte []out, int offset) {
		out[offset] = (byte)in;
		out[offset + 1] = (byte)(in >> 8);
	}
	
	public static void reinterpShortToByte(short in, byte []out) {
		reinterpShortToByte(in, out, 0);
	}
}
