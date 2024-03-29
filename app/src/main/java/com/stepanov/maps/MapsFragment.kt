package com.stepanov.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.stepanov.maps.databinding.FragmentMapsBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.image.ImageProvider


class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding
        get() {
            return _binding!!
        }
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session
    private lateinit var viewModel: MarkerViewModel

    companion object {
        @JvmStatic
        fun newInstance() = MapsFragment()
    }

    fun setViewModel(viewModel: MarkerViewModel) {
        this.viewModel = viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapKitFactory.initialize(requireContext())
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        binding.mapView.map.addInputListener(inputListener)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkPermission()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            explainAccessFineLocation()
        } else {
            mRequestPermissionFineLocation()
        }
    }

    private fun explainAccessFineLocation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_rationale_title))
            .setMessage(getString(R.string.permission))
            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                mRequestPermissionFineLocation()
            }
            .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun mRequestPermissionFineLocation() {
        locationResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val locationResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                moveToStartLocation(location)
                setMarkerInStartLocation(location)
            }
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val street = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }
                ?.name ?: getString(R.string.not_found)

            Toast.makeText(requireContext(), street, Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: com.yandex.runtime.Error) {
        }
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            searchSession = searchManager.submit(point, 20, SearchOptions(), searchListener)
        }

        override fun onMapLongTap(map: Map, point: Point) {
            val imageMarker = R.drawable.ic_map_pin
            mapObjectCollection =
                binding.mapView.map.mapObjects
            placemarkMapObject = mapObjectCollection.addPlacemark(
                Point(point.latitude, point.longitude),
                ImageProvider.fromResource(requireContext(), imageMarker)
            )
            placemarkMapObject.addTapListener(mapObjectTapListener)
            placemarkMapObject.setText(point.latitude.toString() + point.longitude.toString())

            viewModel.markers.add(placemarkMapObject)
            //Log.d("@@@", markers.toString())
        }
    }

    private val mapObjectTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            requireContext(),
            getString(R.string.mutabor) +
                    "${point.latitude}, ${point.longitude}) ",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    private fun setMarkerInStartLocation(location: Location) {
        val imageMarker = R.drawable.ic_map_pin
        mapObjectCollection =
            binding.mapView.map.mapObjects
        placemarkMapObject = mapObjectCollection.addPlacemark(
            Point(
                location.latitude, location.longitude
            ),
            ImageProvider.fromResource(requireContext(), imageMarker)
        )
        placemarkMapObject.addTapListener(mapObjectTapListener)
        placemarkMapObject.setText(getString(R.string.must_have))
    }

    private fun moveToStartLocation(location: Location) {
        binding.mapView.mapWindow.map.move(
            CameraPosition(
                Point(location.latitude, location.longitude),
                17.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}