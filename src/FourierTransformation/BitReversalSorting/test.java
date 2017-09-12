package FourierTransformation.BitReversalSorting;

public class test {
	private static int N = 64;
	
	public static void main (String args[]) {
		int array[] = new int[N];
		for (int i = 0; i < N; ++i) {
			array[i] = i;
		}
		printIntArray(array);
		bitReversalSorting(array);
		printIntArray(array);
	}	
		
	public static void bitReversalSorting(int array[]) {
		int swap, rev;
		double dM = Math.log(array.length) / Math.log(2);
		
		if (Math.abs(dM - (int)dM) > 0.0001) {
			System.err.println("ERROR: array doesn't have a length of l = 2 ^ x");
			return;
		}
		
		int M = (int) Math.round(dM);
		//Index 0 and array.length - 1 stay the same
		for(int i = 1; i < array.length - 1; ++i) {
			rev = 0;
			//loop through each index
			for (int  x = 0; x < M; ++x) {
				//create reversal of each index
				rev |= ((1 << x) & i) << M - 1 - x - x;	//take the bit of i at position x, then move it to the first position of the integer ( >> x), then move it to the correct position for reversal ( << M - 1 - x)	
			}
			//swap the indexes (if not already done)
			if (i < rev) {
				swap = array[i];
				array[i] = array[rev];
				array[rev] = swap;
			}
		}
	}
	
	public static void printIntArray(int array[]) {
		System.out.print("[");
		for (int i = 0; i < array.length; ++i) {
			if(i % 6 == 0)
				System.out.println("");
			System.out.printf("\t%3d - ", array[i]);
			String s = Integer.toBinaryString(array[i]);
			for(int x = s.length(); x < 6; ++x)
				System.out.print("0");
			System.out.print(s);
		
		}
		System.out.println("\n\t]\n");
	}
}
