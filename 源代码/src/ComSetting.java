import com.fazecast.jSerialComm.*;


public class ComSetting {

    GUI gui;

    public ComSetting(GUI gui) {
        this.gui = gui;

    }

    public SerialPort[] ComList = SerialPort.getCommPorts();

    public void showAllCom() {
        SerialPort[] ComList = SerialPort.getCommPorts();
        for (SerialPort it : ComList) {
            System.out.println(it);
        }
    }

    public boolean openCom(int num) {
        if (num < 0 || num >= ComList.length) {
            System.out.println("输入num错误");
            return false;
        }
        if (ComList[num] != null) {
            ComList[num].setComPortParameters(38400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            //设置波特率，数据位，停止位，校验方式

            if (ComList[num].openPort()) {
                //添加监听器监听端口是否有数据
                ComList[num].addDataListener(new PortDataListenner(ComList[num], gui, this));
                return true;
            } else {
                System.out.println("串口打开失败");
                return false;
            }
        } else {
            System.out.println("串口未初始化");
            return false;
        }


    }

    public boolean sendData(int num, byte[] data) {

        if (ComList[num].writeBytes(data, data.length) > 0) {
            return true;
        } else {
            System.out.println("发送失败");
            return false;
        }

    }

    public boolean closeCom(int num) {
        if (num < 0 || num >= ComList.length) {
            System.out.println("输入num错误");
            return false;
        }
        if (ComList[num] != null) {
            if (ComList[num].closePort()) {
                return true;
            } else {
                System.out.println("串口关闭失败");
                return false;
            }
        } else {
            System.out.println("串口未初始化");
            return false;
        }

    }

}
