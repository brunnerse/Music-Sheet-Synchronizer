package Music;

public class Note {
	private final byte volume; //0 corresponding to lowest volume, 127 highest volume
	private byte duration; //should be multiples of 1/64 (which is seen as the lowest duration possible)
	private int time; //duration (in 1/64) which has passed from the start until the note is played
	
	private final Pitch p;
	private final byte articulation; //is in format from Articulation
	
	/**
	 * 
	 * @param p Pitch of the Note
	 * @param volume Value between 0 and 1
	 * @param duration in 1/64, e.g. 16 = 1/4
	 * @param articulation from Note.Articulation
	 * @param time in 1/64 from the start
	 */
	public Note(Pitch p, float volume, int duration, byte articulation, int time) {
		this.p = p;
		this.volume = (byte)(volume * Byte.MAX_VALUE);
		this.duration = (byte)duration;
		this.articulation = articulation;
		this.time = time;
	}
	
	/**
	 * to be used when the time of the Note is automatically set by MusicSheet
	 */
	public Note(Pitch p, float volume, int duration, byte articulation) {
		this(p, volume, duration, articulation, 0);
	}
	
	public Note(Pitch p, float volume, int duration) {
		this(p, volume, duration, Articulation.PLAIN, 0);
	}
	
	public Note(Pitch p, float volume, int duration, int time) {
		this(p, volume, duration, Articulation.PLAIN, time);
	}
	
	public float getVolume() {
		return (float)volume / Byte.MAX_VALUE;
	}

	public byte getDuration() {
		return duration;
	}

	public Pitch getPitch() {
		return p;
	}

	public byte getArticulation() {
		return articulation;
	}

	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public void setDuration(int duration) {
		this.duration = (byte)duration;
	}

	
	//not using enumerations as they take too much memory
	public static abstract class Articulation {
		public static final byte PLAIN = 0, STACCATO = 1, LEGATO = 2;
	};
	
	
	public static float getVolumefromNotation(String vol) throws IllegalArgumentException {
		if (vol.compareToIgnoreCase("ff") == 0) {
			return 1f;
		} else  if (vol.compareToIgnoreCase("f") == 0) {
			return 0.8f;
		} else  if (vol.compareToIgnoreCase("mf") == 0) {
			return 0.6f;
		} else  if (vol.compareToIgnoreCase("mp") == 0) {
			return 0.4f;
		} else  if (vol.compareToIgnoreCase("p") == 0) {
			return 0.2f;
		} else  if (vol.compareToIgnoreCase("pp") == 0) {
			return 0.1f;
		} else {
			throw new IllegalArgumentException("The Volume " + vol + " isnt a denotation for volume.");
		}
	}
}
