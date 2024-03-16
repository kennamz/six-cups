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
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val mainScreenRepository: MainScreenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            aspects = HealthAspectDisplay.entries,
            journalEntry = null
        )
    )
    val uiState = _uiState.asStateFlow()

    fun showJournalPrompt(aspect: HealthAspectDisplay) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(journalEntry = JournalEntryUiState.Success("kenna success :) ${aspect.name}"))
        }
    }

    fun hideJournalPrompt() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(journalEntry = null)
        }
    }
}

data class MainScreenUiState (
    val aspects: List<HealthAspectDisplay>,
    val journalEntry: JournalEntryUiState?
)

sealed interface JournalEntryUiState {
    object Loading : JournalEntryUiState
    data class Error(val throwable: Throwable) : JournalEntryUiState
    data class Success(val message: String) : JournalEntryUiState
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
