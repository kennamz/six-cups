package six.cups.ui.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import six.cups.R
import six.cups.ui.blockBehindClicks
import six.cups.ui.theme.MyApplicationTheme

@Composable
fun MainScreenScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val journalState by viewModel.journalState.collectAsStateWithLifecycle()

    MainScreen(
        aspects = viewModel.healthAspects,
        uiState = uiState,
        onAspectTapped = viewModel::showJournalPrompt,
        journalState = journalState,
        journalEntryQuestions = viewModel.journalEntryQuestions,
        onVeilTapped = viewModel::hideJournalPrompt,
        onViewEntriesButtonClicked = viewModel::viewEntriesButtonClicked
    )
}

@Composable
internal fun MainScreen(
    aspects: List<HealthAspectDisplay>,
    uiState: MainUiState,
    onAspectTapped: (aspect: HealthAspectDisplay) -> Unit,
    journalState: JournalEntryUiState,
    journalEntryQuestions: List<JournalEntryQuestion>,
    onVeilTapped: () -> Unit,
    onViewEntriesButtonClicked: () -> Unit
) {
    Box {
        AspectButtons(
            aspects = aspects,
            onTapped = onAspectTapped
        )

        if (uiState is MainUiState.JournalEntry) {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.black_veil))
                    .fillMaxSize()
                    .clickable(
                        onClick = onVeilTapped
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier)

                val isWritingNewEntry = remember(uiState) { (uiState as? MainUiState.JournalEntry)?.writingNewEntry ?: true }
                JournalPromptDialog(
                    modifier = Modifier.blockBehindClicks(),
                    journalEntryQuestions = journalEntryQuestions,
                    isWritingNewEntry = isWritingNewEntry,
                    journalEntryState = journalState
                )

                Spacer(modifier = Modifier)

                if (isWritingNewEntry) {
                    ViewEntriesButton(
                        modifier = Modifier
                            .padding(
                                end = dimensionResource(id = R.dimen.dialog_screen_margin),
                                bottom = dimensionResource(id = R.dimen.dialog_screen_margin)
                            )
                            .align(Alignment.End),
                        onClick = onViewEntriesButtonClicked
                    )
                }
            }
        }
    }
}

@Composable
internal fun ViewEntriesButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.small_rounded_corner_radius)),
        onClick = onClick
    ) {
        Text("View Entries")
    }
}

@Composable
internal fun JournalPromptDialog(
    journalEntryState: JournalEntryUiState,
    isWritingNewEntry: Boolean,
    journalEntryQuestions: List<JournalEntryQuestion>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.dialog_screen_margin))
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.dialog_rounded_corner_radius))
            )
            .padding(dimensionResource(id = R.dimen.medium_padding))
    ) {
        if (isWritingNewEntry) {
            NewJournalEntry(questions = journalEntryQuestions)
        }
        else {
            when (journalEntryState) {
                is JournalEntryUiState.Success -> JournalEntrySuccess(
                    successState = journalEntryState
                )

                is JournalEntryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(dimensionResource(id = R.dimen.progress_spinner_width))
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onBackground,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

                is JournalEntryUiState.Error -> {
                    Text(text = stringResource(id = R.string.unknown_error))
                }
            }
        }
    }
}

@Composable
internal fun NewJournalEntry(
    questions: List<JournalEntryQuestion>,
    modifier: Modifier = Modifier
) {
    val entries = remember { mutableStateMapOf(*questions.map { it to "" }.toTypedArray()) }

    questions.forEach { questionType ->
        Text(text = stringResource(id = questionType.questionStringId))
        
        TextField(
            modifier = Modifier
                .padding(bottom = dimensionResource(id = R.dimen.small_medium_padding))
                .fillMaxWidth(),
            value = entries.getValue(questionType),
            onValueChange = { entries[questionType] = it }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { TODO() }
        ) {
            Text("Save")
        }
    }
}

@Composable
internal fun JournalEntrySuccess(
    successState: JournalEntryUiState.Success,
    modifier: Modifier = Modifier
) {
    successState.messages.forEach { message ->
        Text(text = message)
    }
}

@Composable
internal fun AspectButtons(
    aspects: List<HealthAspectDisplay>,
    onTapped: (aspect: HealthAspectDisplay) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
    ) {
        val rowAspects = aspects.chunked(2)
        rowAspects.forEach { rowAspect ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                rowAspect.forEach { aspect ->
                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        onClick = { onTapped(aspect) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = aspect.color)),
                        shape = RectangleShape
                    ) {
                        Text(text = stringResource(id = aspect.displayNameId))
                    }
                }
            }
        }
    }
}

// Previews

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun AspectsPreview() {
    MyApplicationTheme {
        MainScreen(
            aspects = HealthAspectDisplay.entries,
            uiState = MainUiState.AspectButtons,
            onAspectTapped = {},
            journalState = JournalEntryUiState.Loading,
            journalEntryQuestions = JournalEntryQuestion.entries,
            onViewEntriesButtonClicked = {},
            onVeilTapped = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun JournalPromptPreview() {
    MyApplicationTheme {
        MainScreen(
            aspects = HealthAspectDisplay.entries,
            uiState = MainUiState.JournalEntry(currentAspect = HealthAspectDisplay.Professional, writingNewEntry = false),
            onAspectTapped = {},
            journalState = JournalEntryUiState.Success(listOf("entry 1", "entry 2", "this is the third entry")),
            journalEntryQuestions = JournalEntryQuestion.entries,
            onViewEntriesButtonClicked = {},
            onVeilTapped = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun AddNewJournalEntryPreview() {
    MyApplicationTheme {
        MainScreen(
            aspects = HealthAspectDisplay.entries,
            uiState = MainUiState.JournalEntry(currentAspect = HealthAspectDisplay.Mental, writingNewEntry = true),
            onAspectTapped = {},
            journalState = JournalEntryUiState.Loading,
            journalEntryQuestions = JournalEntryQuestion.entries,
            onViewEntriesButtonClicked = {},
            onVeilTapped = {}
        )
    }
}
