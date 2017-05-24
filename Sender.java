import java.io.File;
import java.io.FileInputStream;
import java.lang.Integer;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.FileNotFoundException;


public class Sender {
	//print error message and terminal the program
	public static void printError(String str) {
		System.err.println("Error: "+str);
		System.exit(1);
	}
	
	//insert the total number of messages it's going to send
	static void putterminateValue(byte[] data, int val) {
		assert(data.length >= 4);
		System.out.println(val);
		data[0] = (byte) (val >> 24);
		data[1] = (byte) (val >> 16);
		data[2] = (byte) (val >> 8);
		data[3] = (byte) (val);
	}
	
	public static void main(String [] args) {
		//if the number of parameters is wrong
		if (args.length != 4) {
			printError("Number of parameter");
		}
			
		String host = args[0];
		String port = args[1];
		String size = args[2];
		String fileName = args[3];
		
		int portNum = 0;
		int sizeNum = 0;
		int virtualFileSize = 0;
	    boolean virtualFile = true;
	    
		//parse port number and payload size to integers
	    try {
	    	portNum = Integer.parseInt(port);
	    	sizeNum = Integer.parseInt(size);
			if (sizeNum <= 0)
				throw new Exception();
	    } catch (Exception e) {
	    	printError("Invalid parameter format");
	    }
	     
		//check if using a virtual file
	    try {
	    	virtualFileSize = Integer.parseInt(fileName);
	    } catch (Exception e) {
	    	virtualFile = false;
	    }

	    DatagramSocket senderSocket = null;
		InetAddress remoteAddress = null;
		int loopNum = 0;
		int byteNum = 0;
	    int packetSize = 0;
		//open socket and resolve host address
	    try {
	    	senderSocket = new DatagramSocket();
		    remoteAddress = InetAddress.getByName(host);
		} catch (Exception e) {
	    	System.err.println("Cannot open Socket to "+ host);
			return;
		} 

		try {
			//define packet
			byte[] data = new byte[sizeNum+4];
			DatagramPacket p = new DatagramPacket(data, sizeNum+4, remoteAddress, portNum);

		    if (virtualFile) {
				putterminateValue(data, (int)Math.ceil((double)virtualFileSize/sizeNum));
		    	while (virtualFileSize > 0) {
		    		if (virtualFileSize <= sizeNum) {
						packetSize = virtualFileSize+4;
		    			p.setLength(packetSize);
		    			virtualFileSize = 0;
		    		} else {
			    		virtualFileSize -= sizeNum;
			    		packetSize = sizeNum+4; 
		    		}
		    		senderSocket.send(p);
		    		byteNum += packetSize;
		    		loopNum ++;
		    	}
		    } else {
				//read input file
		    	File inputfile = new File(fileName);
		    	FileInputStream istream = new FileInputStream(inputfile);
		    	int totalMsg = (int)Math.ceil((double)inputfile.length()/sizeNum);
				//put terminate value to the head of the message
			    putterminateValue(data, totalMsg);
				int byteRead = istream.read(data, 4, sizeNum);
		    	while (byteRead > 0) {
		    		packetSize = byteRead + 4;
		    		p.setLength(packetSize);
		    		senderSocket.send(p);
		    		byteNum += packetSize;
		    		loopNum ++;
		    		byteRead = istream.read(data, 4, sizeNum);
		    	}
		    	istream.close();
		    }
			//print out the total number of messages and bytes sent 
		    System.out.print(loopNum);
		    System.out.print(" ");
		    System.out.println(byteNum);
	    } catch (FileNotFoundException e) {
	    	System.err.println("Cannot open file "+ fileName);
		} catch (Exception e) {
	    	System.err.println("Cannot send socket");
		} finally {
	    	senderSocket.close();
	    }
	}

}
