package my.id.andraaa.dstory.stories.presentor.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.id.andraaa.dstory.databinding.ActivityMainBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.auth.signin.SignInActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAuthState()
    }

    private fun setupAuthState() {
        lifecycleScope.launchWhenResumed {
            // Not sure why I need to use unconfied?????
            launch(Dispatchers.Unconfined) {
                mainViewModel.state.collectLatest { state ->
                    if (state.session is NetworkResource.Loaded) {
                        if (state.session.data == null) {
                            this@MainActivity.startActivity(
                                Intent(
                                    this@MainActivity, SignInActivity::class.java
                                )
                            )
                        } else {
                            withContext(Dispatchers.Main) {
                                val binding = ActivityMainBinding.inflate(layoutInflater)
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
}