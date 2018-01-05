package support;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

/**
 * Created by b_newyork on 2017-10-09.
 */
public class DialogHref extends JDialog {

    public DialogHref(DefaultMutableTreeNode target, ArrayList dynamicList, ArrayList subscribeList){
        String pageName = target.getUserObject().toString();

        JTree jTree = new JTree(target);
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        jTree.setCellRenderer(new TreeCellRenderer(dynamicList, subscribeList));

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