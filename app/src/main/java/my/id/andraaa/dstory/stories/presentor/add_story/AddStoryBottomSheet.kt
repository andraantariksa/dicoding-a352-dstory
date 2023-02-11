package my.id.andraaa.dstory.stories.presentor.add_story

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.FragmentAddStoryBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.util.getContentUri
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddStoryBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddStoryBinding
    private val viewModel by viewModel<AddStoryViewModel>()

    private lateinit var photoPickerActivityRequest: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var cameraActivityRequest: ActivityResultLauncher<Uri>
    var onFinished: (() -> Unit)? = null
    private lateinit var tempFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tempFile = File.createTempFile("add_story", null, requireActivity().cacheDir)
        photoPickerActivityRequest =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                        launch(Dispatchers.IO) {
                            context?.let { context ->
                                val stream = context.contentResolver.openInputStream(uri)!!
                                val bytes = stream.readBytes()
                                stream.close()
                                viewModel.dispatch(AddStoryAction.ChangeImage(bytes))
                            }
                        }
                    }
                }
            }
        cameraActivityRequest =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
                if (saved) {
                    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                        launch(Dispatchers.IO) {
                            val bytes = tempFile.readBytes()
                            viewModel.dispatch(AddStoryAction.ChangeImage(bytes))
                        }
                    }
                }
            }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        viewModel.dispatch(AddStoryAction.Reset)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStoryBinding.inflate(layoutInflater)
        setupUI()
        return binding.root
    }

    private fun setupUI() = viewLifecycleOwner.lifecycleScope.launchWhenResumed {
        viewModel.state.map { state -> state.image }.distinctUntilChanged().onEach {
            if (it != null) {
                binding.imageView.isVisible = true
                binding.imageView.load(it.value)
            } else {
                binding.imageView.isVisible = false
            }
        }.launchIn(this)

        launch {
            viewModel.state.collectLatest {
                var addStoryButtonEnabled = it.formIsValid()
                if (it.addStoryState is NetworkResource.Loading) {
                    addStoryButtonEnabled = false
                    binding.editTextDescription.isEnabled = false
                    binding.imageViewCamera.isEnabled = false
                    binding.imageViewGallery.isEnabled = false
                    binding.imageViewRemove.isEnabled = false
                } else {
                    binding.editTextDescription.isEnabled = true
                    binding.imageViewCamera.isEnabled = true
                    binding.imageViewGallery.isEnabled = true
                    binding.imageViewRemove.isEnabled = true
                }
                binding.buttonAddStory.isEnabled = addStoryButtonEnabled
                binding.imageViewRemove.isVisible = it.image != null

                if (it.shareCurrentLocation) {
                    binding.imageViewShareCurrentLocation.setBackgroundColor(
                        requireActivity().getColor(
                            R.color.primary
                        )
                    )
                    binding.imageViewShareCurrentLocation.setImageResource(R.drawable.baseline_location_on_24_active)

                    if (ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        viewModel.dispatch(AddStoryAction.ToggleShareCurrentLocation)
                    }
                } else {
                    binding.imageViewShareCurrentLocation.setBackgroundColor(
                        requireActivity().getColor(
                            R.color.background
                        )
                    )
                    binding.imageViewShareCurrentLocation.setImageResource(R.drawable.baseline_location_on_24)
                }

                when (it.addStoryState) {
                    is NetworkResource.Error -> {
                        binding.textViewFormError.text = "Error: ${it.addStoryState.error.message}"
                        binding.textViewFormError.setTextColor(
                            resources.getColor(R.color.danger)
                        )
                        binding.buttonAddStory.isEnabled = true
                    }
                    is NetworkResource.Loaded -> {
                        this@AddStoryBottomSheet.onFinished?.invoke()
                        this@AddStoryBottomSheet.dismiss()
                    }
                    is NetworkResource.Loading -> {
                        binding.textViewFormError.text =
                            this@AddStoryBottomSheet.getText(R.string.loading)
                        binding.buttonAddStory.isEnabled = false
                    }
                    null -> {
                        binding.textViewFormError.text = ""
                        binding.buttonAddStory.isEnabled = true
                    }
                }
            }
        }

        binding.editTextDescription.doOnTextChanged { description, _, _, _ ->
            viewModel.dispatch(AddStoryAction.ChangeDescription(description.toString()))
        }
        binding.imageViewGallery.setOnClickListener {
            photoPickerActivityRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.imageViewCamera.setOnClickListener {
            cameraActivityRequest.launch(
                tempFile.getContentUri(
                    requireContext()
                )
            )
        }
        binding.imageViewRemove.setOnClickListener {
            viewModel.dispatch(AddStoryAction.RemoveImage)
        }
        binding.imageViewShareCurrentLocation.setOnClickListener {
            PermissionX.init(this@AddStoryBottomSheet).permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).explainReasonBeforeRequest().onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "DStory Maps needs your location permission to share your location",
                    "OK",
                    "Cancel"
                )
            }.request { _, _, _ ->
                viewModel.dispatch(AddStoryAction.ToggleShareCurrentLocation)
            }
        }
        binding.buttonAddStory.setOnClickListener {
            viewModel.dispatch(AddStoryAction.ProceedAddStory(requireContext()))
        }
    }
}