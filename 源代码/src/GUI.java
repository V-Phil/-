import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame {

    JLabel title;

    JLabel[] labels;
    public JCheckBox[] checkBoxList;
    JComboBox<Integer> chooseNum;//选择柜子编号
    public JButton lock;
    public JButton unlock;
    JPanel boxPanel;

    JLabel compressor;//压缩机状态
    public JButton openCompressor;
    public JButton closeCompressor;
    JPanel comprePanel;

    public JButton setTemperature;//设置温度
    JLabel currentTemp;
    JLabel tp;
    JTextArea temp;
    JPanel tempPanel;

    public GUI() {
        init();
    }

    public void init() {
        labels = new JLabel[10];
        checkBoxList = new JCheckBox[10];

        for (int i = 0; i < 10; i++) {
            labels[i] = new JLabel("抽屉" + (i + 1) + ":");
            labels[i].setFont(new Font("楷体", Font.BOLD, 16));
            checkBoxList[i] = new JCheckBox();
        }
        chooseNum = new JComboBox<>();
        for (int i = 1; i <= 10; i++)
            chooseNum.addItem(i);
        lock = new JButton("关上抽屉");
        lock.setBackground(Color.LIGHT_GRAY);
        lock.setFont(new Font("楷体", Font.BOLD, 16));
        unlock = new JButton("打/关开抽屉");
        unlock.setBackground(Color.LIGHT_GRAY);
        unlock.setFont(new Font("楷体", Font.BOLD, 16));

        compressor = new JLabel("压缩机状态:");
        compressor.setFont(new Font("楷体", Font.BOLD, 16));
        openCompressor = new JButton("打开压缩机");
        openCompressor.setBackground(Color.LIGHT_GRAY);
        openCompressor.setFont(new Font("楷体", Font.BOLD, 16));
        closeCompressor = new JButton("关闭压缩机");
        closeCompressor.setBackground(Color.LIGHT_GRAY);
        closeCompressor.setFont(new Font("楷体", Font.BOLD, 16));
        comprePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 20, 20));

        JCheckBox circle = new JCheckBox();
        circle.setBackground(Color.black);
        circle.setSize(10,10);
        circle.setVisible(true);




        comprePanel.add(compressor);
        //comprePanel.add(openCompressor);
        //comprePanel.add(closeCompressor);
        setTemperature = new JButton("设置温度");
        setTemperature.setBackground(Color.LIGHT_GRAY);
        setTemperature.setFont(new Font("楷体", Font.BOLD, 16));
        currentTemp = new JLabel("");
        currentTemp.setFont(new Font("楷体", Font.BOLD, 16));
        temp = new JTextArea(2, 8);
        tempPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 20, 20));
        tp = new JLabel("当前温度:");
        tp.setFont(new Font("楷体", Font.BOLD, 16));
        tempPanel.add(setTemperature);
        tempPanel.add(temp);
        tempPanel.add(tp);
        tempPanel.add(currentTemp);
        boxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 40, 40));

        for (int i = 0; i < 10; i++) {

            boxPanel.add(labels[i]);
            boxPanel.add(checkBoxList[i]);
        }

        // boxPanel.add(chooseNum);
        lock.setEnabled(false);
        boxPanel.add(lock);
        boxPanel.add(unlock);
        //boxPanel.add(circle);


        title = new JLabel("控制仿真软件");
        title.setFont(new Font("楷体", Font.BOLD, 20));
        //title.setBorder(BorderFactory.createDashedBorder(Color.red));

        setLayout(new GridLayout(1, 3, 2, 0));
        setBounds(60, 60, 700, 800);

        add(boxPanel);
        add(comprePanel);
        add(title);
        add(tempPanel);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public void changeTemperatureLable(boolean ismin, boolean isf, byte temp) {
        if (!ismin) {
            if (isf) {
                currentTemp.setText(String.valueOf(temp) + ".5");
            } else {
                currentTemp.setText(String.valueOf(temp));
            }

        } else {
            if (isf) {
                currentTemp.setText("-" + String.valueOf(temp) + ".5");
            } else {
                currentTemp.setText("-" + String.valueOf(temp));
            }

        }

    }

    public void changeBoxLabelsStatus(boolean[] isOpen) {

        for (int i = 0; i < isOpen.length; i++) {
            if (isOpen[i]) {
                labels[i].setText("抽屉" + (i + 1) + ":开");

            } else {
                labels[i].setText("抽屉" + (i + 1) + ":关");
            }
        }

    }

    public void CalculateCMD(byte[] SendBuf) {
        int i;
        byte sum1 = 0;
        byte sum2 = 0;
        for (i = 0; i < 8; i++) {
            if (checkBoxList[i].isSelected()) {
                sum1 += (1 << i);
            }
        }
        for (int j = 0; i < 10; i++, j++) {
            if (checkBoxList[i].isSelected()) {
                sum2 += (1 << j);
            }

        }
        SendBuf[7] = sum2;
        SendBuf[6] = sum1;


        System.out.println(sum1 + "   " + sum2);
    }

    public void CalculateTemp(byte[] SendBuf) {
        String s = temp.getText();
        boolean isPos = false;//判断是否为正数
        boolean isf = false;//判断是否为小数
        if (!s.startsWith("-"))
            isPos = true;
        if (s.contains("."))
            isf = true;
        if (isPos && isf) {

            byte t = Byte.parseByte(s.replace(".5", ""));
            byte m = (byte) (t << 1);
            m += 0x01;//最低位置为1
            SendBuf[6] = m;
        }
        if (isPos && !isf) {
            byte t = Byte.parseByte(s);
            byte m = (byte) (t << 1);
            SendBuf[6] = m;
        }
        if (!isPos && !isf) {
            byte t = Byte.parseByte(s.replace("-", ""));
            byte m = (byte) (t << 1);
            m |= 0x80;//最高位置为1
            SendBuf[6] = m;
        }
        if (!isPos && isf) {
            String s1 = s.replace("-", "");
            String s2 = s1.replace(".5", "");
            byte t = Byte.parseByte(s2);
            byte m = (byte) (t << 1);
            m += 0x01;//最低位置为1
            m |= 0x80;//最高位置为1
            SendBuf[6] = m;
        }


    }

    public void changeCompressorStatus(byte status) {


        switch (status) {
            case 0:
                compressor.setText("压缩机状态:关");
                compressor.setForeground(Color.ORANGE);
                break;
            case 1:
                compressor.setText("压缩机状态:预启动");
                compressor.setForeground(Color.ORANGE);
                break;
            case 2:
                compressor.setText("压缩机状态:运行");
                compressor.setForeground(Color.ORANGE);
                break;
            case 3:
                compressor.setText("压缩机状态:故障");
                compressor.setForeground(Color.ORANGE);
                break;
            default:
                break;

        }

    }


}
