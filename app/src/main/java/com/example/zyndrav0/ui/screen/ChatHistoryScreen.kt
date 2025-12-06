package com.example.zyndrav0.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zyndrav0.data.database.entities.Conversation
import com.example.zyndrav0.viewmodel.ChatHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

//Fallos de diseño en el chat history screen tratar de arreglar en proxima entrega (Solovisual no funcional)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    onConversationClick: (Long) -> Unit,
    viewModel: ChatHistoryViewModel = viewModel()
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Historial de Chats",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    conversations.isEmpty() -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "No hay conversaciones",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Toca el botón + para iniciar un nuevo chat",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = conversations,
                                key = { it.conversationId }
                            ) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = { onConversationClick(conversation.conversationId) },
                                    onDelete = { viewModel.deleteConversation(conversation.conversationId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar conversación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta conversación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = conversation.title.ifEmpty { "Sin título" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val lastMsg = conversation.lastMessage.ifEmpty { "" }
                if (lastMsg.isNotEmpty()) {
                    Text(
                        text = lastMsg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val timestamp = if (conversation.lastMessageTime > 0) {
                    conversation.lastMessageTime
                } else {
                    conversation.createdAt
                }
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Hace un momento"
        diff < 3600000 -> "${diff / 60000} min"
        diff < 86400000 -> "${diff / 3600000} h"
        diff < 604800000 -> "${diff / 86400000} días"
        else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
