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

    public static void main(String[] args) throws LineUnavailableException {
    	int sampleRate = 44100;
        final AudioFormat af =  new AudioFormat(sampleRate, 16, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        line.open(af, 44100);
        line.start();
        
        byte[] b = new byte[sampleRate * 4];
        short[] s = new short[sampleRate * 2];
        
        int freq1 = 440, freq2 = 220;
        
        //Sin Wave is better because it doesnt make a step at the start
        for (int i = 0; i < sampleRate; ++i) {
        	s[i] = (short)(10000 * Math.sin(2 * Math.PI * freq1 * i / sampleRate));
        }
       //s[sampleRate - 1] is 10000, s[sampleRate + 1] is 10000
        s[sampleRate] = 0; //difference between two points should be max. 500
        
        for (int i = 1; i < sampleRate; ++i) {
        	s[i + sampleRate] = (short)(10000 * Math.sin(2 * i * freq2 * Math.PI / sampleRate));
        }
        System.out.println(s[sampleRate - 1] + "\t" + s[sampleRate]);
        
        for (int i = 0; i < s.length; ++i) {
        	b[2 * i] = (byte)s[i];
        	b[2 * i + 1] = (byte)(s[i] >> 8);
        }
        
        line.write(b, 0, b.length / 2);
        line.write(b, b.length / 2, b.length / 2);
        line.stop();
        line.close();
    }

}

