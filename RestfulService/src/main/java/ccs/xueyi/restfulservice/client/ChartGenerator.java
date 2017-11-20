/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author ceres
 */
public class ChartGenerator extends JFrame{
    private XYSeriesCollection dataCollection;
    
    public ChartGenerator(){
        dataCollection = new XYSeriesCollection();
    }
    
    public void getChart(long[] dataSet, String chartTitle, String xTile, 
            String yTitle) throws IOException{
        XYSeries series = new XYSeries(chartTitle);
        for(int i = 0; i < dataSet.length; i++){
            series.add(i, dataSet[i]);
        }
        dataCollection.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "Ranking", 
                "Latancy", dataCollection);
        
        File file = new File(chartTitle);
        ChartUtilities.saveChartAsJPEG(file, chart, 500, 270);
    }
}
