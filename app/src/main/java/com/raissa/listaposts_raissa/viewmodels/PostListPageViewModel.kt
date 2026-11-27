package com.raissa.listaposts_raissa.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raissa.listaposts_raissa.jsonplaceholder.JSONPlaceholder
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

class PostListPageViewModel: ViewModel() {
    val state: StateFlow<SuspendState<List<PostListItem>>>
        field = MutableStateFlow<SuspendState<List<PostListItem>>>(SuspendState.Loading)
    fun download(){
        viewModelScope.launch {
            state.value = SuspendState.Loading
            state.value = try {
                SuspendState.Success(
                    data = downloadPosts()
                )
            } catch (_: Exception) {
                SuspendState.Error(
                    message = "Não foi possivel carregar a lista"
                )
            }
        }
    }
    private suspend fun downloadPosts(): List<PostListItem> = withContext(Dispatchers.IO) {
        val jsonText = URL("https://jsonplaceholder.typicode.com/posts").readText()
        Json.decodeFromString<List<JSONPlaceholder.PostListItem>>(jsonText).map {it.toPostListItem()}
    }
}