package helpers;

public class UserIdHolder {

    private static volatile int createdUserId = -1;

    private UserIdHolder() {}

    public static void setCreatedUserId(int id) {
        createdUserId = id;
    }

    public static int getCreatedUserId() {
        return createdUserId;
    }

    public static boolean hasCreatedUser() {
        return createdUserId > 0;
    }
}
