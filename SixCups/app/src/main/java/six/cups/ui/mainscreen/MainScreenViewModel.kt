package six.cups.ui.mainscreen

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import six.cups.R
import six.cups.data.MainScreenRepository
import six.cups.ui.mainscreen.MainScreenUiState.Success
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val mainScreenRepository: MainScreenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainScreenUiState>(Success(HealthAspectDisplay.entries))
    val uiState = _uiState.asStateFlow()

    fun addMainScreen(index: Int) {
        viewModelScope.launch {
            // TODO
        }
    }
}

sealed interface MainScreenUiState {
    object Loading : MainScreenUiState
    data class Error(val throwable: Throwable) : MainScreenUiState
    data class Success(val aspects: List<HealthAspectDisplay>) : MainScreenUiState
}

enum class HealthAspectDisplay(
    @StringRes
    val displayNameId : Int,
    @ColorRes
    val color: Int
    //TODO: icon res
) {
    Physical(R.string.physical, R.color.red),
    Professional(R.string.professional, R.color.orange),
    Social(R.string.social, R.color.yellow),
    Cultural(R.string.cultural, R.color.green),
    Mental(R.string.mental, R.color.blue),
    Spiritual(R.string.spiritual, R.color.purple)
}
