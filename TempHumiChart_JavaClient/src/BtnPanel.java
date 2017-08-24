import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class BtnPanel extends JPanel {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -5260826452834423607L;
	
	//private JXDatePicker datePickerA = new JXDatePicker();
	//private JXDatePicker datePickerB = new JXDatePicker();
	//private JPanel pnDatePicker = new JPanel();
	//private JCheckBox chckbxPeriod = new JCheckBox("Period");
	
	private JPanel pnList = new JPanel();
	private JScrollPane spList = new JScrollPane();

	private JList<String> list;
	private ListModel<String> modelList;
	private static String selectedDate;
	private ShowChart owner;
	private String chartTitle;

	private JLabel jlbAvgTemp = new JLabel("Temp avg: ");
	private JLabel jlbAvgHum = new JLabel("Hum avg: ");
	private JLabel jlbItemSum = new JLabel("Item Sum: ");
	private JTextField jtfAvgTemp = new JTextField();
	private JTextField jtfAvgHum = new JTextField();
	private JTextField jtfItemSum = new JTextField();
	private JPanel jpnAvg = new JPanel();
	
	
	
	/**
	 * Create the panel.
	 */
	public BtnPanel(ShowChart owner, String chartTitle) {
		this.owner = owner;
		this.chartTitle = chartTitle;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 125, 0 };
		gridBagLayout.rowHeights = new int[] { 168, 500, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		GridBagConstraints gbcPnAvg = new GridBagConstraints();
		gbcPnAvg.insets = new Insets(0, 0, 5, 0);
		gbcPnAvg.gridx = 0;
		gbcPnAvg.gridy = 0;
		add(jpnAvg, gbcPnAvg);

		GridBagConstraints gbcPnList = new GridBagConstraints();
		gbcPnList.anchor = GridBagConstraints.NORTHWEST;
		gbcPnList.gridx = 0;
		gbcPnList.gridy = 1;
		add(pnList, gbcPnList);

		jpnAvg.setLayout(new GridLayout(3, 2, 0, 0));
		jpnAvg.add(jlbAvgTemp);
		jpnAvg.add(jtfAvgTemp);
		jpnAvg.add(jlbAvgHum);
		jpnAvg.add(jtfAvgHum);
		jpnAvg.add(jlbItemSum);
		jpnAvg.add(jtfItemSum);
		
		jtfAvgTemp.setEditable(false);
		jtfAvgHum.setEditable(false);
		jtfItemSum.setEditable(false);
		
		
		//pnDatePicker.setLayout(new GridLayout(3, 1, 0, 0));

		//pnDatePicker.add(chckbxPeriod);
		//pnDatePicker.add(datePickerA);
		//pnDatePicker.add(datePickerB);

		initList();
	}

	/**
	 * initiation of the date list.
	 */
	public void initList() {
		modelList = new DefaultListModel<String>();
		for (String str : ShowChart.getDate()) {
			((DefaultListModel<String>) modelList).addElement(str);
			System.out.println("Date found : " + str);
		}
		pnList.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		list = new JList<String>(modelList);
		spList.setPreferredSize(new Dimension(125, 500));
		spList.setViewportView(list);
		pnList.add(spList);

		// add list listener
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent ev) {
				// reset chart
				selectedDate = list.getSelectedValue();
				ShowChart.analyzeData(ShowChart.filterFullList(selectedDate));
				owner.setupChartPanel(chartTitle);
				
				setAvg();
			}
		});
	}
	
	/**
	 * calculate and show avg of temp and hum.
	 */
	public void setAvg() {
		double sumTemp = 0;
		double sumHum = 0;
		int sumTime = 0;
		for (String db : ShowChart.temp) {
			sumTime++;
			sumTemp += Double.parseDouble(db);
		}
		for (String db : ShowChart.hum) {
			sumHum += Double.parseDouble(db);
		}
		jtfAvgTemp.setText(String.valueOf(BigDecimal.valueOf(sumTemp / sumTime).setScale(2, BigDecimal.ROUND_HALF_UP)));
		jtfAvgHum.setText(String.valueOf(BigDecimal.valueOf(sumHum / sumTime).setScale(2, BigDecimal.ROUND_HALF_UP)));
		jtfItemSum.setText(String.valueOf(sumTime));
	}

	public JScrollPane getSpList() {
		return spList;
	}
	
}
