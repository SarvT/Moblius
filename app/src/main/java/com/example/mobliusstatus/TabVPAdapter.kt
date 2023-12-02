package com.example.mobliusstatus

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabVPAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    val fragmentItemList=ArrayList<Fragment>()
    val fragmentItemNamesList=ArrayList<String>()

    override fun getCount(): Int {
        return fragmentItemList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentItemList.get(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentItemNamesList.get(position)
    }

    public fun addFragment(fragment: Fragment, name:String){
        fragmentItemList.add(fragment)
        fragmentItemNamesList.add(name)
    }
}