package support;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by b_newyork on 2017-09-26.
 */
public class SettingBoxList {

    ArrayList<JCheckBox> jCheckBoxArrayList = new ArrayList();

    public SettingBoxList() {
        jCheckBoxArrayList.add(new JCheckBox("show DynamicPage",true));
        jCheckBoxArrayList.add(new JCheckBox("show method",true));
        jCheckBoxArrayList.add(new JCheckBox("show If condition",true));
        jCheckBoxArrayList.add(new JCheckBox("show While condition",true));
        jCheckBoxArrayList.add(new JCheckBox("show input option value",true));
    }

    public int size(){
        return jCheckBoxArrayList.size();
    }

    public String getName(int i){
        return jCheckBoxArrayList.get(i).getText();
    }

    public boolean getSelected(int i){
        return jCheckBoxArrayList.get(i).isSelected();
    }

    public JCheckBox get(int i){
        return jCheckBoxArrayList.get(i);
    }

    public void set(int i,JCheckBox newBox){
        jCheckBoxArrayList.set(i, newBox);
    }

    public boolean showDynamic(){
        return jCheckBoxArrayList.get(0).isSelected();
    }
    public boolean showMethod(){
        return jCheckBoxArrayList.get(1).isSelected();
    }
    public boolean showIf(){
        return jCheckBoxArrayList.get(2).isSelected();
    }
    public boolean showWhile(){
        return jCheckBoxArrayList.get(3).isSelected();
    }

}
