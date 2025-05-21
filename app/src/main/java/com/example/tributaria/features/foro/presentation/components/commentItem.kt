package com.example.tributaria.features.foro.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tributaria.features.foro.model.commentViewModel
import com.example.tributaria.features.foro.model.likesViewModel
import com.example.tributaria.features.foro.presentation.utils.formatTimestamp
import com.example.tributaria.features.foro.repository.Comment
import com.example.tributaria.features.login.viewmodel.LoginViewModel
import kotlinx.coroutines.launch


@Composable
fun CommentItem(comment: Comment, postId: String) {
    val viewModel: commentViewModel = viewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val likesViewModel : likesViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val currentUserId = loginViewModel.currentUserId
    var isEditing by remember { mutableStateOf(false) }
    var editedComment by remember { mutableStateOf(comment.body) }
    val isOwner = comment.authorId == currentUserId
    var isLiked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.observeComments(postId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(comment.id, currentUserId) {
        isLiked = likesViewModel.loadReactionComment(comment.id, currentUserId.toString())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(comment.userName, fontWeight = FontWeight.Bold)
                    Text(formatTimestamp(comment.timestamp), style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isEditing) {
                        TextField(
                            value = editedComment,
                            onValueChange = { editedComment = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(editedComment)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (!isEditing) { Divider(thickness = 1.dp, color = Color.Gray) }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(!isEditing){
                    IconButton(
                        onClick = {
                            scope.launch {
                                likesViewModel.reactionComment(comment.id,
                                    currentUserId.toString()
                                )
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
                    Text(comment.totalLikes.toString(), overflow = TextOverflow.Ellipsis)
                }
                if (isOwner) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        if (!isEditing) {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "Editar", tint = Color.Gray)
                            }
                        }

                        if (isEditing) {
                            IconButton(onClick = {
                                viewModel.updateComment(comment.id, editedComment, comment.postId)
                                isEditing = false
                                Toast.makeText(context, "Comentario editado", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Done, contentDescription = "Guardar", tint = Color.Green)
                            }
                        }

                        IconButton(onClick = {
                            viewModel.deleteComment(comment.id, comment.postId)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}