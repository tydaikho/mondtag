package de.kah2.mondtag.calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.threeten.bp.LocalDate;

import java.util.LinkedList;

import de.kah2.libZodiac.Calendar;
import de.kah2.libZodiac.Day;
import de.kah2.mondtag.Mondtag;
import de.kah2.mondtag.R;

/**
 * This {@link Fragment} is used to display the calendar.
 */
public class CalendarFragment extends Fragment {

    public final static String TAG = CalendarFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private CalendarRecyclerViewAdapter calendarRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.recyclerView.setLayoutManager( this.createLayoutManager() );

        this.calendarRecyclerViewAdapter = new CalendarRecyclerViewAdapter();
        recyclerView.setAdapter(calendarRecyclerViewAdapter);
        this.calendarRecyclerViewAdapter.setDayClickListener( this.createDayClickListener() );

        updateCalendar();

        return view;
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    private CalendarRecyclerViewAdapter.DayClickListener createDayClickListener() {
        return new CalendarRecyclerViewAdapter.DayClickListener() {

            @Override
            public void onShortClick(Day day) {
                Log.d(TAG, "onShortClick: " + day.getDate());

                DayDetailFragment detailFragment = new DayDetailFragment();
                detailFragment.setDay(day);
                detailFragment.show(
                        getFragmentManager(), DayDetailFragment.class.getSimpleName() );
            }

            @Override
            public boolean onLongClick(Day day) {
                Log.d(TAG, "onLongClick: " + day.getDate());

                final CalendarEvent event = new CalendarEvent(
                        CalendarFragment.this.getActivity().getApplicationContext(), day );
                CalendarFragment.this.startActivity( event.toIntent() );
                return true;
            }
        };
    }

    private void updateCalendar() {
        final Calendar calendar =
                ((Mondtag) getActivity().getApplicationContext()).getDataManager().getCalendar();

        if (calendar != null) {

            final LinkedList<Day> days = calendar.getValidDays();
            this.calendarRecyclerViewAdapter.setDays(days);

            LinearLayoutManager layoutManager =
                    (LinearLayoutManager) this.recyclerView.getLayoutManager();

            int count = 0;
            final LocalDate today = LocalDate.now();

            for (Day day : days) {
                if ( day.getDate().isEqual(today) ) break;
                else count++;
            }

            layoutManager.scrollToPositionWithOffset(count, 25);
        }
    }
}
