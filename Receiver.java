
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.lang.Integer;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.FileNotFoundException;

public class Receiver {
	public static void printError(String str) {
		System.err.println("Error: "+str);
		System.exit(1);
	} 
	static int getterminateValue(byte[] data) {
		int value = 0;
		
		value = (int)((data[0] << 24)&0xff000000)|
				 (int)((data[1] << 16)&0x00ff0000)|
				 (int)((data[2] << 8)&0x0000ff00)|
				 (int)(data[3]&0x000000ff);
				 System.out.println(value);
		return value;
	}
	
	public static void main(String [] args) {
		if (args.length != 2) {
			printError("Invalid number of parameter");
		}
			
		String fileName = args[0];
		String timeoutStr = args[1];
		
		int timeout = 0;
	    
	    try {
	    	timeout = Integer.parseInt(timeoutStr);
	    } catch (Exception e) {
	    	printError("Invalid parameter format");
	    }

	    int loopNum = 0;
	    int byteNum = 0;
	    int portNum = 0;
	    DatagramSocket receiverSocket = null;
	    try {
	    	receiverSocket = new DatagramSocket();
		    portNum = receiverSocket.getLocalPort();
	    	System.out.println(portNum);

			File outfile = new File("port");
	    	Writer ostream = new FileWriter(outfile);
			ostream.write(new Integer(portNum).toString()+"\n");
			ostream.close();
		} catch (Exception e) {
			System.err.println("Cannot open Socket");
			return;
		}
	    
		try {
			byte[] data = new byte[65536];
	    	DatagramPacket p = new DatagramPacket(data, 65536);
	    	File outfile = new File(fileName);
	    	FileOutputStream ostream = new FileOutputStream(outfile);
			int totalMsg = 0;
			int msgtoget = 0;
	    	do {
			    receiverSocket.receive(p);
				if (loopNum == 0)
	    			receiverSocket.setSoTimeout(timeout);
			    data = p.getData();
				if (totalMsg == 0)
			    	totalMsg = getterminateValue(data);
				int msglen = p.getLength();
			    ostream.write(data, 4, msglen-4);
			    byteNum += msglen;
			    loopNum ++;
			    msgtoget = totalMsg-loopNum;
	    	} while (msgtoget > 0);
	    	ostream.close();
	    } catch (FileNotFoundException e) {
	    	System.err.println("Cannot open file "+ fileName);
			return;
		} catch (SocketTimeoutException e) {
		} catch (Exception e) {
	    	System.err.println("Cannot receive socket");
			return;
		}finally {
	    	receiverSocket.close();
	    }
	    System.out.print(loopNum);
	    System.out.print(" ");
	    System.out.println(byteNum);
	}

}
