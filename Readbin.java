package packetR1;

//import java.awt.Dimension;
import java.io.*;
//import java.util.ArrayList;
import java.util.Arrays;

//import jdk.jfr.Unsigned;

public class Readbin {
	public static void main(String[] args) {
		new Readbin().read();
	}
	
//	public void go() {
//		try {
//		FileInputStream fileStream = new FileInputStream("E:\\20191111T114600.bin");
//		BufferedInputStream bis = new BufferedInputStream(fileStream);
//		byte[] head = new byte[2];
//		
//		bis.read(head, 0, 2);
//		System.out.println(bytesToInt(head));
//		}catch (IOException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
	public void read() {
		short head = 0;
		int channel = 0;
		short[] data = new short[4096];
		byte[] time = new byte[8];
		char[] hms = new char[6];
		byte[] prems = new byte[2];
		short ms;
		String strtime;
		String[] stimec;
		int pow = 0;
		byte crc = 0;
		short end = 0;
		
		byte[] framelength = new byte[8210];
		byte[] prehead = new byte[2];
		byte[] predata = new byte[2];
		
		short[][] storedata;
		long len = 0;
		boolean flag = true;
		try {
			FileInputStream fis = new FileInputStream("E:\\temp.dat");
			DataInputStream dis = new DataInputStream(fis);
			
			BufferedWriter writer = null;
//			File file = new File("E:\\20191111T130400.bin");
//			FileReader fileReader = new FileReader(file);
//			BufferedReader reader = new BufferedReader(fileReader);
			
			
			//获取文件行数
			File outFile = new File("E:\\temp.datavg.txt");
			
			File file = new File("E:\\temp.dat");
			long flength = file.length()/8210;
			short[][] tmpdata = new short[(int)flength / 40][32780];
			short[][] aftmpdata = new short[(int)flength / 40][1490];
			int pluscount = 0;
			for (int j = 0;j < (flength); j++) {
				// 读取一帧数据
				dis.read(prehead, 0, 2);
				head = bytesToShort(prehead);
				if (head != 15450) {
					System.out.println("error head");
					continue;
				}
				
				channel = dis.readUnsignedByte();
				if (channel != 17 & flag) {
					dis.skipBytes(8207);
					System.out.println("error channel");
					continue;
				}else {
					flag = false;
				}
				
				for (int i = 0; i < 8192; i = i + 2) {
					dis.read(predata, 0, 2);
					data[4095-i / 2] = bytesToShort(predata);
				}
				//dis.read(time, 0, 8);
				//detailed time format
				hms[1] = (char)dis.readByte();
				hms[0] = (char)dis.readByte();
				hms[3] = (char)dis.readByte();
				hms[2] = (char)dis.readByte();
				hms[5] = (char)dis.readByte();
				hms[4] = (char)dis.readByte();
				dis.read(prems, 0, 2);
				ms = bytesToShort(prems);
				strtime = charsToString(hms, ms);
				dis.skipBytes(7);
				
				//System.out.println(head);
				//System.out.println(channel);
				//for(short a:data) {
					//System.out.println(a);
				//}
				
				switch (channel) {
				case 33:
					System.arraycopy(data, 409, tmpdata[(int)len], 0, 3278);
					break;
				case 177:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278, 3278);
					break;
				case 49:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 2, 3278);
					break;
				case 193:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 3, 3278);
					break;
				case 65:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 4, 3278);
					break;
				case 209:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 5, 3278);
					break;
				case 81:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 6, 3278);
					break;
				case 225:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 7, 3278);
					break;
				case 17:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 8, 3278);
					break;
				case 161:
					System.arraycopy(data, 409, tmpdata[(int)len], 3278 * 9, 3278);
					break;
				default:
					break;
				}
				if ((tmpdata[(int) len][0] != 0 & tmpdata[(int) len][3278]!= 0 & tmpdata[(int) len][3278 * 2]!= 0
						& tmpdata[(int) len][3278 * 3]!= 0 & tmpdata[(int) len][3278 * 4]!= 0 & tmpdata[(int) len][3278 * 5]!= 0
						& tmpdata[(int) len][3278 * 6]!= 0 & tmpdata[(int) len][3278 * 7]!= 0 & tmpdata[(int) len][3278 * 8]!= 0
						& tmpdata[(int) len][3278 * 9]!= 0)) {
					
//						for (short b : tmpdata[(int)len]) {
//							System.out.print(b);
//						}
//						System.out.println();
					for (int i = 0; i < 1490; i++) {
						if(pluscount < 22) {
							aftmpdata[(int)len][i] += (float)tmpdata[(int)len][22*i + pluscount];
							pluscount++;
							i--;
						}else {
							pluscount = 0;
							continue;
						}
					}
					String fileString = Arrays.toString(aftmpdata[(int) len]);
					//File outFile = new File("E:\\20191111T130400.txt");
					writer = new BufferedWriter(new FileWriter(outFile));
					writer.write(fileString);
					writer.write("\n");
					
					len++;
					if (len >= (flength / 40)) {
						break;
					}
				}
				
//				for (short[] a : tmpdata) {
//					for (short b : a) {
//						System.out.println(b);
//					}
//				}
			}
			
			writer.close();
//         	storedata = tmpdata;
			storedata = aftmpdata;
			
		} catch (IOException ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}
	
	
	
	private static short bytesToShort(byte[] bs) {
		short a = 0;
		a += ((bs[1] & 0xff) << 8);
		a += ((bs[0] & 0xff));
		return a;
	}
	
	private static String charsToString(char[] time,short minis) {
		String str = String.valueOf(time[0]);
		for (int i = 1; i < time.length; i++) {
			str += String.valueOf(time[i]);
			if (i%2 == 1 && i!=5) {
				str += String.valueOf(':');
			}
		}
		str +=String.valueOf('.');
		str += String.valueOf(minis);
		
		return str;
	}
}
