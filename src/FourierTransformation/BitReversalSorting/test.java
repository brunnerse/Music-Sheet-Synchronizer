package FourierTransformation.BitReversalSorting;

public class test {
	private static int N = 32;
	
	public static void main (String args[]) {
		int array[] = new int[N];
		for (int i = 0; i < N; ++i) {
			array[i] = i;
		}
		printIntArray(array);
		bitReversalSorting(array);
		printIntArray(array);
	}	
		
	//CREDIT: https://www.nayuki.io/page/free-small-fft-in-multiple-languages
	public static void bitReversalSorting(int array[]) {
		int n = array.length;
		int levels = 31 - Integer.numberOfLeadingZeros(n);  // Equal to floor(log2(n))
		// Bit-reversed addressing permutation
		for (int i = 0; i < n; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				int temp = array[i];
				array[i] = array[j];
				array[j] = temp;
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
