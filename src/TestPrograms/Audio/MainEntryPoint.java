package TestPrograms.Audio;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainEntryPoint {

	public static void main(String[] args) throws Exception {
		new freqProgram.Frequency_Modulation();
		//recordSomeTimes(1);
	}

	public static void recordSomeTimes(int times) throws Exception {
		MicToWAVFile.openFile("testing.wav");
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		for (int i = 0; i < times; ++i) {
			System.out.println("enter to record, enter to stop recording");
			b.readLine();
			System.out.println("Recording...");
			MicToWAVFile.startRecording();
			b.readLine();
			MicToWAVFile.stopRecording();
			System.out.println("Recording stopped.");
		}
		MicToWAVFile.closeFile();
		System.out.println("Closed File.");
	}
}
