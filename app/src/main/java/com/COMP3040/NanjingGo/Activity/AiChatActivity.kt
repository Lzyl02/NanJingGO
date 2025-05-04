package com.COMP3040.NanjingGo.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.COMP3040.NanjingGo.Activity.ui.theme.NanJingGoTheme
import com.COMP3040.NanjingGo.network.callGeminiApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class AiChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NanJingGoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AiChatScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Message(val text: String, val isUser: Boolean)

@Composable
fun AiChatScreen(modifier: Modifier = Modifier) {
    var prompt by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var isLoading by remember { mutableStateOf(false) }
    var conversationHistory by remember { mutableStateOf(listOf<Message>()) }
    var userPreferences by remember { mutableStateOf("") }
    
    // Load conversation history and user preferences
    LaunchedEffect(Unit) {
        loadConversationHistory { history ->
            conversationHistory = history
            messages = if (history.isNotEmpty()) history else listOf(Message(
                "Hello! I'm your Nanjing Travel Assistant.\n\n" +
                "I can help you with various questions about traveling in Nanjing, such as:\n" +
                "• What are the must-visit attractions in Nanjing?\n" +
                "• What are the local specialties and food?\n" +
                "• Recommended travel routes\n" +
                "• Accommodation and transportation suggestions\n" +
                "• Historical and cultural information\n\n" +
                "Feel free to ask me anything, I'm happy to help!", 
                false
            ))
        }
        loadUserPreferences { preferences ->
            userPreferences = preferences
        }
    }
    
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top bar with new chat button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    conversationHistory = listOf()
                    messages = listOf(Message(
                        "Hello! I'm your Nanjing Travel Assistant.\n\n" +
                        "I can help you with various questions about traveling in Nanjing, such as:\n" +
                        "• What are the must-visit attractions in Nanjing?\n" +
                        "• What are the local specialties and food?\n" +
                        "• Recommended travel routes\n" +
                        "• Accommodation and transportation suggestions\n" +
                        "• Historical and cultural information\n\n" +
                        "Feel free to ask me anything, I'm happy to help!", 
                        false
                    ))
                    saveConversationHistory(listOf())
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("New Chat")
            }
        }
        
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
        
        // Input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Please enter your question") },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        minLines = 1,
                        maxLines = 3
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (prompt.text.isNotBlank() && !isLoading) {
                                val userMessage = prompt.text
                                messages = messages + Message(userMessage, true)
                                prompt = TextFieldValue("")
                                isLoading = true
                                
                                scope.launch {
                                    try {
                                        // Build context with conversation history and user preferences
                                        val contextPrompt = buildString {
                                            append("You are a travel assistant for Nanjing, China.\n\n")
                                            
                                            if (userPreferences.isNotBlank()) {
                                                append("User preferences:\n$userPreferences\n\n")
                                            }
                                            
                                            if (conversationHistory.isNotEmpty()) {
                                                append("Previous conversation:\n")
                                                conversationHistory.takeLast(5).forEach { message ->
                                                    append("${if (message.isUser) "User" else "Assistant"}: ${message.text}\n")
                                                }
                                                append("\n")
                                            }
                                            
                                            append("Current question: $userMessage")
                                        }
                                        
                                        val result = callGeminiApi(contextPrompt)
                                        val formattedResult = result
                                            .replace("*", "")
                                            .replace("\\n", "\n")
                                            .trim()
                                        
                                        val newMessage = Message(formattedResult, false)
                                        messages = messages + newMessage
                                        
                                        // Update conversation history
                                        val updatedHistory = conversationHistory + 
                                            Message(userMessage, true) + newMessage
                                        conversationHistory = updatedHistory
                                        saveConversationHistory(updatedHistory)
                                        
                                    } catch (e: Exception) {
                                        messages = messages + Message("Error: ${e.message}", false)
                                    } finally {
                                        isLoading = false
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            }
                        },
                        enabled = !isLoading && prompt.text.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Send")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val bubbleColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (message.isUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = bubbleColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    style = if (message.isUser) {
                        MaterialTheme.typography.bodyLarge
                    } else {
                        MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        )
                    }
                )
            }
        }
    }
}

private fun loadConversationHistory(onLoaded: (List<Message>) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: return
    
    FirebaseDatabase.getInstance().getReference("users/$userId/conversationHistory")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val history = mutableListOf<Message>()
                snapshot.children.forEach { message ->
                    val text = message.child("text").getValue(String::class.java) ?: ""
                    val isUser = message.child("isUser").getValue(Boolean::class.java) ?: false
                    history.add(Message(text, isUser))
                }
                onLoaded(history)
            }
            override fun onCancelled(error: DatabaseError) {
                onLoaded(emptyList())
            }
        })
}

private fun saveConversationHistory(history: List<Message>) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: return
    
    val historyMap = history.mapIndexed { index, message ->
        index.toString() to mapOf(
            "text" to message.text,
            "isUser" to message.isUser
        )
    }.toMap()
    
    FirebaseDatabase.getInstance().getReference("users/$userId/conversationHistory")
        .setValue(historyMap)
}

private fun loadUserPreferences(onLoaded: (String) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: return
    
    // Load favorite locations
    FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<String>()
                snapshot.children.forEach { favorite ->
                    favorites.add(favorite.getValue(String::class.java) ?: "")
                }
                onLoaded("User's favorite locations: ${favorites.joinToString(", ")}")
            }
            override fun onCancelled(error: DatabaseError) {
                onLoaded("")
            }
        })
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    NanJingGoTheme {
        AiChatScreen()
    }
}