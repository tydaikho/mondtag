package de.kah2.mondtag.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import de.kah2.libZodiac.Day;
import de.kah2.mondtag.Mondtag;
import de.kah2.mondtag.R;

/**
 * This is a {@link android.app.DialogFragment} to show daily information more verbose than the
 * {@link android.support.v7.widget.CardView}s of {@link CalendarFragment}.
 */
public class DayDetailFragment extends android.app.DialogFragment {

    private final static String TAG = DayDetailFragment.class.getSimpleName();

    private final static String BUNDLE_KEY_DATE =
            DayDetailFragment.class.getName() + ".Day";

    private Day day;

    public DayDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_day_detail, container, false);

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreateView: loading savedInstanceState");

            final LocalDate date = LocalDate.parse( savedInstanceState.getString(BUNDLE_KEY_DATE) );

            this.day = ((Mondtag) getActivity().getApplicationContext()).getDataManager()
                    .getCalendar().get(date);
        }

        DayDataDisplayer viewHolder = new DayDataDisplayer(view);
        viewHolder.setDayData(this.day, true);
        this.setLunarRiseSetDescriptions(view);

        final Button reminderButton = view.findViewById(R.id.buttonCreateReminder);
        reminderButton.setOnClickListener(v -> {
            Log.d(TAG, "reminderButton clicked");

            CalendarEvent event = new CalendarEvent(
                    getActivity().getApplicationContext(), DayDetailFragment.this.day);
            DayDetailFragment.this.startActivity( event.toIntent() );
        });

        return view;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    private void setLunarRiseSetDescriptions(final View view) {
        final LocalDateTime rise = this.day.getPlanetaryData().getLunarRiseSet().getRise();
        final LocalDateTime set = this.day.getPlanetaryData().getLunarRiseSet().getSet();

        final TextView lunarRSFirstDescriptionTextView = view.findViewById(R.id.lunarRiseSetFirstDescription);
        final TextView lunarRSecondDescriptionTextView = view.findViewById(R.id.lunarRiseSetSecondDescription);

        if ( !day.getDate().isEqual(LocalDate.from(rise)) ) {

            // No rise today
            lunarRSFirstDescriptionTextView.setText(R.string.description_lunar_set);
            lunarRSecondDescriptionTextView.setText("");

        } else if (!day.getDate().isEqual(LocalDate.from(set))) {

            // No set today
            lunarRSecondDescriptionTextView.setText("");

        } else if (rise.isAfter(set)) {

            // Rise is after set
            lunarRSFirstDescriptionTextView.setText(R.string.description_lunar_set);
            lunarRSecondDescriptionTextView.setText(R.string.description_lunar_rise);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(BUNDLE_KEY_DATE, this.day.getDate().toString());

        super.onSaveInstanceState(outState);
    }
}
