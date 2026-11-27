package com.raissa.listaposts_raissa.jsonplaceholder

import kotlinx.serialization.Serializable
import com.raissa.listaposts_raissa.models.PostDetail as AppPostDetail
import com.raissa.listaposts_raissa.models.PostDetailComment as AppPostDetailComment
import com.raissa.listaposts_raissa.models.PostListItem as AppPostListItem

object JSONPlaceholder {
    @Serializable
    data class PostDetail(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String,
    ) {
        fun toPostDetail(comments: List<AppPostDetailComment>): AppPostDetail {
            return AppPostDetail(
                id = id.toString(),
                title = title,
                body = body,
                comments = comments
            )
        }
    }
    @Serializable
    data class PostDetailComment(
        val id: Int,
        val postId: Int,
        val name: String,
        val email: String,
        val body: String,
    ){
        fun toComment(): AppPostDetailComment {
            return AppPostDetailComment(
                id = id.toString(),
                title = name,
                commenterEmail = email,
                body = body

            )
        }
    }
    @Serializable
    data class  PostListItem (
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String
    ){
        fun toPostListItem(): AppPostListItem{
            return AppPostListItem(
                postId = id.toString(),
                userId = userId.toString(),
                title = title,
                description = body
            )
        }
    }
}