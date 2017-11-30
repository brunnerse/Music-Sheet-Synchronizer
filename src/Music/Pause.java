package Music;

public class Pause extends Note {

	public Pause(byte duration, byte time) {
		super(Pitch.getPitch('c', (byte)1, Pitch.Vorzeichen.PLAIN), 0, duration, Note.Articulation.PLAIN, time);
	}
	
	@Override
	public String toString() {
		int dauer;
		int divisor = 2;
		do {
			divisor *= 2;
			dauer = this.getDuration() * divisor / 64;
		} while(dauer != (float)(this.getDuration() * divisor) / 64);
		return "PAUSE:" + dauer + "/" + divisor;
	}
	
}
