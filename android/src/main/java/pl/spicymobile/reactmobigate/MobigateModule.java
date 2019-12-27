package pl.spicymobile.reactmobigate;


import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pl.spicymobile.mobience.sdk.Category;
import pl.spicymobile.mobience.sdk.EventCategory;
import pl.spicymobile.mobience.sdk.EventParameter;
import pl.spicymobile.mobience.sdk.MobigateSDK;
import pl.spicymobile.mobience.sdk.SDK;

public class MobigateModule extends ReactContextBaseJavaModule {
    private SDK mobigateSDK;

    public MobigateModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Mobigate";
    }


    @ReactMethod
    public void init(String apiKey, ReadableMap _options, Callback success, Callback error) {
        try {
            MobigateSDK.Builder builder = new MobigateSDK.Builder(getReactApplicationContext().getApplicationContext(), apiKey);
            JSONObject options = Utils.readableMapToJson(_options);

            if (options != null) {
                Iterator<String> optionsIterator = options.keys();
                while (optionsIterator.hasNext()) {
                    String key = optionsIterator.next();
                    switch (key) {
                        case Constants.BUILDER_APP_IDENTIFIER:
                            builder.setAppIdentifier(options.getString(key));
                            break;
                        case Constants.BUILDER_APP_INSTALLATION_SOURCE:
                            builder.setAppInstallationSource(options.getString(key));
                            break;
                        case Constants.BUILDER_EMAIL:
                            builder.setEmail(options.getString(key));
                            break;
                        case Constants.BUILDER_CUSTOM_USER_ID:
                            builder.setCustomUserID(options.getString(key));
                            break;
                        case Constants.BUILDER_ENABLE_USER_FIELDS:
                            JSONArray arrayOfFields = options.getJSONArray(key);
                            if (arrayOfFields != null && arrayOfFields.length() > 0) {
                                ArrayList<SDK.UserField> array = new ArrayList<>();
                                for (int i = 0; i < arrayOfFields.length(); i++) {
                                    array.add(SDK.UserField.valueOf(arrayOfFields.getString(i)));
                                }
                                if (array.size() > 0) {
                                    SDK.UserField[] table = new SDK.UserField[array.size()];
                                    for (int i = 0; i < array.size(); i++) {
                                        table[i] = array.get(i);
                                    }
                                    builder.enableUserField(table);
                                }
                            }

                            break;
                        case Constants.BUILDER_DATA_GATHER_POLICY:
                            builder.setDataGatherPolicy(
                                    SDK.MONITOR_STATE.valueOf(options.getJSONObject(key).getString(Constants.BUILDER_MONITOR_STATE))
                                    , options.getJSONObject(key).getString(Constants.BUILDER_NOTIFICATION_TEXT));

                            break;
                        case Constants.BUILDER_ENABLE_ID_PROFILES:
                            builder.enableIDsProfiles(options.getBoolean(key));
                            break;
                    }
                }

            }
            mobigateSDK = builder.build();
            if (mobigateSDK != null)
                success.invoke(Constants.SUCCESS);
            else
                error.invoke(Constants.FAILURE);
        } catch (JSONException ex) {
            error.invoke("init() _options parsing exception, message: " + ex.getMessage());
        }
    }

    @ReactMethod
    public void startSDK(Callback success, Callback error) {
        if (mobigateSDK != null) {
            mobigateSDK.startService();
            success.invoke("success");
        } else {
            error.invoke("failure, init() library first!");
        }
    }

    @ReactMethod
    public void setCollectAll() {
        if (mobigateSDK != null) {
            mobigateSDK.setCollectAll();
        }
    }

    @ReactMethod
    public void configureDataCollectors(Boolean enable, ReadableArray _collectors) {
        try {
            JSONArray collectors = Utils.convertArrayToJson(_collectors);

            if (collectors != null && collectors.length() > 0) {
                if (mobigateSDK != null)
                    mobigateSDK.configureDataCollectors(enable, Utils.JSonArray2IntArray(collectors));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @ReactMethod
    public void disableAllDataCollector() {
        if (mobigateSDK != null)
            mobigateSDK.disableAllDataCollector();
    }

    @ReactMethod
    public void setEmail(String email) {
        if (mobigateSDK != null)
            mobigateSDK.setEmail(email);
    }

    @ReactMethod
    public void getEmail(Callback success, Callback error) {
        if (mobigateSDK != null)
            success.invoke(mobigateSDK.getEmail());
        else
            error.invoke("failure, init() library first!");
    }

    @ReactMethod
    public void setFbToken(String token) {
        if (mobigateSDK != null)
            mobigateSDK.setFbToken(token);
    }

    @ReactMethod
    public void getSDKInfo(Callback success, Callback error) {
        if (mobigateSDK != null)
            success.invoke(mobigateSDK.getSDKInfo());
        else
            error.invoke("failure, init() library first!");
    }

    @ReactMethod
    public void getSDKUniqueIdentifier(Callback success, Callback error) {
        if (mobigateSDK != null)
            success.invoke(mobigateSDK.getSDKUniqueIdentifier());
        else
            error.invoke("failure, init() library first!");
    }

    @ReactMethod
    public void getIDsProfiles(Callback success, Callback error) {
        if (mobigateSDK != null) {
            success.invoke(Utils.intList2JSONArray(mobigateSDK.getIDsProfiles()).toString());
        } else
            error.invoke("failure, init() library first!");
    }

    @ReactMethod
    public void getAdOceanTargeting(Callback success, Callback error) {
        if (mobigateSDK != null) {
            success.invoke(new JSONObject(mobigateSDK.getAdOceanTargeting()).toString());
        } else
            error.invoke("failure, init() library first!");
    }

    @ReactMethod
    public void trackAppInstall(Double timestamp) {
        mobigateSDK.trackAppInstall(timestamp.longValue());
    }

    @ReactMethod
    public void trackEvent(ReadableMap _options) {
        try {
            JSONObject options = Utils.readableMapToJson(_options);
            if (options != null) {
                String categoryName = options.getString(Constants.BUILDER_CATEGORY_NAME);
                Category.Builder builder = new Category.Builder(categoryName);

                JSONObject parameters = options.optJSONObject(Constants.BUILDER_CATEGORY_PARAMETERS);
                if (parameters != null) {
                    Iterator<String> parametersIterator = parameters.keys();
                    while (parametersIterator.hasNext()) {
                        String key = parametersIterator.next();
                        builder.setParameter(key,parameters.opt(key));
                    }
                }
                if(mobigateSDK != null)
                    mobigateSDK.trackEvent(builder.build());
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(Constants.USER_FIELD_EMAIL, SDK.UserField.EMAIL.name());
        constants.put(Constants.USER_FIELD_IMSI, SDK.UserField.IMSI.name());
        constants.put(Constants.USER_FIELD_SERIAL, SDK.UserField.SERIAL.name());
        constants.put(Constants.USER_FIELD_IMEI, SDK.UserField.IMEI.name());
        constants.put(Constants.DATA_GATHER_POLICY_BLOCK_DATA_BG, SDK.MONITOR_STATE.BLOCK_DATA_BG.name());
        constants.put(Constants.DATA_GATHER_POLICY_DEFAULT, SDK.MONITOR_STATE.DEFAULT.name());
        constants.put(Constants.DATA_GATHER_POLICY_HIDE_TRAY, SDK.MONITOR_STATE.HIDE_TRAY.name());
        constants.put(Constants.DATA_COLLECTOR_APPS_LIST, 0);
        constants.put(Constants.DATA_COLLECTOR_APPS_USAGE, 1);
        constants.put(Constants.DATA_COLLECTOR_BATTERY, 2);
        constants.put(Constants.DATA_COLLECTOR_BROWSER, 3);
        constants.put(Constants.DATA_COLLECTOR_CALENDAR_EVENTS, 4);
        constants.put(Constants.DATA_COLLECTOR_CPU_PROCESS, 6);
        constants.put(Constants.DATA_COLLECTOR_DICTIONARY, 7);
        constants.put(Constants.DATA_COLLECTOR_GEOLOCATION, 8);
        constants.put(Constants.DATA_COLLECTOR_HEADSET_PLUG, 9);
        constants.put(Constants.DATA_COLLECTOR_MEDIA_FILES, 10);
        constants.put(Constants.DATA_COLLECTOR_MEMORY_USEAGE, 11);
        constants.put(Constants.DATA_COLLECTOR_NETWORK_CONNECTION, 13);
        constants.put(Constants.DATA_COLLECTOR_NETWORK_USEAGE, 14);
        constants.put(Constants.DATA_COLLECTOR_PACKAGE_CHANGE, 15);
        constants.put(Constants.DATA_COLLECTOR_PHONE_INFO, 16);
        constants.put(Constants.DATA_COLLECTOR_ROAMING, 17);
        constants.put(Constants.DATA_COLLECTOR_SCREEN_ORIENTED, 18);
        constants.put(Constants.DATA_COLLECTOR_SIGNAL_STRENGTH, 19);
        constants.put(Constants.DATA_COLLECTOR_PROFILE_MODE, 20);
        constants.put(Constants.DATA_COLLECTOR_WIFI_DATA_CONNECTION, 21);
        constants.put(Constants.DATA_COLLECTOR_PERMISSION_COLLECTOR, 22);
        constants.put(Constants.DATA_COLLECTOR_NFC_COLLECTOR, 23);
        constants.put(Constants.DATA_COLLECTOR_BLUETOOTH_COLLECTOR, 24);
        constants.put(Constants.DATA_COLLECTOR_BLUETOOTH_DEVICES_COLLECTOR, 26);
        constants.put(Constants.DATA_COLLECTOR_MOVEMENT_COLLECTOR, 27);
        constants.put(Constants.DATA_COLLECTOR_FACEBOOK_NETWORK_COLLECTOR, 28);
        constants.put(Constants.DATA_COLLECTOR_ROOT_COLLECTOR, 29);

        constants.put(Constants.CATEGORY_LEVEL_ACCOMPLISHED, EventCategory.LEVEL_ACCOMPLISHED.name());
        constants.put(Constants.CATEGORY_ADD_PAYMENT_INFO, EventCategory.ADD_PAYMENT_INFO.name());
        constants.put(Constants.CATEGORY_ADD_TO_BASKET, EventCategory.ADD_TO_BASKET.name());
        constants.put(Constants.CATEGORY_REMOVED_FROM_BASKET, EventCategory.REMOVED_FROM_BASKET.name());
        constants.put(Constants.CATEGORY_ADD_TO_WISH_LIST, EventCategory.ADD_TO_WISH_LIST.name());
        constants.put(Constants.CATEGORY_REGISTRATION, EventCategory.REGISTRATION.name());
        constants.put(Constants.CATEGORY_TUTORIAL_COMPLETION, EventCategory.TUTORIAL_COMPLETION.name());
        constants.put(Constants.CATEGORY_TRIGGER_CHECKOUT, EventCategory.TRIGGER_CHECKOUT.name());
        constants.put(Constants.CATEGORY_PURCHASE, EventCategory.PURCHASE.name());
        constants.put(Constants.CATEGORY_SUBSCRIBE, EventCategory.SUBSCRIBE.name());
        constants.put(Constants.CATEGORY_BEGIN_TRIAL, EventCategory.BEGIN_TRIAL.name());
        constants.put(Constants.CATEGORY_RATE, EventCategory.RATE.name());
        constants.put(Constants.CATEGORY_SEARCH, EventCategory.SEARCH.name());
        constants.put(Constants.CATEGORY_USED_CREDIT, EventCategory.USED_CREDIT.name());
        constants.put(Constants.CATEGORY_UNLOCKED_ACHIEVEMENT, EventCategory.UNLOCKED_ACHIEVEMENT.name());
        constants.put(Constants.CATEGORY_VIEW_CONTENT, EventCategory.VIEW_CONTENT.name());
        constants.put(Constants.CATEGORY_VIEW_LIST, EventCategory.VIEW_LIST.name());
        constants.put(Constants.CATEGORY_CLICK_AD, EventCategory.CLICK_AD.name());
        constants.put(Constants.CATEGORY_VIEW_AD, EventCategory.VIEW_AD.name());
        constants.put(Constants.CATEGORY_EVENT_BOOKING, EventCategory.EVENT_BOOKING.name());
        constants.put(Constants.CATEGORY_SHARE, EventCategory.SHARE.name());
        constants.put(Constants.CATEGORY_INVITE, EventCategory.INVITE.name());
        constants.put(Constants.CATEGORY_LOGIN, EventCategory.LOGIN.name());
        constants.put(Constants.CATEGORY_EVENT_RETURN, EventCategory.EVENT_RETURN.name());
        constants.put(Constants.CATEGORY_OPENED_PUSH_NOTIFICATION, EventCategory.OPENED_PUSH_NOTIFICATION.name());
        constants.put(Constants.CATEGORY_UPDATE, EventCategory.UPDATE.name());

        constants.put(Constants.PARAMETER_REVENUE, EventParameter.REVENUE.name());
        constants.put(Constants.PARAMETER_PRICE, EventParameter.PRICE.name());
        constants.put(Constants.PARAMETER_LEVEL, EventParameter.LEVEL.name());
        constants.put(Constants.PARAMETER_SUCCESS, EventParameter.SUCCESS.name());
        constants.put(Constants.PARAMETER_CONTENT_TYPE, EventParameter.CONTENT_TYPE.name());
        constants.put(Constants.PARAMETER_CONTENT_LIST, EventParameter.CONTENT_LIST.name());
        constants.put(Constants.PARAMETER_CONTENT_ID, EventParameter.CONTENT_ID.name());
        constants.put(Constants.PARAMETER_CURRENCY, EventParameter.CURRENCY.name());
        constants.put(Constants.PARAMETER_REGISTRATION_METHOD, EventParameter.REGISTRATION_METHOD.name());
        constants.put(Constants.PARAMETER_QUANTITY, EventParameter.QUANTITY.name());
        constants.put(Constants.PARAMETER_PAYMENT_INFO_AVAILABLE, EventParameter.PAYMENT_INFO_AVAILABLE.name());
        constants.put(Constants.PARAMETER_RATING_VALUE, EventParameter.RATING_VALUE.name());
        constants.put(Constants.PARAMETER_MAX_RATING_VALUE, EventParameter.MAX_RATING_VALUE.name());
        constants.put(Constants.PARAMETER_SEARCH_STRING, EventParameter.SEARCH_STRING.name());
        constants.put(Constants.PARAMETER_DESCRIPTION, EventParameter.DESCRIPTION.name());
        constants.put(Constants.PARAMETER_SCORE, EventParameter.SCORE.name());
        constants.put(Constants.PARAMETER_DESTINATION_A, EventParameter.DESTINATION_A.name());
        constants.put(Constants.PARAMETER_DESTINATION_B, EventParameter.DESTINATION_B.name());
        constants.put(Constants.PARAMETER_CLASS, EventParameter.CLASS.name());
        constants.put(Constants.PARAMETER_DATE_A, EventParameter.DATE_A.name());
        constants.put(Constants.PARAMETER_DATE_B, EventParameter.DATE_B.name());
        constants.put(Constants.PARAMETER_EVENT_START, EventParameter.EVENT_START.name());
        constants.put(Constants.PARAMETER_EVENT_END, EventParameter.EVENT_END.name());
        constants.put(Constants.PARAMETER_LATITUDE, EventParameter.LATITUDE.name());
        constants.put(Constants.PARAMETER_LONGITUDE, EventParameter.LONGITUDE.name());
        constants.put(Constants.PARAMETER_CUSTOMER_USER_ID, EventParameter.CUSTOMER_USER_ID.name());
        constants.put(Constants.PARAMETER_CUSTOMER_SEGMENT, EventParameter.CUSTOMER_SEGMENT.name());
        constants.put(Constants.PARAMETER_VALIDATED, EventParameter.VALIDATED.name());
        constants.put(Constants.PARAMETER_RECEIPT_ID, EventParameter.RECEIPT_ID.name());
        constants.put(Constants.PARAMETER_ORDER_ID, EventParameter.ORDER_ID.name());
        constants.put(Constants.PARAMETER_TUTORIAL_ID, EventParameter.TUTORIAL_ID.name());
        constants.put(Constants.PARAMETER_ACHIEVEMENT_ID, EventParameter.ACHIEVEMENT_ID.name());
        constants.put(Constants.PARAMETER_VIRTUAL_CURRENCY_NAME, EventParameter.VIRTUAL_CURRENCY_NAME.name());
        constants.put(Constants.PARAMETER_DEEP_LINK, EventParameter.DEEP_LINK.name());
        constants.put(Constants.PARAMETER_OLD_VERSION, EventParameter.OLD_VERSION.name());
        constants.put(Constants.PARAMETER_NEW_VERSION, EventParameter.NEW_VERSION.name());
        constants.put(Constants.PARAMETER_REVIEW_TEXT, EventParameter.REVIEW_TEXT.name());
        constants.put(Constants.PARAMETER_COUPON_CODE, EventParameter.COUPON_CODE.name());
        constants.put(Constants.PARAMETER_DEPARTING_DEPARTURE_DATE, EventParameter.DEPARTING_DEPARTURE_DATE.name());
        constants.put(Constants.PARAMETER_RETURNING_DEPARTURE_DATE, EventParameter.RETURNING_DEPARTURE_DATE.name());
        constants.put(Constants.PARAMETER_DESTINATION_LIST, EventParameter.DESTINATION_LIST.name());
        constants.put(Constants.PARAMETER_CITY, EventParameter.CITY.name());
        constants.put(Constants.PARAMETER_REGION, EventParameter.REGION.name());
        constants.put(Constants.PARAMETER_COUNTRY, EventParameter.COUNTRY.name());
        constants.put(Constants.PARAMETER_DEPARTING_ARRIVAL_DATE, EventParameter.DEPARTING_ARRIVAL_DATE.name());
        constants.put(Constants.PARAMETER_RETURNING_ARRIVAL_DATE, EventParameter.RETURNING_ARRIVAL_DATE.name());
        constants.put(Constants.PARAMETER_SUGGESTED_DESTINATIONS, EventParameter.SUGGESTED_DESTINATIONS.name());
        constants.put(Constants.PARAMETER_TRAVEL_START, EventParameter.TRAVEL_START.name());
        constants.put(Constants.PARAMETER_TRAVEL_END, EventParameter.TRAVEL_END.name());
        constants.put(Constants.PARAMETER_NUM_ADULTS, EventParameter.NUM_ADULTS.name());
        constants.put(Constants.PARAMETER_NUM_CHILDREN, EventParameter.NUM_CHILDREN.name());
        constants.put(Constants.PARAMETER_NUM_INFANTS, EventParameter.NUM_INFANTS.name());
        constants.put(Constants.PARAMETER_SUGGESTED_HOTELS, EventParameter.SUGGESTED_HOTELS.name());
        constants.put(Constants.PARAMETER_USER_SCORE, EventParameter.USER_SCORE.name());
        constants.put(Constants.PARAMETER_HOTEL_SCORE, EventParameter.HOTEL_SCORE.name());
        constants.put(Constants.PARAMETER_PURCHASE_CURRENCY, EventParameter.PURCHASE_CURRENCY.name());
        constants.put(Constants.PARAMETER_PREFERRED_STAR_RATINGS, EventParameter.PREFERRED_STAR_RATINGS.name());
        constants.put(Constants.PARAMETER_PREFERRED_PRICE_RANGE, EventParameter.PREFERRED_PRICE_RANGE.name());
        constants.put(Constants.PARAMETER_PREFERRED_NEIGHBORHOODS, EventParameter.PREFERRED_NEIGHBORHOODS.name());
        constants.put(Constants.PARAMETER_PREFERRED_NUM_STOPS, EventParameter.PREFERRED_NUM_STOPS.name());
        constants.put(Constants.PARAMETER_CONTENT, EventParameter.CONTENT.name());
        constants.put(Constants.PARAMETER_PARAM_1, EventParameter.PARAM_1.name());
        constants.put(Constants.PARAMETER_PARAM_2, EventParameter.PARAM_2.name());
        constants.put(Constants.PARAMETER_PARAM_3, EventParameter.PARAM_3.name());
        constants.put(Constants.PARAMETER_PARAM_4, EventParameter.PARAM_4.name());
        constants.put(Constants.PARAMETER_PARAM_5, EventParameter.PARAM_5.name());
        constants.put(Constants.PARAMETER_PARAM_6, EventParameter.PARAM_6.name());
        constants.put(Constants.PARAMETER_PARAM_7, EventParameter.PARAM_7.name());
        constants.put(Constants.PARAMETER_PARAM_8, EventParameter.PARAM_8.name());
        constants.put(Constants.PARAMETER_PARAM_9, EventParameter.PARAM_9.name());
        constants.put(Constants.PARAMETER_PARAM_10, EventParameter.PARAM_10.name());

        return constants;
    }
}
