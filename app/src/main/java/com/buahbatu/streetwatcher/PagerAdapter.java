package com.buahbatu.streetwatcher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by maaakbar on 10/29/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                //Fragment for ID Tab
                return new IdentityFragment();
            case 1:
                //Fragement for Camera Tab
                return new CameraFragment();
            case 2:
                //Fragment for Mic Tab
                return new MicrophoneFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mNumOfTabs; //No of Tabs
    }

}
