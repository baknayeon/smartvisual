package support;

import node.Method;
import node.SmartApp;
import preferenceNode.Subscribe;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by b_newyork on 2017-09-13.
 */
public class TreeCellRenderer extends DefaultTreeCellRenderer {

    SmartApp smartApp;
    ArrayList dynamicPageList;
    ArrayList subscribeList;
    HashMap action_methodFlowsssMap;
    String i = null;

    TreeCellRenderer(SmartApp smartApp, String i) {
        this.smartApp = smartApp;
        this.dynamicPageList = smartApp.getDynamicPageList();
        this.subscribeList = smartApp.getSubscribeList();
        this.action_methodFlowsssMap = smartApp.getActionsMap();
        this.i = i.toLowerCase();
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        JLabel label= new JLabel();
        DefaultMutableTreeNode Object = ((DefaultMutableTreeNode) value);
        DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) Object.getParent());
        String ObjectName = Object.getUserObject().toString();
        String imageUrl = "";

        if(i.equals("action")){
            imageUrl = setActionImageUrl(Object);

        }else if(i.equals("page")) {
            int num = whartKindofDev(ObjectName);
            Font f = label.getFont();
            switch (num) {
                case 1:  //event
                    label.setForeground(Color.decode("#3eb2af"));
                    break;
                case 2: //action
                    label.setForeground(Color.decode("#e5ba0d"));
                    break;
                case 3: //both
                    label.setForeground(Color.decode("#653077"));
                    break;
                default: //another
                    if (isPreference(Object))
                        label.setForeground(Color.black);
                    else
                        label.setForeground(Color.gray);
                    imageUrl = setPreImageUrl(Object);
                    break;
            }

        }

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

    private int whartKindofDev(String inputName){
        if(inputName.contains("input") && inputName.split(" ").length > 1) {
            String device = inputName.split(" ")[1];
            if (smartApp.isitActionDevice(device) && smartApp.isitEventDevice(device)) {
                return 3;
            } else if (smartApp.isitActionDevice(device)) {
                return 2;
            } else if (smartApp.isitEventDevice(device)) {
                return 1;
            }
        }
        return 0;
    }

    private String setPreImageUrl(DefaultMutableTreeNode Object) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
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
        else if (isItSubscribe(parent))
            imageUrl = iconFolder + "/point_end.png";
        else if (isItActions(Object))
            imageUrl = iconFolder + "/action.png";
        else if (isItSendMethod(Object))
            imageUrl = iconFolder + "/send.png";
        return imageUrl;
    }

    private String setActionImageUrl(DefaultMutableTreeNode Object) {
        String ObjectName = Object.getUserObject().toString();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)Object.getParent();
        String parentName = parent!=null?parent.getUserObject().toString():"";
        int childCount =  Object.getChildCount();

        String iconFolder = "./icon";
        String imageUrl = "";

        if ("Action".equals(parentName)) {
            imageUrl = iconFolder + "/input.png";
        }else if(childCount == 0 && parent != null){
            DefaultMutableTreeNode actionNode = ((DefaultMutableTreeNode)parent.getLastChild());
            String lastNode = actionNode.getUserObject().toString();

            DefaultMutableTreeNode handlerNode = ((DefaultMutableTreeNode)parent.getFirstChild());
            String firstNode = handlerNode.getUserObject().toString();

            if(lastNode.equals(ObjectName)) {
                imageUrl = iconFolder + "/action.png";
            }else if(firstNode.equals(ObjectName)){
                imageUrl = iconFolder + "/point_end.png";

            }else
                imageUrl = iconFolder + "/point.png";
        }

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
        if(Object != null) {
            String ObjectName = Object.getUserObject().toString().toLowerCase();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) Object.getParent();
            String parentName = parent != null ? parent.getUserObject().toString().toLowerCase() : "";

            if (subscribeList != null) {
                int size = subscribeList.size();
                for (int i = 0; i < size; i++) {
                    Subscribe method = (Subscribe) subscribeList.get(i);
                    String capability = method.capability.toLowerCase();
                    String handler = method.handler.toLowerCase();
                    String inputeviceName = "input " + method.input.toLowerCase();
                    if (ObjectName.equals(capability) && parentName.equals(inputeviceName))
                        return true;
                    else if(parentName.equals("location") || parentName.equals("app") )
                        if(ObjectName.equals(handler))
                            return true;
                }
            }
        }
        return false;
    }
    private boolean isItActions(DefaultMutableTreeNode Object){
        String ObjectName = Object.getUserObject().toString();
        if(ObjectName.contains("()"))
            return true;
        else
            return false;
    }
    private boolean isItSendMethod(DefaultMutableTreeNode Object){
        String ObjectName = Object.getUserObject().toString();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) Object.getParent();
        String parentName = parent != null ? parent.getUserObject().toString() : "";

        if(parentName.contains("input") && parentName.split(" ").length > 1) {
            String device = parentName.split(" ")[1];
            if(ObjectName.contains("("+device+")"))
                return true;
        }
        return false;
    }
}
