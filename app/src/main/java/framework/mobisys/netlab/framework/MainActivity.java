package framework.mobisys.netlab.framework;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.ERequest;
import com.android.volley.Response;

import framework.mobisys.netlab.transframeworkandroid.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";

    private WebView webView;
    long t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * 下面的这段代码是用来执行悬浮的mail按钮被按下的时候进行的操作，其实就是下面有一个Snackbar，这个bar会弹出然后显示一行字。
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

//                E3Framework e3 = E3Framework.getInstance(MainActivity.this);
//                String url = "http://52.88.216.252/system.png";
//                LObjectRequest lor = e3.createObjectRequest(url, 5, "ObjectTest");
//                lor.setShouldCache(false);
//                e3.putObjectRequest(lor, new Response.Listener<byte[]>() {
//                    @Override
//                    public void onResponse(byte[] response) {
//                        //imageView.setImageBitmap(Tools.getBitmap(response));
//                    }
//                }, new Response.ProgressListener() {
//                    @Override
//                    public void onProgress(long transferredBytes, long totalSize) {
//                    }
//                });
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * 在主界面的TextView上显示下载到的内容
         */
        /**
         * Test: 利用Volley读取数据，然后显示在主界面的TextView上
         */


        webView = (WebView) findViewById(R.id.MainWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("http://52.88.216.252/boat.jpg");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        E3Framework e3 = E3Framework.getInstance(this);

        /**
         * 2016-02-24-更新：统一使用ERequest
         * 加载图片内容
         */


        String url;
        /**
         * 加载文本内容
         */
        final TextView textView = (TextView) findViewById(R.id.MainTextView);
        url = "http://121.42.158.232/json_test.txt";
        ERequest text_er = e3.createRequest(url, ERequest.ACTIVE, "New Text Request");
        text_er.setShouldCache(false);
        text_er.setEndTime(text_er.getEndTime() + 1000);

        e3.putERequest(text_er, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                //System.out.println("Response Length:"+response.length);
                textView.setText(new String(response));
            }
        });

        final ImageView imageView = (ImageView) findViewById(R.id.MainImageView);
        url = "http://121.42.158.232/mountain.jpg";
        ERequest er = e3.createRequest(url, ERequest.ACTIVE, "New Image Request");
        er.setShouldCache(false);
        er.setEndTime(er.getEndTime() + 3000);

        e3.putERequest(er, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(response, 0 , response.length));
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
