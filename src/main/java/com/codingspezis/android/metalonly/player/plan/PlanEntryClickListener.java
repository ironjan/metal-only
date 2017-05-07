package com.codingspezis.android.metalonly.player.plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.codingspezis.android.metalonly.player.PlanActivity;
import com.codingspezis.android.metalonly.player.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ClickListener for entries in the plan shown by {@link PlanActivity}
 */
public final class PlanEntryClickListener implements DialogInterface.OnClickListener {

    private static final int ADD_TO_CALENDAR_ACTION = 0;
    private static final int SHARE_ACTION = 1;

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("dd.MM.yy");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT_DATE_DAY = new SimpleDateFormat("dd");

    private final PlanData data;
    private Context context;

    public PlanEntryClickListener(PlanData data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case ADD_TO_CALENDAR_ACTION:
                addEntryToCalendar(data);
                break;
            case SHARE_ACTION:
                shareEntry(data);
                break;
        }
    }

    private void addEntryToCalendar(final PlanData data) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        String description = data.getMod() + "\n" + data.getGenre();

        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", data.getTitle());
        intent.putExtra("description", description);
        intent.putExtra("beginTime", data.getStartTimeAsMillis());
        intent.putExtra("endTime", data.getEndTimeAsMillis());

        context.startActivity(intent);
    }

    private void shareEntry(final PlanData data) {
        String message = getDateString(data) + " " + getTimeString(data) + "\n" + data.getTitle()
                + "\n" + data.getMod() + "\n" + data.getGenre();

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(share,
                context.getResources().getStringArray(R.array.plan_options_array)[1]));
    }

    public CharSequence getDateString(PlanData pd) {
        CharSequence ret;
        if (pd.getStart().get(Calendar.DAY_OF_WEEK) == pd.getEnd().get(Calendar.DAY_OF_WEEK)
                || pd.getEnd().get(Calendar.HOUR_OF_DAY) == 0) {
            ret = DATE_FORMAT_DATE.format(pd.getStart().getTime());
        } else {
            ret = DATE_FORMAT_DATE_DAY.format(pd.getStart().getTime()) + "/"
                    + DATE_FORMAT_DATE.format(pd.getEnd().getTime());
        }

        return ret;
    }


    public CharSequence getTimeString(PlanData pd) {
        return DATE_FORMAT_TIME.format(pd.getStart().getTime()) + " - "
                + DATE_FORMAT_TIME.format(pd.getEnd().getTime());
    }
}