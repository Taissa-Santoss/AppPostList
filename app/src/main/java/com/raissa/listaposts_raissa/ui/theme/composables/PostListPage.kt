package com.raissa.listaposts_raissa.ui.theme.composables

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.raissa.listaposts_raissa.jsonplaceholder.JSONPlaceholder
import com.raissa.listaposts_raissa.models.PostListItem
import com.raissa.listaposts_raissa.models.SuspendState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListPage(onPostClick: (PostListItem) -> Unit) {
    var state by remember { mutableStateOf<SuspendState<List<PostListItem>>>(SuspendState.Loading) }
    val isLoading = state is SuspendState.Loading
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        state = try {
            SuspendState.Success(
                data = downloadPosts()
            )
        } catch (_: Exception) {
            SuspendState.Error(
                message = "Não foi possivel carregar a lista"
            )

        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Posts") },
                actions = {
                    TextButton(
                        // se isloading for falso, botão retry fica cinza, nao deixa carregar
                        enabled = !isLoading,
                        onClick = {
                            scope.launch {
                                state = SuspendState.Loading
                                state = try {
                                    SuspendState.Success(
                                        data = downloadPosts()
                                    )
                                } catch (_: Exception) {
                                    SuspendState.Error(
                                        message = "Não foi possivel atualizar a lista"
                                    )

                                }
                            }
                        }
                    ){
                        Text("Retry")
                    }
                }
            )
        }

    ) { innerPadding ->
        when ( val state = state) {
            is SuspendState.Error -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(state.message) }

            is SuspendState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            }
            is SuspendState.Success -> LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPadding)
            ) {
                items(state.data , key = { it.postId }) { post ->
                    PostListTile(post){
                        onPostClick(post)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostListTile(post: PostListItem, onClick: ()-> Unit) {
    Column(Modifier.fillMaxWidth().clickable{ onClick()}) {
        Text(
            "User ID: ${post.userId} • Post #${post.postId}",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            post.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            post.description,
            maxLines = 3
        )

        HorizontalDivider(Modifier)
    }
}


private suspend fun downloadPosts(): List<PostListItem> = withContext(Dispatchers.IO) {
    val jsonText = URL("https://jsonplaceholder.typicode.com/posts").readText()
    Json.decodeFromString<List<JSONPlaceholder.PostListItem>>(jsonText).map {it.toPostListItem()}
}