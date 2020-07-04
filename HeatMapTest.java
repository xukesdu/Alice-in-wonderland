package packetR1;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Orient;
import com.github.abel533.echarts.code.Position;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.VisualMapType;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Heatmap;
import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.VisualMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class HeatMapTest {
	@Test
    public void test() {
		heatmapdataprocess process = new heatmapdataprocess();
		process.read();
        //地址：http://echarts.baidu.com/doc/example/heatmap.html
        Option option = new Option();
        option.tooltip().position(Position.top);
        option.animation(false);
        
        option.title("20200621T155500频谱图");
		option.toolbox().show(true).feature(Tool.saveAsImage,Tool.restore,Tool.dataZoom);

        CategoryAxis hoursC = new CategoryAxis();
        hoursC.name("频率 (GHz)");
        //hoursC.interval(149);
        hoursC.axisLabel().textStyle().fontSize(18);
        
        CategoryAxis daysC = new CategoryAxis();
        daysC.axisLabel().textStyle().fontSize(18);
        
        option.grid().height("50%").y("20%");
        String[] raincolor = {"#FFFFFF","#ff3f00","#fffa00","#4cff00","#00ff6a","#00ffff","#002aff","#55009a","#000000"}; //从小到大
        option.visualMapNew().min(0).max(100).calculable(true).orient(Orient.horizontal).left(X.center).bottom("5%").color(raincolor);
        
        Object[] datas = new Object[process.storedata.length * process.storedata[0].length];
		double k = 0;
		for (int i = 0; i < process.storedata.length; i++) {
			for (int j = 0; j < process.storedata[i].length; j++) {
				datas[i * process.storedata[i].length + j] = new Integer[] { i, j, (int) process.storedata[i][j] / process.getYcount() / process.getXcount() };
				if (i == 0) {
					k = (35 + (double) j * 5 / process.getYnum());
					String s = String.format("%.2f", k);
					hoursC.data(s);
				}
			}
			daysC.data(process.stimec[i]);
		}
		option.xAxis(daysC).yAxis(hoursC);
		
        Heatmap heatmap = new Heatmap("Punch Card");
        heatmap.data(datas);
        heatmap.label().normal().show(false);
        heatmap.itemStyle().emphasis().shadowBlur(10).shadowColor("rgba(0, 0, 0, 0.5)");

        option.series(heatmap);
        HeatMapTest heat = new HeatMapTest();
		heat.exportToHtml(option);
    }
	
	 public static String exportToHtml(Option option) {
	        String folderPath = "E:\\HTML";
	        return exportToHtml(option, folderPath);
	    }
	    
	    public static String exportToHtml(Option option, String folderPath) {
	        String fileName = "ECharts-" + System.currentTimeMillis() + ".html";
	        return exportToHtml(option, folderPath, fileName);
	    }
	    
	    public static String exportToHtml(Option option, String folderPath, String fileName) {
	        if (fileName == null || fileName.length() == 0) {
	            return exportToHtml(option, folderPath);
	        }
	        Writer writer = null;
	        List<String> lines = readLines(option);
	        //鍐欏叆鏂囦欢
	        File html = new File(getFolderPath(folderPath) + "/" + fileName);
	        try {
	            writer = new OutputStreamWriter(new FileOutputStream(html), "UTF-8");
	            for (String l : lines) {
	                writer.write(l + "\n");
	            }
	        } catch (Exception e) {
	        } finally {
	            if (writer != null) {
	                try {
	                    writer.close();
	                } catch (IOException e) {
	                    //ignore
	                }
	            }
	        }
	        //澶勭悊
	        try {
	            return html.getAbsolutePath();
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	    private static String getFolderPath(String folderPath) {
	        File folder = new File(folderPath);
	        if (folder.exists() && folder.isFile()) {
	            String tempPath = folder.getParent();
	            folder = new File(tempPath);
	        }
	        if (!folder.exists()) {
	            folder.mkdirs();
	        }
	        return folder.getPath();
	    }
	    
	    private static List<String> readLines(Option option) {
	    	String jsonOutput = JSON.toJSONString(option);
	    	File file = new File("E:\\eclipse\\workspace\\ReceiverProgram\\template");
	    	
	        InputStream is = null;
	        InputStreamReader iReader = null;
	        BufferedReader bufferedReader = null;
	        List<String> lines = new ArrayList<String>();
	        String line;
	        try {
	            is = new FileInputStream(file);
	            iReader = new InputStreamReader(is, "UTF-8");
	            bufferedReader = new BufferedReader(iReader);
	            while ((line = bufferedReader.readLine()) != null) {
	                if (line.contains("##option##")) {
	                    line = line.replace("##option##", jsonOutput);
	                }
	                lines.add(line);
	            }
	        } catch (Exception e) {
	        } finally {
	            if (is != null) {
	                try {
	                    is.close();
	                } catch (IOException e) {
	                    //ignore
	                }
	            }
	        }
	        return lines;
	    }
	    
	  //*****MAIN*****
	    public static void main(String[] args) {
	    	TestLine testline = new TestLine();
			testline.test();
			
			//commondivisor c = new commondivisor();
			//ArrayList<Integer> a = c.suitnum(30);
			//System.out.println(a);
	    }

	}

class heatmapdataprocess {
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
	
	int xAxis;
	int xAvgcount = 5;
	int yAxis = 32780;
	int yAvgcount = 55;
	
	String filename = "F:\\20200621\\Csgr50l01SPE35G20200621T155500.bin";
	public long getFilelength() {
		File file = new File(filename);
		long flength = file.length() / 8210;
		xAxis = (int)flength;
		return xAxis;
	}

	public void read() {

		try {
			FileInputStream fis = new FileInputStream(filename);
			DataInputStream dis = new DataInputStream(fis);

			BufferedWriter writer = null;
//				File file = new File("E:\\20191111T130400.bin");
//				FileReader fileReader = new FileReader(file);
//				BufferedReader reader = new BufferedReader(fileReader);

			// 获取文件行数
			File outFile = new File("E:\\20200621T155500.txt");

			// File file = new File("E:\\20191111T130400.bin");
			// long flength = file.length() / 8210;
			getFilelength();
			short[][] tmpdata = new short[(int) xAxis / 40][yAxis];
			short[][] aftmpdata = new short[(int) xAxis / 40][yAxis / yAvgcount];
			short[][] ultmpdata = new short[(int) xAxis / 40 / xAvgcount][yAxis / yAvgcount];
			String[] prestimec = new String[(int) xAxis / 40 / xAvgcount];
			int pluscount = 0;
			int xpluscount = 0;
			for (int j = 0; j < (xAxis); j++) { //
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
				} else {
					flag = false;
				}

				for (int i = 0; i < 8192; i = i + 2) {
					dis.read(predata, 0, 2);
					data[4095 - i / 2] = bytesToShort(predata);
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

				// System.out.println(head);
				// System.out.println(channel);
				// for(short a:data) {
				// System.out.println(a);
				// }

				switch (channel) {
				case 33:
					System.arraycopy(data, 409, tmpdata[(int) len], 0, 3278);
					break;
				case 177:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278, 3278);
					break;
				case 49:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 2, 3278);
					break;
				case 193:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 3, 3278);
					break;
				case 65:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 4, 3278);
					break;
				case 209:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 5, 3278);
					break;
				case 81:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 6, 3278);
					break;
				case 225:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 7, 3278);
					break;
				case 17:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 8, 3278);
					break;
				case 161:
					System.arraycopy(data, 409, tmpdata[(int) len], 3278 * 9, 3278);
					break;
				default:
					break;
				}
				if ((tmpdata[(int) len][0] != 0 & tmpdata[(int) len][3278] != 0 & tmpdata[(int) len][3278 * 2] != 0
						& tmpdata[(int) len][3278 * 3] != 0 & tmpdata[(int) len][3278 * 4] != 0
						& tmpdata[(int) len][3278 * 5] != 0 & tmpdata[(int) len][3278 * 6] != 0
						& tmpdata[(int) len][3278 * 7] != 0 & tmpdata[(int) len][3278 * 8] != 0
						& tmpdata[(int) len][3278 * 9] != 0)) {

//							for (short b : tmpdata[(int)len]) {
//								System.out.print(b);
//							}
//							System.out.println();
					for (int i = 0; i < yAxis / yAvgcount; i++) {
						if (pluscount < yAvgcount) {
							aftmpdata[(int) len][i] += (float) tmpdata[(int) len][yAvgcount * i + pluscount];
							pluscount++;
							i--;
						} else {
							pluscount = 0;
							continue;
						}
					}
					for (int i = 0; i < yAxis / yAvgcount; i++) {
						ultmpdata[(int) len / xAvgcount][i] += aftmpdata[(int) len][i];
					}
					prestimec[(int)len / xAvgcount] = strtime;
					
					String fileString = Arrays.toString(aftmpdata[(int) len]);
					// File outFile = new File("E:\\20191111T130400.txt");
					writer = new BufferedWriter(new FileWriter(outFile));
					writer.write(fileString);
					writer.write("\n");

					len++;
					if (len >= (xAxis / 40 - (xAxis / 40 % xAvgcount))) {
						break;
					}
				}

//					for (short[] a : tmpdata) {
//						for (short b : a) {
//							System.out.println(b);
//						}
//					}
			}

			writer.close();
//	         	storedata = tmpdata;
			storedata = ultmpdata;
			stimec = prestimec;

		} catch (IOException ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}

	public int getXnum() {
		getFilelength();
		return xAxis / 40 / xAvgcount;
	}

	public int getYnum() {
		return yAxis / yAvgcount;
	}

	public int getYcount() {
		return yAvgcount;
	}

	public int getXcount() {
		return xAvgcount;
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

//class commondivisor{
//	ArrayList<Integer> array = new ArrayList<Integer>();
//	
//	public ArrayList<Integer> suitnum(int testnum) {
//		for (int i = 1; i < Math.sqrt(testnum); i++) {
//			if (testnum % i == 0) {
//				array.add(i);
//				array.add(testnum / i);
//			}
//		}
//		Collections.sort(array);
//		return array;
//	}
//}
