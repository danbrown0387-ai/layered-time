package com.layeredtime.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    static final int REQ_LOCATION = 42;
    LocationManager lm; FrameLayout globeFrame; TextView locName, locInfo, contemplative, civil, date, decimal, angle, sector, progress; View marker;
    final Handler handler = new Handler();
    final Runnable clock = new Runnable(){ public void run(){ updateTime(); handler.postDelayed(this,50); }};

    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    TextView tv(String text,float size,int color){ TextView t=new TextView(this); t.setText(text); t.setTextSize(size); t.setTextColor(color); return t; }
    GradientDrawable bg(int color,int stroke){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(22)); g.setStroke(dp(1),stroke); return g; }
    TextView value(String text){ TextView t=tv(text,34,Color.WHITE); t.setTypeface(null,1); t.setFontFeatureSettings("tnum"); return t; }

    @Override public void onCreate(Bundle b){ super.onCreate(b); buildUi(); lm=(LocationManager)getSystemService(LOCATION_SERVICE); handler.post(clock); requestLocation(); }

    void buildUi(){
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(Color.rgb(17,17,17));
        LinearLayout main=new LinearLayout(this); main.setOrientation(LinearLayout.VERTICAL); main.setPadding(dp(16),dp(20),dp(16),dp(36)); scroll.addView(main);
        TextView h=tv("Layered Time",30,Color.WHITE); h.setTypeface(null,1); main.addView(h);
        TextView sub=tv("A live view of time through multiple layers",16,Color.rgb(153,153,153)); LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2); sp.setMargins(0,dp(3),0,dp(16)); main.addView(sub,sp);

        LinearLayout location=new LinearLayout(this); location.setGravity(Gravity.CENTER_VERTICAL); location.setPadding(dp(16),dp(16),dp(16),dp(16)); location.setBackground(bg(Color.rgb(27,27,27),Color.rgb(48,48,48)));
        globeFrame=new FrameLayout(this); int gs=dp(180); ImageView globe=new ImageView(this); globe.setImageResource(com.layeredtime.app.R.drawable.earth_globe); globe.setScaleType(ImageView.ScaleType.CENTER_CROP); globeFrame.addView(globe,new FrameLayout.LayoutParams(gs,gs));
        marker=new View(this); GradientDrawable md=new GradientDrawable(); md.setShape(GradientDrawable.OVAL); md.setColor(Color.WHITE); md.setStroke(dp(3),Color.rgb(255,176,0)); marker.setBackground(md); marker.setElevation(dp(4)); FrameLayout.LayoutParams mp=new FrameLayout.LayoutParams(dp(14),dp(14)); mp.leftMargin=gs/2-dp(7); mp.topMargin=gs/2-dp(7); marker.setLayoutParams(mp); marker.setVisibility(View.GONE); globeFrame.addView(marker); location.addView(globeFrame,new LinearLayout.LayoutParams(gs,gs));
        LinearLayout lt=new LinearLayout(this); lt.setOrientation(LinearLayout.VERTICAL); lt.setPadding(dp(16),0,0,0); locName=tv("Finding your location…",23,Color.WHITE); locName.setTypeface(null,1); locInfo=tv("Allow location access to place you on Earth.",14,Color.rgb(170,170,170)); lt.addView(tv("YOUR LOCATION ON EARTH",13,Color.rgb(153,153,153))); lt.addView(locName); lt.addView(locInfo); location.addView(lt,new LinearLayout.LayoutParams(0,-2,1)); main.addView(location,new LinearLayout.LayoutParams(-1,-2));

        addCard(main,"CONTEMPLATIVE TIME","",v->contemplative=v);
        addCard(main,"CIVIL TIME","",v->civil=v, t->date=t);
        addCard(main,"DECIMAL TIME","",v->decimal=v);
        addCard(main,"360° GEOMETRIC DAY","",v->angle=v, t->sector=t);
        addCard(main,"DAY PROGRESS","",v->progress=v);
        setContentView(scroll);
    }
    interface One{void set(TextView v);} interface Two{void set(TextView v);}
    void addCard(LinearLayout main,String label,String unused,One setter){ LinearLayout c=card(); c.addView(tv(label,13,Color.rgb(153,153,153))); TextView v=value("—"); c.addView(v); setter.set(v); TextView small=tv("",14,Color.rgb(170,170,170)); c.addView(small); main.addView(c); }
    void addCard(LinearLayout main,String label,String unused,One setter,Two setter2){ LinearLayout c=card(); c.addView(tv(label,13,Color.rgb(153,153,153))); TextView v=value("—"); c.addView(v); setter.set(v); TextView small=tv("",14,Color.rgb(170,170,170)); c.addView(small); setter2.set(small); main.addView(c); }
    LinearLayout card(){ LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(20),dp(18),dp(20),dp(18)); c.setBackground(bg(Color.rgb(27,27,27),Color.rgb(48,48,48))); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,dp(12),0,0); c.setLayoutParams(p); return c; }

    void updateTime(){ Date now=new Date(); Calendar cal=Calendar.getInstance(); cal.setTime(now); int h=cal.get(Calendar.HOUR_OF_DAY),m=cal.get(Calendar.MINUTE),s=cal.get(Calendar.SECOND); long ms=cal.get(Calendar.MILLISECOND); double p=(h*3600+m*60+s+ms/1000.0)/86400.0;
        double units=p*12000; contemplative.setText(String.format(Locale.US,"%,d · %d",(int)units,(int)((units%1)*10))); civil.setText(String.format(Locale.US,"%02d:%02d:%02d",h,m,s)); date.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy",Locale.US).format(now));
        double dec=p*10; int dh=(int)dec, dm=(int)((dec-dh)*100), ds=(int)((((dec-dh)*100)-dm)*100); decimal.setText(String.format(Locale.US,"%02d:%02d:%02d",dh,dm,ds));
        double a=p*360; angle.setText(String.format(Locale.US,"%.3f°",a)); sector.setText("Geometric sector "+(int)(a/30)*30+"–"+((int)(a/30)+1)*30+"°"); progress.setText(String.format(Locale.US,"%.4f%%",p*100));
    }

    void requestLocation(){ if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){ requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQ_LOCATION); } else startLocation(); }
    void startLocation(){ try { lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,5,listener); lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,5,listener); Location l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); if(l==null) l=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); if(l!=null) showLocation(l); } catch(SecurityException ignored){} }
    final LocationListener listener=new LocationListener(){ public void onLocationChanged(Location l){ showLocation(l); } };
    void showLocation(Location l){ double lat=l.getLatitude(),lon=l.getLongitude(); locName.setText("Location found"); locInfo.setText(String.format(Locale.US,"%.5f° %s, %.5f° %s\nAccuracy: ±%.0f m",Math.abs(lat),lat>=0?"N":"S",Math.abs(lon),lon>=0?"E":"W",l.getAccuracy())); marker.setVisibility(View.VISIBLE); int w=globeFrame.getWidth(); int h=globeFrame.getHeight(); if(w>0){ FrameLayout.LayoutParams p=(FrameLayout.LayoutParams)marker.getLayoutParams(); p.leftMargin=Math.max(dp(10),Math.min(w-dp(10),(int)(w*(0.50+(lon+100)*0.0036))-dp(7))); p.topMargin=Math.max(dp(10),Math.min(h-dp(10),(int)(h*(0.50-lat*0.0042)))-dp(7)); marker.setLayoutParams(p); } }
    @Override public void onRequestPermissionsResult(int r,String[] p,int[] g){ super.onRequestPermissionsResult(r,p,g); if(r==REQ_LOCATION && g.length>0 && (g[0]==PackageManager.PERMISSION_GRANTED || (g.length>1&&g[1]==PackageManager.PERMISSION_GRANTED))) startLocation(); else {locName.setText("Location permission needed");locInfo.setText("Enable location permission in Android Settings to show your actual position.");} }
    @Override protected void onDestroy(){ super.onDestroy(); handler.removeCallbacks(clock); if(lm!=null) try{lm.removeUpdates(listener);}catch(Exception ignored){} }
}
