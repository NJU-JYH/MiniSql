package main;

public class Util {
    /**
     * int 转 byte[]   高字节在前（高字节序）
     * */
    public static byte[] int2Bytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * byte[] 转 int 高字节在前（高字节序）
     * */
    public static int bytes2Int(byte[] b){
        int res = 0;
        for(int i=0;i<b.length;i++){
            res += (b[i] & 0xff) << ((3-i)*8);
        }
        return res;
    }
}
