package barqsoft.footballscores.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by kody.day on 10/22/2015.
 */
public class ListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Cursor cursor;

    public ListWidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long token = Binder.clearCallingIdentity();
        try {
            // 2015-10-23 has games if test data is needed
            String[] args = new String[]{new SimpleDateFormat("yyyy-MM-dd").format(new Date())};

            cursor = context.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null, null, args, null);

            if(cursor != null && cursor.getCount() == 0) {
                cursor = null;
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    @Override
    public void onDestroy() {
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 1;
    }

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_MATCHTIME = 2;

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.scores_widget_list_item);
        if(cursor != null) {
            remoteView.setViewVisibility(R.id.container, View.VISIBLE);
            remoteView.setViewVisibility(R.id.no_games_label, View.INVISIBLE);
            cursor.moveToPosition(position);
            remoteView.setTextViewText(R.id.home_name, cursor.getString(COL_HOME));
            remoteView.setTextViewText(R.id.away_name, cursor.getString(COL_AWAY));
            remoteView.setTextViewText(R.id.score_textview, Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
            remoteView.setTextViewText(R.id.data_textview, cursor.getString(COL_MATCHTIME));
            remoteView.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    cursor.getString(COL_HOME)));
            remoteView.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    cursor.getString(COL_AWAY)));
            return remoteView;
        } else {
            remoteView.setViewVisibility(R.id.container, View.INVISIBLE);
            remoteView.setViewVisibility(R.id.no_games_label, View.VISIBLE);
            return remoteView;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
