package support;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

/**
 * Created by b_newyork on 2017-10-09.
 */
public class DialogHref extends JDialog {

    public DialogHref(JTree tree, String hrefName, ArrayList dynamicList, ArrayList subscribeList){
        TreeNode treeNode = ((DefaultMutableTreeNode)tree.getModel().getRoot()).getChildAt(2);

        int count = ((DefaultMutableTreeNode)treeNode).getChildCount();
        int i;
        for(i =0; i < count; i++){
            DefaultMutableTreeNode target = ((DefaultMutableTreeNode)(treeNode).getChildAt(i));
            String pageName = target.getUserObject().toString();

            if(pageName.equals("dynamicMethod ".concat(hrefName))){
                JTree jTree = new JTree(target);
                jTree.setRootVisible(false);
                jTree.setShowsRootHandles(true);
                jTree.setCellRenderer(new TreeCellRenderer(dynamicList, subscribeList));

                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(jTree);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                getContentPane().add(scrollPane);
            }
        }

        this.setSize(300,300);
        this.setModal(true);
        this.setVisible(true);
    }
}