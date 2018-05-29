package support;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by b_newyork on 2017-10-09.
 */
public class DialogDevi extends JDialog {

    String[]  device  = { "switch1", "motion", "light"};
    JPanel jPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    JPanel rightPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel inputInfo_Panel = new JPanel();

    int WIDTH = 600;

    public DialogDevi(){
        setTitle("Device");

        JList jList = new JList(device);
        JLabel jLable = new JLabel("virtual Deveice List",JLabel.CENTER);
        JPanel jbut_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));

        JButton insertButton = new JButton("add");
        JButton deleteButton = new JButton("delete");
        insertButton.setSize(60,10);
        deleteButton.setSize(60,10);
        jbut_Panel.add(insertButton);
        jbut_Panel.add(deleteButton);
        jbut_Panel.setBorder(new LineBorder(Color.black, 1));
        inputInfo_Panel.setBorder(new LineBorder(Color.black, 1));

        jbut_Panel.setPreferredSize(new Dimension(275,100));
        inputInfo_Panel.setPreferredSize(new Dimension(275, 450));

        rightPanel.setLayout(new FlowLayout());
        rightPanel.add(jbut_Panel);
        rightPanel.add(inputInfo_Panel);

        leftPanel.setLayout(new FlowLayout());
        scrollPane.setViewportView(jList);
        leftPanel.add(jLable);
        leftPanel.add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(250, 450));
        jLable.setPreferredSize(new Dimension(WIDTH/2, 40));
        jLable.setFont(new Font(jLable.getName(), Font.PLAIN, 15));

        jPanel.setLayout(new GridLayout(1, 2));
        jPanel.add(leftPanel);
        jPanel.add(rightPanel);

        getContentPane().add(jPanel);

        this.setSize(WIDTH,600);
        this.setModal(true);
        this.setVisible(true);
    }
}