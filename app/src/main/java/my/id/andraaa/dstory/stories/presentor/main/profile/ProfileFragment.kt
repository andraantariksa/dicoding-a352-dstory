package my.id.andraaa.dstory.stories.presentor.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.FragmentProfileBinding
import my.id.andraaa.dstory.stories.data.Dicebear
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.auth.signin.SignInActivity
import my.id.andraaa.dstory.stories.presentor.main.MainAction
import my.id.andraaa.dstory.stories.presentor.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        lifecycleScope.launchWhenResumed {
            launch {
                mainViewModel.state.collectLatest { state ->
                    if (state.session is NetworkResource.Loaded && state.session.data != null) {
                        val session = state.session.data
                        binding.textViewName.text = session.name
                        val avatar = Dicebear.getAvatarUrl(session.name)
                        binding.imageViewProfilePicture.load(avatar)
                    }
                }
            }
        }

        binding.textViewSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.signout_confirmation)
                .setPositiveButton(R.string.yes) { _, _ ->
                    startActivity(Intent(requireContext(), SignInActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    mainViewModel.dispatch(MainAction.SignOut)
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }
    }
}