import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
//import sun.misc.CRC16;

public class PortDataListenner implements SerialPortDataListener {


    SerialPort port;
    GUI gui;
    ComSetting setting;

    byte[] open_cmd = {(byte) 0xFF, (byte) 0xFF, 0x0B, 0x76, 0x01, 0x02, 0x01, 0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};
    byte[] close_cmd = {(byte) 0xFF, (byte) 0xFF, 0x0B, 0x76, 0x01, 0x02, 0x00, 0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};

    public PortDataListenner(SerialPort port, GUI gui, ComSetting setting) {
        this.port = port;
        this.gui = gui;
        this.setting = setting;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        } else {
            if (port.isOpen()) {
                while (port.bytesAvailable() > 0) {
                    byte[] receiveBuff = new byte[port.bytesAvailable()];
                    int num = port.readBytes(receiveBuff, receiveBuff.length);
                    System.out.println("类型:" + receiveBuff[5] + "字节数：" + num);
                    //处理数据，更新GUI


                    if (receiveBuff[5] == 0x10 && num == 44) {
                        byte[] bb = CrC.CRC16(receiveBuff, 2, 38);
                        System.out.println("crc:" + bb[0] + "," + bb[1]);
                        System.out.println("buf:" + receiveBuff[40] + "," + receiveBuff[41]);
                        if (bb[0] != receiveBuff[40] || bb[1] != receiveBuff[41]) {
                            System.out.println("Crc校验不合法");
                            return;
                        }


                        //十个抽屉
                        byte low = receiveBuff[37];//03
                        System.out.println("low:" + receiveBuff[37]);
                        byte high = receiveBuff[36];//de
                        System.out.println("high:" + receiveBuff[36]);
                        boolean[] isOpen = new boolean[10];
                        isOpen[0] = (high & 0x01) == 0x01;
                        isOpen[1] = (high & 0x02) == 0x02;
                        isOpen[2] = (high & 0x04) == 0x04;
                        isOpen[3] = (high & 0x08) == 0x08;

                        isOpen[4] = (high & 0x10) == 0x10;
                        isOpen[5] = (high & 0x20) == 0x20;
                        isOpen[6] = (high & 0x40) == 0x40;
                        isOpen[7] = (high & 0x80) == 0x80;

                        isOpen[8] = (low & 0x01) == 0x01;
                        isOpen[9] = (low & 0x02) == 0x02;

                        boolean isMin = false;//判断是否为负数
                        boolean isf = false;//判断是否为小数
                        byte temp;//整数部分
                        if ((receiveBuff[33] & 0x80) == 0x80) {
                            isMin = true;
                            byte t = (byte) (receiveBuff[33] & 0x7F);
                            temp = (byte) (t >> 1);

                        } else {
                            byte t = (byte) (receiveBuff[33] & 0x7F);
                            temp = (byte) (t >> 1);

                        }

                        //压缩机循环控制
                        if ((receiveBuff[33] & 0x01) == 0x01)
                            isf = true;
                        int current;
                        if (isMin) {
                            current = temp;
                            current *= -1;
                        } else {
                            current = temp;
                        }
                        byte offset = receiveBuff[18];

                        byte bt2;
                        if ((receiveBuff[17] & 0x80) == 0x80) {
                            bt2 = (byte) (receiveBuff[17] >> 1);
                            bt2 *= -1;
                        } else {
                            bt2 = (byte) (receiveBuff[17] >> 1);
                        }
                        System.out.println("当前温度" + current + "  " + "偏差" + offset + "  " + "设定的温度" + bt2);
                        if ((current > bt2 + offset) && receiveBuff[31] == 0) {
                            open_cmd[7] = 96;
                            open_cmd[8] = -56;
                            setting.sendData(1, open_cmd);
                        }
                        if ((current < bt2 - offset) && receiveBuff[31] == 2) {
                            close_cmd[7] = 65;
                            close_cmd[8] = -40;
                            setting.sendData(1, close_cmd);
                        }

                        gui.changeBoxLabelsStatus(isOpen);
                        gui.changeCompressorStatus(receiveBuff[31]);
                        gui.changeTemperatureLable(isMin, isf, temp);


                    }

                    if (receiveBuff.length == 14) {
                        System.out.println("帧号为" + receiveBuff[5] + ":Ok");

                    }

                }
            }


        }

    }
}
