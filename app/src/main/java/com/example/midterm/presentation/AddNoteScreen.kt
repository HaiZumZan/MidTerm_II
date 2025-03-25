package com.example.midterm.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    state: NoteState,
    navController: NavController,
    onEvent: (NotesEvent) -> Unit,
    noteID: Int
) {
    // Nếu noteId != -1, ta đang edit
    LaunchedEffect(key1 = noteID) {
        if (noteID != -1) {
            val note = state.notes.find { it.id == noteID }
            note?.let {
                state.title.value = it.title
                state.description.value = it.description
                state.imageUri.value = it.imageUri?.let { uriStr -> Uri.parse(uriStr) }
            }
        } else {
            // Trường hợp -1 => đang tạo note mới => có thể reset
            state.title.value = ""
            state.description.value = ""
        }
    }

    // Tạo launcher để pick ảnh
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Kết quả trả về
        if (uri != null) {
            state.imageUri.value = uri
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {

                onEvent(NotesEvent.SaveNote(
                    noteID = noteID,
                    title = state.title.value,
                    description = state.description.value,
                    imageUri = state.imageUri.value?.toString()
                ))
                navController.popBackStack()
            }) {

                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Save Note"
                )

            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = state.title.value,
                onValueChange = {
                    state.title.value = it
                },
                textStyle = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                placeholder = {
                    Text(text = "Title")
                }

            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                value = state.description.value,
                onValueChange = {
                    state.description.value = it
                },
                placeholder = {
                    Text(text = "Description")
                }

            )

            // Nút chọn ảnh
            Button(
                onClick = {
                    pickImageLauncher.launch("image/*")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Pick Image")
            }

            // Hiển thị ảnh (nếu có)
            state.imageUri.value?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                )
            }

        }

    }

}