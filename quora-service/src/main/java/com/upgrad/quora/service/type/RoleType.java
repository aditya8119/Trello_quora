package com.upgrad.quora.service.type;
//enum for role of the user
import java.util.HashMap;
import java.util.Map;


public enum RoleType {

    admin(0), nonadmin(1);

    private static final Map<Integer, RoleType> Lookup = new HashMap<>();

    static {
        for (RoleType userStatus : RoleType.values()) {
            Lookup.put(userStatus.getCode(), userStatus);
        }
    }

    private final int code;

    private RoleType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RoleType getEnum(final int code) {
        return Lookup.get(code);
    }

    public static void main(String[] args) {
        System.out.println(RoleType.getEnum(1).toString());
    }
}