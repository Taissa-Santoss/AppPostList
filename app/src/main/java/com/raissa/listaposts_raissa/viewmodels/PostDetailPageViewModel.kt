package com.raissa.listaposts_raissa.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raissa.listaposts_raissa.jsonplaceholder.JSONPlaceholder
import com.raissa.listaposts_raissa.models.PostDetail
import com.raissa.listaposts_raissa.models.PostDetailComment
import com.raissa.listaposts_raissa.models.PostListItem
import com.raissa.listaposts_raissa.models.SuspendState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

class PostDetailPageViewModel: ViewModel() {
    val state: StateFlow<SuspendState<PostDetail>>
        field = MutableStateFlow<SuspendState<PostDetail>>(SuspendState.Loading)

    fun downloadPostDetail(postId: String){
        viewModelScope.launch {
            state.value = SuspendState.Loading
            state.value = try {
                SuspendState.Success(
                    data = fetchData(postId)
                )
            } catch (_: Exception) {
                SuspendState.Error(
                    message = "Não foi possivel carregar o post com o id $postId"
                )
            }
        }
    }
    private suspend fun fetchData(postId: String): PostDetail = withContext(Dispatchers.IO){
        require(postId.toIntOrNull() != null) {"Invalid postId"}
        val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId").readText()
        val apiPostDetail = Json{ ignoreUnknownKeys=true}.decodeFromString<JSONPlaceholder.PostDetail>(jsonText)
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
}