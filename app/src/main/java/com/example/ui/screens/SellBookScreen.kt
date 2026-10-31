package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellBookDialog(
    isPublishing: Boolean,
    onDismiss: () -> Unit,
    onPublish: (
        title: String,
        author: String,
        category: String,
        description: String,
        priceEtb: Double,
        pages: Int,
        colorHex: String,
        content: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Asoosama") }
    var priceText by remember { mutableStateOf("120") }
    var isFree by remember { mutableStateOf(false) }
    var pagesText by remember { mutableStateOf("95") }
    var description by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf("#8B263E") }
    var contentText by remember {
        mutableStateOf(
            """# Boqonnaa 1: Seensa
Barreessi kun dhaloota haaraaf beekumsa fi muuxannoo dabarsuuf qophaa'e.

# Boqonnaa 2: Qabsoo fi Milkaa'ina
Namni abjuu qabu yeroo hunda ni carraaqaa, abdii hin kutatu."""
        )
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Asoosama", "Seenaa", "Walaloo", "Barnoota", "Ogummaa", "Aadaa")

    val colorOptions = listOf(
        Pair("Crimson", "#8B263E"),
        Pair("Navy", "#1E3A8A"),
        Pair("Emerald", "#065F46"),
        Pair("Amber", "#B45309"),
        Pair("Charcoal", "#1F2937"),
        Pair("Violet", "#4C1D95")
    )

    Dialog(onDismissRequest = { if (!isPublishing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("sell_book_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kitaaba Gurguruuf Dhiheessi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kitaaba kee gabaa irratti maxxansi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss, enabled = !isPublishing) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cufi")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Mataduree Kitaabaa *") },
                    placeholder = { Text("Fkn: Seenaa Oromoo Ammayyaa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Author Input
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Maqaa Barreessaa *") },
                    placeholder = { Text("Fkn: Obsaa Kabbadaa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_author_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kutaa (Category)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Gatii Kitaabaa (Pricing):", style = MaterialTheme.typography.labelLarge)
                    FilterChip(
                        selected = isFree,
                        onClick = { isFree = !isFree },
                        label = { Text(if (isFree) "Tola (Free)" else "Gatii Qaba") }
                    )
                }

                if (!isFree) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Gatii Qarshiidhaan (ETB)") },
                        placeholder = { Text("Fkn: 120") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("book_price_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Estimated Pages
                OutlinedTextField(
                    value = pagesText,
                    onValueChange = { pagesText = it },
                    label = { Text("Baay'ina Fuulotaa") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cover Color Theme Picker
                Text(text = "Halluu Qolmaata Kitaabaa:", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colorOptions.forEach { option ->
                        val isSelected = selectedColorHex == option.second
                        val color = Color(android.graphics.Color.parseColor(option.second))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = option.second }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Ibsa Gabaabaa Kitaabaa") },
                    placeholder = { Text("Kitaabni kun maal irratti xiyyeeffata?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_desc_input"),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Content / First Chapter text
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("Qabiyyee / Boqonnaalee (# Boqonnaa 1)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_content_input"),
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Publish Action Button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val price = if (isFree) 0.0 else priceText.toDoubleOrNull() ?: 50.0
                            val pages = pagesText.toIntOrNull() ?: 60
                            onPublish(
                                title,
                                author,
                                category,
                                description.ifBlank { "Kitaaba afaan Oromoo kan daldalaaf dhihaate." },
                                price,
                                pages,
                                selectedColorHex,
                                contentText
                            )
                        }
                    },
                    enabled = title.isNotBlank() && !isPublishing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_book_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Maxxansaa Jira...")
                    } else {
                        Icon(imageVector = Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gurguruuf Maxxansi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
