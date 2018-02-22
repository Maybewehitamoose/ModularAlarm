package com.darylkurtz.modularalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.provider.AlarmClock;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

// ModularAlarmAppWidgetProvider is for the main widget component.

public class ModularAlarmAppWidgetProvider extends AppWidgetProvider {
    //Declare intents and actions for broadcast for button functionality
    private static final String TAP_INTENT1 = "com.darylkurtz.modularalarm.BUTTON1_ACTION";
    private final Intent tapIntent1 = new Intent( TAP_INTENT1 );

    private static final String TAP_INTENT2 = "com.darylkurtz.modularalarm.BUTTON2_ACTION";
    private final Intent tapIntent2 = new Intent( TAP_INTENT2 );

    //Main Override for AppWidget interaction
    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
        // Call the intents for the button actions
        Intent button3Intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);

        PendingIntent pendingButton1Intent = PendingIntent.getBroadcast(context, 0, tapIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingButton2Intent = PendingIntent.getBroadcast(context, 0, tapIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingButton3Intent = PendingIntent.getActivity(context, 0, button3Intent, 0);


        // String to define the next scheduled alarm
        String nextAlarm = getNextAlarmString( context );

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget_layout );
        views.setTextViewText( R.id.alarm_text, nextAlarm );

        // onClick events for the buttons
        views.setOnClickPendingIntent( R.id.button1, pendingButton1Intent );
        views.setOnClickPendingIntent( R.id.button2, pendingButton2Intent );
        views.setOnClickPendingIntent( R.id.button3, pendingButton3Intent );

        // There may be multiple widgets active, so update all of them
        for ( int appWidgetId : appWidgetIds ) {
            appWidgetManager.updateAppWidget( appWidgetId, views );
        }
    }

    //onReceive override for broadcasts
    @Override
    public void onReceive( Context context, Intent intent )
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );
        ComponentName thisAppWidget = new ComponentName( context.getPackageName(), getClass().getName() );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds( thisAppWidget );

        onUpdate( context, appWidgetManager, appWidgetIds );

        // TODO (3) Update functions below after settings panel is implemented to allow for custom alarm variable
        //Determine current time and add two hours, then set alarm
        if ( TAP_INTENT1.equals( intent.getAction() ) ) {
            Integer plusTwoHours;

            //Make sure hour returned is not greater than 24
            if (getCurrentHour() + 2 >= 24) {
                plusTwoHours = getCurrentHour() - 22;
            } else {
                plusTwoHours = getCurrentHour() + 2;
            }

            Integer carryTheMinutes = getCurrentMinutes();
            Intent setAlarm1Hour = new Intent(AlarmClock.ACTION_SET_ALARM);
            setAlarm1Hour.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            setAlarm1Hour.putExtra(AlarmClock.EXTRA_HOUR, plusTwoHours);
            setAlarm1Hour.putExtra(AlarmClock.EXTRA_MINUTES, carryTheMinutes);
            context.startActivity(setAlarm1Hour);
        }

        //Determine current time and add four hours, then set alarm
        if ( TAP_INTENT2.equals( intent.getAction() ) ) {
            Integer plusFourHours;

            //Make sure hour returned is not greater than 24
            if (getCurrentHour() + 4 >= 24) {
                plusFourHours = getCurrentHour() - 20;
            } else {
                plusFourHours = getCurrentHour() + 4;
            }

            Integer carryTheMinutes = getCurrentMinutes();
            Intent setAlarm1Hour = new Intent(AlarmClock.ACTION_SET_ALARM);
            setAlarm1Hour.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            setAlarm1Hour.putExtra(AlarmClock.EXTRA_HOUR, plusFourHours);
            setAlarm1Hour.putExtra(AlarmClock.EXTRA_MINUTES, carryTheMinutes);
            context.startActivity(setAlarm1Hour);
        }

    }

    //Get the current hour from Calendar
    private static Integer getCurrentHour() {
        Integer currentHour;
        currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return currentHour;
    }

    //Get the current minutes from Calendar
    private static Integer getCurrentMinutes() {
        Integer currentMinutes;
        currentMinutes = Calendar.getInstance().get(Calendar.MINUTE);
        return currentMinutes;
    }

    // Determine the closest set alarm, if no alarm is set display a blank time.
    private static String getNextAlarmString( Context context )
    {
        String res = null;
        AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );

        AlarmManager.AlarmClockInfo nextAlarmClock = am.getNextAlarmClock();
        if ( nextAlarmClock != null ) {
            // Format alarm time as e.g. "Di. 06:30"
            res = DateFormat.format("EEE HH:mm", nextAlarmClock.getTriggerTime() ).toString();
        }

        if ( ( res == null ) || res.isEmpty() ) {
            res = context.getString( R.string.alarm_time );
        }

        return res;
    }

}
