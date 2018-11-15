package com.example.user.friendsandfamily;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessAdapter extends FragmentPagerAdapter {


    public TabsAccessAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int i) {

        //For getting the position of Fragments class we've created.
        switch (i){
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;

            case 1:
                GroupFragment groupFragment=new GroupFragment();
                return groupFragment;

            case 2:
                ContactFragment contactFragment=new ContactFragment();
                return contactFragment;

                default:
                    return null;
        }

    }

    // the number of total fragment will be counted here.
    @Override
    public int getCount() {
        return 3;
    }


    // Set the tittle of each fragment by using this method.
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:

                return "Contact";

            default:
                return null;
        }
    }
}
