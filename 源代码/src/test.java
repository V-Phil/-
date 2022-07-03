import org.junit.Test;

import javax.swing.*;
import java.util.Scanner;

public class test {
    @Test
    public void test(){
//        byte [] open_cmd={(byte) 0xFF, (byte) 0xFF,0x0B,0x76,0x01,0x02,0x01,0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};
//        byte [] close_cmd={(byte) 0xFF, (byte) 0xFF,0x0B,0x76,0x01,0x02,0x00,0x60, (byte) 0xC8, (byte) 0xFF, (byte) 0xF7};
//        byte [] b2= CrC.CRC16(close_cmd,2,5);
//        byte [] b3= CrC.CRC16(open_cmd,2,5);
//    for(int i=0;i<2;i++)
//    {
//        System.out.println("b2:"+b2[0]+","+b2[1]);
//        System.out.println("b3:"+b3[0]+","+b3[1]);
//
//    }
        new GUI2().calculateSendData(new byte[20]);


        new Scanner(System.in).nextByte();

    }
}
