import javax.swing.*;
import java.awt.*;

public class GUI2 extends JFrame {

    JTextField device_ID, device_addr, period, timeout, temp, tempoffset;
    public JButton button;

    public GUI2() {

        init();
    }

    public void init() {
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());

        device_ID = new JTextField("1111111111", 22);
        device_ID.setHorizontalAlignment(JTextField.CENTER);
        device_ID.setFont(new Font("楷体", Font.BOLD, 16));
        device_addr = new JTextField("10", 22);
        device_addr.setHorizontalAlignment(JTextField.CENTER);
        device_addr.setFont(new Font("楷体", Font.BOLD, 16));
        period = new JTextField("1", 22);
        period.setHorizontalAlignment(JTextField.CENTER);
        period.setFont(new Font("楷体", Font.BOLD, 16));
        timeout = new JTextField("1", 22);
        timeout.setHorizontalAlignment(JTextField.CENTER);
        timeout.setFont(new Font("楷体", Font.BOLD, 16));
        temp = new JTextField("20", 22);
        temp.setHorizontalAlignment(JTextField.CENTER);
        temp.setFont(new Font("楷体", Font.BOLD, 16));
        tempoffset = new JTextField("5", 22);
        tempoffset.setHorizontalAlignment(JTextField.CENTER);
        tempoffset.setFont(new Font("楷体", Font.BOLD, 16));
        button = new JButton("写入");
        button.setBackground(Color.LIGHT_GRAY);
        jp.add(new JLabel("设备编码"));
        jp.add(device_ID);
        jp.add(new JLabel("设备地址"));
        jp.add(device_addr);
        jp.add(new JLabel("状态间隔"));
        jp.add(period);
        jp.add(new JLabel("启动延时"));
        jp.add(timeout);
        jp.add(new JLabel("设定温度"));

        jp.add(temp);
        jp.add(new JLabel("温度偏差"));
        jp.add(tempoffset);
        jp.add(button);
        add(jp);
        setSize(260, 400);
        setLocation(500, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


    }

    public void calculateSendData(byte[] DataToSend) {
        String s1 = device_ID.getText();
        for (int i = 0; i < 5; i++) {
            DataToSend[6 + i] = Byte.parseByte(s1.substring(2 * i, (i + 1) * 2));
            System.out.println(DataToSend[6 + i]);
        }
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
            DataToSend[17] = m;
        }
        if (isPos && !isf) {
            byte t = Byte.parseByte(s);
            byte m = (byte) (t << 1);
            DataToSend[17] = m;
            System.out.println(m);
        }
        if (!isPos && !isf) {
            byte t = Byte.parseByte(s.replace("-", ""));
            byte m = (byte) (t << 1);
            m |= 0x80;//最高位置为1
            DataToSend[17] = m;

        }
        if (!isPos && isf) {
            String ss = s.replace("-", "");
            String s2 = ss.replace(".5", "");
            byte t = Byte.parseByte(s2);
            byte m = (byte) (t << 1);
            m += 0x01;//最低位置为1
            m |= 0x80;//最高位置为1
            DataToSend[17] = m;
        }

        DataToSend[11] = Byte.parseByte(device_addr.getText());
        DataToSend[13] = Byte.parseByte(period.getText());
        DataToSend[14] = Byte.parseByte(timeout.getText());

        DataToSend[18] = Byte.parseByte(tempoffset.getText());


    }


}
