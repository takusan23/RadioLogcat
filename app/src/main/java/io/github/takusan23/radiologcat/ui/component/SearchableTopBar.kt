package io.github.takusan23.radiologcat.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.radiologcat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableTopBar(
    modifier: Modifier = Modifier,
    isSearch: Boolean,
    searchWord: String,
    onSearchWordChange: (String) -> Unit,
    onSearchChange: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    if (isSearch) {
        Surface(
            modifier = modifier.padding(5.dp),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val color = MaterialTheme.colorScheme.primary
                IconButton(onClick = onSearchChange) {
                    Icon(painter = painterResource(id = R.drawable.outline_arrow_back_24), contentDescription = null)
                }
                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .drawBehind {
                            drawLine(
                                color = color,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                        },
                    value = searchWord,
                    onValueChange = onSearchWordChange
                )
                IconButton(onClick = { onSearchWordChange("") }) {
                    Icon(painter = painterResource(id = R.drawable.outline_close_24), contentDescription = null)
                }
            }
        }
    } else {
        TopAppBar(
            modifier = modifier,
            title = { Text(text = stringResource(id = R.string.app_name)) },
            actions = actions
        )
    }
}