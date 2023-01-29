package my.id.andraaa.dstory.stories.presentor.auth.signup

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import my.id.andraaa.dstory.R
import my.id.andraaa.dstory.databinding.ActivitySignUpBinding
import my.id.andraaa.dstory.stories.domain.NetworkResource
import my.id.andraaa.dstory.stories.presentor.add_story.AddStoryBottomSheet
import my.id.andraaa.dstory.stories.presentor.auth.signin.SignInActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModel<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() = lifecycleScope.launchWhenResumed {
        launch {
            viewModel.state.collectLatest {
                var signUpButtonEnabled = it.formIsValid()
                if (it.signUpState is NetworkResource.Loading) {
                    signUpButtonEnabled = false
                    binding.editTextEmail.isEnabled = false
                    binding.editTextName.isEnabled = false
                    binding.editTextPassword.isEnabled = false
                } else {
                    binding.editTextEmail.isEnabled = true
                    binding.editTextName.isEnabled = true
                    binding.editTextPassword.isEnabled = true
                }
                binding.buttonSignUp.isEnabled = signUpButtonEnabled

                when (it.signUpState) {
                    is NetworkResource.Error -> {
                        binding.textViewFormError.text = "Error: ${it.signUpState.error.message}"
                        binding.textViewFormError.setTextColor(
                            resources.getColor(R.color.danger)
                        )
                    }
                    is NetworkResource.Loaded -> {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Account is created!",
                            Toast.LENGTH_LONG
                        ).show()
                        this@SignUpActivity.startActivity(
                            Intent(
                                this@SignUpActivity,
                                SignInActivity::class.java
                            ).apply {
                                flags = FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                    }
                    is NetworkResource.Loading -> {
                        binding.textViewFormError.text =
                            this@SignUpActivity.getText(R.string.loading)
                    }
                    null -> {
                        binding.textViewFormError.text = ""
                    }
                }
            }
        }

        binding.editTextName.doOnTextChanged { name, _, _, _ ->
            viewModel.dispatch(SignUpAction.ChangeName(name.toString()))
        }
        binding.editTextEmail.doOnTextChanged { email, _, _, _ ->
            viewModel.dispatch(SignUpAction.ChangeEmail(email.toString()))
        }
        binding.editTextPassword.doOnTextChanged { password, _, _, _ ->
            viewModel.dispatch(SignUpAction.ChangePassword(password.toString()))
        }
        binding.buttonSignUp.setOnClickListener {
            viewModel.dispatch(SignUpAction.ProceedSignUp)
        }
        binding.textViewSignIn.setOnClickListener {
            this@SignUpActivity.startActivity(
                Intent(
                    this@SignUpActivity, AddStoryBottomSheet::class.java
                ).apply {
                    flags = FLAG_ACTIVITY_CLEAR_TOP
                }
            )
        }
    }
}