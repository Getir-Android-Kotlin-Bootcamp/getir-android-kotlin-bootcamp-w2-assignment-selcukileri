package com.selcukileri.foodcouriers

import android.content.Context.LOCATION_SERVICE
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.selcukileri.foodcouriers.databinding.FragmentMapsBinding
import java.io.IOException
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        binding = FragmentMapsBinding.bind(view)
        binding.searchButton.setOnClickListener {
            showAddressOnMap()
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val currentLocation = LatLng(41.025569, 28.9715537)

        mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

        mMap.setOnMapClickListener { latLng ->
            addMarker(latLng)
            resolveAddress(latLng)
        }

        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager

    }

    private fun addMarker(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
    }

    private fun resolveAddress(latLng: LatLng) {
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.let {
                if (it.isNotEmpty()) {
                    val address: String = it[0].getAddressLine(0)
                    setAddress(address)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAddress(address: String) {

        binding.addressTextView.text = address
        //@drawable/ic_location 
    }

    private fun showAddressOnMap() {
        val address = binding.locationAddress.text.toString()
        if (address.isNotEmpty()) {
            setAddress(address)
            println("address: $address")
            try {
                val addressList: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
                println("addressList:$addressList")
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                        println("latlng $latLng")
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        mMap.clear()
                        addMarker(latLng)
                    }
                }
            } catch (e: IOException) {
                //binding.addressTextView.text = e.printStackTrace().toString()
                println("errorrr ${e.printStackTrace()}")
            }
        }
    }
}