package AudioFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

public class WAVReader {
	private ArrayList<Chunk> chunks;
	private String fileName;
	private AudioFormat format;
	private RandomAccessFile raf;
	private int fileSize;
	private long dataIdx;
	
	public WAVReader(String fileName) {
		this.fileName = fileName;
		chunks = new ArrayList<Chunk>();
	}
	
	public void open() throws IOException {
		open(fileName);
	}
	
	public void open(String fileName) throws IOException{
		try {
			raf = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e) {
			throw new IOException(e.getMessage());
		}
		if (readString(4).compareTo("RIFF") != 0)
			throw new IllegalArgumentException("File is not in RIFF-Format");
		fileSize = readInt();
		if (readString(4).compareTo("WAVE") != 0)
			throw new IllegalArgumentException("File does not contain a WAVE");
		
		//read all chunks until you get to the data chunk
		Chunk fmtChunk = null;
		while(true) { //TODO: handle nested Chunks
			Chunk c = new Chunk(readString(4), readInt(), raf.getFilePointer());
			chunks.add(c);
			if (c.getTag().compareTo("data") == 0) {
				break;
			} else if (c.getTag().compareTo("LIST") == 0) { //LIST chunk can be nested
				if (readString(4).compareTo("INFO") == 0) {
					while (raf.getFilePointer() < c.getIdx() + c.getLength()) {
						Chunk cn = new Chunk(readString(4), readInt(), raf.getFilePointer());
						cn.setContent(readString(cn.getLength()));
						chunks.add(cn);
						if (cn.getTag().compareTo("ICMT") == 0 ||
								cn.getTag().compareTo("INAM") == 0) //for some reason, len of ICMT and INAM is one longer than given
							raf.seek(cn.getIdx() + cn.getLength() + 1);
						else
							raf.seek(cn.getIdx() + cn.getLength());
					}
				}
			} else if (c.getTag().compareTo("fmt ") == 0){
				fmtChunk = c;
			} else {
				c.setContent(readString(c.getLength())); //Only read content if it's not a nested or data chunk
			}
			if (c.getIdx() + c.getLength() >= fileSize)
			break;
			raf.seek(c.getIdx() + c.getLength());
		}
		dataIdx = chunks.get(chunks.size() - 1).getIdx();

		raf.seek(fmtChunk.getIdx());
		short formatTag = readShort();
		switch (formatTag) {
		case 0x0001: //Data Format is PCM
			break;
		default:
			System.err.println("Data Format of WAV is not supported!");
			return;
		}
		int channels = readShort();
		int sampleRate = readInt();
		readInt(); //FrameRate; (sampleRate * frameSize [bytes\second] : Value not needed
		readShort(); //frameSize; (bitsPerSample * channels (rounded so it fills n bytes): Value not needed
		int bitsPerSample = readShort();
		this.format = new AudioFormat((float)sampleRate, bitsPerSample, channels, true, false);
		raf.seek(dataIdx);
		
		System.out.println("Chunks in RIFF File:");
		for (Chunk c : chunks)
			System.out.println(c.getTag() + "\t" + c.getContent() + "\t" + c.getLength() +
					"\t" + c.getIdx());
		System.out.println("");
	}
	
	public void close() throws IOException {
		if (raf != null)	
			raf.close();
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		return raf.read(b, off, len);
	}
	
	//Reads String until either maxLen Characters were read or a Null-Termination was reached
	private String readString(int maxLen) throws IOException {
		StringBuilder sb = new StringBuilder(maxLen);
		char c;
		for (int i = 0; i < maxLen; ++i) {
			c = (char)raf.read();
			sb.append(c);
		}
		return sb.toString();
	}
	
	//Need own method instead of RandomAcessFile.write(int) because the Integer has to be in little endian format
	private int readInt() throws IOException {
		return raf.read() | raf.read() << 8 | raf.read() << 16 | raf.read() << 24;
	}
	
	private short readShort() throws IOException {
		return (short)(raf.read() | raf.read() << 8);
	}
	
	public String getArtist() {
		for (Chunk c : chunks)
			if (c.getTag().compareTo("IART") == 0)
				return c.getContent();
		return "";
	}
	
	public String getName() {
		for (Chunk c : chunks)
			if (c.getTag().compareTo("INAM") == 0)
				return c.getContent();
		return "";
	}
	
	public AudioFormat getFormat() {
		return format;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	private class Chunk {
		private long idx;
		private int len;
		private String tag;
		private String content;
		
		//Idx is the start-Index of the Data segment, len is the length of the Data segment
		public Chunk(String tag, int len, long idx) {
			this.tag = tag;
			this.idx = idx;
			this.len = len;
		}
		
		public void setContent(String content) {
			this.content = content;
		}
		
		public String getContent() {
			return content;
		}
		
		public long getIdx() {
			return idx;
		}
		
		public int getLength() {
			return len;
		}
		
		public String getTag() {
			return tag;
		}
	}
}
