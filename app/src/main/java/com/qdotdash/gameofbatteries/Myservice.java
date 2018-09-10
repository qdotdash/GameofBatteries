package com.qdotdash.gameofbatteries;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Myservice extends Service{

    /////////////////////////////////////////////////////public variable declarations
    ///////////////////////////////Channel ID
    private static final String CHANNEL_ID = "AGOB" ;
    ////////////////////////////////////////////////////////////////////shared preference
    private SharedPreferences datas;
    ////////////////////////////////////////////////////////////////////Notification
    private Notification notification;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private NotificationCompat.Builder builder;
    private Context context;
    boolean randshowcheck = true;
    private RemoteViews remoteViews;
    private static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private RemoteViews remoteViews0,remoteViews1,remoteViews2,remoteViews3,remoteViews4,remoteViews5;
    ///////////////////////////////////////////////////////////////////boolean
    boolean ishighscore=false;
    boolean offflag = false;
    boolean isnewlevel=false;
    private boolean afterdisconnection = false;
    private boolean flagsleep=false;
    private boolean firsttimeentry = true;
    int n=2;
    ///////////////////////////////////////////////////////////////////////////int
    private int dlevel=-1,clevel,level,dayf,minutesf,hoursf,totaltimehighscore;
    private int nextlevel,scoretime,highscoretime;
    private int XP;
    int checklevelrv;

    int flag=-1,dr=0;
    int show0level,show1level;
    boolean scandermesto=false;
    private int datanumber;
    int connectioncheck;
    private int score = 0;
    int instantlevel=-1;
    long notificationchange1,notificationchange2;
    //////////////////////////////////////////////////////////////////long
    private long dtime,ctime,dissipationrate,onepercentdepletion;
    long insttime=-1,batteryrem;
    long temptime;
    //////////////////////////////////////////////////////////////////String
    private String day,hours,minutes;
    //////////////////////////////////////////////////////////////////double + mah initialising
    private double bc = getBatteryCapacity();
    Handler handler = new Handler();
    boolean initfinalsame=false,randomgen=true;
    long timerate;
    int a = -4,temp=-1;
    ScheduledExecutorService scheduler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    ////////////////////////////////////////////////On create beginning
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"My Notifications",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        context = Myservice.this;
        if(bc==0)
            bc = getBatteryCapacity(context);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Toast.makeText(Myservice.this,toString().valueOf(bc),Toast.LENGTH_LONG).show();
        remoteViews0 = new RemoteViews(getPackageName(),R.layout.customnotifbatteryremainingblue);
        remoteViews1 = new RemoteViews(getPackageName(),R.layout.customnotifbatteryremaininggreen);
        remoteViews2 = new RemoteViews(getPackageName(),R.layout.customnotifbatteryremainingyellow);
        remoteViews3 = new RemoteViews(getPackageName(),R.layout.customnotifbatteryremainingred);
        remoteViews4 = new RemoteViews(getPackageName(),R.layout.customnotiftextnormal);
        remoteViews5 = new RemoteViews(getPackageName(),R.layout.customnotiftextnormalforother);
        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////shared preference
        datas = getSharedPreferences("AGOB",0);
        SharedPreferences.Editor editor = datas.edit();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        /////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////sending activity that game has started
        editor.putBoolean("ifpause",true);
        editor.commit();
        /////////////////////////////////////////////////////////////////////////////Broad cast receiver for getting
        /////////////////////////////////////////////////////////////////////////////level when play is pressed
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int initlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        connectioncheck = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
        //Toast.makeText(Myservice.this,toString().valueOf(connectioncheck),Toast.LENGTH_LONG).show();
        //Toast.makeText(Myservice.this,toString().valueOf(bc),Toast.LENGTH_LONG).show();
        /////////////////////////////////////////////////////////////////////////////taking values at play press
        /////////////////////////////////////////////////////////////////////////////
         if(connectioncheck!=2) {
             flag=0;
             afterdisconnection = true;
             dlevel = initlevel;
             dtime = Calendar.getInstance().getTimeInMillis();
         }
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////registering receivers
        //registerReceiver(instantdatacalculation,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(plugstatusreciever,new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
        registerReceiver(plugstatusreciever,new IntentFilter(Intent.ACTION_POWER_CONNECTED));

        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        instantbatterycalculation();
                    }
                },0,60, TimeUnit.SECONDS
        );
        /////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    /////////////////////////////////////////////////////////////////////////////Broadcast receivers
    /////////////////////////////////////////////////////////////////////////////////////BR for instant data
   public void instantbatterycalculation() {
       builder = new NotificationCompat.Builder(Myservice.this, NOTIFICATION_CHANNEL_ID);
       datas = getSharedPreferences("AGOB", 0);
       Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
       final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
               0, notificationIntent, 0);
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
               if (dlevel != -1) {
                   if (instantlevel == -1) {
                       instantlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                       Random random = new Random();
                       n = random.nextInt((10-6)+1) + 6;
                       insttime = Calendar.getInstance().getTimeInMillis();
                   }
                   int l1 = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                   int lv = dlevel-l1;
                   long t1 = Calendar.getInstance().getTimeInMillis();
                   if (l1 != instantlevel) {
                       Random random = new Random();
                       n = random.nextInt((10-6)+1) + 6;
                       temp = instantlevel;
                       temptime = insttime;
                       instantlevel = l1;
                       insttime = t1;
                   }
                   if (temp != -1) {
                       batteryrem = ((insttime - temptime) * instantlevel) / (long) (temp - instantlevel);
                       timerate = ((insttime - temptime) * 100) / (long) (temp - instantlevel);
                   } else {
                       batteryrem = timerate = 1L;
                   }

                   Long temp = batteryrem / (1000 * 60);
                   Long t = timerate / (1000 * 60);
                   String instday = toString().valueOf(temp / 1440) + "D ";
                   int instdayf = Integer.parseInt(toString().valueOf(t / 1440));
                   temp = temp % 1440;
                   String insthours = toString().valueOf(temp / 60) + "H ";
                   int insthoursf = Integer.parseInt(toString().valueOf(t / 60));
                   temp = temp % 60;
                   String instminutes = toString().valueOf(temp) + "M";
                   int instminutesf = Integer.parseInt(toString().valueOf(t / 60));
                   String totalbatterytimerem = instday + insthours + instminutes;
                   int insttimeinhours = instdayf * 24 + insthoursf + instminutesf / 60;
                   int instscore = 0;
                   if (insttimeinhours != 0 && batteryrem != 1) {
                       double c = 1 + insttimeinhours * 10000 / (5 * bc);
                       instscore = (int) Math.round(c);
                   }
                   if (lv>1) {
                       scandermesto = true;
                       remoteViews1.setTextViewText(R.id.t1, totalbatterytimerem);
                       remoteViews2.setTextViewText(R.id.t1, totalbatterytimerem);
                       remoteViews3.setTextViewText(R.id.t1, totalbatterytimerem);
                       remoteViews0.setTextViewText(R.id.t1, totalbatterytimerem);
                       RemoteViews rv = remoteViews2;
                       if (insttimeinhours >= 18)
                           rv = remoteViews0;
                       if (insttimeinhours >= 12 && insttimeinhours < 18)
                           rv = remoteViews1;
                       if (insttimeinhours >= 6 && insttimeinhours < 12)
                           rv = remoteViews2;
                       if (insttimeinhours >= 0 && insttimeinhours < 6)
                           rv = remoteViews3;
                       //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                       builder.setContentText(totalbatterytimerem)
                               .setSmallIcon(R.drawable.bulb)
                               .setContent(rv)
                               .setContentIntent(pendingIntent);
                       //notificationManager.notify(NOTIFICATION_ID,builder.build());
                       startForeground(NOTIFICATION_ID, builder.build());
                   } else {
                      // Toast.makeText(Myservice.this,toString().valueOf(instantlevel) + toString().valueOf(dlevel),Toast.LENGTH_LONG).show();
                       Intent batteryIntentt = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                       int il = batteryIntentt.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                       int randtime = (il * 60) / n;
                       String randday = toString().valueOf(randtime / 1440) + "D ";
                       randtime=randtime%1440;
                       String randhours = toString().valueOf(randtime / 60) + "H ";
                       randtime=randtime%60;
                       String randminutes = toString().valueOf(randtime) + "M";
                       String randtimestring = randday + randhours + randminutes;
                       remoteViews1.setTextViewText(R.id.t1, randtimestring);
                       builder.setContent(remoteViews1)
                               .setSmallIcon(R.drawable.bulb)
                               .setContentIntent(pendingIntent);
                       startForeground(NOTIFICATION_ID, builder.build());
                   }
               } else {
                   remoteViews4.setTextViewText(R.id.t2, "Disconnect the charger");
                   builder.setSmallIcon(R.drawable.bulb)
                           .setContent(remoteViews4)
                           .setContentIntent(pendingIntent);
                   startForeground(NOTIFICATION_ID, builder.build());


               }
           }
       }, 100);
   }
    ///////////////////////////////////////////////////////////////////////////////////BR for power disconnection and conn
    private BroadcastReceiver plugstatusreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ///////////////////////////////////////////////////////sp
            datas = getSharedPreferences("AGOB",0);
            SharedPreferences.Editor editor = datas.edit();
            ///////////////////////////////////////////////////////state taking
            String plugstatus = intent.getAction();
            /////////////////////////////////////////////////////////////pending intent
            Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0,notificationIntent,0);
            ///////////////////////////////////////////////////////Power Disconnection
            if(plugstatus.equals(Intent.ACTION_POWER_DISCONNECTED)){
                randshowcheck=true;
                instantbatterycalculation();
                notificationManager.cancel(4);
                afterdisconnection = true;
                instantlevel=-1;
                temp=-1;
                initfinalsame=false;
                editor.putBoolean("initfinalsamelevel",initfinalsame);
                editor.apply();
                firsttimeentry=false;
                notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, level);
                dlevel = level;
                dtime= Calendar.getInstance().getTimeInMillis();

            }
            ///////////////////////////////////////////////////////Power Connection
            else if(plugstatus.equals(Intent.ACTION_POWER_CONNECTED)){
                instantbatterycalculation();
                notificationchange1=Calendar.getInstance().getTimeInMillis();
                Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                clevel = level;
                ///////////////////////////////////////////////////////checking after disconnection
                if(afterdisconnection){
                    ctime = Calendar.getInstance().getTimeInMillis();
                    afterdisconnection=false;
                    if (dlevel != clevel)
                    {
                        dissipationrate = ((long) (dlevel - clevel) * 1000 * 60 * 60) / ((ctime - dtime));
                        dr=Integer.parseInt(toString().valueOf(dissipationrate));
                        onepercentdepletion = ((ctime - dtime) * 100) / (long) (dlevel - clevel);
                    }
                    else
                    {
                        dissipationrate = 0L;
                        onepercentdepletion = 1L;
                    }
                    ///////////////////////////////////////getting day and other values
                    conversion(onepercentdepletion);
                    ////////////////////////////////////////////////100% time in hrs for score calc
                    double mahtime = dayf*24 + hoursf + minutesf/60;
                    //////////////////////////////////////////////if mahtime!=0 calculate score
                    if(mahtime!=0)
                    {
                        scorecalculation(mahtime);
                    }
                    else
                    {
                        score = 0;
                    }
                    ////////////////////////////////////////////////////////////////for shifting values STATISTICS
                    if(score!=0) {
                        datanumber = datas.getInt("datanumber", 0);
                        if (datanumber != 0)
                            shift();
                        datanumber = datanumber + 1;
                        //////////////////////////////////////////////Inserting incoming data
                        editor.putInt("datanumber", datanumber);
                        editor.putString("daylatest", day);
                        editor.putString("hourlatest", hours);
                        editor.putString("minutelatest", minutes);
                        editor.putInt("clevellatest", clevel);
                        editor.putInt("dlevellatest", dlevel);
                        editor.putLong("ctimelatest", ctime);
                        editor.putLong("dtimelatest", dtime);
                        editor.putLong("ratelatest0", dissipationrate);
                        editor.putInt("dayintlatest", dayf);
                        editor.putInt("hoursintlatest", hoursf);
                        editor.putInt("minuteintlatest", minutesf);
                        editor.putInt("scorelatest0", score);
                        editor.putBoolean("newdata", true);
                        int hoursused = Integer .parseInt(toString().valueOf(ctime-dtime))/(1000*60);
                        String hu;
                        if(hoursused<60){
                            hu = toString().valueOf(hoursused) + " minutes";
                        }
                        else {
                            hoursused = hoursused/60;
                            hu = toString().valueOf(hoursused) + " hours";
                        }
                        editor.putString("hoursused",hu);
                        /////////////////////////////////////////////////////////for high score
                        totaltimehighscore = dayf * 1440 + hoursf * 60 + minutesf;
                        scoretime = dayf * 24 + hoursf + minutesf/60;
                        editor.putInt("hoursstatistics0",dr);
                        /////////////////////////////////////////////////////high score insertion
                        if (dissipationrate > 0L) {
                            if (datas.getInt("tth", -30) < totaltimehighscore) {
                                ishighscore = true;
                                editor.putBoolean("ishighscore", ishighscore);
                                editor.putString("dayhighscore", day);
                                editor.putString("hourhighscore", hours);
                                editor.putString("minutehighscore", minutes);
                                editor.putInt("clevelhighscore", clevel);
                                editor.putInt("dlevelhighscore", dlevel);
                                editor.putLong("ctimehighscore", ctime);
                                editor.putLong("dtimehighscore", dtime);
                                editor.putLong("ratehighscore", dissipationrate);
                                editor.putInt("dayinths", dayf);
                                editor.putInt("hoursinths", hoursf);
                                editor.putInt("minuteinths", minutesf);
                                editor.putInt("totaltimehighscore", dr);
                                editor.putInt("tth", totaltimehighscore);
                                editor.putInt("highscore", score);
                            }
                        }
                        editor.apply();
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                        String str  = df.format(date);
                        String scoremessage = "New Rate arrived : " + toString().valueOf(dr) + "\n" + str;
                        remoteViews5.setTextViewText(R.id.t2,scoremessage);
                        builder.setContent(remoteViews5)
                                .setSmallIcon(R.drawable.bulb)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManager.notify(4,builder.build());
                        dlevel = -1;
                        firsttimeentry = false;
                        ishighscore = false;
                    }
                    else{
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
                        String str  = df.format(date);

                        remoteViews5.setTextViewText(R.id.t2,"Initial and final percentages are same\n" + str);
                        builder.setContent(remoteViews5)
                                .setPriority(Notification.PRIORITY_MAX)
                                .setSmallIcon(R.drawable.bulb)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        notificationManager.notify(4,builder.build());
                        dlevel=-1;
                        firsttimeentry=false;
                        initfinalsame=true;
                        editor.putBoolean("initfinalsamelevel",true);
                        editor.apply();
                    }

                }
            }
        }
    };


    ////////////////////////////////////////////////////////////////////////////Functions
    /////////////////////////////////////////////////////////conversion of values for days,minutes and hours
    public void conversion(Long td) {
        Long temp = td / (1000 * 60);
        day = toString().valueOf(temp / 1440) + "D";
        dayf = Integer.parseInt(toString().valueOf(temp / 1440));
        temp = temp % 1440;
        hours = toString().valueOf(temp / 60) + "H";
        hoursf = Integer.parseInt(toString().valueOf(temp / 60));
        temp = temp % 60;
        minutes = toString().valueOf(temp) + "M";
        minutesf = Integer.parseInt(toString().valueOf(temp / 60));
    }
    ///////////////////////////////////////////////////////////////shift values function
    private void shift() {
        datas = getSharedPreferences("AGOB",0);                 //for graph drawing
        SharedPreferences.Editor editor = datas.edit();
        int i;
        if(datanumber>4)
            i=4;
        else
            i=datanumber;
        for(;i>=1;i--){
            editor.putInt("scorelatest"+ toString().valueOf(i),datas.getInt("scorelatest"+toString().valueOf(i-1),-2));
            editor.putInt("hoursstatistics"+ toString().valueOf(i),datas.getInt("hoursstatistics"+toString().valueOf(i-1),-2));
            editor.apply();
            }
    }
    ////////////////////////////////////////////////////////////score calculation
    private void scorecalculation(double mahtime){
        double cu = 1+mahtime*10000/(5*bc);
        score = (int) Math.round(cu);
    }
    /////////////////////////////////////////////////////////////////////////MAH Calculation
    @SuppressLint("PrivateApi")
    public Double getBatteryCapacity() {

        // Power profile class instance
        Object mPowerProfile_ = null;

        // Reset variable for battery capacity
        double batteryCapacity = 0;

        // Power profile class name
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {

            // Get power profile class and create instance. We have to do this
            // dynamically because android.internal package is not part of public API
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);

        } catch (Exception e) {

            // Class not found?
            e.printStackTrace();
        }

        try {

            // Invoke PowerProfile method "getAveragePower" with param "battery.capacity"
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");

        } catch (Exception e) {

            // Something went wrong
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;

    }
    //////////////////////////////////////////////////////////////////////////////////On destroy
    @Override
    public void onDestroy() {
        ///////////////////////////////////////////////////////////////////////////////////////////
        unregisterReceiver(plugstatusreciever);
        //unregisterReceiver(instantdatacalculation);
        ////////////////////////////////////////////////////////////////////////////////////sp
        datas = getSharedPreferences("AGOB",0);
        SharedPreferences.Editor editor = datas.edit();
        ///////////////////////////////////////////////////////////game has ended
        editor.putBoolean("ifpause",false);
        editor.apply();
        stopForeground(true);
        ///////////////////////////////////////////////////////////
        super.onDestroy();
    }
}
