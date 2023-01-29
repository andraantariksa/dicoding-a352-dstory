package my.id.andraaa.dstory.stories.presentor.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.databinding.ActivityMainBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.auth.signin.SignInActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()

        setupAuthState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setupAuthState()
    }

    private fun setupAuthState() {
        lifecycleScope.launchWhenResumed {
            launch {
                mainViewModel.state.collectLatest { state ->
                    if (state.session is NetworkResource.Loaded) {
                        if (state.session.data == null) {
                            this@MainActivity.startActivity(
                                Intent(
                                    this@MainActivity,
                                    SignInActivity::class.java
                                )
                            )
                        } else {
                            setContentView(binding.root)

                            val navController =
                                binding.fragmentContainerView.findNavController()
                            binding.bottomNavigation.setupWithNavController(navController)
                        }
                    }
                }
            }
        }
    }
}