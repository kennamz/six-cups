package six.cups.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import six.cups.ui.theme.MyApplicationTheme

@Composable
fun MainScreenScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items is MainScreenUiState.Success) {
        MainScreenScreen(
            aspects = (items as MainScreenUiState.Success).aspects,
            onTapped = viewModel::addMainScreen,
            modifier = modifier
        )
    }
}

@Composable
internal fun MainScreenScreen(
    aspects: List<HealthAspectDisplay>,
    onTapped: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
    ) {
        val rowAspects = aspects.chunked(2)
        rowAspects.forEachIndexed { rowIndex, rowAspect ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                rowAspect.forEachIndexed { columnIndex, aspect ->
                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        onClick = { onTapped(rowIndex + columnIndex) },
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
private fun DefaultPreview() {
    MyApplicationTheme {
        MainScreenScreen(
            HealthAspectDisplay.entries,
            onTapped = {}
        )
    }
}
