package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.data.ShelfStatus

enum class LibrarySubFilter(val title: String) {
    ALL("Hunda"),
    WANT_TO_READ("Dubbisuuf Hawwa"),
    CURRENTLY_READING("Amma Dubbisaa Jira"),
    READ("Dubbisee Xumure"),
    DOWNLOADED("Buufatame")
}

@Composable
fun LibraryScreen(
    libraryBooks: List<BookEntity>,
    wantToReadBooks: List<BookEntity>,
    currentlyReadingBooks: List<BookEntity>,
    readBooks: List<BookEntity>,
    downloadedBooks: List<BookEntity>,
    initialShelfFilter: String? = null,
    isImportingPdf: Boolean = false,
    onBookClick: (BookEntity) -> Unit,
    onContinueReading: (BookEntity) -> Unit,
    onShelfChanged: (BookEntity, ShelfStatus) -> Unit,
    onRateAndReview: (BookEntity) -> Unit,
    onRemoveDownload: (String) -> Unit,
    onUploadPdf: (Uri) -> Unit = {},
    onExportPdf: (BookEntity) -> Unit = {}
) {
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onUploadPdf(it) }
    }

    var selectedFilter by remember(initialShelfFilter) {
        val initial = when (initialShelfFilter) {
            "WANT_TO_READ" -> LibrarySubFilter.WANT_TO_READ
            "CURRENTLY_READING" -> LibrarySubFilter.CURRENTLY_READING
            "READ" -> LibrarySubFilter.READ
            else -> LibrarySubFilter.ALL
        }
        mutableStateOf(initial)
    }

    val displayedBooks = when (selectedFilter) {
        LibrarySubFilter.ALL -> libraryBooks
        LibrarySubFilter.WANT_TO_READ -> wantToReadBooks
        LibrarySubFilter.CURRENTLY_READING -> currentlyReadingBooks
        LibrarySubFilter.READ -> readBooks
        LibrarySubFilter.DOWNLOADED -> downloadedBooks
    }

    val totalStorageMb = downloadedBooks.sumOf { it.fileSizeMb }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("library_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Library Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Kuusaa Kiyya (My Library)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kuusaa dhuunfaa: Hawwa, Amma Dubbisaa Jira, fi Dubbisee Xumure.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Storage Usage Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Iddoo Buufataa: ${"%.1f".format(totalStorageMb)} MB",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Kitaabota ${downloadedBooks.size} offline dubbisuuf qophii",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF16A34A).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Offline",
                            color = Color(0xFF16A34A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // PDF Upload Banner & Action
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kitaaba PDF Fe'adhu (Upload PDF)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Faayilii PDF meeshaa kee irraa galchiitii kuusaa keetti dubbisi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        enabled = !isImportingPdf,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("upload_pdf_library_button")
                    ) {
                        if (isImportingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fe'aa jira...", fontSize = 12.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDF Fe'i", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Virtual Bookshelf Sub-filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LibrarySubFilter.values().forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val count = when (filter) {
                        LibrarySubFilter.ALL -> libraryBooks.size
                        LibrarySubFilter.WANT_TO_READ -> wantToReadBooks.size
                        LibrarySubFilter.CURRENTLY_READING -> currentlyReadingBooks.size
                        LibrarySubFilter.READ -> readBooks.size
                        LibrarySubFilter.DOWNLOADED -> downloadedBooks.size
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text("${filter.title} ($count)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("library_filter_${filter.name}")
                    )
                }
            }
        }

        // Book list
        if (displayedBooks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Kuusaa kana keessatti kitaabni hin jiru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kitaaba barbaaduun kuusaa '${selectedFilter.title}' tti dabalaa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            items(displayedBooks) { book ->
                val coverColor = remember(book.coverColorHex) {
                    try {
                        Color(android.graphics.Color.parseColor(book.coverColorHex))
                    } catch (e: Exception) {
                        Color(0xFF8C2D19)
                    }
                }

                var shelfMenuExpanded by remember { mutableStateOf(false) }

                val bookShelf = remember(book.shelfStatus) {
                    try {
                        ShelfStatus.valueOf(book.shelfStatus)
                    } catch (e: Exception) {
                        ShelfStatus.NONE
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onBookClick(book) }
                        .testTag("library_book_card_${book.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Book Thumbnail
                            Surface(
                                modifier = Modifier
                                    .width(62.dp)
                                    .height(90.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = coverColor,
                                shadowElevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = book.title,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = FontFamily.Serif,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = book.category,
                                        fontSize = 7.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = book.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Reading progress
                                val progressFloat = (book.currentPage.toFloat() / book.pagesCount).coerceIn(0f, 1f)
                                val progressPercent = (progressFloat * 100).toInt()

                                LinearProgressIndicator(
                                    progress = { progressFloat },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Fuula ${book.currentPage}/${book.pagesCount} ($progressPercent%)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )

                                    if (book.isDownloaded) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF16A34A),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Buufatame",
                                                fontSize = 10.sp,
                                                color = Color(0xFF16A34A),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { onContinueReading(book) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("continue_read_${book.id}")
                            ) {
                                Text(
                                    text = if (book.currentPage > 1) "Itti Fufi" else "Dubbisi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom Actions: Shelf Selector & Review CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Shelf Switcher Chip
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { shelfMenuExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Kuusaa: ${bookShelf.titleAfaanOromo}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = shelfMenuExpanded,
                                    onDismissRequest = { shelfMenuExpanded = false }
                                ) {
                                    ShelfStatus.values().forEach { shelf ->
                                        DropdownMenuItem(
                                            text = { Text(shelf.titleAfaanOromo) },
                                            onClick = {
                                                onShelfChanged(book, shelf)
                                                shelfMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Review / Rating & PDF Actions
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onExportPdf(book) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF Buufadhu",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                if (book.shelfStatus == ShelfStatus.READ.name || book.userRating > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onRateAndReview(book) }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (book.userRating > 0) "${book.userRating} ★ (Yaada)" else "Sadarkaa Kenni",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (book.userRating > 0) Color(0xFFB45309) else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onRateAndReview(book) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RateReview,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Yaada Kenni", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
