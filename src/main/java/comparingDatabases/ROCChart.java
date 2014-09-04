package alignmentStudy;

import java.util.*;

import javax.swing.WindowConstants;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ROCChart extends JFrame {
		
	private XYSeriesCollection dataset;
	private String applicationTitle;
	private double area;
	
  	public ROCChart(String applicationTitle) {
        	
		super(applicationTitle);

		dataset = new XYSeriesCollection();	
		this.applicationTitle = applicationTitle;
	}
						
	void addNewChart(String title, ArrayList<Point> newData)	{
		
		calculateArea(newData);				
		XYSeries series = new XYSeries(title + " area = " + area);
		for (Point p : newData)	{
			series.add(p.x, p.y);
		}
		dataset.addSeries(series);
	}

	void drawChart()	{
  						  
    		JFreeChart chart = ChartFactory.createXYLineChart(applicationTitle, "falsePositive", "truePositive", dataset, PlotOrientation.VERTICAL, true, true, false);
    		ChartPanel chartPanel = new ChartPanel(chart);
    		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    		setContentPane(chartPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  		pack();
 		setVisible(true);
	}	

	double calculateArea(ArrayList<Point> newData)	{
		
		area = 0.0;

		Collections.sort(newData, new Point());
		for (int i = 0; i < newData.size() - 1; i++)	{
			Point p1 = (Point) newData.get(i);
			Point p2 = (Point) newData.get(i + 1);
			area = area + ((p2.x - p1.x) * p2.y - ((p2.x - p1.x) * (p2.y - p1.y))/2.0); 
		}
		return area;
	}
} 
