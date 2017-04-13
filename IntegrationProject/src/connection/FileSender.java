package connection;

import java.io.File;
import java.io.IOException;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import application.Session;
import model.Message;
import model.Person;
import packet.FileMessage;
import packet.Packet;
import packet.Payload;
import userinterface.GUIHandler;

public class FileSender extends Thread {

	private Session session;
	private Connection connection;
	private TransportLayer transportLayer;
	private File image;
	private Person receiver;
	
	public FileSender(File img, Person receiver, Session session) {
		this.session = session;
		this.connection = new Connection(session);
		this.transportLayer = connection.getTransportLayer();
		this.image = img;
		this.receiver = receiver;
	}
	public void run(){
		sendFile(image, receiver);
	}
	
	public void sendFile(File img, Person receiver) {
		Path path = Paths.get(img.getPath());
		byte[] imgData;
		try {
			imgData = Files.readAllBytes(path);
			int chunkSize = 64000;
			int numOfChunks = (int)Math.ceil((double)imgData.length / chunkSize);
			byte[][] output = new byte[numOfChunks][];
			
			for(int i = 0; i < numOfChunks; i++) {
				int start = i * chunkSize;
				int length = Math.min(imgData.length - start, chunkSize);
				
				byte[] temp = new byte[length];
				System.arraycopy(imgData, start, temp, 0, length);
				output[i] = temp;
			}
			int nextMessageID = receiver.getNextMessageID();
			int nextFileID = receiver.getNextFileID();
			int counter = output.length - 1;
			ArrayList<byte[]> file = new ArrayList<>();
			for (byte[] data : output) {
				FileMessage payload = new FileMessage(nextMessageID, data.length, nextFileID, counter, data);
				Packet packet = new Packet(session.getID(), receiver.getID(), session.getNextSeq(), Payload.FILE_MESSAGE, payload);
				session.getConnection().getSender().send(packet);
				
				synchronized (transportLayer.unacknowledgedPackets) {
					transportLayer.unacknowledgedPackets.add(packet);
					new RetransmissionThread(transportLayer, packet);
				}	
				counter--;
				file.add(data);
			}
			if (!session.getFileMessages().containsKey(receiver)) {
				Map<Integer, ArrayList<byte[]>> files = new HashMap<>();
				files.put(nextFileID, file);
				session.getFileMessages().put(0, files);
			} else {
				Map<Integer, ArrayList<byte[]>> currentFiles = session.getFileMessages().get(receiver);
				currentFiles.put(nextFileID, file);
				session.getFileMessages().put(0, currentFiles);
			}
			Message message = new Message(session.getID(), receiver.getID(), nextMessageID, FileMessage.FILEMESSAGE_INDICATOR + nextFileID, true);
			if (!session.getChatMessages().containsKey(receiver)) {
				ArrayList<Message> messages = new ArrayList<>();
				messages.add(message);
				session.getChatMessages().put(receiver, messages);
			} else {
				ArrayList<Message> currentMessages = session.getChatMessages().get(receiver);
				currentMessages.add(message);
				session.getChatMessages().put(receiver, currentMessages);
			}
		} catch (IOException e) {
			
		}
		GUIHandler.messagePutInMap(receiver);
	}
}
