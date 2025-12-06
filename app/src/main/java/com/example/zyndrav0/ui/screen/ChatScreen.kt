package com.example.zyndrav0.ui.screen

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zyndrav0.model.Message
import com.example.zyndrav0.model.getBubbleStyleById
import com.example.zyndrav0.viewmodel.ChatViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel
) {
    var messageInput by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val equippedBubbleId by viewModel.equippedBubbleId.collectAsState()
    
    // Color de tema equipado desde el propio ChatViewModel
    val equippedThemeColor by viewModel.equippedThemeColor.collectAsState()
    val equippedFontFamily by viewModel.equippedFontFamily.collectAsState()

    val status = viewModel.statusText
    val listState = rememberLazyListState()
    
    var showCamera by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showCamera) {
        Dialog(
            onDismissRequest = { showCamera = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            ),
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraScreen(
                        onImageCaptured = { uri ->
                            viewModel.sendMessage("[Imagen Adjunta]", uri, "image")
                            showCamera = false
                        },
                        onDismiss = { showCamera = false }
                    )
                }
            }
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    equippedBubbleId = equippedBubbleId,
                    equippedThemeColor = equippedThemeColor,
                    equippedFontFamily = equippedFontFamily
                )
            }
        }
        
        Text(status, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = {
                if (cameraPermissionState.status.isGranted) {
                    showCamera = true
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            }) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
            }
            
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                label = { Text("Escribe tu mensaje...") },
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        viewModel.sendMessage(messageInput)
                        messageInput = ""
                    }
                },
                enabled = messageInput.isNotBlank() && status != "Enviando..."
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    equippedBubbleId: Int? = null,
    equippedThemeColor: String?,
    equippedFontFamily: String?
) {
    val horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start

    val baseStyle = if (message.isUser) getBubbleStyleById(equippedBubbleId ?: 1) else getBubbleStyleById(1)
    val bubbleColorOverride = equippedThemeColor?.let { hex ->
        runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
    }
    val bubbleStyle = if (bubbleColorOverride != null && message.isUser) {
        baseStyle.copy(color = bubbleColorOverride)
    } else {
        baseStyle
    }
    val fontFamily = equippedFontFamily?.let { resolveFontFamily(it) }

    val textColor = if (message.isUser) Color.White else Color.Black

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalAlignment = horizontalAlignment
    ) {
        Card(
            shape = bubbleStyle.shape,
            colors = CardDefaults.cardColors(containerColor = if (message.isUser) bubbleStyle.color else Color(0xFFE0E0E0)),
            border = bubbleStyle.borderStroke,
            modifier = Modifier.widthIn(min = 70.dp, max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (message.attachmentUri != null) {
                    AsyncImage(
                        model = message.attachmentUri,
                        contentDescription = "Imagen adjunta",
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(bottom = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = fontFamily ?: MaterialTheme.typography.bodyMedium.fontFamily)
                    )
                }
            }
        }
    }
}

private fun resolveFontFamily(name: String): FontFamily {
    return when (name.lowercase()) {
        "serif" -> FontFamily.Serif
        "mono", "monospace" -> FontFamily.Monospace
        "cursive", "script" -> FontFamily.Cursive
        else -> FontFamily.SansSerif
    }
}