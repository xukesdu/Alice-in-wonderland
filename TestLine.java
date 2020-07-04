
package packetR1;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Line;
//import com.github.abel533.echarts.util.EnhancedOption;
import com.github.abel533.echarts.json.GsonOption;
import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.Option;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestLine {

	//@Test 
	public void test() {
		dataprocess process = new dataprocess();
		process.read();
		short[] data = process.storedata[0];
//		short[] data = {1,2,4,5,6,7,8};
		// 地址:http://echarts.baidu.com/doc/example/line5.html
		Option option = new Option();
		option.legend("幅值与频率变化关系");

		option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar),
				Tool.restore, Tool.saveAsImage);

		option.calculable(true);
		option.tooltip().trigger(Trigger.axis).formatter("Amplitude : <br/>{b}GHz : {c}~V");

		ValueAxis valueAxis = new ValueAxis();
		valueAxis.axisLabel().formatter("{value} ~V");
		//valueAxis.axisLabel().formatter("function(value){return parseDouble(value);}");
		//valueAxis.axisLabel().formatter("(function(value){return value.toFixed(2);})()");
		option.yAxis(valueAxis);

		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.axisLabel().interval(149);
		categoryAxis.axisLabel().formatter("{value} GHz");
		//categoryAxis.axisLabel().formatter("function(value){return parseDouble(value);}");
		//categoryAxis.axisLabel().formatter("(function(value){return value.toFixed(2);})()");
		categoryAxis.boundaryGap(false);
		

		Line line = new Line();
		//动态数组循环
		for (int i = 0; i < data.length; i++) {
		double k = (35 + (double)i * 5 / 1490);
		String s = String.format("%.2f", k);
		//BigDecimal b = new BigDecimal(k);
		double da = data[i];
		categoryAxis.data(s);
		line.smooth(false).name("幅值与频率变化关系").data(String.format("%.2f", da / 22))
				.itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
		}
		option.xAxis(categoryAxis);	
		option.series(line);
		//String jsonOutput = JSON.toJSONString(option);
		//System.out.println(jsonOutput);
		TestLine testline = new TestLine();
		testline.exportToHtml(option);
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
    }

}

class dataprocess {
	short head = 0;
	int channel = 0;
	short[] data = new short[4096];
	byte[] time = new byte[8];
	int pow = 0;
	byte crc = 0;
	short end = 0;

	byte[] framelength = new byte[8210];
	byte[] prehead = new byte[2];
	byte[] predata = new byte[2];

	short[][] storedata;
	long len = 0;
	boolean flag = true;
	public void read() {
		
		try {
			FileInputStream fis = new FileInputStream("E:\\temp.dat");
			DataInputStream dis = new DataInputStream(fis);
			
			BufferedWriter writer = null;
//			File file = new File("E:\\20191111T130400.bin");
//			FileReader fileReader = new FileReader(file);
//			BufferedReader reader = new BufferedReader(fileReader);
			
			
			//获取文件行数
			File outFile = new File("E:\\temp.vg.txt");
			
			File file = new File("E:\\temp.dat");
			long flength = file.length()/8210;
			short[][] tmpdata = new short[(int)flength / 40][32780];
			short[][] aftmpdata = new short[(int)flength / 40][1490];
			int pluscount = 0;
			for (int j = 0;j < (flength); j++) {    //
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
				dis.read(time, 0, 8);
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
}
