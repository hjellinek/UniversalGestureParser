package com.apprture.universalgestureparser;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

/**
 * Simple Universal Gesture Parser demo app.
 *
 * @author Herb Jellinek
 */
public class MainActivity extends ActionBarActivity {

    /*
     * Debug tag.
     */
    private static final String TAG = "MainActivity";

    private GestureView mGestureView;

    private TextView mGestureLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGestureLabel = (TextView)findViewById(R.id.gesture_label);

        GestureListener gestureListener = new GestureListener() {
            @Override
            public void gesture(GestureType type, float startX, float startY, float endX, float endY,
                                List<FPoint> points) {
                mGestureLabel.setText(type.toString());
                mGestureView.clearPath();
                mGestureView.addPoints(points);
                mGestureView.invalidate();
            }

            @Override
            public void points(List<FPoint> points) {
                mGestureView.addPoints(points);
                mGestureView.invalidate();
            }
        };

        // This is where we hook in the gesture parser
        final GestureParser listener = new GestureParser(gestureListener);
        mGestureView = (GestureView)findViewById(R.id.gesture_view);
        listener.listenToView(mGestureView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
