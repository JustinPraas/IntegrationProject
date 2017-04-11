package packet;

import java.io.File;

public class EncryptedFile implements Payload {
	
	private int messageID;
	private File encryptedFile;
	
	public EncryptedFile(int messageID, File encryptedFile) {
		this.messageID = messageID;
		this.encryptedFile = encryptedFile;
	}

	@Override
	public byte[] getPayloadData() {
		// TODO Auto-generated method stub
		return null;
	}

}
