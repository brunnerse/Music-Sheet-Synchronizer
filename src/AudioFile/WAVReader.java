package AudioFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;

import javax.sound.sampled.AudioFormat;

public class WAVReader {
	private Stack<Chunk> chunks;
	private String fileName;
	private AudioFormat format;
	private RandomAccessFile raf;
	private int fileSize;
	private long dataIdx;
	
	public WAVReader(String fileName) {
		this.fileName = fileName;
		chunks = new Stack<Chunk>();
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
		//read all chunks until you get to the data chunk
		while(true) { //TODO: handle nested Chunks
			Chunk c = new Chunk(readString(4), readInt(), raf.getFilePointer());
			chunks.add(c);
			if (c.getTag().compareTo("data") == 0)
				break;
			c.setContent(readString(c.getLength()));
			raf.seek(c.getIdx() + c.getLength());
		}
		dataIdx = chunks.peek().getIdx();
		
		//Get Format
		Chunk fmtChunk = null;
		for (Chunk chunk : chunks) {
			if (chunk.getTag().compareTo("fmt ") == 0) {
				fmtChunk = chunk;
				break;
			}
		}
		if (fmtChunk == null)
			throw new IllegalArgumentException("The file didn't contain a format description");
		//TODO: Read format
		
		raf.seek(dataIdx);
	}
	
	public void close() throws IOException {
		if (raf != null)	
			raf.close();
	}
	
	public void read(byte[] b, int off, int len) throws IOException {
		raf.read(b, off, len);
	}
	
	//Reads String until either maxLen Characters were read or a Null-Termination was reached
	private String readString(int maxLen) throws IOException {
		StringBuilder sb = new StringBuilder(maxLen);
		char c;
		for (int i = 0; i < maxLen; ++i) {
			c = (char)raf.read();
			if (c == 0)
				break;
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
		
		return "";
	}
	
	public String getName() {
		
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
