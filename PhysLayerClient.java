import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class PhysLayerClient{

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("codebank.xyz",38002);

		try {
			System.out.println("Connect to Server");
			// Read from input
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			// Write to stream 
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(out);

			//Establishing a baseline from preamble based on first 64 signals 
			double baseline = preamble(is);

			//Creating a HashMap that uses 4B5B consversions 
			HashMap<String, String> bitMap = new HashMap<>();
			tableConversion(bitMap);

			String[] halfBytes = new String[64];
			//False is Low and True is High
			//LastSeen is the last Bit higher or lower than baseline
			boolean lastSeen = false;
			String fivebits = "" ;


			for (int i = 0; i < halfBytes.length; i++) {
			 	fivebits = "";
			 	for (int j = 0;j< 5 ;j++ ) {
			 		//Current bit is the bit that is currently being read
			 		boolean current = is.read() > baseline;
			 		if (lastSeen == current){
			 			fivebits += "0";
			 		}else{
			 			fivebits += "1";
			 		}
			 		lastSeen = current;
			 	}
			 	halfBytes[i] = (String)bitMap.get(fivebits);
			 }

			//Byte array 
			byte[] byteArray = new byte[32];

			byteArray = printBytes(halfBytes);

			 out.write(byteArray);

			 if(is.read()==1)
				System.out.println("Response good.");
			else 
				System.out.println("Response bad.");

			System.out.println("Disconnected from server.");
		}catch(Exception e){
			System.out.println("Client Closed");
		}

	}

	public static byte[] printBytes(String[] halfBytes) {
		byte[] b = new byte[32];
		System.out.print("Received 32 bytes: ");
		
		for(int i = 0; i < 32; i++){
			String firstHalf = halfBytes[2*i];
			String secondHalf = halfBytes[2*i+1];
			System.out.printf("%X", Integer.parseInt(firstHalf, 2));
			System.out.printf("%X", Integer.parseInt(secondHalf, 2));
			String wholeByte = firstHalf + secondHalf;
			b[i] = (byte)Integer.parseInt(wholeByte, 2);
		}
		System.out.println();
		return b;
	}

	public static void tableConversion(Map table){
		table.put("11110","0000");
		table.put("01001","0001");
		table.put("10100","0010");
		table.put("10101","0011");
		table.put("01010","0100");
		table.put("01011","0101");
		table.put("01110","0110");
		table.put("01111","0111");
		table.put("10010","1000");
		table.put("10011","1001");
		table.put("10110","1010");
		table.put("10111","1011");
		table.put("11010","1100");
		table.put("11011","1101");
		table.put("11100","1110");
		table.put("11101","1111");
	}

	public static double preamble (InputStream is) throws Exception{
		try {
			double baseline = 0;
			int pulse = 0;
			for (int i = 0; i < 64; i++){
				pulse = is.read();
				baseline+= pulse;
			}
			baseline /= 64.00;

			System.out.print("Baseline established from preamble: " + baseline + "\n");
			return baseline;
	    } catch (Exception e){}
	    	return 0;
	}
}