package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BookEntity
import com.example.ui.theme.ReaderThemeDarkBg
import com.example.ui.theme.ReaderThemeDarkText
import com.example.ui.theme.ReaderThemeLightBg
import com.example.ui.theme.ReaderThemeLightText
import com.example.ui.theme.ReaderThemeSepiaBg
import com.example.ui.theme.ReaderThemeSepiaText
import com.example.ui.viewmodel.ReaderFont
import com.example.ui.viewmodel.ReaderTheme

data class ParsedChapter(
    val title: String,
    val body: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    book: BookEntity,
    currentChapterIndex: Int,
    readerTheme: ReaderTheme,
    fontSizeSp: Int,
    readerFont: ReaderFont,
    isTtsPlaying: Boolean,
    onBack: () -> Unit,
    onChapterSelected: (Int) -> Unit,
    onPageChanged: (Int) -> Unit,
    onThemeSelected: (ReaderTheme) -> Unit,
    onFontSizeDelta: (Int) -> Unit,
    onFontFamilySelected: (ReaderFont) -> Unit,
    onToggleTts: (String) -> Unit,
    onAddBookmark: (note: String, quote: String) -> Unit,
    onExportPdf: () -> Unit = {}
) {
    BackHandler { onBack() }

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showChaptersSheet by remember { mutableStateOf(false) }
    var showBookmarkDialog by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }

    // Parse chapters from content
    val chapters = remember(book.content) {
        val rawParts = book.content.split(Regex("(?=# Boqonnaa|# Kutaa)"))
            .filter { it.isNotBlank() }
        if (rawParts.isEmpty()) {
            listOf(ParsedChapter(book.title, book.content))
        } else {
            rawParts.map { part ->
                val lines = part.trim().lines()
                val title = lines.firstOrNull()?.replace("#", "")?.trim() ?: "Boqonnaa"
                val body = lines.drop(1).joinToString("\n").trim()
                ParsedChapter(title, body)
            }
        }
    }

    val safeChapterIndex = currentChapterIndex.coerceIn(0, (chapters.size - 1).coerceAtLeast(0))
    val currentChapter = chapters.getOrNull(safeChapterIndex) ?: ParsedChapter("Boqonnaa", book.content)

    // Colors according to chosen reader theme
    val (backgroundColor, textColor) = when (readerTheme) {
        ReaderTheme.LIGHT -> Pair(ReaderThemeLightBg, ReaderThemeLightText)
        ReaderTheme.SEPIA -> Pair(ReaderThemeSepiaBg, ReaderThemeSepiaText)
        ReaderTheme.DARK -> Pair(ReaderThemeDarkBg, ReaderThemeDarkText)
    }

    val fontFamily = when (readerFont) {
        ReaderFont.SERIF -> FontFamily.Serif
        ReaderFont.SANS -> FontFamily.SansSerif
        ReaderFont.MONO -> FontFamily.Monospace
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("reader_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Reader Bar
            Surface(
                color = backgroundColor.copy(alpha = 0.95f),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Deebi'i",
                                tint = textColor
                            )
                        }

                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                text = book.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = textColor,
                                maxLines = 1
                            )
                            Text(
                                text = currentChapter.title,
                                fontSize = 12.sp,
                                color = textColor.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // TTS Audio Play/Stop
                        IconButton(
                            onClick = { onToggleTts(currentChapter.body) },
                            modifier = Modifier.testTag("reader_tts_button")
                        ) {
                            Icon(
                                imageVector = if (isTtsPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Sagalee",
                                tint = if (isTtsPlaying) MaterialTheme.colorScheme.primary else textColor
                            )
                        }

                        // Bookmark / Note
                        IconButton(
                            onClick = { showBookmarkDialog = true },
                            modifier = Modifier.testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Qabxii",
                                tint = textColor
                            )
                        }

                        // Chapters Menu
                        IconButton(
                            onClick = { showChaptersSheet = true },
                            modifier = Modifier.testTag("reader_toc_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Boqonnaalee",
                                tint = textColor
                            )
                        }

                        // Formatting / Settings
                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier.testTag("reader_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Qindeessa",
                                tint = textColor
                            )
                        }

                        // Export as PDF
                        IconButton(
                            onClick = onExportPdf,
                            modifier = Modifier.testTag("reader_export_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF Buufadhu",
                                tint = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }

            // Book Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = currentChapter.title,
                    fontSize = (fontSizeSp + 6).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = currentChapter.body,
                    fontSize = fontSizeSp.sp,
                    fontFamily = fontFamily,
                    color = textColor,
                    lineHeight = (fontSizeSp * 1.6).sp,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Chapter navigation buttons at end of text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (safeChapterIndex > 0) {
                        Button(
                            onClick = { onChapterSelected(safeChapterIndex - 1) },
                            modifier = Modifier.testTag("prev_chapter_button")
                        ) {
                            Text("Boqonnaa Duraa")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (safeChapterIndex < chapters.size - 1) {
                        Button(
                            onClick = { onChapterSelected(safeChapterIndex + 1) },
                            modifier = Modifier.testTag("next_chapter_button")
                        ) {
                            Text("Boqonnaa Itti Aanu")
                        }
                    }
                }
            }

            // Bottom Progress Bar
            Surface(
                color = backgroundColor.copy(alpha = 0.95f),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Boqonnaa ${safeChapterIndex + 1} / ${chapters.size}",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )

                    val currentProgressPercent = ((safeChapterIndex + 1).toFloat() / chapters.size * 100).toInt()
                    Text(
                        text = "$currentProgressPercent% Xumurame",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }

    // Reader Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Qindaa'ina Dubbisaa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Theme selection
                Text(text = "Halluu Duubbee (Theme):", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ReaderTheme.values().forEach { theme ->
                        val isSelected = readerTheme == theme
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onThemeSelected(theme) },
                            color = when (theme) {
                                ReaderTheme.LIGHT -> Color(0xFFF3F4F6)
                                ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
                                ReaderTheme.DARK -> Color(0xFF27272A)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = theme.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (theme == ReaderTheme.DARK) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Font size adjuster
                Text(text = "Guddina Qubee ($fontSizeSp sp):", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onFontSizeDelta(-2) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("A-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "$fontSizeSp pt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { onFontSizeDelta(+2) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("A+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Font Family selection
                Text(text = "Gosa Qubee (Font):", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReaderFont.values().forEach { font ->
                        FilterChip(
                            selected = readerFont == font,
                            onClick = { onFontFamilySelected(font) },
                            label = { Text(font.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Chapters / Table of Contents Bottom Sheet
    if (showChaptersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChaptersSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Baafata Boqonnaalee",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(chapters) { index, chapter ->
                        val isCurrent = index == safeChapterIndex
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onChapterSelected(index)
                                    showChaptersSheet = false
                                },
                            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = chapter.title,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Bookmark / Note Dialog
    if (showBookmarkDialog) {
        Dialog(onDismissRequest = { showBookmarkDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Qabxii / Yaadannoo Qabadhu",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${book.title} - ${currentChapter.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Yaada keessan barreessaa...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (noteInput.isNotBlank()) {
                                    onAddBookmark(noteInput, currentChapter.title)
                                    noteInput = ""
                                }
                                showBookmarkDialog = false
                            }
                        ) {
                            Text("Olkaayi (Save)")
                        }
                    }
                }
            }
        }
    }
}
