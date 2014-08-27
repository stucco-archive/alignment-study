package alignmentStudy;

import javax.swing.WindowConstants;
import javax.swing.JFrame;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ROCChart extends JFrame {
		
	private XYSeriesCollection dataset;
	private String applicationTitle;
	private double area;
	
/*	class Point implements Comparator<Point>	{
	
		double x;
		double y;
		Point() {};
		
		Point(double x, double y)	{
			this.x = x;
			this.y = y;	
		}
		public int compare(Point one, Point two)	{
			return Double.compare(one.x, two.x);
		}
	}
*/
  	public ROCChart(String applicationTitle) {
        	super(applicationTitle);

		dataset = new XYSeriesCollection();	
		this.applicationTitle = applicationTitle;

	//	series = new ArrayList<XYSeries>();
	}
						
	void addNewChart(String title, ArrayList<Point> newData)	{
		
		calculateArea(newData);				
		XYSeries series = new XYSeries(title + " area = " + area);
		for (Point p : newData)	{
			series.add(p.x, p.y);
		}
		dataset.addSeries(series);
	}

//	void addData(double x, double y)	{
		
	//	series.add(x, y);
//		data.add(new Point(x, y));
		
//	}

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
