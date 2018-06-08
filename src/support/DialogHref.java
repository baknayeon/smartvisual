package support;

import node.SmartApp;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by b_newyork on 2017-10-09.
 */
public class DialogHref extends JDialog {

    public DialogHref(DefaultMutableTreeNode target, SmartApp smartApp) {
        String pageName = target.getUserObject().toString();

        JTree jTree = new JTree(target);
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        jTree.setCellRenderer(new TreeCellRenderer(smartApp, "page"));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(jTree);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane);


        this.setTitle(pageName);
        this.setSize(500,500);
        this.setModal(true);
        this.setVisible(true);
    }
}