package util;

import enums.Role;

public class IDGenerator {
    private static int adminID = 1;
    private static int userId = 1;

    public static String generateAdminID(Role role) {
        if(role.toString().equals("ADMIN")){
            return "ADM-" + adminID++;
        }else if(role.toString().equals("USER")){
            return "USR" + userId++;
        }
        return null;
    }
}
