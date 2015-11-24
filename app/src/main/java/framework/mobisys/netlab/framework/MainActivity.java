package framework.mobisys.netlab.framework;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import framework.mobisys.netlab.transframeworkandroid.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";

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
        final TextView textView = (TextView) findViewById(R.id.MainTextView);
        String url = "http://52.88.216.252/json_test.txt";

//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        textView.setText("Response: " + response.toString());
//                        System.out.println("Load successfully!");
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//                        System.out.println(error.getLocalizedMessage());
//                    }
//                });
//        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
//        System.out.println("Add request to queue");
//        queue.add(jsObjRequest);


        /**
         * 2015-11-23-测试更新之后的同步/异步API
         */
        E3Framework e3 = new E3Framework(this);

        /**
         * 测试看能不能下载内容
         */
        LStringRequest lsr = e3.createStringRequest(url, 0, "StringTest");
        e3.putStringRequest(lsr, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                textView.setText("Response: " + response.toString());
                //System.out.println("Load successfully!"+response.toString());
            }
        });


        /**
         * 使用ImageRequest获取数据并显示
         */
        final ImageView imageView = (ImageView) findViewById(R.id.MainImageView);
        url = "http://52.88.216.252/boat.jpg";
//        try {
//            HttpURLConnection conn=(HttpURLConnection)(new URL(url).openConnection());
//            conn.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        ImageRequest imgrequest = new ImageRequest(url,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        imageView.setImageBitmap(bitmap);
//                    }
//                }, 0, 0, null,
//                new Response.ErrorListener() {
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.println(error.getLocalizedMessage());
//                    }
//                });

        /**
         * 测试我们自己写的ObjectRequest
         */

        LObjectRequest lor = e3.createObjectRequest(url, 5, "ObjectTest");
        lor.setShouldCache(false);
        e3.putObjectRequest(lor, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                imageView.setImageBitmap(Tools.getBitmap(response));
            }
        }, new Response.ProgressListener() {
            @Override
            public void onProgress(int percentage) {

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
