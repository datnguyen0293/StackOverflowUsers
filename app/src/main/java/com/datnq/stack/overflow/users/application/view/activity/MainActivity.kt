package com.datnq.stack.overflow.users.application.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.datnq.stack.overflow.users.R
import com.datnq.stack.overflow.users.application.model.Tab
import com.datnq.stack.overflow.users.application.view.adapter.TabAdapter
import com.datnq.stack.overflow.users.application.view.fragment.AllUsersFragment
import com.datnq.stack.overflow.users.application.view.fragment.FavoriteUsersFragment
import com.datnq.stack.overflow.users.application.view.listener.FragmentListener
import com.datnq.stack.overflow.users.core.BaseActivity
import com.datnq.stack.overflow.users.core.BaseFragment
import com.datnq.stack.overflow.users.databinding.ActivityMainBinding

/**
 * @author dat nguyen
 * @since 2019 Sep 13
 */
class MainActivity : BaseActivity<ActivityMainBinding>(), FragmentListener {
    
    private var adapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val tabs: ArrayList<Tab> = arrayListOf(
            Tab(R.drawable.ic_home, R.string.all),
            Tab(R.drawable.ic_favorite, R.string.favorite)
        )
        adapter = TabAdapter()
        adapter?.setListener(this)
        binding.tabLayout.layoutManager = GridLayoutManager(this, 2)
        binding.tabLayout.setHasFixedSize(true)
        binding.tabLayout.adapter = adapter
        adapter?.setData(tabs)
        moveToFragment(AllUsersFragment())
    }

    override fun onDestroy() {
        adapter?.setListener(null)
        adapter = null
        super.onDestroy()
    }

    private fun moveToFragment(fragment: BaseFragment<*>) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, fragment, fragment::class.java.simpleName)
            .commitAllowingStateLoss()
    }

    override fun moveTo(position: Int) {
        moveToFragment(
            if (position == 0) {
                AllUsersFragment()
            } else {
                FavoriteUsersFragment()
            }
        )
    }

}