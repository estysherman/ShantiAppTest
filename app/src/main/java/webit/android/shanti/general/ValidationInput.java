package webit.android.shanti.general;

import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by AndroIT on 04/02/2015.
 */
public class ValidationInput {

    public static final String PHONE_PATTERN =  "\\d{3}-\\d{7}";
    public static boolean email(String mail) {
        if (!isStringEmpty(mail)) {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(mail).matches();
        }
        return false;
    }


    public static boolean phone(String phone) {
        if (!isStringEmpty(phone)) {
            Pattern pattern = Pattern.compile("^[+]?[0-9]{10,13}$");
            return pattern.matcher(phone).matches();
        }
        return false;
    }


    //
    public static boolean isStringEmpty(String s) {
        return !(s != null && !s.equalsIgnoreCase(""));
    }
}
