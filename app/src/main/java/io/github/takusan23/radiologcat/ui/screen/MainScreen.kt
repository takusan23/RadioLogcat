package io.github.takusan23.radiologcat.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.takusan23.radiologcat.LogCatTool
import io.github.takusan23.radiologcat.R
import io.github.takusan23.radiologcat.ui.component.SearchableTopBar
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val logList = remember { mutableStateListOf<LogCatTool.LogCatData>() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val isActive = remember { mutableStateOf(false) }
    val isSearchMode = remember { mutableStateOf(false) }
    val searchWord = remember { mutableStateOf("") }

    LaunchedEffect(isActive.value) {
        if (isActive.value) {
            LogCatTool.listenLogcat()
                .filter { it.message.contains(searchWord.value) }
                .collect { logList.add(0, it) }
        }
    }

    Scaffold(
        topBar = {
            SearchableTopBar(
                isSearch = isSearchMode.value,
                onSearchChange = { isSearchMode.value = false },
                searchWord = searchWord.value,
                onSearchWordChange = { searchWord.value = it }
            ) {
                IconButton(onClick = {
                    isSearchMode.value = true
                }) { Icon(painter = painterResource(id = R.drawable.outline_search_24), contentDescription = null) }
                IconButton(onClick = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }) { Icon(painter = painterResource(id = R.drawable.outline_vertical_align_top_24), contentDescription = null) }
                IconButton(onClick = {
                    logList.clear()
                }) { Icon(painter = painterResource(id = R.drawable.outline_delete_24), contentDescription = null) }
                IconButton(onClick = {
                    isActive.value = !isActive.value
                }) { Icon(painter = painterResource(id = if (isActive.value) R.drawable.baseline_pause_circle_outline_24 else R.drawable.outline_play_circle_outline_24), contentDescription = null) }
            }
        }
    )
    {
        LazyColumn(
            modifier = Modifier.padding(it),
            state = scrollState
        ) {
            items(logList) { log ->
                Text(text = "${log.date} ${log.time}")
                Text(text = log.message)
                Divider(modifier = Modifier.fillMaxWidth())
            }
        }
    }

}