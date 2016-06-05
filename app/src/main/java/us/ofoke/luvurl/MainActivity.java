package us.ofoke.luvurl;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
//public class MainActivity extends Activity {

    public static final String TAG = "Link";
    private DatabaseReference mRef;
    private RecyclerView mLinks;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Link, LinkHolder> mRecyclerViewAdapter;

    private WebView myWebView;

    private FABToolbarLayout layout;
    private View fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        fab = findViewById(R.id.fabtoolbar_fab);

        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://www.google.com");




      //  mLinks = (RecyclerView) findViewById(R.id.rview);
//        mLinks.setHasFixedSize(true);
//
//        mManager = new LinearLayoutManager(this);
//        mManager.setReverseLayout(false);
//
//        mLinks.setLayoutManager(mManager);

        //THIS IS TEMP
//        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Link, LinkHolder>(Link.class, R.layout.link, LinkHolder.class, mRef) {
//            @Override
//            protected void populateViewHolder(LinkHolder viewHolder, Link model, final int position) {
//                viewHolder.setUrl(model.getUrl());
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mRecyclerViewAdapter.getRef(position);
//                        Log.w(TAG, "You clicked on "+position);
//                    }
//                });
//            }
//            //android.R.layout.two_line_list_item
//        };
//
//        mLinks.setAdapter(mRecyclerViewAdapter);
    }

    public void oh(View view){
        String bob = myWebView.getUrl();
        Log.v("bob", bob);
    }

    public WebView getMyWebView() {
        return myWebView;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.cleanup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_search:
              // openCustomTab();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


/*    public void onTimeRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_time_hour:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_time_day:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.radio_time_week:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_time_4ever:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }*/

/*    public void onLuvRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_luv_yes:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_luv_no:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }*/


    public static class LinkHolder extends RecyclerView.ViewHolder {
        View mView;

        public LinkHolder(View itemView) {
            super(itemView);
            mView = itemView;


        }

//        public void setName(String name) {
//            TextView field = (TextView) mView.findViewById(R.id.name_text);
//            field.setText(name);
//        }

        public void setUrl(String text) {
            TextView field = (TextView) mView.findViewById(R.id.link);
            field.setText(text);
        }
    }
}
