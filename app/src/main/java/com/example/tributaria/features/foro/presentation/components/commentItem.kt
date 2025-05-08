package com.example.tributaria.features.foro.presentation.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tributaria.features.foro.model.CommentViewModel
import com.example.tributaria.features.foro.model.LikesViewModel
import com.example.tributaria.features.foro.presentation.utils.formatTimestamp
import com.example.tributaria.features.foro.repository.Comment
import com.example.tributaria.features.login.viewmodel.LoginViewModel


@Composable
fun CommentItem(comment: Comment, likesViewModel: LikesViewModel) {
    val viewModel: CommentViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val currentUserId = loginViewModel.currentUserId
    var isEditing by remember { mutableStateOf(false) }
    var editedComment by remember { mutableStateOf(comment.body) }
    val isOwner = comment.authorId == currentUserId
    val likedComments by likesViewModel.likedComments.collectAsState()
    val isLiked = likedComments.contains(comment.id)

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

            if (isOwner) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.DriveFileRenameOutline, contentDescription = "Editar", tint = Color.Blue)
                        }
                    }

                    if (isEditing) {
                        IconButton(onClick = {
                            viewModel.updateComment(comment.id, editedComment, comment.postId)
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Guardar", tint = Color.Green)
                        }
                    }

                    IconButton(onClick = {
                        viewModel.deleteComment(comment.id, comment.postId)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                }
            }
        }
    }
}