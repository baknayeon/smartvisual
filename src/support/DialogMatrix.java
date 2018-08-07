package support;

import node.SmartApp;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Created by b_newyork on 2017-10-09.
 */
public class DialogMatrix extends JDialog {

    JPanel appInfo_Panel;

    public DialogMatrix() {
        appInfo_Panel = new JPanel();
        appInfo_Panel.setLayout(new BoxLayout(appInfo_Panel, BoxLayout.PAGE_AXIS));
        appInfo_Panel.setBorder(new EmptyBorder(3 , 10 , 10 , 10));

        ImageIcon loading = new ImageIcon("icon/ajax-loader.gif");
        appInfo_Panel.add(new JLabel("loading... ", loading, JLabel.CENTER));

        getContentPane().add(appInfo_Panel);
        this.setTitle("SmartApps Matrix");
        this.setSize(500,300);
        this.setModal(true);
        this.setVisible(true);
    }


    public DialogMatrix(Result result) {

        appInfo_Panel = new JPanel();
        appInfo_Panel.setLayout(new BoxLayout(appInfo_Panel, BoxLayout.PAGE_AXIS));
        appInfo_Panel.setBorder(new EmptyBorder(15 , 15 , 15 , 15));

        JLabel textPane1 = new JLabel();
        textPane1.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 17));
        textPane1.setBorder(new EmptyBorder(0 , 0 , 5 , 0));
        JLabel textPane2 = new JLabel();
        textPane2.setFont(new Font("Yu Gothic UI Semibold", Font.PLAIN, 13));

        appInfo_Panel.add(textPane1);
        appInfo_Panel.add(textPane2);

        textPane1.setText( "-----------No. of SmartApp : "+result.getTotal()+"-----------");

        String s = "<html>";
        s = s +"Simple SmartApp : "+result.getSimpleSmartApp()+"";
        s = s +"<br/>Frequent Event SmartApp : "+result.getEvent_freq()+"";
        s = s +"<br/>Frequent Action SmartApp : "+result.getAction_freq()+"";
        s = s +"<br/>   Frequent Event &&  Frequent Action : "+result.getEvent_and_Action()+"";
        s = s +"<br/>  !Frequent Event && !Frequent Action : "+result.getnEvent_and_nAction() +"";
        s = s +"<br/>Event and Action device SmartApp : "+result.getDuplicate()+"";
        s = s +"<br/>Only Sending message SmartApp : "+result.getSendingMessage()+"";
        s = s +"</html>";

        textPane2.setText(s);

        getContentPane().add(appInfo_Panel);
        this.setTitle("SmartApps Matrix");
        this.setSize(400,250);
        this.setModal(true);
        this.setVisible(true);
    }


}