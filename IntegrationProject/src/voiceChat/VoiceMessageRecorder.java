package voiceChat;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class VoiceMessageRecorder {
	
	static final int MAX_RECORD_TIME = 30000; // 30 seconds
	
	File wavFile = new File("AudioSample.wav");
	
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
	
	TargetDataLine line;
	
	AudioFormat getAudioFormat() {
		float sampleRate = 8000.0f;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		
		return format;
	}
	
	void start() {
		try {
			
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			
			if(!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported!");
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start(); // start capturing
			
			System.out.println("Start capturing!");
			AudioInputStream in = new AudioInputStream(line);
			System.out.println("Start recording!");
			AudioSystem.write(in, fileType, wavFile);
			
			
		} catch(LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			
		}
	}
	
	void finish() {
		line.stop();
		line.close();
		System.out.println("Finished!");
	}
	
//	public static void main(String[] args) {
//		final VoiceMessageRecorder recorder = new VoiceMessageRecorder();
//		
//		Thread stopper = new Thread(new Runnable() {
//			public void run() {
//				try {
//					Thread.sleep(MAX_RECORD_TIME);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				recorder.finish();
//			}
//			
//		});
//		
//		stopper.start();
//		
//		recorder.start();
//	}
}
