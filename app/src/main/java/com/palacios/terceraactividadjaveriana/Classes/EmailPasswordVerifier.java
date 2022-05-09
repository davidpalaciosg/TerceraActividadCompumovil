package com.palacios.terceraactividadjaveriana.Classes;

public class EmailPasswordVerifier {
    static public boolean verifyEmailAndPassword(String email, String password) {
        //Verify email with regex
        if (email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            //Verify password lenght
            if (password.length() > 6)
                return true;
        }
        return false;
    }
}
