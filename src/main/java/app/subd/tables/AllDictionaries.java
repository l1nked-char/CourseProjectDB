package app.subd.tables;

import app.subd.Database_functions;
import app.subd.Session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class AllDictionaries {
    private static final Map<String, Integer> citiesIdMap = new HashMap<>();
    private static final Map<Integer, String> citiesNameMap = new HashMap<>();

    private static final Map<String, Integer> socialStatusIdMap = new HashMap<>();
    private static final Map<Integer, String> socialStatusNameMap = new HashMap<>();

    private static final Map<String, Integer> conveniencesIdMap = new HashMap<>();
    private static final Map<Integer, String> conveniencesNameMap = new HashMap<>();

    private static final Map<String, Integer> typesOfRoomIdMap = new HashMap<>();
    private static final Map<Integer, String> typesOfRoomNameMap = new HashMap<>();

    private static final Map<String, Integer> servicesIdMap = new HashMap<>();
    private static final Map<Integer, String> servicesNameMap = new HashMap<>();

    public static void initialiseCitiesMaps() throws Exception
    {
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_cities");

        citiesIdMap.clear();
        citiesNameMap.clear();

        while (rs.next()) {
            int cityId = rs.getInt("city_id");
            String cityName = rs.getString("city_name");

            citiesIdMap.put(cityName, cityId);
            citiesNameMap.put(cityId, cityName);
        }
    }

    public static void initialiseSocialStatusMaps() throws Exception
    {
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_social_statuses");

        socialStatusIdMap.clear();
        socialStatusNameMap.clear();

        while (rs.next()) {
            int id = rs.getInt("status_id");
            String name = rs.getString("status_name");

            socialStatusIdMap.put(name, id);
            socialStatusNameMap.put(id, name);
        }
    }

    public static void initialiseConveniencesMaps() throws Exception
    {
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_conveniences");

        conveniencesIdMap.clear();
        conveniencesNameMap.clear();

        while (rs.next()) {
            int id = rs.getInt("conv_name_id");
            String name = rs.getString("conv_name");

            conveniencesIdMap.put(name, id);
            conveniencesNameMap.put(id, name);
        }
    }

    public static void initialiseTypesOfRoomMaps() throws Exception
    {
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_types_of_room");

        typesOfRoomIdMap.clear();
        typesOfRoomNameMap.clear();

        while (rs.next()) {
            int id = rs.getInt("room_type_id");
            String name = rs.getString("room_type_name");

            typesOfRoomIdMap.put(name, id);
            typesOfRoomNameMap.put(id, name);
        }
    }

    public static void initialiseServicesMaps() throws Exception
    {
        Connection connection = Session.getConnection();
        ResultSet rs = Database_functions.callFunction(connection, "get_all_services");

        servicesIdMap.clear();
        servicesNameMap.clear();

        while (rs.next()) {
            int id = rs.getInt("service_name_id");
            String name = rs.getString("service_name");

            servicesIdMap.put(name, id);
            servicesNameMap.put(id, name);
        }
    }

    public static Map<String, Integer> getCitiesIdMap() { return citiesIdMap; }
    public static Map<String, Integer> getSocialStatusIdMap() { return socialStatusIdMap; }
    public static Map<String, Integer> getConveniencesIdMap() { return conveniencesIdMap; }
    public static Map<String, Integer> getTypesOfRoomIdMap() { return typesOfRoomIdMap; }
    public static Map<String, Integer> getSevicesIdMap() { return servicesIdMap; }

    public static Map<Integer, String> getCitiesNameMap() { return citiesNameMap; }
    public static Map<Integer, String> getSocialStatusNameMap() { return socialStatusNameMap; }
    public static Map<Integer, String> getConveniencesNameMap() { return conveniencesNameMap; }
    public static Map<Integer, String> getTypesOfRoomNameMap() { return typesOfRoomNameMap; }
    public static Map<Integer, String> getSevicesNameMap() { return servicesNameMap; }
}
