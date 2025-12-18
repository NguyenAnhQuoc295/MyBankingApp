package com.anhquoc.mybankingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.anhquoc.mybankingapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy SupportMapFragment và thông báo khi bản đồ sẵn sàng
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Hàm này được gọi khi bản đồ đã sẵn sàng để sử dụng.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 1. Vị trí giả lập của Ngân hàng (Ví dụ: Đại học Tôn Đức Thắng)
        val bankLocation = LatLng(10.732668, 106.699764)
        mMap.addMarker(
            MarkerOptions()
                .position(bankLocation)
                .title("Trụ sở Ngân hàng")
                .snippet("19 Nguyễn Hữu Thọ, Q.7")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        // 2. Vị trí giả lập của người dùng (Ví dụ: Gần đó)
        val userLocation = LatLng(10.738, 106.701)
        mMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title("Vị trí của bạn")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )

        // 3. Di chuyển camera đến vị trí ngân hàng và zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bankLocation, 15f))
    }
}