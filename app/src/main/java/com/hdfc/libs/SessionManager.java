package com.hdfc.libs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sudesi infotech on 6/23/2016.
 */
public class SessionManager {


    // User password (make variable public to access from outside)
    static final String KEY_PASSWORD = "password";
    // Email address (make variable public to access from outside)
    private static final String KEY_EMAIL = "email";
    // customer id (make variable public to access from outside)
    private static final String KEY_CUST_ID = "customerId";
    // service saved (make variable public to access from outside)
    private static final String KEY_SERVICE_SAVED = "service_saved";
    // notification saved (make variable public to access from outside)
    private static final String KEY_NOTIFICATION_SAVED = "notification_saved";
    // dependents saved (make variable public to access from outside)
    private static final String KEY_DEPENDENTS_SAVED = "dependents_saved";
    // service customer saved (make variable public to access from outside)
    private static final String KEY_SERVICE_CUSTOMER = "service_customer";
    // provider saved (make variable public to access from outside)
    private static final String KEY_PROVIDER_SAVED = "provider_saved";
    // activity saved (make variable public to access from outside)
    private static final String KEY_ACTIVITY_SAVED = "activity_saved";
    // Sharedpref file name
    private static final String PREF_NAME = "CareTaker";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
    private final String KEY_DEPENDENTS_IDS = "dependents_ids";
    private final String KEY_PROVIDERS_IDS = "providers_ids";
    private final String KEY_OLD_PASSWORD = "old_password";
    private final String KEY_UPDATED_DEPENDENTS = "updated_dependents";
    private final String KEY_CHECKIN_CARE_STATUS = "checkin_care_status";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        try {
            this._context = context;
            pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create login session
     */
    public void createLoginSession(String password, String email) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getPassword() {
        return pref.getString(KEY_PASSWORD, "");
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        try {
            editor.remove(KEY_SERVICE_SAVED);
            editor.remove(KEY_CHECKIN_CARE_STATUS);
            editor.remove(KEY_UPDATED_DEPENDENTS);
            editor.remove(KEY_ACTIVITY_SAVED);
            editor.remove(KEY_CUST_ID);
            editor.remove(KEY_DEPENDENTS_IDS);
            editor.remove(KEY_DEPENDENTS_SAVED);
            editor.remove(KEY_NOTIFICATION_SAVED);
            editor.remove(KEY_SERVICE_CUSTOMER);
            editor.remove(KEY_PROVIDERS_IDS);
            editor.remove(KEY_PROVIDER_SAVED);
            editor.remove(KEY_CHECKIN_CARE_STATUS);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.remove(IS_LOGIN);
            editor.remove(KEY_DEVICE_TOKEN);
            editor.clear();
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    String getDeviceToken() {

        String strDeviceToken;

        try {
            strDeviceToken = pref.getString(KEY_DEVICE_TOKEN, "");
        } catch (Exception e) {
            e.printStackTrace();
            strDeviceToken = "";
        }
        return strDeviceToken;
    }

    public void setDeviceToken(String strToken) {
        try {

            editor.putString(KEY_DEVICE_TOKEN, strToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // commit changes
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    /**
     * save old password
     */
    public void saveOldPassword(String password) {


        // Storing customerId in pref
        editor.putString(KEY_OLD_PASSWORD, password);


        // commit changes
        editor.commit();
    }

    /**
     * Get old password
     */
    public String getOldPassword() {


        // return customer id
        return pref.getString(KEY_OLD_PASSWORD, "");
    }

    /**
     * save dependents update status for syncing
     */
    public void saveUpdateDependent(List<String> dependentsIdsList) {


        Set<String> set = new HashSet<String>();
        //set = pref.getStringSet(KEY_UPDATED_DEPENDENTS, new HashSet<String>());
        set.addAll(dependentsIdsList);
        editor.putStringSet(KEY_UPDATED_DEPENDENTS, set);
        editor.commit();
    }

    /**
     * Get dependents update status for syncing
     */
    public List<String> getUpdateDependent() {


        List<String> dependentsIdsList = new ArrayList<String>();

        Set<String> set = pref.getStringSet(KEY_UPDATED_DEPENDENTS, new HashSet<String>());
        if (set != null && set.size() > 0) {
            dependentsIdsList = new ArrayList<String>(set);
        }
        return dependentsIdsList;
    }


    /**
     * save customer id
     */
    public void saveCustomerId(String customerId) {


        // Storing customerId in pref
        editor.putString(KEY_CUST_ID, customerId);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public String getCustomerId() {


        // return customer id
        return pref.getString(KEY_CUST_ID, "");
    }

    /**
     * save service status
     */
    public void saveServiceStatus(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_SERVICE_SAVED, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getServiceStatus() {


        // return customer id
        return pref.getBoolean(KEY_SERVICE_SAVED, false);
    }

    /**
     * save checkin care status
     */
    public void saveCheckInCareStatus(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_CHECKIN_CARE_STATUS, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getCheckInCareStatus() {


        // return customer id
        return pref.getBoolean(KEY_CHECKIN_CARE_STATUS, false);
    }


//    /**
//     * save notification status
//     */
//    public void saveNotificationStatus(boolean status) {
//
//
//        // Storing service status in pref
//        editor.putBoolean(KEY_NOTIFICATION_SAVED, status);
//
//
//        // commit changes
//        editor.commit();
//    }
//
//    /**
//     * Get stored session data
//     */
//    public boolean getNotificationStatus() {
//
//
//        // return customer id
//        return pref.getBoolean(KEY_NOTIFICATION_SAVED, false);
//    }

    /**
     * save dependent status
     */
    void saveDependentsStatus(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_DEPENDENTS_SAVED, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getDependentsStatus() {


        // return customer id
        return pref.getBoolean(KEY_DEPENDENTS_SAVED, false);
    }

    /**
     * save service customer
     */
    public void saveServiceCustomer(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_SERVICE_CUSTOMER, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getServiceCustomer() {


        // return customer id
        return pref.getBoolean(KEY_SERVICE_CUSTOMER, false);
    }

    /**
     * save provider status
     */
    void saveProviderStatus(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_PROVIDER_SAVED, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getProviderStatus() {


        // return customer id
        return pref.getBoolean(KEY_PROVIDER_SAVED, false);
    }

    /**
     * save provider status
     */
    public void saveActivityStatus(boolean status) {


        // Storing service status in pref
        editor.putBoolean(KEY_ACTIVITY_SAVED, status);


        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public boolean getActivityStatus() {


        // return customer id
        return pref.getBoolean(KEY_ACTIVITY_SAVED, false);
    }

    /**
     * save dependents id's list
     */
    public void saveDependentsIds(List<String> dependenstidsList) {


        Set<String> set = new HashSet<String>();
        set = pref.getStringSet(KEY_DEPENDENTS_IDS, new HashSet<String>());
        set.addAll(dependenstidsList);
        editor.putStringSet(KEY_DEPENDENTS_IDS, set);
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public List<String> getDependentsIds() {
        List<String> dependenstidsList = new ArrayList<String>();

        Set<String> set = pref.getStringSet(KEY_DEPENDENTS_IDS, new HashSet<String>());
        if (set != null && set.size() > 0) {
            dependenstidsList = new ArrayList<String>(set);
        }
        return dependenstidsList;
    }

    /**
     * save dependents id's list
     */
    public void saveNotificationIds(List<String> notificationIdsList) {


        Set<String> set = new HashSet<String>();
        set = pref.getStringSet(KEY_NOTIFICATION_SAVED, new HashSet<String>());
        set.addAll(notificationIdsList);
        editor.putStringSet(KEY_NOTIFICATION_SAVED, set);
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public List<String> getNotificationIds() {
        List<String> notificationIdsList = new ArrayList<String>();

        Set<String> set = pref.getStringSet(KEY_NOTIFICATION_SAVED, new HashSet<String>());
        if (set != null && set.size() > 0) {
            notificationIdsList = new ArrayList<String>(set);
        }
        return notificationIdsList;
    }

    /**
     * save providers id's list
     */
    public void saveProvidersIds(List<String> providersidsList) {


        Set<String> set = new HashSet<String>();
        set = pref.getStringSet(KEY_PROVIDERS_IDS, new HashSet<String>());
        set.addAll(providersidsList);
        editor.putStringSet(KEY_PROVIDERS_IDS, set);
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public List<String> getProvidersIds() {
        List<String> providersidsList = new ArrayList<String>();

        Set<String> set = pref.getStringSet(KEY_PROVIDERS_IDS, new HashSet<String>());
        if (set != null && set.size() > 0) {
            providersidsList = new ArrayList<String>(set);
        }

        return providersidsList;
    }

}
