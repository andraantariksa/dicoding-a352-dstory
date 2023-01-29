package my.id.andraaa.dstory.stories.presentor.add_story

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
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
                    viewModel.dispatch(
                        AddStoryAction.ChangeImage(
                            tempFile.getContentUri(
                                requireContext()
                            )
                        )
                    )
                }
            }
        cameraActivityRequest =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
                if (saved) {
                    viewModel.dispatch(AddStoryAction.ChangeImage(tempFile.toUri()))
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

    private fun setupUI() = lifecycleScope.launchWhenResumed {
        launch {
            viewModel.state.collectLatest {
                var signInButtonEnabled = it.formIsValid()
                if (it.addStoryState is NetworkResource.Loading) {
                    signInButtonEnabled = false
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
                binding.buttonAddStory.isEnabled = signInButtonEnabled
                binding.imageView.isVisible = it.image != null
                binding.imageViewRemove.isVisible = it.image != null
                binding.imageView.setImageURI(it.image)

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
        binding.buttonAddStory.setOnClickListener {
            viewModel.dispatch(AddStoryAction.ProceedAddStory)
        }
    }
}