package com.raissa.listaposts_raissa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.raissa.listaposts_raissa.models.PostListItem
import com.raissa.listaposts_raissa.ui.theme.composables.PostDetailPage
import com.raissa.listaposts_raissa.ui.theme.composables.PostListPage
import com.raissa.listaposts_raissa.ui.theme.theme.ListaPosts_RaissaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            ListaPosts_RaissaTheme {
                val backStack = remember { mutableStateListOf<Any>(AppRoute.PostList) }
                NavDisplay(
                    backStack = backStack,
                    onBack= { backStack.removeLastOrNull() },
                    entryProvider = {key ->
                        when (key){
                            is AppRoute.PostList -> NavEntry(key){
                                PostListPage(viewModel()) { post ->
                                    backStack.add(AppRoute.PostDetail(post.postId))
                                }
                            }
                            is AppRoute.PostDetail -> NavEntry(key) {
                                PostDetailPage(viewModel(), key.id)
                            }
                            else -> NavEntry(key) {
                                Text("404: Rota não encontrada")
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class AppRoute{
    data object PostList : AppRoute()
    data class PostDetail(val id: String): AppRoute()
}






