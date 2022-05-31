package main;

import java.io.Serializable;
import java.util.Arrays;

public class Row implements Serializable {
    final static int COLUMN_USERNAME_SIZE = 32;
    final static int COLUMN_EMAIL_SIZE = 255;
    final static int ID_SIZE = 4;
    final static int ID_OFFSET = 0;
    final static int USERNAME_SIZE = 32;
    final static int USERNAME_OFFSET = ID_OFFSET+ID_SIZE;
    final static int EMAIL_SIZE = 255;
    final static int EMAIL_OFFSET = USERNAME_OFFSET + EMAIL_SIZE;
    final static int ROW_SIZE = ID_SIZE + USERNAME_SIZE + EMAIL_SIZE;
    int id;
    String username;
    String email;

    @Override
    public String toString() {
        return "main.Row{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    void setBytes(byte[] bytes,int begin, int end){
        int id = 0;
        for(int i=begin + ID_OFFSET; i < begin + ID_OFFSET + ID_SIZE;i++){
            id += (bytes[i] & 0xff) << ((3-i)*8);
        }
        byte[] username_bytes = new byte[USERNAME_SIZE];
        for(int i = begin + USERNAME_OFFSET;i < begin + USERNAME_OFFSET + USERNAME_SIZE;i++){
            username_bytes[i-begin-USERNAME_OFFSET] = bytes[i];
        }
        String username = username_bytes.toString();

        byte[] email_bytes = new byte[EMAIL_SIZE];
        for(int i = begin + EMAIL_OFFSET;i<begin+EMAIL_OFFSET+EMAIL_SIZE;i++){
            email_bytes[i-begin-EMAIL_OFFSET] = bytes[i];
        }
        String email = email_bytes.toString();

        this.id = id;
        this.username = username;
        this.email = email;
    }

    byte[] getBytes(){
        byte[] bytes = new byte[ROW_SIZE];
        bytes[3] = (byte) (id & 0xff);
        bytes[2] = (byte) (id >> 8 & 0xff);
        bytes[1] = (byte) (id >> 16 & 0xff);
        bytes[0] = (byte) (id >> 24 & 0xff);
        int i = Row.USERNAME_OFFSET;
        if(username != null){
            for(Byte b:username.getBytes()){
                bytes[i++] = b;
            }
        }
        if(email != null){
            for(Byte b:email.getBytes()){
                bytes[i++] = b;
            }
        }
        return bytes;
    }
}
