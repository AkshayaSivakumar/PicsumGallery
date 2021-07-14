package com.experiment.android.picsumgallery.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.experiment.android.picsumgallery.R
import com.experiment.android.picsumgallery.databinding.ActivityBaseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityBaseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setSupportActionBar(binding.toolbar)
        setupNavigationController()
        setupAppBarConfiguration()
    }

    /**
     * Setup Binding
     */
    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base)
    }

    /**
     * Setup navigation controller
     */
    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { _: NavController, destination: NavDestination, _: Bundle? ->
            title = when (destination.id) {
                R.id.picsumGalleryFragment -> resources.getString(R.string.fragment_name_picsum_gallery)
                R.id.detailsFragment -> resources.getString(R.string.fragment_name_details)
                R.id.aboutFragment -> resources.getString(R.string.fragment_name_about)
                else -> resources.getString(R.string.fragment_name_picsum_gallery)
            }
        }
    }

    /**
     * Setup appbar configuration
     */
    private fun setupAppBarConfiguration() {
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}