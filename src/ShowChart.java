import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class ShowChart extends ApplicationFrame {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -7080082617678515203L;
	/**
	 * list of date extracted.
	 */
	private static ArrayList<String> date = new ArrayList<String>();
	/**
	 * list of time extracted.
	 */
	public static ArrayList<String> time = new ArrayList<String>();
	/**
	 * list of temperature extracted.
	 */
	public static ArrayList<String> temp = new ArrayList<String>();
	/**
	 * list of humidity extracted.
	 */
	public static ArrayList<String> hum = new ArrayList<String>();
	/**
	 * list of original file data.
	 */
	private static ArrayList<String> originalList = new ArrayList<String>();
	/**
	 * button panel.
	 */
	private JPanel btnPanel;
	/**
	 * main content pane.
	 */
	private static JPanel contentPane = new JPanel();

	/**
	 * Constructor of ShowChart.
	 * 
	 * @param applicationTitle
	 *            title of application
	 * @param chartTitle
	 *            title of chart
	 */
	public ShowChart(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// read the file in RPI, extract data to originalList.
		readDataFile();
		// readTestingDataFile();

		// Extract data to temp, hum, date and time fields.
		analyzeData(originalList);

		// create the button panel, init the list of date.
		btnPanel = new BtnPanel(this, chartTitle);

		// load today's data
		analyzeData(filterFullList(originalList.get(originalList.size() - 2).substring(0, 10)));

		// add the button panel in contentPane
		contentPane.add(btnPanel);

		// setup the chart panel
		setupChartPanel(chartTitle);

		// set content pane.
		setContentPane(contentPane);
		
		// show avg
		((BtnPanel) btnPanel).setAvg();
	}

	/**
	 * setup the chart panel.
	 * 
	 * @param chartTitle
	 *            title of the chart
	 */
	public void setupChartPanel(String chartTitle) {
		System.out.println("Generating chart");
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, "Time", "Temperature / Humitity",
				createDataset(), PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setMaximumDrawWidth(3000);
		chartPanel.setPreferredSize(new java.awt.Dimension(1720, 768));

		CategoryPlot catPlot = lineChart.getCategoryPlot();

		// xAxis, turn 90 degree
		CategoryAxis xdAxis = catPlot.getDomainAxis();
		xdAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);

		// remove the chart when selected another date
		// then add the new chart
		try {
			contentPane.remove(1);
		} catch (Exception ex) {
			System.out.println("Remove ChartPanel Failed");
		}
		contentPane.add(chartPanel);
		this.paintAll(getGraphics());
	}

	/**
	 * read data in the file in raspberry pi. Extract to originalList.
	 */
	public static void readDataFile() {
		// define SHH & SFTP
		String sftpHost = "192.168.1.105";
		int sftpPort = 22;
		String suftUser = "pi";
		String sftpPass = "password";
		String sftpWorkingPath = "/home/pi/Adafruit_Python_DHT/data/";
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		StringBuffer strFileContents = new StringBuffer();
		try {
			// connect RPI
			System.out.println("Loading file");
			JSch jsch = new JSch();
			session = jsch.getSession(suftUser, sftpHost, sftpPort);
			session.setPassword(sftpPass);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(sftpWorkingPath);

			// download file
			BufferedInputStream bis = new BufferedInputStream(channelSftp.get("data.txt"));
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = bis.read(contents)) != -1) {
				strFileContents.append(new String(contents, 0, bytesRead));
			}
			// System.out.println(strFileContents);

			bis.close();

			// separate the date and data
			String[] splitedStr = strFileContents.toString().split("\n");
			for (int i = 0; i < splitedStr.length; i++) {
				originalList.add(splitedStr[i]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * read data from the testing file. Extract to originalList.
	 */
	public static void readTestingDataFile() {
		String strFileContents = "";

		Scanner scanner = null;
		try {
			// System.out.println(new File(".").getAbsoluteFile());
			scanner = new Scanner(new File("src/dataTesting.txt"));
			strFileContents = scanner.useDelimiter("\\A").next();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}

		String[] splitedStr = strFileContents.toString().split("\n");
		// System.out.println(strFileContents);

		for (int i = 0; i < splitedStr.length; i++) {
			originalList.add(splitedStr[i]);
		}
	}

	/**
	 * Analyze and extract data from a ArrayList, to static fields : temp, hum,
	 * date, time.
	 */
	public static void analyzeData(ArrayList<String> fullList) {
		System.out.println("Analyzing data");

		for (int i = temp.size() - 1; i >= 0; i--) {
			temp.remove(i);
		}
		for (int i = hum.size() - 1; i >= 0; i--) {
			hum.remove(i);
		}
		for (int i = date.size() - 1; i >= 0; i--) {
			date.remove(i);
		}
		for (int i = time.size() - 1; i >= 0; i--) {
			time.remove(i);
		}
		System.out.println("fullList size = " + fullList.size());
		ArrayList<String> dataSubList = new ArrayList<String>();
		ArrayList<String> dateTimeSubList = new ArrayList<String>();
		for (int i = 0; i < fullList.size(); i++) {
			if (i % 2 == 0) {
				dateTimeSubList.add(fullList.get(i));
				// System.out.println(splitedStr[i]);
			} else {
				dataSubList.add(fullList.get(i));
				// System.out.println(splitedStr[i]);
			}
		}

		// separate temperature and humidity
		for (int i = 0; i < dataSubList.size(); i++) {
			String[] sp = dataSubList.get(i).split("    ");
			temp.add(sp[0]);
			hum.add(sp[1]);
			// System.out.println(dataSubList.get(i));
		}

		// generate date list
		for (String str : dateTimeSubList) {
			String subStr = str.substring(0, 10);
			if (!date.contains(subStr)) {
				date.add(subStr);
				// System.out.println("date : " + subStr);
			}

			subStr = str.substring(11);
			// if (!time.contains(subStr)) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(subStr.substring(0, 2));
			stringBuilder.append(" : ");
			stringBuilder.append(subStr.substring(2, 4));
			// stringBuilder.append("\n");
			// stringBuilder.append(subStr.substring(4, 6));
			//
			// time.add(stringBuilder.toString());
			// System.out.println("time : " + subStr);
			time.add(stringBuilder.toString());
			// }
		}
	}

	/**
	 * filter the full list by a selectedDate.
	 * 
	 * @param selectedDate
	 *            the selected date
	 * @return the filtered ArrayList
	 */
	public static ArrayList<String> filterFullList(String selectedDate) {
		ArrayList<String> filteredDataList = new ArrayList<String>();
		for (int i = 0; i < originalList.size(); i++) {
			String string = originalList.get(i);
			// System.out.println(originalList.get(i));
			if (string.contains(selectedDate)) {
				filteredDataList.add(string);
				filteredDataList.add(originalList.get(i + 1));
			}
		}
		// for (String str : filteredDataList) {
		// System.out.println(str);
		// }
		return filteredDataList;
	}

	/**
	 * create data set for chart.
	 * 
	 * @return data set
	 */
	public static DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < hum.size(); i++) {
			dataset.addValue(Float.parseFloat(temp.get(i)), "Temperature", time.get(i));
			dataset.addValue(Float.parseFloat(hum.get(i)), "Humidity", time.get(i));
		}

		return dataset;
	}

	public static ArrayList<String> getDate() {
		return date;
	}

	public static void setDate(ArrayList<String> date) {
		ShowChart.date = date;
	}

	public static ArrayList<String> getTemp() {
		return temp;
	}

	public static void setTemp(ArrayList<String> temp) {
		ShowChart.temp = temp;
	}

	public static ArrayList<String> getHum() {
		return hum;
	}

	public static void setHum(ArrayList<String> hum) {
		ShowChart.hum = hum;
	}

	public static ArrayList<String> getTime() {
		return time;
	}

	public static void setTime(ArrayList<String> time) {
		ShowChart.time = time;
	}

	/**
	 * Launch application.
	 * 
	 * @param args
	 *            for main
	 */
	public static void main(String[] args) {
		ShowChart chartHum = new ShowChart("Temperature && Humidity", "Temperature && Humidity vs Time");
		chartHum.pack();
		RefineryUtilities.centerFrameOnScreen(chartHum);
		chartHum.setVisible(true);
		

	  	
	}

}





