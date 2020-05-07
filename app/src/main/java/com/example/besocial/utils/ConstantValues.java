package com.example.besocial.utils;

public interface ConstantValues {
    String EVENTS = "Events";
    String USERS = "users";
    String EVENTS_WITH_ATTENDINGS = "EventsWithAttending";
    String USERS_ATTENDING_TO_EVENTS = "UsersAttendingToEvents";
    String BENEFITS = "Benefits";
    String USERS_LIST = "UsersList";

    //categories
    String CATEGORY= "eventCategory";
    String GENERAL= "General";
    String HELP_ME= "Help Me!";
    String CHATS="Chats";
    String CHAT_MESSAGES="Chat Messages";

    //events
    String EVENT_ID = "eventId";
    String EVENT_TITLE= "title";
    String EVENT_HOST_UID = "eventCreatorUid";
    String BEGIN_DATE = "beginDate";
    String BEGIN_TIME = "beginTime";
    String FINISH_DATE = "finishDate";
    String FINISH_TIME = "finishTime";
    String IS_CHECKED_IN = "isCheckedIn";

    //user levels
    String USER_LEVEL_1 = "Shy Socializer";
    String USER_LEVEL_2 = "Out Of The Shell Socializer";
    String USER_LEVEL_3 = "Academic Socializer";
    String USER_LEVEL_4 = "Socialized Ninja Turtle";
    String USER_LEVEL_5 = "Socialozaurus";

    //times in milliseconds
    int SECOND= 1000;
    int MINUTE= 60*1000;
    int HOUR= 3600*1000;

    String IS_HELP_EVENT = "isHelpEvent";
    float GEOFENCE_RADIUS = 70;
}