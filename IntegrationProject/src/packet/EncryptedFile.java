package packet;

import java.io.File;

public class EncryptedFile implements Payload {
	
	private int messageID;
	private File file;
	
	public EncryptedFile(int messageID, File file) {
		this.messageID = messageID;
		this.file = file;
	}

	@Override
	public byte[] getPayloadData() {
		// TODO Auto-generated method stub
		return null;
	}

}
