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
import com.raissa.listaposts_raissa.jsonplaceholder.JSONPlaceholder
import com.raissa.listaposts_raissa.models.PostDetail
import com.raissa.listaposts_raissa.models.PostDetailComment
import com.raissa.listaposts_raissa.models.SuspendState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

@Composable
fun PostDetailPage(postId: String) {
    var state by remember { mutableStateOf<SuspendState<PostDetail>>(SuspendState.Loading) }


    LaunchedEffect(postId) {
        state = SuspendState.Loading
        state = try {
            SuspendState.Success(
                data = fetchData(postId)
            )
        } catch (_: Exception) {
            SuspendState.Error(
                message = "Não foi possivel carregar o post com o id $postId"
            )
        }
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



private suspend fun fetchData(postId: String): PostDetail = withContext(Dispatchers.IO){
    require(postId.toIntOrNull() != null) {"Invalid postId"}
    val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId").readText()
    val apiPostDetail = Json.decodeFromString<JSONPlaceholder.PostDetail>(jsonText)
    PostDetail(
        id = apiPostDetail.id.toString(),
        title = apiPostDetail.title,
        body = apiPostDetail.body,
        comments = fetchComments(postId)
    )

}

private suspend fun fetchComments(postId: String): List<PostDetailComment> =
    withContext(Dispatchers.IO) {
        require(postId.toIntOrNull() != null) { "Invalid postId" }
        val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId/comments").readText()
        Json.decodeFromString<List< JSONPlaceholder.PostDetailComment>>(jsonText).map {
            PostDetailComment(
                id = it.id.toString(),
                title = it.name,
                commenterEmail = it.email,
                body = it.body
            )
        }
    }


