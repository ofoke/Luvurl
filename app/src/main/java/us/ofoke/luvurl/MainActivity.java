package us.ofoke.luvurl;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends Activity {

    public static final String TAG = "Lurl";
    private DatabaseReference mRef;
    private DatabaseReference lRef;
    private DatabaseReference nRef;
    private DatabaseReference recyclerRef;
    private Query recRef;
    private RecyclerView mLinks;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Lurl, LinkHolder> mRecyclerViewAdapter;

    private WebView myWebView;

    private BottomSheetBehavior<View> behavior;
    private BottomSheetDialog dialog;
    private CoordinatorLayout coordinatorLayout;

    private ImageButton luvRaterBtn;
    private ImageButton noLuvRaterBtn;
    private TextView luvRateTV;
    private TextView noLuvRateTV;
    private ImageButton hotBtn;
    private ImageButton shareBtn;

    private int luvrater;
    private int noluvrater;
    private Long luvRatingCum;
    private Long noLuvRatingCum;
    private Map<String, Long> ratingsCum;
    private String key;

    private String url;
    private ChildEventListener ceListen;
    private ValueEventListener veListen;
    private ValueEventListener uiListen;

    private Lurl lurl = null;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (android.widget.Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        luvRaterBtn = (ImageButton) findViewById(R.id.bsheet_luv);
        noLuvRaterBtn = (ImageButton) findViewById(R.id.bsheet_noluv);

        luvRateTV = (TextView) findViewById(R.id.textview_luv);
        noLuvRateTV = (TextView) findViewById(R.id.textview_noluv);

        hotBtn = (ImageButton) findViewById(R.id.bsheet_hot);
        shareBtn = (ImageButton) findViewById(R.id.bsheet_share);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        mRef = FirebaseDatabase.getInstance().getReference();
        recyclerRef = mRef.child("lurls");
        recRef = recyclerRef.orderByChild("luvRating").limitToLast(25);

        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                urlInFB(url);
            }
        });

        myWebView.loadUrl("https://www.google.com");

        mLinks = (RecyclerView) findViewById(R.id.rview);
        mLinks.setHasFixedSize(true);
        mLinks.setLayoutManager(new LinearLayoutManager(this));
        //mManager = new LinearLayoutManager(this);
        // mManager.setReverseLayout(false);
        //mLinks.setLayoutManager(mManager);

        queryRunner(recRef, true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        initBottomSheet();
    }

    public void queryRunner(Query q, boolean b) {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.cleanup();
        }

        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<Lurl, LinkHolder>(Lurl.class, android.R.layout.two_line_list_item, LinkHolder.class, recRef) {
            @Override
            protected void populateViewHolder(final LinkHolder viewHolder, final Lurl model, final int position) {
                viewHolder.setUrl(model.getUrl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myWebView.loadUrl(model.getUrl());
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
            }
        };

        mLinks.setAdapter(mRecyclerViewAdapter);

        //descending sort hack
        if (b) {
            LinearLayoutManager llm = (LinearLayoutManager) mLinks.getLayoutManager();
            llm.setReverseLayout(true);
            llm.setStackFromEnd(true);
        }
    }

    private void initBottomSheet() {
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
/*        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });*/
    }

    public void hotBottomDrawer(View view) {
        if (behavior.getState() == 3) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (behavior.getState() == 4) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void shareUrl(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, myWebView.getUrl());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, ""));
    }

    public void urlInFB(String rawUrl) {
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

            //check to see if url exists in FB
            veListen = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean urlExists = dataSnapshot.exists();

                    if (urlExists) {
                        Map<String, Object> rawkey = (Map<String, Object>) dataSnapshot.getValue();
                        key = (String) rawkey.keySet().toArray()[0];

                        lRef = mRef.child("lurls").child(key).child("luvRating");
                        nRef = mRef.child("lurls").child(key).child("noLuvRating");
                        uiRatingCounter(lRef, nRef);
                    } else {
                        luvRateTV.setText("0");
                        noLuvRateTV.setText("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mRef.child("lurls").orderByChild("url").equalTo(url).addListenerForSingleValueEvent(veListen);
        }
    }

    public void uiRatingCounter(DatabaseReference lRef, DatabaseReference nRef) {
        lRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long luV = (Long) dataSnapshot.getValue();
                luvRateTV.setText(luV.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        nRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long noLuV = (Long) dataSnapshot.getValue();
                noLuvRateTV.setText(noLuV.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void rater(final int luvrater, int noluvrater) {
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

            final long ts = System.currentTimeMillis();
            lurl = new Lurl(luvrater, noluvrater, ts, url);

            //check to see if url has been rated
            veListen = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean prevRated = dataSnapshot.exists();

                    if (prevRated) {
                        //get key cuz getKey() doesn't work here
                        Map<String, Object> rawkey = (Map<String, Object>) dataSnapshot.getValue();
                        key = (String) rawkey.keySet().toArray()[0];

                        //get luvrating and noluvrating
                        ratingsCum = (Map<String, Long>) rawkey.values().toArray()[0];

                        if (luvrater == 1) {
                            luvRatingCum = ratingsCum.get("luvRating");
                            lRef = mRef.child("lurls").child(key).child("luvRating");
                            transIncrement(lRef, luvRatingCum);
                        } else {
                            noLuvRatingCum = ratingsCum.get("noLuvRating");
                            nRef = mRef.child("lurls").child(key).child("noLuvRating");
                            transIncrement(nRef, noLuvRatingCum);
                        }

                    } else {
                        //create it
                        String key = mRef.child("lurls").push().getKey();
                        mRef.child("lurls").child(key).setValue(lurl);

                        //bind to ui
                        lRef = mRef.child("lurls").child(key).child("luvRating");
                        nRef = mRef.child("lurls").child(key).child("noLuvRating");
                        uiRatingCounter(lRef, nRef);

                        //update list
                        RadioGroup rgluv = (RadioGroup) findViewById(R.id.luvgroup);
                        int rgl = rgluv.getCheckedRadioButtonId();
                        RadioButton rbluv = (RadioButton) findViewById(rgl);
                        String rbluvtext = (String) rbluv.getText();

                        if (rbluvtext.contains("no")) {
                            recRef = mRef.child("lurls").orderByChild("noLuvRating").limitToLast(25);
                            queryRunner(recRef, true);
                        } else {
                            recRef = mRef.child("lurls").orderByChild("luvRating").limitToLast(25);
                            queryRunner(recRef, true);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mRef.child("lurls").orderByChild("url").equalTo(url).addListenerForSingleValueEvent(veListen);
        }
    }


    public void transIncrement(DatabaseReference oRef, Long mData) {
        //write it via transaction
        oRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mData) {

                if (mData.getValue() != null) {
                    Long theValue = (Long) mData.getValue();
                    theValue++;
                    mData.setValue(theValue);
                }
                return Transaction.success(mData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
            case R.id.search:
                myWebView.loadUrl("https://www.google.com");
                return true;
            case R.id.refresh:
                myWebView.reload();
                return true;
            case R.id.go_back:
                myWebView.goBack();
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
                    queryConfig();
                    break;
            case R.id.radio_time_day:
                if (checked)
                    queryConfig();
                    break;
            case R.id.radio_time_week:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_time_4ever:
                if (checked)
                    queryConfig();
                    break;
        }
    }*/

    public void onLuvRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_luv_yes:
                if (checked)
                    recRef = mRef.child("lurls").orderByChild("luvRating").limitToLast(50);
                queryRunner(recRef, true);
                // queryConfig();
                break;
            case R.id.radio_luv_no:
                if (checked)
                    recRef = mRef.child("lurls").orderByChild("noLuvRating").limitToLast(50);
                queryRunner(recRef, true);
                // queryConfig();
                break;
        }
    }

/*

    public void queryConfig() {
        RadioGroup rgtime = (RadioGroup) findViewById(R.id.timegroup);
        int rgt = rgtime.getCheckedRadioButtonId();
        RadioButton rbtime = (RadioButton) findViewById(rgt);
        String rbtimetext = (String) rbtime.getText();

        RadioGroup rgluv = (RadioGroup) findViewById(R.id.luvgroup);
        int rgl = rgluv.getCheckedRadioButtonId();
        RadioButton rbluv = (RadioButton) findViewById(rgl);
        String rbluvtext = (String) rbluv.getText();

        if (rbluvtext.contains("love") & rbtimetext.contains("forever")) {
            recRef = mRef.child("lurls").orderByChild("luvRating").limitToLast(25);
            queryRunner(recRef, true);
        } else if (rbluvtext.contains("love") & rbtimetext.contains("day")) {
            Long timeback = System.currentTimeMillis() - (TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS));
            Log.v("ok", timeback.toString());
            recRef = mRef.child("lurls").orderByChild("timestamp").startAt(timeback);


            queryRunner(recRef, true);
        } else if (rbluvtext.contains("love") & rbtimetext.contains("hour")) {
            Long timeback = System.currentTimeMillis() - (1 / 24 * TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
            recRef = mRef.child("lurls").orderByChild("luvRating").limitToLast(25).startAt(timeback, "timestamp");
            queryRunner(recRef, true);
        }
    }
*/

    public static class LinkHolder extends RecyclerView.ViewHolder {
        View mView;

        public LinkHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUrl(String text) {
            //TextView field = (TextView) mView.findViewById(R.id.link);
            TextView field = (TextView) mView.findViewById(android.R.id.text1);
            field.setText(text);
        }
    }
}
