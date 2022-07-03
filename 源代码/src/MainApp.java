public class MainApp {

    public static void main(String[] argv) {
        byte[] unlock_cmd = {(byte) 0xFF, (byte) 0xFF, 0x0C, 0x77, 0x01, 0x03, (byte) 0x00, 0x00, 0x1E, 0x58, (byte) 0xFF, (byte) 0xF7};
        byte[] open_cmd = {(byte) 0xFF, (byte) 0xFF, 0x0B, 0x76, 0x01, 0x02, 0x01, 0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};
        byte[] close_cmd = {(byte) 0xFF, (byte) 0xFF, 0x0B, 0x76, 0x01, 0x02, 0x00, 0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};
        byte[] DataToSend = {(byte) 0xFF, (byte) 0xFF, 0x1C, 0x75, 0x7F, 0x05, 0x10, 0x01, 0x25, (byte) 0xF0, 0x02, 0x01, 0x0A, 0x02, 0x05, 0x07, 0x08, 0x08, 0x02, (byte) 0xFF, 0x03, (byte) 0xFF, 0x03, 0x20, 0x36, (byte) 0xE8, (byte) 0xFF, (byte) 0xF7};
        byte[] setTemp = {(byte) 0xFF, (byte) 0xFF, 0x0B, 0x78, 0x01, 0x04, 0x10, (byte) 0x8C, (byte) 0xC2, (byte) 0xFF, (byte) 0xF7};
        GUI gui = new GUI();
        ComSetting setting = new ComSetting(gui);
        setting.showAllCom();

        setting.openCom(1);//打开第一个串口
        gui.unlock.addActionListener(e -> {
            System.out.println("写数据");
            gui.CalculateCMD(unlock_cmd);//计算所有要打开的抽屉的代码
            byte[] b2 = CrC.CRC16(unlock_cmd, 2, 6);
            unlock_cmd[8] = b2[0];
            unlock_cmd[9] = b2[1];
            setting.sendData(1, unlock_cmd);//发送打开抽屉命令

        });

        gui.closeCompressor.addActionListener(e -> {
            byte[] b2 = CrC.CRC16(close_cmd, 2, 5);
            close_cmd[7] = b2[0];
            close_cmd[8] = b2[1];
            setting.sendData(1, close_cmd);//发送关闭压缩机
        });
        gui.openCompressor.addActionListener(e -> {
            byte[] b2 = CrC.CRC16(open_cmd, 2, 5);
            open_cmd[7] = b2[0];
            open_cmd[8] = b2[1];
            setting.sendData(1, open_cmd);//发送启动压缩机
        });
        gui.setTemperature.addActionListener(e -> {
            byte[] b2 = CrC.CRC16(setTemp, 2, 5);
            setTemp[7] = b2[0];
            setTemp[8] = b2[1];
            gui.CalculateTemp(setTemp);
            setting.sendData(1, setTemp);//设置温度

        });
        GUI2 ui2 = new GUI2();
        ui2.button.addActionListener(e -> {
            ui2.calculateSendData(DataToSend);
            byte[] b2 = CrC.CRC16(DataToSend, 2, 22);
            DataToSend[24] = b2[0];
            DataToSend[25] = b2[1];
            setting.sendData(1, DataToSend);//设置参数

        });


    }


}
