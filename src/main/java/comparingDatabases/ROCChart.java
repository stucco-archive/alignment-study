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
			
	private XYSeries series;
	private List data;
	private String title;
	private double area;

	class Point implements Comparator<Point>	{
	
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

  	public ROCChart(String applicationTitle, String chartTitle) {
        	super(applicationTitle);

		//series = new XYSeries(chartTitle);
    		data = new ArrayList();
		title = chartTitle;
	}

	void addData(double x, double y)	{
		
	//	series.add(x, y);
		data.add(new Point(x, y));
		
	}

	void drawChart()	{
  						  
		calculateArea();				
		series = new XYSeries("Area = " + Double.toString(area));
		for (Object point : data)	{
			Point p = (Point) point;
			series.add(p.x, p.y);
		}
		final XYSeriesCollection collection = new XYSeriesCollection(series);
    		final JFreeChart chart = ChartFactory.createXYLineChart(title, "falsePositive", "truePositive", collection, PlotOrientation.VERTICAL, true, true, false);
    		final ChartPanel chartPanel = new ChartPanel(chart);
    		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    		setContentPane(chartPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  		pack();
 		setVisible(true);
	}	

	double calculateArea()	{
		
		area = 0.0;

		Collections.sort(data, new Point());
		for (int i = 0; i < data.size() - 1; i++)	{
			Point p1 = (Point) data.get(i);
			Point p2 = (Point) data.get(i + 1);
			area = area + ((p2.x - p1.x) * p2.y - ((p2.x - p1.x) * (p2.y - p1.y))/2.0); 
		}
		return area;
	}
} 
