package main;

import java.nio.MappedByteBuffer;



public class Row implements Cloneable {
    public final static int COLUMN_USERNAME_SIZE = 32;
    public final static int COLUMN_EMAIL_SIZE = 255;
    public final static int ID_SIZE = 4;
    public final static int ID_OFFSET = 0;
    public final static int USERNAME_SIZE = 32;
    public final static int USERNAME_OFFSET = ID_OFFSET + ID_SIZE;
    public final static int EMAIL_SIZE = 255;
    public final static int EMAIL_OFFSET = USERNAME_OFFSET + USERNAME_SIZE;
    public final static int ROW_SIZE = ID_SIZE + USERNAME_SIZE + EMAIL_SIZE;
    int id;
    String username;
    String email;

    Row(){

    }

    Row(MappedByteBuffer buffer, int offset, int size){
        buffer2Row(buffer, offset, size);
    }

    boolean isNull(){
        return id == 0 && username.equals("") && email.equals("");
    }

    void buffer2Row(MappedByteBuffer buffer, int offset, int size){
        int id = 0;
        for (int i = offset + ID_OFFSET; i < offset + ID_OFFSET + ID_SIZE; i++) {
            id += (buffer.get() & 0xff) << ((3 - i + offset + ID_OFFSET) * 8);
        }
        this.id = id;

        byte[] userNameBytes = new byte[USERNAME_SIZE];
        buffer.get(userNameBytes, 0, USERNAME_SIZE);
        this.username = new String(userNameBytes).trim();

        byte[] emailBytes = new byte[EMAIL_SIZE];
        buffer.get(emailBytes, 0, EMAIL_SIZE);
        this.email = new String(emailBytes).trim();
    }

    void row2Buffer(MappedByteBuffer buffer){
        byte[] idBytes = Util.int2Bytes(id);
        buffer.put(idBytes);
        byte[] userNameBytes = new byte[USERNAME_SIZE];
        int i = 0;
        for(byte b: username.getBytes()){
            userNameBytes[i++] = b;
        }
        buffer.put(userNameBytes);

        byte[] emailBytes = new byte[EMAIL_SIZE];
        i = 0;
        for(byte b: email.getBytes()){
            emailBytes[i++] = b;
        }
        buffer.put(emailBytes);
    }

    @Override
    public String toString() {
        return "(" + id + ", " + username + ", " + email + ")";
    }

    void copyTo(Row destination){
        destination.id = id;
        destination.username = username;
        destination.email = email;
    }

    void copyFrom(Row source){
        source.copyTo(this);
    }
}
