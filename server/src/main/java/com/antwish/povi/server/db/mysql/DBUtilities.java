package com.antwish.povi.server.db.mysql;

import com.antwish.povi.server.Day;
import com.antwish.povi.server.EventType;
import com.antwish.povi.server.IdentityType;
import com.antwish.povi.server.ParentingTipCategory;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;

public class DBUtilities {
    private static Connection conn = null;
    private static int port = 3306;        // default port for mysql
    private static final String SEPARATOR = ":";

    /**
     * create a DB connection to mysql
     *
     * @param dbUrl
     * @param dbName
     * @param userName
     * @param password
     * @return
     */
    public static Connection getConnection(String dbUrl, String dbName,
                                           String userName, String password) {

        if (conn == null) {
            try {
                String connString = "jdbc:mysql://" + dbUrl + ":" + port + "/" + dbName + "?autoReconnect=true";
                conn = DriverManager.getConnection(connString, userName, password);
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }

        return conn;
    }

    /**
     * utility to convert from DB column 'user_type' to IdentityType
     *
     * @param regType
     * @return
     */
    public static IdentityType toIdentityType(char regType) {
        switch (regType) {
            case 'p':
                return IdentityType.PHONE;
            case 'f':
                return IdentityType.FB;
            case 'e':
                return IdentityType.EMAIL;
            default:
                return IdentityType.$UNKNOWN;
        }
    }

    /**
     * utility to convert from IdentityType to the corresponding value for the DB column 'user_type'
     *
     * @param iType
     * @return
     */
    public static String identityTypeToString(IdentityType iType) {
        switch (iType) {
            case PHONE:
                return "p";
            case FB:
                return "f";
            case EMAIL:
                return "e";
            default:
                return "u";
        }
    }

    /**
     * utility to convert an EventType enum to a 2-letter string to be saved in DB
     *
     * @param eType
     * @return
     */
    public static String eventTypeToString(EventType eType) {
        switch (eType) {
            case LOGIN:
                return "LN";
            case REGISTRATION:
                return "RG";
            case LOGOUT:
                return "LO";
            case READING:
                return "RD";
            case REMOTEPLAY:
                return "RP";
            case PHYSICAL:
                return "PH";
            case TABLET:
                return "TB";
            case CHILD:
                return "CH";
            case COMMENT:
                return "CM";
            case TIPOFTHEDAY:
                return "TP";
            default:
                return "UN";
        }
    }

    /**
     * utility to convert the string representation of EventType in DB to EventType
     * @param eType
     * @return
     */
    public static EventType stringToEventType(String eType){
            if(eType.equals("LN")) return EventType.LOGIN;
            else if(eType.equals("RG")) return EventType.REGISTRATION;
            else if(eType.equals("LO")) return EventType.LOGOUT;
            else if(eType.equals("RD")) return EventType.READING;
            else if(eType.equals("RP")) return EventType.REMOTEPLAY;
            else if(eType.equals("PH")) return EventType.PHYSICAL;
            else if(eType.equals("TB")) return EventType.TABLET;
            else if(eType.equals("CH")) return EventType.CHILD;
            else if(eType.equals("CM")) return EventType.COMMENT;
            else if(eType.equals("TP")) return EventType.TIPOFTHEDAY;
            else return EventType.$UNKNOWN;
    }
    /**
     * convert 'y', or 'n' to boolean
     *
     * @param c
     * @return
     */
    public static Boolean toBoolean(char c) {
        switch (c) {
            case 'y':
                return true;
            case 'n':
                return false;
            default:
                return null;
        }
    }

    public static short dayToNumber(Day day) {
        switch (day) {
            case MONDAY:
                return 1;
            case TUESDAY:
                return 2;
            case WEDNESDAY:
                return 3;
            case THURSDAY:
                return 4;
            case FRIDAY:
                return 5;
            case SATURDAY:
                return 6;
            case SUNDAY:
                return 7;
            case EVERYDAY:
                return 8;
            case WORKDAY:
                return 9;
        }

        return 0;
    }

    public static Day numberToDay(short number) {
        switch (number) {
            case 1:
                return Day.MONDAY;
            case 2:
                return Day.TUESDAY;
            case 3:
                return Day.WEDNESDAY;
            case 4:
                return Day.THURSDAY;
            case 5:
                return Day.FRIDAY;
            case 6:
                return Day.SATURDAY;
            case 7:
                return Day.SUNDAY;
            case 8:
                return Day.EVERYDAY;
            case 9:
                return Day.WORKDAY;
        }

        return Day.$UNKNOWN;
    }

    public static Time stringToTime(String schedule) {
        if (schedule == null || schedule.isEmpty())
            return null;

        String[] parts = schedule.split(SEPARATOR);
        if (parts.length != 3)
            return null;

        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int second = Integer.parseInt(parts[2]);

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59)
            return null;

        return new Time(hour, minute, second);
    }

    public static ParentingTipCategory toParentingTipCategory(int category) {
        if(category<0 || category> ParentingTipCategory.values().length)
            return ParentingTipCategory.$UNKNOWN;

        return ParentingTipCategory.values()[category - 1];
    }

    public static void printStackTrace(Logger logger, StackTraceElement[] elements){
        if(logger != null && elements != null) {
            for(StackTraceElement element : elements)
                logger.error(String.valueOf(element));
        }
    }
}
