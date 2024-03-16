package six.cups.ui.mainscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    MainScreenScreen(
        uiState = uiState,
        onVeilTapped = viewModel::hideJournalPrompt,
        onAspectTapped = viewModel::showJournalPrompt
    )
}

@Composable
internal fun MainScreenScreen(
    uiState: MainScreenUiState,
    onVeilTapped: () -> Unit,
    onAspectTapped: (aspect: HealthAspectDisplay) -> Unit,
) {
    Box {
        AspectButtons(
            aspects = uiState.aspects,
            onTapped = onAspectTapped
        )

        AnimatedVisibility(
            visible = uiState.journalEntry != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(colorResource(id = R.color.black_veil))
                    .fillMaxSize()
                    .clickable(
                        onClick = onVeilTapped
                    ),
                contentAlignment = Alignment.Center
            ) {
                uiState.journalEntry?.let { journalEntry ->
                    JournalPromptDialog(
                        modifier = Modifier.blockBehindClicks(),
                        journalEntryState = journalEntry
                    )
                }
            }
        }
    }
}

@Composable
internal fun JournalPromptDialog(
    journalEntryState: JournalEntryUiState,
    modifier: Modifier = Modifier
) {
    when(journalEntryState) {
        is JournalEntryUiState.Success -> JournalEntrySuccess(
            successState = journalEntryState,
            modifier = modifier
        )
        is JournalEntryUiState.Loading -> {
            TODO()
        }
        is JournalEntryUiState.Error -> {
            TODO()
        }
    }
}

@Composable
internal fun JournalEntrySuccess(
    successState: JournalEntryUiState.Success,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(20.dp)
    ) {
        Text(text = successState.message)
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
        MainScreenScreen(
            MainScreenUiState(
                aspects = HealthAspectDisplay.entries,
                journalEntry = null
            ),
            onVeilTapped = {},
            onAspectTapped = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun JournalPromptPreview() {
    MyApplicationTheme {
        MainScreenScreen(
            MainScreenUiState(
                aspects = HealthAspectDisplay.entries,
                journalEntry = JournalEntryUiState.Success("kenna's preview message")
            ),
            onVeilTapped = {},
            onAspectTapped = {}
        )
    }
}
