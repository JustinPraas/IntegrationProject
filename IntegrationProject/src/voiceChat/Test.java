package voiceChat;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.sound.sampled.*;


public class Test {
	
	static boolean stopped = false;
	
	public static void main(String[] args) {
		System.out.println("Microphone supported: " + AudioSystem.isLineSupported(Port.Info.MICROPHONE));
		
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		TargetDataLine microphone;
		SourceDataLine speakers;
		
		try {
			microphone = AudioSystem.getTargetDataLine(format);
			
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[microphone.getBufferSize() / 5];
			microphone.start();
			
			// int bytesRead = 0;
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
			speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			speakers.open(format);
			speakers.start();
			while (!stopped) {
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
				// bytesRead += numBytesRead;
				// Write the microphone data to a stream for later use
				out.write(data, 0, numBytesRead);
				// Write microphone data to stream for immediate playback
				speakers.write(data, 0, numBytesRead);
			}
			speakers.drain();
			speakers.close();
			microphone.close();
			
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
