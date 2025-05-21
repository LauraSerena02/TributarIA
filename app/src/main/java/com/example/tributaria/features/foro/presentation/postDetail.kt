package com.example.tributaria.features.foro.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.model.commentViewModel
import com.example.tributaria.features.foro.model.postViewModel
import com.example.tributaria.features.foro.presentation.components.CommentItem
import com.example.tributaria.features.foro.presentation.components.PostOptionsMenu
import com.example.tributaria.features.foro.presentation.utils.formatTimestamp
import com.example.tributaria.features.foro.repository.LikesRepository
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(
    navController: NavHostController,
    postId: String,
    loginViewModel: LoginViewModel = hiltViewModel(),
    viewModel: postViewModel = viewModel(),
    viewModelComment: commentViewModel = viewModel(),
) {
    var showInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val commentViewModel : commentViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val repositoryLike = LikesRepository()
    val comments = viewModelComment.comments.collectAsState().value
    val currentUserId = loginViewModel.currentUserId
    val username by loginViewModel.userName.collectAsState(initial = "")
    var isLiked by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        loginViewModel.checkUserSession()
    }

    LaunchedEffect(postId, currentUserId) {
        isLiked = repositoryLike.hasUserLikedPost(postId, currentUserId.toString())
    }
    LaunchedEffect(postId) {
        loginViewModel.checkUserSession()
        viewModel.getPostById(postId)
        viewModelComment.loadComments(postId)
    }
    val posts = viewModel.posts.collectAsState().value
    if (posts.isEmpty()) {
        Text("Cargando posts...")
        return
    }

    val post = viewModel.getPostById(postId)

    if (post != null) {
        Scaffold { paddingValues ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E40AF))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detalle de la publicación",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(post!!.Title, fontWeight = FontWeight.Bold)
                                        Text(post.userName, fontWeight = FontWeight.Medium)
                                        Text(formatTimestamp(post.timestamp), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                if (post.authorId == currentUserId) {
                                    Box(modifier = Modifier.padding(8.dp)) {
                                        PostOptionsMenu(
                                            post = post,
                                            navController = navController,
                                            onDelete = { postId: String -> viewModel.deletePost(postId)  }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(post!!.body, maxLines = 5, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(thickness = 1.dp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { showInput = !showInput }) {
                                    Icon(Icons.Default.AddComment, contentDescription = "addComment", modifier = Modifier.size(18.dp), tint = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(1.dp))
                                Text(post.countComment.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.width(16.dp))
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repositoryLike.toggleLikeOnPost(post.id,
                                                currentUserId.toString()
                                            )
                                            isLiked = !isLiked
                                        }
                                        commentViewModel.loadComments(postId)
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
                                                    commentViewModel.createComment(postId, commentText,
                                                        currentUserId.toString(), username)
                                                    showInput = false
                                                    commentText = ""
                                                    Toast.makeText(context, "Comentario agregado", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ArrowCircleUp, contentDescription = "Send", modifier = Modifier.size(24.dp), tint = Color.Blue)
                                    }
                                }
                            }
                        }
                    }

                    Column {
                        Text("Comentarios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LazyColumn {
                                items(comments) { comment ->
                                    CommentItem(comment, postId)
                                }
                            }
                        }
                    }
                }


            }

        }

    } else {
        Toast.makeText(context, "Post no encontrado: ", Toast.LENGTH_SHORT).show()
    }
}