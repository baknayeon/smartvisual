package support;

import node.Method;
import node.Subscribe;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by b_newyork on 2017-09-13.
 */
public class TreeCellRenderer extends DefaultTreeCellRenderer {

    ArrayList dynamicPageList = new ArrayList();
    ArrayList subscribeList = new ArrayList();
    HashMap actionMap = new HashMap();

    TreeCellRenderer(ArrayList dynamicPageList, ArrayList subscribeList, HashMap actionMap) {
        this.dynamicPageList = dynamicPageList;
        this.subscribeList = subscribeList;
        this.actionMap = actionMap;
    }

    TreeCellRenderer(ArrayList wrongSubscribeList) {
        subscribeList = wrongSubscribeList;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        JLabel label= new JLabel();
        DefaultMutableTreeNode Object = ((DefaultMutableTreeNode) value);
        String ObjectName = Object.getUserObject().toString();

        if (isPreference(Object))
            label.setForeground(Color.black);
        else
            label.setForeground(Color.gray);

        String imageUrl = setImageUrl(Object);

        try {
            if (imageUrl.equals("null")) {
                label.setIcon(null);
            }else{
                ImageIcon originIcon = new ImageIcon(imageUrl);
                Image originImg = originIcon.getImage();
                Image changedImg = originImg.getScaledInstance(17, 17, Image.SCALE_SMOOTH);
                ImageIcon Icon = new ImageIcon(changedImg);

                label.setIcon(Icon);
            }
        }
        catch (Exception e) {
            // This line will not be reached.
        }

        label.setOpaque(true);
        label.setBackground(Color.white);
        label.setText(ObjectName);
        label.setFont(new Font(null, Font.PLAIN, 15));

        return label;
    }


    private String setImageUrl(DefaultMutableTreeNode Object) {
        String ObjectName = Object.getUserObject().toString().toLowerCase();
        String iconFolder = "./icon";
        String imageUrl = "null";

        if (ObjectName.equals("preferences")) {
            imageUrl = iconFolder + "/preferences.png";
        } else if (ObjectName.equals("location")) {
            imageUrl = iconFolder + "/location.png";
        } else if (ObjectName.equals("app")) {
            imageUrl = iconFolder + "/app.png";
        } else if (ObjectName.startsWith("page")) {
            imageUrl = iconFolder + "/page.png";
        } else if (ObjectName.startsWith("dynamic")){
            imageUrl = iconFolder + "/page_d.png";
        } else if (ObjectName.startsWith("section")) {
            imageUrl = iconFolder + "/section.png";
        } else if (ObjectName.startsWith("input") || ObjectName.equals("href") || ObjectName.equals("label")) {
            imageUrl = iconFolder + "/input.png";
        }else {
            /*DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
            if(parent != null) {
                String parentObject = parent.getUserObject().toString();
                if (parentObject.equals("input") || ObjectName.equals("href") || ObjectName.equals("label"))
                    imageUrl = iconFolder + "/infor.png";
            }*/
        }
        if(isItSubscribe(Object))
            imageUrl = iconFolder + "/handler.png";
        else if (isItActions(Object))
            imageUrl = iconFolder + "/actions.png";
        return imageUrl;
    }

    private boolean isPreference(DefaultMutableTreeNode Object) {

        String ObjectName = Object.getUserObject().toString().toLowerCase();

        if(ObjectName.equals("root") || ObjectName.equals("preferences") || ObjectName.equals("location") || ObjectName.equals("app")
                || ObjectName.startsWith("page") || ObjectName.startsWith("dynamic") || ObjectName.startsWith("section")
                || ObjectName.startsWith("input") || ObjectName.startsWith("href") || ObjectName.startsWith("label")) {
            return true;
        }

        if(isItDynamicPage(ObjectName))
            return true;

        if(isItSubscribe(Object))
            return true;

        if(isItActions(Object))
            return true;

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
        if(parent != null) {
            String parentObject = parent.getUserObject().toString().toLowerCase();
            if (parentObject.startsWith("input"))
                return true;
            else if (isItSubscribe(parent))
                return true;
            else if (isItActions(parent))
                return true;
        }


        return false ;

    }

    private boolean isItDynamicPage(String ObjectName){

        if(dynamicPageList != null) {
            int size = dynamicPageList.size();
            for (int i = 0; i < size; i++) {
                Method method = (Method) dynamicPageList.get(i);
                if (ObjectName.equals(method.getMethodName().toLowerCase()))
                    if (method.getDynamic())
                        return true;

            }
        }
        return false;
    }

    private boolean isItSubscribe(DefaultMutableTreeNode Object){
        String ObjectName = Object.getUserObject().toString().toLowerCase();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
        String parentName = parent!=null?parent.getUserObject().toString().toLowerCase():"";

        if(subscribeList != null) {
            int size = subscribeList.size();
            for (int i = 0; i < size; i++) {
                Subscribe method = (Subscribe) subscribeList.get(i);
                String Handler = method.handler.toLowerCase();
                String deviceName = "input "+method.input.toLowerCase();
                if (ObjectName.equals(Handler) && parentName.equals(deviceName))
                    return true;
            }
        }
        return false;
    }
    private boolean isItActions(DefaultMutableTreeNode Object){
        String ObjectName = Object.getUserObject().toString();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
        String[] parentName = parent!=null?parent.getUserObject().toString().toLowerCase().split(" "): new String[1];
        String device_name = parentName.length > 1 ? parentName[1]:null;

        if(actionMap.containsKey(device_name)){
            HashMap methods = (HashMap)actionMap.get(device_name);
            if(methods.containsKey(ObjectName)) {
                return true;
            }
        }
        return false;
    }
}
