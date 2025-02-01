package io.github.takusan23.radiologcat.ui.screen

import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.contentValuesOf
import io.github.takusan23.radiologcat.LogCatTool
import io.github.takusan23.radiologcat.R
import io.github.takusan23.radiologcat.ui.component.SearchableTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val logList = remember { mutableStateListOf<LogCatTool.LogCatData>() }
    val context = LocalContext.current
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

    /** Logcat テキストファイルにしてドキュメントフォルダに保存する */
    fun saveLogToText() {
        scope.launch {
            withContext(Dispatchers.IO) {
                val values = contentValuesOf(
                    MediaStore.Files.FileColumns.DISPLAY_NAME to "RadioLogcat_${System.currentTimeMillis()}.txt",
                    MediaStore.Files.FileColumns.RELATIVE_PATH to "Documents",
                )
                val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val uri = context.contentResolver.insert(collection, values)!!
                context.contentResolver.openOutputStream(uri, "w")?.bufferedWriter()?.use { writer ->
                    logList.forEach { log ->
                        writer.write("${log.date} ${log.time}")
                        writer.newLine()
                        writer.write(log.message)
                        writer.newLine()
                        writer.write("---")
                        writer.newLine()
                    }
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "保存しました", Toast.LENGTH_SHORT).show()
            }
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
                    saveLogToText()
                }) { Icon(painter = painterResource(id = R.drawable.save_24px), contentDescription = null) }
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
    { innerPadding ->
        LazyColumn(
            state = scrollState,
            contentPadding = innerPadding
        ) {
            items(logList) { log ->
                Text(text = "${log.date} ${log.time}")
                Text(text = log.message)
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }

}