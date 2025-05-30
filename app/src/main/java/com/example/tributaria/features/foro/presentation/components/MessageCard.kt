package com.example.tributaria.features.foro.presentation.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.model.commentViewModel
import com.example.tributaria.features.foro.model.likesViewModel
import com.example.tributaria.features.foro.model.postViewModel
import com.example.tributaria.features.foro.presentation.utils.HighlightedText
import com.example.tributaria.features.foro.presentation.utils.formatTimestamp
import com.example.tributaria.features.foro.repository.Post
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MessageCard(
    post: Post,
    currentUserId: String,
    onDelete: (String) -> Unit,
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    viewModel: postViewModel = viewModel(),
    highlightQuery: String = ""
) {
    var showInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val commentViewModel: commentViewModel = viewModel()
    val likesViewModel: likesViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLiked by rememberSaveable { mutableStateOf(false) }
    val username by loginViewModel.userName.collectAsState(initial = "")

    LaunchedEffect(Unit) {
        loginViewModel.checkUserSession()
    }

    LaunchedEffect(post.id, currentUserId) {
        isLiked = likesViewModel.loadReactionPost(post.id, currentUserId)
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("postDetail/${post.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        HighlightedText(
                            fullText = post.Title,
                            query = highlightQuery,
                            fontWeight = FontWeight.Bold
                        )
                        HighlightedText(
                            fullText = post.userName,
                            query = highlightQuery,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            formatTimestamp(post.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (post.authorId == currentUserId) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        PostOptionsMenu(
                            post = post,
                            navController = navController,
                            onDelete = onDelete
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            HighlightedText(
                fullText = post.body,
                query = highlightQuery,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showInput = !showInput }) {
                    Icon(
                        Icons.Default.AddComment,
                        contentDescription = "addComment",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(1.dp))
                Text(post.countComment.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = {
                        scope.launch {
                            likesViewModel.reactionPost(post.id, currentUserId)
                            isLiked = !isLiked
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isLiked) "Unlike Post" else "Like Post",
                        tint = if (isLiked) Color.Blue else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(1.dp))
                Text(post.totalLikes.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (showInput) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Escribe tu comentario...") },
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF1E40AF),
                            unfocusedIndicatorColor = Color(0xFF1E40AF),
                            focusedLabelColor = Color(0xFF1E40AF),
                            cursorColor = Color(0xFF1E40AF)
                        )
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (commentText.isNotBlank()) {
                                    commentViewModel.createComment(
                                        post.id,
                                        commentText,
                                        currentUserId.toString(),
                                        username
                                    )
                                    showInput = false
                                    commentText = ""
                                    Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowCircleUp,
                            contentDescription = "Send",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Blue
                        )
                    }
                }
            }
        }
    }
}