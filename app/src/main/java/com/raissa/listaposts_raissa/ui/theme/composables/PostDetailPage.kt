package com.raissa.listaposts_raissa.ui.theme.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raissa.listaposts_raissa.jsonplaceholder.JSONPlaceholder
import com.raissa.listaposts_raissa.models.PostDetail
import com.raissa.listaposts_raissa.models.PostDetailComment
import com.raissa.listaposts_raissa.models.SuspendState
import com.raissa.listaposts_raissa.viewmodels.PostDetailPageViewModel
import com.raissa.listaposts_raissa.viewmodels.PostListPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

@Composable
fun PostDetailPage(viewModel: PostDetailPageViewModel, postId: String) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    LaunchedEffect(postId) {
        viewModel.downloadPostDetail(postId)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = state) {
                is SuspendState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }

                is SuspendState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SuspendState.Success -> {
                    LazyColumn {
                        item{
                            ElevatedCard(Modifier.padding(16.dp)) {
                                Column(Modifier.padding(16.dp)) {
                                    Badge {
                                        Text("Post #${state.data.id}")
                                    }
                                    Text(state.data.title)
                                    Text(state.data.body)
                                }
                            }

                            Text("Comments",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(state.data.comments) {
                            ElevatedCard(
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 8.dp)
                            ){
                                Column(Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                    Text(it.title)
                                    Badge {
                                        Text(it.commenterEmail)
                                    }
                                    Text(it.body)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}






