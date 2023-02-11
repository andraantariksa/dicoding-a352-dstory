package my.id.andraaa.dstory.stories.presentor.main.maps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import my.id.andraaa.dstory.databinding.MapsInfoWindowBinding

class DStoryInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    data class Tag(
        val id: String
    )

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val layoutInflater = LayoutInflater.from(context)
        val binding = MapsInfoWindowBinding.inflate(layoutInflater, null, false)
        binding.apply {
            textViewTitle.text = marker.title
            textViewDescription.text = marker.snippet?.trim()
        }

        return binding.root
    }
}
