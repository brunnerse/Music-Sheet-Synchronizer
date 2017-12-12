package Music;

/**
 * Klasse, die einen Takt repräsentiert
 * @author Severin
 */
public class Bar {
	private int startIdx;
	private Beat takt;

	/**
	 * @param t:
	 *            Beat of the Bar
	 * @param startIdx:
	 *            Idx of the first note of the Bar in the note-Array
	 */
	public Bar(Beat t, int startIdx) {
		this.takt = t;
		this.startIdx = startIdx;
	}

	public Beat getBeat() {
		return takt;
	}

	/**
	 * @return the index of the first note in the Bar in the note-Array
	 */
	public int getStartIdx() {
		return startIdx;
	}
	
	
	
	/**
	 * Repräsentiert die Zählzeit eines Taktes.
	 */
	public static class Beat {
		int first, scnd;
		float beatsPerBar;

		public Beat(int n, int divisor) {
			this.first = n;
			this.scnd = divisor;
			this.beatsPerBar = first / scnd;
		}

		/**
		 * @return the number of beats in one bar as Ones (e.g. 4/4 returns 1, 3/4
		 *         return 0.75)
		 */
		public float getBeatsPerBar() {
			return beatsPerBar;
		}

		public int getFirst() {
			return first;
		}

		public int getScnd() {
			return scnd;
		}

		@Override
		public String toString() {
			return first + "/" + scnd;
		}
	}
}

