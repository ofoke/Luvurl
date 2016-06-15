package us.ofoke.luvurl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    public static final String TAG = "Lurl";
    private DatabaseReference mRef;
    private RecyclerView mLinks;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Lurl, LinkHolder> mRecyclerViewAdapter;

    private WebView myWebView;

    private BottomSheetBehavior<View> behavior;
    private BottomSheetDialog dialog;
    private CoordinatorLayout coordinatorLayout;

    private ImageButton luvRaterBtn;
    private ImageButton noLuvRaterBtn;

    private int luvrater;
    private int noluvrater;
    String url;

    Lurl lurl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (android.widget.Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        luvRaterBtn = (ImageButton) findViewById(R.id.bsheet_luv);
        noLuvRaterBtn = (ImageButton) findViewById(R.id.bsheet_noluv);

        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://www.google.com");

        mRef = FirebaseDatabase.getInstance().getReference();

        mLinks = (RecyclerView) findViewById(R.id.rview);
        mLinks.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mLinks.setLayoutManager(mManager);

        // THIS IS TEMP
//        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Lurl, LinkHolder>(Lurl.class, R.layout.link, LinkHolder.class, mRef) {
//            @Override
//            protected void populateViewHolder(LinkHolder viewHolder, Lurl model, final int position) {
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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        initBottomSheet();
    }


    private void initBottomSheet() {
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
    }


    public void rater_luv(View view) {
        luvrater = 1;
        noluvrater = 0;
        rater(luvrater, noluvrater);
    }

    public void rater_noluv(View view) {
        luvrater = 0;
        noluvrater = 1;
        rater(luvrater, noluvrater);
    }

    public void rater(int luvrater, int noluvrater) {
        lurl = null;

        String rawUrl = myWebView.getUrl();

        if (null != rawUrl && rawUrl.length() > 0) {
            if (rawUrl.contains("?")) {
                //url cleanup - remove everything after ?
                StringBuilder str = new StringBuilder(rawUrl);
                int qMarkStart = str.indexOf("?");
                int qMarkEnd = str.length();
                url = str.delete(qMarkStart, qMarkEnd).toString();
            } else {
                url = rawUrl;
            }

            //add them all
            long ts = System.currentTimeMillis();
            lurl = new Lurl(luvrater, noluvrater, ts, url);
            mRef.child("lurls").push().setValue(lurl);

            //find the max rating
            mRef.child("lurls").limitToLast(1).orderByChild("luvrating").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    lurl = dataSnapshot.getValue(Lurl.class);
                    int newluvrating = lurl.getLuvRating() + 1;
                    int oldnoluvrating = lurl.getNoLuvRating();
                    if(newluvrating > 2){
                        long ts = System.currentTimeMillis();
                        lurl = new Lurl(newluvrating, oldnoluvrating, ts, url);
                        mRef.child("lurls").push().setValue(lurl);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            //query for existence of url
//            mRef.child("lurls").orderByChild("url").equalTo(url).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    lurl = dataSnapshot.getValue(Lurl.class);
//                    String bob = lurl.getUrl();
//                    Log.v("bob", bob);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                Log.v("larry", "larry");
//                }
//            });

            //if url exists - increment appropriate count, else - add new url

//            if(lurl != null){
//                long ts = System.currentTimeMillis();
//                lurl.setTimestamp(ts);
//
//              //  int incrementedLuvRating = lurl.getLuvRating() + 1;
//               // lurl.setLuvrating(incrementedLuvRating);
//
//                String bob = lurl.getUrl();
//                mRef.child("lurls").child(bob).setValue(lurl);
//            } else {
//                long ts = System.currentTimeMillis();
//                lurl = new Lurl(luvrater, noluvrater, ts, url);
//                mRef.child("lurls").push().setValue(lurl);
//            }



//            String key = mRef.child("lurls").push().getKey();
//            mRef.child("lurls").child(key).setValue(lurl);
        }


    }

    public void oh(View view) {
        String bob = myWebView.getUrl();
        Log.v("bob", bob);

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

/*    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

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
            case R.id.go_back:
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
