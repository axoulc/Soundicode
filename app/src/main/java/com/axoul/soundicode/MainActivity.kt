package com.axoul.soundicode

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.axoul.soundicode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val requestRecordAudioPermission = 200
    private val permission = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var audioRecordingPermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, permission, requestRecordAudioPermission)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_mic, R.id.navigation_file, R.id.navigation_history
        )
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestRecordAudioPermission -> audioRecordingPermissionGranted =
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!audioRecordingPermissionGranted) {
            finish()
        }
    }

    public override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                Log.i(MotionEffect.TAG, "Uri: " + uri.toString())
            }
        }
    }

}