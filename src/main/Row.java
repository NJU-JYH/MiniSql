package main;

import java.util.Arrays;

public class Row {
    final static int ID_SIZE = 4;
    final static int USERNAME_SIZE = 32;
    final static int EMAIL_SIZE = 255;
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
}
