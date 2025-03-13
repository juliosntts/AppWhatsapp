package com.juliosantos.aulawhatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juliosantos.aulawhatsapp.fragments.ContactsFragment
import com.juliosantos.aulawhatsapp.fragments.ConversationsFragment

class ViewPagerAdapter(private val tabs: List<String>, fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter( fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return tabs.size
    } // get the amount of items

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContactsFragment()
        }
        return ConversationsFragment()
    } // creates the fragment and associates it with its position

}