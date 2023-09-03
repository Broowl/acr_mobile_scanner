package com.acr_mobile_scanner

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.acr_mobile_scanner.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var _appBarConfiguration: AppBarConfiguration
    private lateinit var _binding: ActivityMainBinding
    private val _viewModel: EntityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewModel.setLogDir(applicationContext.filesDir.path)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setSupportActionBar(_binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        _appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, _appBarConfiguration)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_configuration -> {
                return onConfigurationSelected()
            }

            R.id.action_about -> {
                return onAboutSelected()
            }

            R.id.action_help -> {
                return onHelpSelected()
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onConfigurationSelected(): Boolean {
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.ConfigurationFragment)
        return true
    }

    private fun onAboutSelected(): Boolean {
        AlertDialog.Builder(this)
        .setMessage("Author: Daniel Krieger\n" +
                "Version: " + resources.getString(R.string.version))
        .setTitle("About")
        .setCancelable(true)
        .setPositiveButton("Ok") { _: DialogInterface, _: Int -> }
        .create().show()
        return true
    }

    private fun onHelpSelected(): Boolean {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Broowl/acr_mobile_scanner"))
        startActivity(browserIntent)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(_appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}