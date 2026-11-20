package com.raissa.listaposts_raissa.models

data class PostDetail(
    val id: String,
    val title: String,
    val comments: List<PostDetailComment>,
    val body: String,
)
data class PostDetailComment(
    val id: String,
    val title: String,
    val commenterEmail: String,
    val body: String,
)
