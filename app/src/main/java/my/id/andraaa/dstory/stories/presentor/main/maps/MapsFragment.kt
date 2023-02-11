package my.id.andraaa.dstory.stories.presentor.main.maps

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.FragmentMapsBinding
import my.id.andraaa.dstory.stories.presentor.story.StoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {
    private val viewModel by viewModel<MapsViewModel>()

    private var _map: GoogleMap? = null
    private val map: GoogleMap
        get() = _map!!

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding
        get() = _binding!!

    private var mapFragment: SupportMapFragment? = null

    override fun onDestroyView() {
        super.onDestroyView()

        _map = null
        _binding = null
        mapFragment = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionX.init(this).permissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .explainReasonBeforeRequest().onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList, "DStory Maps needs your location permission", "OK", "Cancel"
                )
            }.request { _, _, _ ->
                viewModel.dispatch(MapsAction.FetchCurrentLocation(requireContext()))
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = binding.fragmentContainerViewMap.getFragment<SupportMapFragment>()
        mapFragment?.getMapAsync {
            _map = it
            setupUI()
        }
    }

    private fun setupUI() {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        map.setInfoWindowAdapter(DStoryInfoWindowAdapter(mapFragment!!.requireContext()))
        map.setOnInfoWindowClickListener { marker ->
            val tag = marker.tag as? DStoryInfoWindowAdapter.Tag
            val intent = Intent(context, StoryActivity::class.java).apply {
                putExtra(
                    StoryActivity.STORY_ID_EXTRA,
                    tag?.id
                )
            }
            context?.startActivity(intent)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.sideEffect.onEach {
                when (it) {
                    is MapsSideEffect.Snackbar -> {
                        Snackbar.make(binding.root, it.messsage, it.duration).show()
                    }
                }
            }.launchIn(this)

            viewModel.state.map { it.userLocation }.distinctUntilChanged().onEach {
                it?.dataOrNull?.let { coordinate ->
                    map.moveCamera(CameraUpdateFactory.newLatLng(coordinate.toLatLng()))
                }
            }.launchIn(this)

            viewModel.state.map { it.stories }.onEach { stories ->
                stories.dataOrNull?.forEach { story ->
                    map.addMarker(MarkerOptions().apply {
                        position(LatLng(story.lat!!, story.lon!!))
                        title(story.name)
                        snippet(story.description)
                    })?.apply {
                        tag = DStoryInfoWindowAdapter.Tag(story.id)
                    }
                }
            }.launchIn(this)
        }
    }
}