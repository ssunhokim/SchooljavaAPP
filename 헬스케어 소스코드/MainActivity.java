package kimsunho.medicalsignalendcoderapplication;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

/*   탭별 액티비티를 설정하기위한 액티비티   */
public class MainActivity extends TabActivity {

    TabHost tabHost;

    TextView tv1;
    TextView tv2;
    TextView tv3;

    ImageView iv1;
    ImageView iv2;
    ImageView iv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost=getTabHost();

            /*   탭 이벤트 설정   */
        tabHost.addTab(tabHost.newTabSpec("Tab01")
                .setIndicator("Info", ResourcesCompat.getDrawable(getResources(), R.drawable.info, null))
                .setContent(new Intent(this, InfoActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("Tab02")
                .setIndicator("Wave", ResourcesCompat.getDrawable(getResources(), R.drawable.wave, null))
                .setContent(new Intent(this, WaveActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("Tab03")
                .setIndicator("Form", ResourcesCompat.getDrawable(getResources(), R.drawable.option, null))
                .setContent(new Intent(this, FormActivity.class)));

        for(int i=0;i<4;i++)
            tabHost.setCurrentTab(i);

        tabHost.setBackgroundColor(Color.parseColor("#232323"));

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#474747"));
        }

        tabHost.setCurrentTab(0);

        tv1 = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tv3 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);

        iv1=(ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.icon);
        iv2=(ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
        iv3=(ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.icon);

        iv1.setImageResource(0);
        iv1.setBackground(getResources().getDrawable(R.drawable.tabinfo));
        tv1.setTextColor(Color.parseColor("#004554"));
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#A1A1A1"));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
                {
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#474747"));
                }

                iv1.setImageResource(0);
                iv2.setImageResource(0);
                iv3.setImageResource(0);

                if(tabId=="Tab01") {
                    iv1.setBackground(getResources().getDrawable(R.drawable.tabinfo));
                    iv2.setBackground(getResources().getDrawable(R.drawable.wave));
                    iv3.setBackground(getResources().getDrawable(R.drawable.option));

                    tv1.setTextColor(Color.parseColor("#004554"));
                    tv2.setTextColor(Color.parseColor("#FFFFFF"));
                    tv3.setTextColor(Color.parseColor("#FFFFFF"));

                    tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#A1A1A1"));
                }
                else if(tabId=="Tab02") {
                    iv1.setBackground(getResources().getDrawable(R.drawable.info));
                    iv2.setBackground(getResources().getDrawable(R.drawable.tabwave));
                    iv3.setBackground(getResources().getDrawable(R.drawable.option));

                    tv1.setTextColor(Color.parseColor("#FFFFFF"));
                    tv2.setTextColor(Color.parseColor("#004554"));
                    tv3.setTextColor(Color.parseColor("#FFFFFF"));

                    tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#A1A1A1"));
                }
                else{
                    iv1.setBackground(getResources().getDrawable(R.drawable.info));
                    iv2.setBackground(getResources().getDrawable(R.drawable.wave));
                    iv3.setBackground(getResources().getDrawable(R.drawable.taboption));

                    tv1.setTextColor(Color.parseColor("#FFFFFF"));
                    tv2.setTextColor(Color.parseColor("#FFFFFF"));
                    tv3.setTextColor(Color.parseColor("#004554"));

                    tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#A1A1A1"));
                }
            }
        });
    }
}