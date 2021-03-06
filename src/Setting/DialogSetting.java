package Setting;

import javax.swing.*;
import java.awt.*;

/**
 * Created by b_newyork on 2017-09-26.
 */
public class DialogSetting extends JDialog {

    SettingBoxList checkBoxArrayList = new SettingBoxList();

    public SettingBoxList getJDialog(){
        return checkBoxArrayList;
    }

    public DialogSetting(SettingBoxList boxList){

        setting(boxList);

        this.setBounds(100, 100, 450, 300);
        this.setModal(true);
        this.setVisible(true);
    }

    public void setting(SettingBoxList boxList){

        setTitle("setting");
        setBounds(100, 100, 600, 600);
        setLayout(new GridLayout(boxList.size(),1));


        int i = 0;


        while (i < boxList.size()) {

            GridBagConstraints gbc_xy1 = new GridBagConstraints();
            gbc_xy1.gridx = 0;
            gbc_xy1.gridy = i;

            JCheckBox checkBox = boxList.get(i);
            checkBoxArrayList.set(i, checkBox);
            getContentPane().add(checkBox, gbc_xy1);
            i++;
        }

    }

}
