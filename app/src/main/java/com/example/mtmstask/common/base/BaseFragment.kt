package com.example.mtmstask.common.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.mtmstask.ViewsManager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment @JvmOverloads constructor(
    @LayoutRes layoutId: Int = 0
) : Fragment(layoutId) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val userLocationMutable = MutableLiveData<Location>()
    private lateinit var locationRequest: LocationRequest

    val storagePermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    val locationPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private val permissionsResults = MutableLiveData<Map<String, Boolean>?>()
    val permissionsResultsLive = permissionsResults

    private val activityResultsData = MutableLiveData<Intent?>()
    val activityResultsDataLive = activityResultsData

    private var requestPermissionsCallBack = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsResults.value = it
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                activityResultsData.value = data
            } else {
                Log.d("myDebug", "BaseFragment resultLauncher ELse")
            }
        }

    override fun onStop() {
        super.onStop()
        permissionsResults.value = null
        activityResultsData.value = null
        hideLoadingView()
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermissionsCallBack.unregister()
        resultLauncher.unregister()
    }

    protected fun launchPermission(permissions: Array<String>) {
        try {
            requestPermissionsCallBack.launch(permissions)
        } catch (ex: ActivityNotFoundException) {
            Log.d("myDebug", "BaseFragment launchPermission  " + ex.localizedMessage)
        }
    }

    protected fun launchActivityForResult(intent: Intent) {
        try {
            resultLauncher.launch(intent)
        } catch (ex: Exception) {
            Log.d("myDebug", "BaseFragment launchActivityForResult  " + ex.localizedMessage)
        }
    }

    protected fun isPermissionGranted(permission: String): Boolean {
        return try {
            ContextCompat.checkSelfPermission(
                requireContext(), permission
            ) == PackageManager.PERMISSION_GRANTED
        } catch (ex: IllegalStateException) {
            return false
        }
    }

    protected fun makeToast(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show()
    }

    protected fun showLoadingView() {
        (requireContext() as ViewsManager).showProgressBar()
    }

    protected fun hideLoadingView() {
        (requireActivity() as ViewsManager).hideProgressBar()
    }

    fun showExplanation(
        title: String, message: String, permissions: Array<String>,
    ) {
        val builder = AlertDialog.Builder(
            requireContext(),
        )
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
        builder.create().show()
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        locationRequest = LocationRequest.create().apply {
            fastestInterval = 3000
            interval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                userLocationMutable.value = locationResult.lastLocation
                fusedLocationClient.removeLocationUpdates(locationCallback) // get location once and remove update
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()!!
        )
    }
}
