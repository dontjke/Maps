package com.stepanov.maps


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.stepanov.maps.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val mapsFragment = MapsFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, mapsFragment)
                .commit()
            mapsFragment.setViewModel(viewModel)
        }
    }

    private val viewModel: MarkerViewModel by lazy {
        ViewModelProvider(this)[MarkerViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.markers -> {
                val markersFragment = MarkersFragment.newInstance()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, markersFragment)
                    .addToBackStack("")
                    .commit()
                markersFragment.setViewModel(viewModel)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
