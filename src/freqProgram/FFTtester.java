package freqProgram;

import AudioFile.WavePattern_Decoder.WaveManager;

public class FFTtester {
	
	private static float sampleRate = 44100f;
	private static int sampleSizeinBits = 16;
	private static int sampleSize = 2;
	private static int channels = 1;
	private static byte[] sampleList;
	
	public static void main(String args[]) {
		sampleList = new byte[1024];
		createSampleList();
		
	}
	
	private static void createSampleList() {
		double sin;
		WaveManager wm[] = new WaveManager[2];
		final double amplitude = 0.5;
		wm[0] = new WaveManager(440, amplitude, sampleRate);
		wm[1] = new WaveManager(880, amplitude, sampleRate);
		//wm[2] = new WaveManager(1760, amplitude, sampleRate);
		final int valueRange = (int)(Math.pow(2,  sampleSizeinBits - 1) - 1);
		int sinVal;
		for (int i = 0; i < sampleList.length; i += sampleSize * channels) {
			sin = 0;
			int curSample = i / (sampleSize * channels);
			for(int idx = 0; idx < wm.length; ++idx) {
				sin += Math.sin(2 * Math.PI * curSample / wm[idx].samplesPerPeriod) * wm[idx].amplitude;
			}
			//Left Sample
			sinVal = limitVal((int)(sin * valueRange), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
			//Right Sample
			sinVal = limitVal((int)(sin * valueRange * 1), -valueRange, valueRange);
			for (int x = 0; x < sampleSize; ++x) {
				sampleList[i + 2 + x] = (byte)(sinVal >> (8 * x)); //Therefore saved in Little Endian
			}
		}
		
	}
	
	private static int limitVal(int val, int min, int max) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}
}
