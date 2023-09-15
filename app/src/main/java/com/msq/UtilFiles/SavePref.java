package com.msq.UtilFiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SavePref {

    public static final String TAG = "SavePref";
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static final String CHAT_SCREEN = "chat_screen";
    public static final String PREF_TOKEN = "AppoloProtocol";

    public SavePref(Context c) {
        context = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }


    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    public void setCHAT_ID(String chat_id) {
        editor.putString("chat_id", chat_id);
        editor.commit();
    }

    public String getCHAT_ID() {
        String chat_id = preferences.getString("chat_id", "");
        return chat_id;
    }
    public String getString(String key, String def_value) {
        return preferences.getString(key, def_value);
    }


    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean def_value) {

        return preferences.getBoolean(key, def_value);
    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

    public String getEmail() {
        String email = preferences.getString("email", "");
        return email;
    }

    public void setID(String id) {
        editor.putString("id", id);
        editor.commit();
    }

    public String getID() {
        String id = preferences.getString("id", "");
        return id;
    }

    public void setCOCH_ID(String COCH_ID) {
        editor.putString("COCH_ID", COCH_ID);
        editor.commit();
    }

    public String getCOCH_ID() {
        String COCH_ID = preferences.getString("COCH_ID", "");
        return COCH_ID;
    }

    public void setName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    public String getName() {
        String name = preferences.getString("name", "");
        return name;
    }

    public void setPhone(String phone) {
        editor.putString("phone", phone);
        editor.commit();
    }

    public String getPhone() {
        String phone = preferences.getString("phone", "");
        return phone;
    }

    public void setUser_type(String user_type) {
        editor.putString("user_type", user_type);
        editor.commit();
    }

    public String getUser_type() {
        String user_type = preferences.getString("user_type", "");
        return user_type;
    }
    public void setIs_online(String is_online) {
        editor.putString("is_online", is_online);
        editor.commit();
    }

    public String getIs_online() {
        String is_online = preferences.getString("is_online", "");
        return is_online;
    }

    public void setFirstName(String firstName) {
        editor.putString("firstName", firstName);
        editor.commit();
    }

    public String getFirstName() {
        String firstName = preferences.getString("firstName", "");
        return firstName;
    }

    public void setLastName(String last_name) {
        editor.putString("last_name", last_name);
        editor.commit();
    }

    public String getLastName() {
        String last_name = preferences.getString("last_name", "");
        return last_name;
    }

    public void setfrom_time(String from_time) {
        editor.putString("from_time", from_time);
        editor.commit();
    }

    public String getfrom_time() {
        String from_time = preferences.getString("from_time", "");
        return from_time;
    }

    public void setInterval(String interval) {
        editor.putString("interval", interval);
        editor.commit();
    }

    public String getInterval() {
        String interval = preferences.getString("interval", "");
        return interval;
    }

    public void setWeight(String weight) {
        editor.putString("weight", weight);
        editor.commit();
    }

    public String getWeight() {
        String weight = preferences.getString("weight", "");
        return weight;
    }

    public void setBecieps(String becieps) {
        editor.putString("becieps", becieps);
        editor.commit();
    }

    public String getBecieps() {
        String becieps = preferences.getString("becieps", "");
        return becieps;
    }

    public void setHeight(String height) {
        editor.putString("height", height);
        editor.commit();
    }

    public String getHeight() {
        String height = preferences.getString("height", "");
        return height;
    }

    public void setChest(String chest) {
        editor.putString("chest", chest);
        editor.commit();
    }

    public String getChest() {
        String chest = preferences.getString("chest", "");
        return chest;
    }


    public void setTripceps(String tripceps) {
        editor.putString("tripceps", tripceps);
        editor.commit();
    }

    public String getTripces() {
        String tripceps = preferences.getString("tripceps", "");
        return tripceps;
    }

    public void setFriendId(String friend_id) {
        editor.putString("friend_id", friend_id);
        editor.commit();
    }

    public String getFriendId() {
        String friend_id = preferences.getString("friend_id", "");
        return friend_id;
    }

    public void setWaist(String waist) {
        editor.putString("waist", waist);
        editor.commit();
    }

    public String getWaist() {
        String waist = preferences.getString("waist", "");
        return waist;
    }

    public void setAge(String age) {
        editor.putString("age", age);
        editor.commit();
    }

    public String getAge() {
        String age = preferences.getString("age", "");
        return age;
    }

    public void setTraget_valuer(String traget_value) {
        editor.putString("traget_value", traget_value);
        editor.commit();
    }

    public String getTraget_valuer() {
        String traget_value = preferences.getString("traget_value", "");
        return traget_value;
    }

    public void setdob(String dob) {
        editor.putString("dob", dob);
        editor.commit();
    }

    public String getdob() {
        String dob = preferences.getString("dob", "");
        return dob;
    }

    public void setGender(String gender) {
        editor.putString("gender", gender);
        editor.commit();
    }

    public String getGender() {
        String gender = preferences.getString("gender", "");
        return gender;
    }


    public void setImage(String image) {
        editor.putString("image", image);
        editor.commit();
    }

    public String getImage() {
        String image = preferences.getString("image", "");
        return image;
    }


    public void setUpperLeg(String upperleg) {
        editor.putString("upperleg", upperleg);
        editor.commit();
    }

    public String getUpperLeg() {
        String upperleg = preferences.getString("upperleg", "");
        return upperleg;
    }

    public void setforearm(String forearm) {
        editor.putString("forearm", forearm);
        editor.commit();
    }

    public String getforearm() {
        String forearm = preferences.getString("forearm", "");
        return forearm;
    }
    public void isChatScreen(boolean value) {
        editor.putBoolean(CHAT_SCREEN, value);
        editor.commit();
    }

    public boolean getChatScreen() {
        return preferences.getBoolean(CHAT_SCREEN, false);
    }

    public void setunder_leg(String under_leg) {
        editor.putString("under_leg", under_leg);
        editor.commit();
    }

    public String getunder_leg() {
        String under_leg = preferences.getString("under_leg", "");
        return under_leg;
    }

    public void setbody_fat(String body_fat) {
        editor.putString("body_fat", body_fat);
        editor.commit();
    }

    public String getbody_fat() {
        String body_fat = preferences.getString("body_fat", "");
        return body_fat;
    }

    public void setAuthorization_key(String authorization_key) {
        editor.putString("authorization_key", authorization_key);
        editor.commit();
    }

    public String getAuthorization_key() {
        String authorization_key = preferences.getString("authorization_key", "");
        return authorization_key;
    }


    public void setStringLatest(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringLatest(String key) {
        String authorization_key = preferences.getString(key, "");
        return authorization_key;
    }


    public static void setDeviceToken(Context mContext, String key, String value) {
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDeviceToken(Context mContext, String key) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE);
        String stringvalue = preferences.getString(key, "");
        return stringvalue;
    }

    public void clearPreferences() {

        preferences.edit().clear().commit();
    }


}
