package my.id.andraaa.dstory.stories.presentor.auth.signin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.ActivitySignInBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.auth.signup.SignUpActivity
import my.id.andraaa.dstory.stories.presentor.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel by viewModel<SignInViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() = lifecycleScope.launchWhenResumed {
        launch {
            viewModel.state.collectLatest {
                var signInButtonEnabled = it.formIsValid()
                if (it.signInState is NetworkResource.Loading) {
                    signInButtonEnabled = false
                    binding.editTextEmail.isEnabled = false
                    binding.editTextPassword.isEnabled = false
                } else {
                    binding.editTextEmail.isEnabled = true
                    binding.editTextPassword.isEnabled = true
                }
                binding.buttonSignIn.isEnabled = signInButtonEnabled

                when (it.signInState) {
                    is NetworkResource.Error -> {
                        binding.textViewFormError.text = "Error: ${it.signInState.error.message}"
                        binding.textViewFormError.setTextColor(
                            resources.getColor(R.color.danger)
                        )
                    }
                    is NetworkResource.Loaded -> {
                        this@SignInActivity.startActivity(
                            Intent(
                                this@SignInActivity,
                                MainActivity::class.java
                            ).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                    }
                    is NetworkResource.Loading -> {
                        binding.textViewFormError.text =
                            this@SignInActivity.getText(R.string.loading)
                    }
                    null -> {
                        binding.textViewFormError.text = ""
                    }
                }
            }
        }

        binding.editTextEmail.doOnTextChanged { email, _, _, _ ->
            viewModel.dispatch(SignInAction.ChangeEmail(email.toString()))
        }
        binding.editTextPassword.doOnTextChanged { password, _, _, _ ->
            viewModel.dispatch(SignInAction.ChangePassword(password.toString()))
        }
        binding.buttonSignIn.setOnClickListener {
            viewModel.dispatch(SignInAction.ProceedAddStory)
        }
        binding.textViewSignUp.setOnClickListener {
            this@SignInActivity.startActivity(
                Intent(
                    this@SignInActivity, SignUpActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            )
        }
    }
}