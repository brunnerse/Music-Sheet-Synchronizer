package testing_and_notes;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * NOTE: The ugly jump between two notes is when two consecutive PCM Values are too far apart, e.g. 10000 and 1000
 *TODO: Make one note fastly more silent and then the consecutive one fastly more loud to get a fluent transition
 * see the current example in main()
 */
public class Note_Frequency_Playback {
	private static int freq1 = 440, freq2 = 220;
	private static int sampleRate = 44100;
	
	public static void main(String[] args) throws LineUnavailableException {
		final AudioFormat af = new AudioFormat(sampleRate, 16, 1, true, false);
		SourceDataLine line = AudioSystem.getSourceDataLine(af);
		line.open(af, 44100);
		line.start();

		byte[] b = new byte[sampleRate * 4];
		short[] s = new short[sampleRate * 2 - 10];

		for (int ix = 0; ix < 3; ++ix) {
			if (ix == 0)
				processRaw(s);
			else if (ix == 1)
				processAbrupt(s); 
			else processSmooth(s); //Testing confirms this seems to be the best tactic

			s[s.length / 2] = 0; // difference between two points should be max. 500

			for (int i = 0; i < s.length / 2 - 1; ++i) {
				s[i + 1 + s.length / 2] = (short) (10000 * Math.sin(2 * i * freq2 * Math.PI / sampleRate));
			}

			System.out.println("\nProcessing\n");
			for (int i = s.length / 2 - 50; i < s.length / 2 + 25; ++i) {
				System.out.print(i + ":\t" + s[i] + "\t");
				if (i % 10 == 0)
					System.out.println();
			}

			for (int i = 0; i < s.length; ++i) {
				b[2 * i] = (byte) s[i];
				b[2 * i + 1] = (byte) (s[i] >> 8);
			}

			line.write(b, 0, b.length / 2);
			line.write(b, b.length / 2, b.length / 2);
		}
		line.stop();
		line.close();
	}

    public static void processSmooth(short[] s) {
        boolean reachedZero = false;
        float volStep = 0.01f, volume = 1f;
    	for (int i = 0; i < s.length / 2; ++i) {
        	if (reachedZero) {
        		s[i] = 0;
        		System.out.println("reachedzero");
        	} else {
        			s[i] = (short)(10000 * Math.sin(2 * Math.PI * freq1 * i / sampleRate));
        			if (i > s.length / 2 - 100) {
        				volume -= volStep;
    					s[i] = (short)(s[i] * volume);
    					if (volume <= 0f)
    						reachedZero = true;
        			}
        	}	
        }
	}

	public static void processRaw(short[] s) {
        //Sin Wave is better because it doesnt make a step at the start
        for (int i = 0; i < s.length / 2; ++i) {
        			s[i] = (short)(10000 * Math.sin(2 * Math.PI * freq1 * i / sampleRate));		
        }
    }
    
    public static void processAbrupt(short[] s) {
        //Sin Wave is better because it doesnt make a step at the start
        boolean reachedZero = false;
        for (int i = 0; i < s.length / 2; ++i) {
        	if (reachedZero) {
        		s[i] = 0;
        	} else {
        			s[i] = (short)(10000 * Math.sin(2 * Math.PI * freq1 * i / sampleRate));
        			if (i > s.length / 2 - 50) {
        				if (Math.abs(s[i]) <= 200) {
        					reachedZero = true;
        				}
        				System.out.println(i + "\t" + reachedZero + "\t" + s[i]);
        			}
        	}	
        }
    }
    
}

