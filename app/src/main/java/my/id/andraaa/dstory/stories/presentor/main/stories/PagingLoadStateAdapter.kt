package my.id.andraaa.dstory.stories.presentor.main.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import my.id.andraaa.dstory.databinding.NetworkStateItemBinding

class PagingLoadStateAdapter(
    private val adapter: StoriesPagingAdapter
) : LoadStateAdapter<NetworkStateItemViewHolder>() {
    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NetworkStateItemBinding.inflate(layoutInflater, parent, false)
        return NetworkStateItemViewHolder(binding) {
            adapter.retry()
        }
    }
}

class NetworkStateItemViewHolder(
    private val binding: NetworkStateItemBinding,
    private val onRetry: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.buttonRetry.setOnClickListener {
            onRetry()
        }
    }

    fun bind(state: LoadState) {
        binding.apply {
            proggressBar.isVisible = state == LoadState.Loading
            buttonRetry.isVisible = state is LoadState.Error
            val errorMessage = (state as? LoadState.Error)?.error?.message
            buttonRetry.isVisible = !errorMessage.isNullOrBlank()
            textViewError.text = errorMessage
        }
    }
}
