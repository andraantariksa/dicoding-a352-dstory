package my.id.andraaa.dstory.stories.presentor.add_story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.FragmentAddStoryBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddStoryBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddStoryBinding
    private val viewModel by viewModel<AddStoryViewModel>()

    private val photoPickerActivityRequest =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.dispatch(AddStoryAction.ChangeImage(uri))
            }
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
                } else {
                    binding.editTextDescription.isEnabled = true
                }
                binding.buttonAddStory.isEnabled = signInButtonEnabled
                binding.imageView.isVisible = it.image != null
                binding.imageView.setImageURI(it.image)

                when (it.addStoryState) {
                    is NetworkResource.Error -> {
                        binding.textViewFormError.text = "Error: ${it.addStoryState.error.message}"
                        binding.textViewFormError.setTextColor(
                            resources.getColor(R.color.danger)
                        )
                    }
                    is NetworkResource.Loaded -> {
                        this@AddStoryBottomSheet.dismiss()
                    }
                    is NetworkResource.Loading -> {
                        binding.textViewFormError.text =
                            this@AddStoryBottomSheet.getText(R.string.loading)
                    }
                    null -> {
                        binding.textViewFormError.text = ""
                    }
                }
            }
        }

        binding.editTextDescription.doOnTextChanged { description, _, _, _ ->
            viewModel.dispatch(AddStoryAction.ChangeDescription(description.toString()))
        }
        binding.imageButton.setOnClickListener {
            photoPickerActivityRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}