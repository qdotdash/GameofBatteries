package com.qdotdash.gameofbatteries;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.michaldrabik.tapbarmenulib.TapBarMenu;

;



public class MainActivity extends AppCompatActivity{

    private TapBarMenu tapBarMenu;

    //public variable declarations
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////shared preference
    ///////////////////////////////textview
    private TextView scoretext,score,hours;
    ///////////////////////////////layouts
    private RelativeLayout scorelayout;
    private FloatingActionButton touchexpand;
    //private LinearLayout Dots_layout;
    private FrameLayout bgp;
    private ImageButton share,about;
    ///////////////////////////////Buttons
    private Button play_quit,htp;
    ///////////////////////////////ImageViews
    //private ImageView[] dots;
    ///////////////////////////////handler
    private Handler pausetosettext = new Handler();
    private boolean firsttime;
    long delay = 60;
    String achievementmessage="Rate according to your last usage";
    Context context;
    boolean nd;
    TypeWriter scoremessage;

    //private Animation animator1,animator2,animator3,animator4;

    int dc;
    Dialog d1,d3;
    //RelativeLayout cloud1,cloud2;
    private MediaPlayer buttonclick1,buttonclick2;
    private ImageView sound;
    boolean soundcheck;
    boolean ft;
    boolean bouncescorestate=false;
    int besttextcolor,scoretextcolor;

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    ///////////////////////On create starts
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d3 = new Dialog(this);
        context = MainActivity.this;
        //Fullscreen make

        //////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////


        setContentView(R.layout.activity_main);
        tapBarMenu = findViewById(R.id.tapBarMenu);
        //sharedpreference
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        final SharedPreferences sp = getSharedPreferences("AGOB",0);
        final SharedPreferences.Editor editor = sp.edit();

        firsttime = sp.getBoolean("firsttime",true);
        /////////////////////////////////////////////////////////////////////////////Emergency Termination Solution
        if(!isServiceRunning()&&setboolean()){
            editor.putBoolean("ifpause",false);
            editor.apply();
            dc = R.drawable.customtoastemergency;
            toastshow("App was closed unexpectedly",dc,1);
       }
       if(sp.getBoolean("initfinalsamelevel",false)) {
              dc = R.drawable.customtoastemergency;
              toastshow("Initial and final percentages are same", dc,2);
              editor.putBoolean("initfinalsamelevel", false);
              editor.apply();
        }

        /////////////////////////////////////////////////////////////////////////////high score,level up frag display
        nd = sp.getBoolean("newdata", false);
        if(nd){
            dc=R.drawable.customtoastachievement;
            toastshow(achievementmessage,dc,3);
        }
        editor.putBoolean("scorenotification",true);
        editor.apply();

        //////////////////////////////////////////////////////////////Spotlight
        if(sp.getBoolean("ft",true)) {
           Intent startuphelp = new Intent(MainActivity.this,help.class);
           startActivity(startuphelp);
           editor.putBoolean("ft",false);
           editor.apply();
        }
        //////////////////////////////////////////////////////////////////////////////////

        //registering receiver
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        registerReceiver(incoming_values_set,new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////





        //View Initialising
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////Buttons
        play_quit = findViewById(R.id.start_stop);
        htp = findViewById(R.id.htp);
        //////////////////////////////TextViews
        scoretext = findViewById(R.id.latest);
        score = findViewById(R.id.latestscore);
        hours = findViewById(R.id.hours);
       scoremessage = findViewById(R.id.scoremessage);
        /////////////////////////////////////////////layouts
        scorelayout = findViewById(R.id.scorelayout);
        touchexpand = findViewById(R.id.touch_expand);
        bgp = findViewById(R.id.bgp);
        share = findViewById(R.id.share);
        about = findViewById(R.id.about);
        sound = findViewById(R.id.item2);

        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////

        //Font initialising
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Walkway_Black.ttf");
        Typeface gamefont = Typeface.createFromAsset(getAssets(), "fonts/Minecrafter.Alt.ttf");

        ////////////////////////////setting font
        scoretext.setTypeface(gamefont);
        score.setTypeface(custom_font);
        hours.setTypeface(custom_font);
        play_quit.setTypeface(custom_font);
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        buttonclick1 = MediaPlayer.create(getApplicationContext(),R.raw.buttonclicksound1);
        buttonclick2 = MediaPlayer.create(getApplicationContext(),R.raw.buttonclicksound2);

        soundcheck=sp.getBoolean("soundcheck",true);
        if(soundcheck)
            sound.setImageResource(R.drawable.soundon);
        else
            sound.setImageResource(R.drawable.soundoff);

        //Animation
        /////////////////////////////////////////////////////////////////////////////////color animation
        int colorFrom1 = getResources().getColor(R.color.transitionbuttonclick1);
        int colorTo1 = getResources().getColor(R.color.transitionbuttonclick2);

        int colorFrom2 = getResources().getColor(R.color.scorestextcolor1);
        int colorTo2 = getResources().getColor(R.color.scorestextcolor2);

        int colorFrom3 = getResources().getColor(R.color.besttextcolor1);
        int colorTo3 = getResources().getColor(R.color.besttextcolor2);

        final ValueAnimator colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom1, colorTo1);
        colorAnimation1.setDuration(800); // milliseconds
        colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                bgp.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        //////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////
        final ValueAnimator colorAnimation3 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom2, colorTo2);
        colorAnimation3.setDuration(800); // milliseconds
        colorAnimation3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                scoretext.setTextColor((int) animator.getAnimatedValue());
                score.setTextColor((int) animator.getAnimatedValue());
                hours.setTextColor((int) animator.getAnimatedValue());
                scoremessage.setTextColor((int) animator.getAnimatedValue());

            }

        });
        //////////////////////////////////////////////////////////////////////////////////////////
        final ValueAnimator colorAnimation4 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom3, colorTo3);
        colorAnimation4.setDuration(800); // milliseconds
        colorAnimation4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                    scoretext.setTextColor((int) animator.getAnimatedValue());
                    score.setTextColor((int) animator.getAnimatedValue());
                    hours.setTextColor((int) animator.getAnimatedValue());
            }

        });
        //Onclick Listeners
        //////////////////////////////////////////////////////////////////////////////
        tapBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(soundcheck) {
                    if (tapBarMenu.isOpened()) {
                        buttonclick2.start();
                    } else {
                        buttonclick1.start();
                    }
                }
                tapBarMenu.toggle();
            }
        });
        /////////////////////////////////////////////////////////////////////////////
        htp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpintent = new Intent(MainActivity.this,help.class);
                startActivity(helpintent);

            }
        });
        ////////////////////////////////////////////////////////////Layout listener

        touchexpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(setboolean()){
                    besttextcolor = R.color.besttextcolor2;
                    scoretextcolor = R.color.scorestextcolor2;

                }
                else{
                    besttextcolor = R.color.besttextcolor1;
                    scoretextcolor = R.color.scorestextcolor1;
                }

                if(soundcheck) {
                    if (bouncescorestate) {
                        buttonclick2.start();
                    } else {
                        buttonclick1.start();
                    }
                }
                    didTapButton(v);
                    scorebounce(scorelayout);
                    if (bouncescorestate){
                        score.setTextSize(60);
                        hours.setVisibility(View.VISIBLE);
                        scoretext.setTextColor(getResources().getColor(scoretextcolor));
                        score.setTextColor(getResources().getColor(scoretextcolor));
                        hours.setTextColor(getResources().getColor(scoretextcolor));
                        if (nd) {
                            scoretext.setText("NEW RATE");
                            editor.putBoolean("newdata", false);
                            editor.commit();
                        } else {
                                scoretext.setText("RATE");
                        }
                        touchexpand.setImageResource(R.drawable.expand);
                        score.setText(toString().valueOf(sp.getInt("hoursstatistics0", 0)));
                        bouncescorestate = false;
                        int dr = sp.getInt("hoursstatistics0", 0);
                        String sm = setscoremessage(dr);
                            scoremessage.setText(sm);
                    }
                    else {
                        hours.setVisibility(View.INVISIBLE);
                        score.setTextSize(20);
                        scoretext.setText("BRIEF");
                        scoretext.setTextColor(getResources().getColor(besttextcolor));
                        score.setTextColor(getResources().getColor(besttextcolor));
                        hours.setTextColor(getResources().getColor(besttextcolor));
                        touchexpand.setImageResource(R.drawable.contract);
                        score.setText(toString().valueOf(sp.getInt("dlevellatest",100)) + "% to " + toString().valueOf(sp.getInt("clevellatest",0)) + "%\n took " + sp.getString("hoursused","0") + "\nat " + sp.getInt("hoursstatistics0",0) + "% per hour.");
                        if(sp.getInt("dlevellatest",-20)==-20){
                            score.setText("Your brief will appear when the rate value arrives.");
                        }
                        bouncescorestate = true;
                    }

            }
        });
        //////////////////////////////////////////////////////////////////////////////play and quit button
        play_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                didTapButton(v);
                if(setboolean()){
                    besttextcolor = R.color.besttextcolor1;
                    scoretextcolor = R.color.scorestextcolor1;
                }
                else{
                    besttextcolor = R.color.besttextcolor2;
                    scoretextcolor = R.color.scorestextcolor2;
                }
                if(setboolean())
                {
                    if(firsttime){
                        colorAnimation1.reverse();
                        if(bouncescorestate){
                            colorAnimation4.reverse();
                        }
                        else {
                            colorAnimation3.reverse();
                        }
                        if(bouncescorestate){
                            scoretext.setTextColor(getResources().getColor(besttextcolor));
                            score.setTextColor(getResources().getColor(besttextcolor));
                            hours.setTextColor(getResources().getColor(besttextcolor));
                        }
                        else {
                            scoretext.setTextColor(getResources().getColor(scoretextcolor));
                            score.setTextColor(getResources().getColor(scoretextcolor));
                            hours.setTextColor(getResources().getColor(scoretextcolor));

                        }
                        StopService(v);
                        play_quit.setText("Start");

                    }
                    else{
                        d3.setContentView(R.layout.alertdialouge);
                        TextView yes = d3.findViewById(R.id.yesbutton);
                        TextView no = d3.findViewById(R.id.Nobutton);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(soundcheck)
                                    buttonclick2.start();
                                d3.dismiss();

                            }
                        });
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(soundcheck)
                                    buttonclick1.start();
                                colorAnimation1.reverse();
                                if(bouncescorestate){
                                    colorAnimation4.reverse();
                                }
                                else {
                                    colorAnimation3.reverse();
                                }
                                if(bouncescorestate){
                                    scoretext.setTextColor(getResources().getColor(besttextcolor));
                                    score.setTextColor(getResources().getColor(besttextcolor));
                                    hours.setTextColor(getResources().getColor(besttextcolor));
                                }
                                else {
                                    scoretext.setTextColor(getResources().getColor(scoretextcolor));
                                    score.setTextColor(getResources().getColor(scoretextcolor));
                                    hours.setTextColor(getResources().getColor(scoretextcolor));
                                }
                                StopService(v);
                                play_quit.setText("Start");
                                d3.dismiss();
                            }
                        });
                        d3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        d3.show();


                    }
                    if(soundcheck)
                         buttonclick2.start();
                }
                else{
                    if(soundcheck)
                        buttonclick1.start();
                    colorAnimation1.start();
                    if(bouncescorestate){
                        colorAnimation4.start();
                    }
                    else {
                        colorAnimation3.start();
                    }
                    if(bouncescorestate){
                        scoretext.setTextColor(getResources().getColor(besttextcolor));
                        score.setTextColor(getResources().getColor(besttextcolor));
                        hours.setTextColor(getResources().getColor(besttextcolor));

                    }
                    else {
                        scoretext.setTextColor(getResources().getColor(scoretextcolor));
                        score.setTextColor(getResources().getColor(scoretextcolor));
                        hours.setTextColor(getResources().getColor(scoretextcolor));
                    }
                    StartService(v);
                    play_quit.setText("Stop");
                }
                scoremessage.setTextColor(getResources().getColor(scoretextcolor));
            }
        });
        //////////////////////////////////////////////////////////////////////////////
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);
                Intent ab_share = new Intent(MainActivity.this,about.class);
                startActivity(ab_share);

            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(v);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"Game of Batteries\n_________________\n\nI found this cool app to monitor phone battery usage along with live battery life notifications \n\nhttps://play.google.com/store/apps/details?id=com.qdotdash.gameofbatteries");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Game of Batteries");
                startActivity(Intent.createChooser(sharingIntent,"Share app in"));

            }
        });
        //setting values
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////button
        play_quit.setText("Start");
        /////////////////////////////////////////////////////////////////////////////Text views
        scoretext.setText("RATE");
        ////////////////////////////////////////////////////////////////functions
        setvalues();
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////


        //Initial game page setting -- setting background,text and button text
        //////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
        if(setboolean()){
            scoretext.setTextColor(getResources().getColor(R.color.scorestextcolor2));
            score.setTextColor(getResources().getColor(R.color.scorestextcolor2));
            hours.setTextColor(getResources().getColor(R.color.scorestextcolor2));
            bgp.setBackgroundResource(R.drawable.secondpage);
            play_quit.setText("Stop");
        }

        else{
            scoretext.setTextColor(getResources().getColor(R.color.scorestextcolor1));
            score.setTextColor(getResources().getColor(R.color.scorestextcolor1));
            hours.setTextColor(getResources().getColor(R.color.scorestextcolor1));
            scoremessage.setTextColor(getResources().getColor(R.color.scorestextcolor1));
            bgp.setBackgroundResource(R.drawable.firstpage);
            play_quit.setText("Start");
        }

    }
    //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////On create ends



    //Functions
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////Emergency Termination fun
    private boolean isServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.qdotdash.gameofbatteries.Myservice".equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////tapbar items
    public void Openstatistics(View view){
        Intent statisticsopen = new Intent(MainActivity.this,Statistics.class);
        startActivity(statisticsopen);
    }
    public void Soundtoggle(View view){
        SharedPreferences sp = getSharedPreferences("AGOB",0);
        SharedPreferences.Editor editor = sp.edit();
        soundcheck = sp.getBoolean("soundcheck",true);
       if(soundcheck){
           sound.setImageResource(R.drawable.soundoff);
           soundcheck=false;
       }
       else{
           sound.setImageResource(R.drawable.soundon);
           soundcheck=true;
       }
       editor.putBoolean("soundcheck",soundcheck);
       editor.apply();
    }
    //////////////////////////////////////////////////////////////////////////////bounce animation
    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 20);
        myAnim.setInterpolator(interpolator);

        view.startAnimation(myAnim);
    }
    public void scorebounce(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.22, 20);
        myAnim.setInterpolator(interpolator);

        view.startAnimation(myAnim);
    }
    /////////////////////////////////////////////////////////setting scores function
    public void setvalues(){
        SharedPreferences sp = getSharedPreferences("AGOB",0);
        SharedPreferences.Editor editor = sp.edit();
        if(!bouncescorestate) {
            if (sp.getBoolean("newdata", false)) {
                scoretext.setText("NEW RATE");
                dc = R.drawable.customtoastachievement;
                toastshow("New Score", dc,3);
                editor.putBoolean("newdata", false);
                editor.commit();
            } else {
                if (!firsttime) {
                    scoretext.setText("RATE");
                }
            }
        }
        int dr = sp.getInt("hoursstatistics0", 0);
        String sm = setscoremessage(dr);
        if(nd) {
            scoremessage.setText("");
            scoremessage.setCharacterDelay(delay);
            scoremessage.animateText(sm);
        }
        else
            scoremessage.setText(sm);
        if(!bouncescorestate) {
            score.setText(toString().valueOf(sp.getInt("hoursstatistics0", 0)));
        }

    }

    private String setscoremessage(int dr){
        String smess;
        if(dr==1||dr==2)
            smess = "Standing Ovation";
        else if(dr==3||dr==4)
            smess = "Amazing";
        else if(dr==5||dr==6)
            smess = "Good";
        else if(dr==7||dr==8)
            smess = "Normal usage";
        else if(dr==9||dr==10)
            smess = "Above normal usage";
        else if(dr==11||dr==12)
            smess = "Usage high";
        else if(dr==13||dr==14)
            smess = "Usage very High";
        else if(dr==15||dr==16)
            smess = "Heavy Usage";
        else if(dr==17||dr==18)
            smess = "Extreme usage";
        else if(dr==19||dr==20)
            smess = "Battery is so hot";
        else if(dr>=21&&dr<=25)
            smess = "You are burning battery";
        else if(dr>=26)
            smess = "Bombarda Maxima";
        else
            smess = "Instructions through notifications";
        return smess;


    }
    /////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////Broadcast receiver for refresh setting values
    private BroadcastReceiver incoming_values_set = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction()==Intent.ACTION_POWER_CONNECTED) {
                final SharedPreferences sp = getSharedPreferences("AGOB",0);
                final SharedPreferences.Editor editor = sp.edit();
                /////////////////////////////////////////////delaying for the values to arrive
                pausetosettext.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doStuff();
                        nd = sp.getBoolean("newdata", false);
                        if(nd){
                            dc=R.drawable.customtoastachievement;
                            toastshow(achievementmessage,dc,3);
                        }
                        int dr;
                            dr = sp.getInt("hoursstatistics0", 0);
                            String sm = setscoremessage(dr);
                            nd = sp.getBoolean("newdata", false);
                            if (nd) {
                                scoremessage.setText("");
                                scoremessage.setCharacterDelay(delay);
                                scoremessage.animateText(sm);
                            }
                            else{
                                scoremessage.setText(sm);
                            }

                        if(sp.getBoolean("initfinalsamelevel",false)) {
                            dc = R.drawable.customtoastemergency;
                            toastshow("Initial and final percentages are same", dc,2);
                            editor.putBoolean("initfinalsamelevel", false);
                            editor.apply();
                        }


                    }
                },10);
                /////////////////////////////////////////////

            }

        }
        ///////////////////////set values in app while running
        private void doStuff(){
            setvalues();
        }
        ////////////////////////
    };
    /////////////////////////////////////////////////////////////////////////////
    private void toastshow(String tt,int drawable_code,int code){
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.mylayout));
        LinearLayout ll = toastLayout.findViewById(R.id.ll);
        ll.setBackground(getResources().getDrawable(drawable_code));
        TextView tvv = toastLayout.findViewById(R.id.textview1);
        tvv.setTextColor(Color.WHITE);
        if(code==1||code==2)
            tvv.setGravity(Gravity.BOTTOM);
        else
            tvv.setGravity(Gravity.TOP);
        tvv.setText(tt);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();
    }
    ////////////////////////////////////////////////////////////starting service
    private void StartService(View v) {
        Intent i = new Intent(MainActivity.this,Myservice.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            context.startForegroundService(i);
        }
        else{
            startService(i);
        }

    }
    /////////////////////////////////////////////////////////////Stopping service
    private void StopService(View v) {

        Intent i = new Intent(MainActivity.this,Myservice.class);
        stopService(i);

    }
    /////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////shared preference for the play/quit mode knowing
    public boolean setboolean(){
        SharedPreferences sp = getSharedPreferences("AGOB",0);
        boolean ip;
        ip=sp.getBoolean("ifpause",false);
        return ip;
    }
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////Showcasing
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
}
