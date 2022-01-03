package com.example.mtmstask.map.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtmstask.R
import com.example.mtmstask.ViewsManager
import com.example.mtmstask.common.base.BaseFragment
import com.example.mtmstask.database.model.SourceLocation
import com.example.mtmstask.database.model.SourceModel
import com.example.mtmstask.databinding.FragmentMapBinding
import com.example.mtmstask.handleData.HandleError
import com.example.mtmstask.map.adapter.PlacesAdapter
import com.example.mtmstask.map.adapter.SearchPlacesAdapter
import com.example.mtmstask.map.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    private val vm: MapViewModel by viewModels()
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var searchPlacesAdapter: SearchPlacesAdapter

    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    private lateinit var sourceList: List<SourceModel>
    private var sourceLocation: SourceLocation = SourceLocation()

    private val onPlacesClickCallback: (position: Int, sourceModel: SourceModel) -> Unit =
        { _, sourcePlace ->
            sourceLocation.name = sourcePlace.name
            sourceLocation.latitude = sourcePlace.latitude
            sourceLocation.longitude = sourcePlace.longitude
            binding.txtLocation.setText(sourcePlace.name)
            binding.rvPlaces.visibility = View.GONE
        }

    private val onSearchPlacesClickCallback: (position: Int, place: String) -> Unit =
        { _, place ->
            binding.txtDestination.setText(place)
            binding.rvPlaces.visibility = View.GONE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getAllSource()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)

        // Get the childFragmentManager when the map is ready to be used.
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)

        // check permission
        if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            getUserLocation()
        } else {
            launchPermission(locationPermissions)
        }

        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribePermissions()
        setBtnListener()
        subscribeHandleData()
    }

    override fun onMapReady(gm: GoogleMap) {
        googleMap = gm
        subscribeLocation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBtnListener() {
        binding.imgMenu.setOnClickListener {
            (requireActivity() as ViewsManager).openMenu()
        }

        binding.txtLocation.setOnTouchListener { _, _ ->
            initSourceRecycler(sourceList)
            true
        }

        binding.txtDestination.setOnClickListener {
            (requireContext() as ViewsManager).showProgressBar()
            vm.getAllPlaces(binding.txtDestination.text.toString())
        }
    }

    private fun initDestinationRecycler(placesList: List<String>) {
        if (placesList.isNotEmpty()) {
            binding.rvPlaces.visibility = View.VISIBLE
            binding.rvPlaces.apply {
                searchPlacesAdapter = SearchPlacesAdapter(placesList, onSearchPlacesClickCallback)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchPlacesAdapter
            }
        }
    }

    private fun initSourceRecycler(sourceList: List<SourceModel>) {
        binding.rvPlaces.visibility = View.VISIBLE
        binding.rvPlaces.apply {
            placesAdapter = PlacesAdapter(sourceList, onPlacesClickCallback)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placesAdapter
        }
    }

    private fun subscribeHandleData() {
        vm.handleData.observe(viewLifecycleOwner, {
            it.let {
                when (it.getStatus()) {
                    HandleError.DataStatus.SUCCESS -> {
                        hideLoadingView()
                        sourceList = it.getData()!!
                    }
                    HandleError.DataStatus.ERROR -> {
                        hideLoadingView()
                        makeToast(it.getError().toString())
                    }
                    HandleError.DataStatus.CONNECTIONERROR -> {
                        hideLoadingView()
                        makeToast(it.getConnectionError().toString())
                    }
                }
            }
        })

        vm.handlePlaces.observe(viewLifecycleOwner, {
            it.let {
                when (it.getStatus()) {
                    HandleError.DataStatus.SUCCESS -> {
                        hideLoadingView()
                        initDestinationRecycler(it.getData()!!)
                    }
                    HandleError.DataStatus.ERROR -> {
                        hideLoadingView()
                        makeToast(it.getError().toString())
                    }
                    HandleError.DataStatus.CONNECTIONERROR -> {
                        hideLoadingView()
                        makeToast(it.getConnectionError().toString())
                    }
                }
            }
        })
    }

    private fun subscribeLocation() {
        userLocationMutable.observe(viewLifecycleOwner, {
            it.let {
                googleMap.isMyLocationEnabled = true
                hideLoadingView()
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude), 11f
                    )
                )
                userLocationMutable.removeObservers(viewLifecycleOwner)
            }
        })
    }

    private fun subscribePermissions() {
        permissionsResultsLive.observe(viewLifecycleOwner, {
            it?.keys.apply {
                this?.forEach { st ->
                    when (st) {
                        Manifest.permission.ACCESS_FINE_LOCATION -> {
                            if (it?.get(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                                getUserLocation()
                            } else {
                                showExplanation(
                                    getString(R.string.permission_needed),
                                    getString(R.string.picture_permission_explanation),
                                    storagePermissions
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mapFragment.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        mapFragment.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapFragment.onSaveInstanceState(outState)
    }
}