package com.example.tributaria.features.foro.presentation.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tributaria.features.foro.presentation.utils.formatTimestamp
import com.example.tributaria.features.foro.repository.Post


@Composable
fun MessageCard(post: Post, currentUserId: String, onDelete: (String) -> Unit, navController: NavHostController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { /* Navegar al detalle o expandir */ },
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
                        Text(post.Title, fontWeight = FontWeight.Bold)
                        Text(post.userName, fontWeight = FontWeight.Medium)
                        Text(
                            formatTimestamp(post.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Box(modifier = Modifier
                    .padding(8.dp)) {
                    PostOptionsMenu(
                        post = post,
                        currentUserId = currentUserId,
                        navController = navController,
                        onDelete = onDelete
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.body.toString(), maxLines = 5, overflow = TextOverflow.Ellipsis )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(thickness = 1.dp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /*Add action*/ }) {
                    Icon(Icons.Default.AddComment, contentDescription = "addComment", modifier = Modifier.size(18.dp), tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text( post.countComment.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis )
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { /*Add action*/ }) {
                    Icon(Icons.Default.AddReaction, contentDescription = "likes", modifier = Modifier.size(18.dp), tint = Color.Cyan)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text( post.totalLikes.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}