package six.cups.ui.mainscreen

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import six.cups.R
import six.cups.data.MainScreenRepository
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val mainScreenRepository: MainScreenRepository
) : ViewModel() {
    val healthAspects = HealthAspectDisplay.entries
    val journalEntryQuestions = JournalEntryQuestion.entries

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.AspectButtons)
    val uiState = _uiState.asStateFlow()

    val journalState: StateFlow<JournalEntryUiState> = mainScreenRepository
        .mainScreens.map<List<String>, JournalEntryUiState>(JournalEntryUiState::Success)
        .catch { emit(JournalEntryUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JournalEntryUiState.Loading)

    fun addJournalEntry(entry: String) {
        viewModelScope.launch {
            mainScreenRepository.add(entry)
        }
    }

    fun showJournalPrompt(aspect: HealthAspectDisplay) {
        _uiState.value = MainUiState.JournalEntry(aspect, writingNewEntry = true)
    }

    fun hideJournalPrompt() {
        _uiState.value = MainUiState.AspectButtons
    }

    fun viewEntriesButtonClicked() {
        _uiState.value = (_uiState.value as MainUiState.JournalEntry).copy(writingNewEntry = false)
    }
}

sealed interface MainUiState {
    data object AspectButtons : MainUiState
    data class JournalEntry(
        val currentAspect: HealthAspectDisplay,
        val writingNewEntry: Boolean
    ) : MainUiState
}

sealed interface JournalEntryUiState {
    data object Loading : JournalEntryUiState
    data class Error(val throwable: Throwable) : JournalEntryUiState
    data class Success(val messages: List<String>) : JournalEntryUiState
}

enum class JournalEntryQuestion(
    @StringRes
    val questionStringId : Int
) {
    Doing(R.string.question_doing),
    Goals(R.string.question_goal),
    Importance(R.string.question_importance),
    Improve(R.string.question_improve),
    Effects(R.string.question_effects)
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
