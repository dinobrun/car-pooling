package it.uniba.di.sms.carpooling.Passaggio;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import it.uniba.di.sms.carpooling.R;
import it.uniba.di.sms.carpooling.SectionsPageAdapter;

public class RidesActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);
        Log.d(TAG, "onCreate: Starting.");

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setTitle(R.string.my_rides);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        (RidesActivity.this).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RidesActivity.this.onBackPressed();
            }
        });



        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new RequestedRidesFragment(), getString(R.string.requested_rides));
        adapter.addFragment(new OfferedRidesFragment(),getString(R.string.offered_rides));

        viewPager.setAdapter(adapter);
    }
}
