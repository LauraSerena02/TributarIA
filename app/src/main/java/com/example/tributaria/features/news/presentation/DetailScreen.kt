package com.example.tributaria.features.news.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.tributaria.R
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.news.viewmodel.DetailsScreenViewModel
import com.example.tributaria.ui.theme.TributarIATheme

@Composable
fun DetailsScreen(
    newTitle: String,
    navController: NavController,
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val new by viewModel.getNewsByTitle(newTitle).observeAsState(initial = null)
    DetailsScreen(newTitle, navController, new)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    newTitle: String,
    navController: NavController,
    new: News?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(newTitle, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) {
        new?.let {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp).fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Column {
                    Image(
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                        painter = rememberImagePainter(
                            data = new.urlToImage,
                            builder = {
                                placeholder(R.drawable.placeholder)
                                error(R.drawable.placeholder)
                            }
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                    Column(Modifier.padding(8.dp)) {
                        val context = LocalContext.current

                        Text(new.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(new.content ?: "")
                        Box(Modifier.size(8.dp))
                        Button(
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(new.url))
                                context.startActivity(intent)
                            }) {
                            Text("Ver mas...")
                        }
                    }
                }
            }
        } ?: run {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    TributarIATheme {
        DetailsScreen(
            newTitle = "Hello",
            navController = rememberNavController(),
            new = News(
                "Title", "Content description", "", "",
                "https://via.placeholder.com/540x300?text=yayocode.com"
            )
        )
    }
} 