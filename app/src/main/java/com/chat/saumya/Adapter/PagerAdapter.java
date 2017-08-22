package com.chat.saumya.Adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chat.saumya.Fragments.ChatTabFragment;
import com.chat.saumya.Fragments.Contactlistfragmnt;

public class PagerAdapter extends FragmentPagerAdapter {
    String tabtitles[] = new String[]{"Chat","Contacts"};
    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatTabFragment();
            case 1:
                return new Contactlistfragmnt();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
