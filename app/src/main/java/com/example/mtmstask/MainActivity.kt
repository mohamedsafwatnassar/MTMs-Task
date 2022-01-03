package com.example.mtmstask

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mtmstask.databinding.ActivityMainBinding
import com.example.mtmstask.broadcast.ConnectivityReceiver
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener, ViewsManager{

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize Snack bar
        initSnackBar()

        // initialize Broadcast Receiver
        initBroadcastReceiver()

        // initialize navigation component with drawer layout
        initNavigationComponent()
    }

    private fun initNavigationComponent() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        binding.navigationView.setupWithNavController(navController)

        val drawerToggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, null, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initSnackBar() {
        snackBar = Snackbar.make(
            binding.root, getString(R.string.checkConnection), Snackbar.LENGTH_INDEFINITE
        )
    }

    private fun initBroadcastReceiver() {
        registerReceiver(
            ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            snackBar!!.show()
        } else if (isConnected) {
            if (snackBar!!.isShown) {
                snackBar!!.dismiss()
            }
        }
    }

    override fun showProgressBar() {
        binding.loadingView.root.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.loadingView.root.visibility = View.GONE
    }

    override fun openMenu() {
        binding.drawerLayout.open()
    }

    override fun closeMenu() {
        binding.drawerLayout.close()
    }
}