import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.annyslamp.core.viewmodel.ConnectionViewModel
import com.example.annyslamp.data.models.ESPWebSocketViewModel

class ESPWebSocketViewModelFactory(
    private val connectionViewModel: ConnectionViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ESPWebSocketViewModel::class.java)) {
            return ESPWebSocketViewModel(connectionViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}