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


    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "CareTaker";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // customer id (make variable public to access from outside)
    public static final String KEY_CUST_ID = "customerId";
    // service saved (make variable public to access from outside)
    public static final String KEY_SERVICE_SAVED = "service_saved";

    // notification saved (make variable public to access from outside)
    public static final String KEY_NOTIFICATION_SAVED = "notification_saved";

    // dependents saved (make variable public to access from outside)
    public static final String KEY_DEPENDENTS_SAVED = "dependents_saved";
    // service customer saved (make variable public to access from outside)
    public static final String KEY_SERVICE_CUSTOMER = "service_customer";

    // provider saved (make variable public to access from outside)
    public static final String KEY_PROVIDER_SAVED = "provider_saved";

    // activity saved (make variable public to access from outside)
    public static final String KEY_ACTIVITY_SAVED = "activity_saved";


    public final String KEY_DEPENDENTS_IDS = "dependents_ids";
    public final String KEY_PROVIDERS_IDS = "providers_ids";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
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
    public HashMap<String, String> getUserDetails() {
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

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
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
    public void saveDependentsStatus(boolean status) {


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
    public void saveProviderStatus(boolean status) {


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
