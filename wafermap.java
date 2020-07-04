package packetR1;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.UIUtils;

public class wafermap extends ApplicationFrame{
	/**
     * Creates a new demo.
     * 
     * @param title  the frame title.
     */
    public wafermap(final String title) {
        super(title);
        final WaferMapDataset dataset = DemoDatasetFactory.createRandomWaferMapDataset(5);
        final JFreeChart chart = ChartFactory.createWaferMapChart(
            "Wafer Map Demo",         // title
            dataset,                  // wafermapdataset
            PlotOrientation.VERTICAL, // vertical = notchdown
            true,                     // legend           
            false,                    // tooltips
            false
        ); 
        
//        final Legend legend = chart.getLegend();
  //      legend.setAnchor(Legend.EAST);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        
        final TextTitle copyright = new TextTitle(
            "JFreeChart WaferMapPlot", new Font("SansSerif", Font.PLAIN, 9)
        );
        copyright.setPosition(RectangleEdge.BOTTOM);
        copyright.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        chart.addSubtitle(copyright);
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 400));
        setContentPane(chartPanel);
    }

    /**
     * Starting point for the demo application.
     * 
     * @param args  command line arguments (ignored).
     */
    public static void main(final String[] args) {
        final wafermap demo = new wafermap("Wafer Map Demo");
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
