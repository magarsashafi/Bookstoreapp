package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BookEntity
import com.example.ui.components.BookDetailSheet
import com.example.ui.components.BuyCheckoutDialog
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BookViewModel

@Composable
fun MainAppScreen(
    viewModel: BookViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val filteredBooks by viewModel.filteredExploreBooks.collectAsStateWithLifecycle()
    val libraryBooks by viewModel.libraryBooks.collectAsStateWithLifecycle()
    val downloadedBooks by viewModel.downloadedBooks.collectAsStateWithLifecycle()
    val userListedBooks by viewModel.userListedBooks.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()

    val downloadingBookId by viewModel.downloadingBookId.collectAsStateWithLifecycle()
    val selectedBookForDetail by viewModel.selectedBookForDetail.collectAsStateWithLifecycle()
    val activeReadingBook by viewModel.activeReadingBook.collectAsStateWithLifecycle()

    val readerTheme by viewModel.readerTheme.collectAsStateWithLifecycle()
    val readerFontSize by viewModel.readerFontSize.collectAsStateWithLifecycle()
    val readerFont by viewModel.readerFont.collectAsStateWithLifecycle()
    val activeChapterIndex by viewModel.activeChapterIndex.collectAsStateWithLifecycle()
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()

    val bookToBuy by viewModel.bookToBuy.collectAsStateWithLifecycle()
    val isProcessingPurchase by viewModel.isProcessingPurchase.collectAsStateWithLifecycle()
    val purchaseSuccessTransaction by viewModel.purchaseSuccessTransaction.collectAsStateWithLifecycle()
    val isPublishing by viewModel.isPublishing.collectAsStateWithLifecycle()

    var showSellDialog by remember { mutableStateOf(false) }

    // If active reading book is open, display full reader screen
    if (activeReadingBook != null) {
        val book = activeReadingBook!!
        ReaderScreen(
            book = book,
            currentChapterIndex = activeChapterIndex,
            readerTheme = readerTheme,
            fontSizeSp = readerFontSize,
            readerFont = readerFont,
            isTtsPlaying = isTtsPlaying,
            onBack = { viewModel.closeReader() },
            onChapterSelected = { viewModel.selectChapter(it) },
            onPageChanged = { viewModel.updatePage(it) },
            onThemeSelected = { viewModel.setReaderTheme(it) },
            onFontSizeDelta = { viewModel.adjustFontSize(it) },
            onFontFamilySelected = { viewModel.setReaderFont(it) },
            onToggleTts = { viewModel.toggleTts(it) },
            onAddBookmark = { note, quote ->
                viewModel.addBookmark(note, quote)
                Toast.makeText(context, "Qabxiin olkaawameera!", Toast.LENGTH_SHORT).show()
            }
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = MaterialTheme.colorScheme.surfaceTint.let { 4.dp }
                ) {
                    // Explore / Store Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.EXPLORE,
                        onClick = { viewModel.setTab(AppTab.EXPLORE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.EXPLORE) Icons.Filled.Store else Icons.Outlined.Store,
                                contentDescription = "Gabaa"
                            )
                        },
                        label = { Text("Gabaa") },
                        modifier = Modifier.testTag("nav_tab_explore")
                    )

                    // Library Tab with Badge
                    NavigationBarItem(
                        selected = currentTab == AppTab.LIBRARY,
                        onClick = { viewModel.setTab(AppTab.LIBRARY) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (libraryBooks.isNotEmpty()) {
                                        Badge { Text("${libraryBooks.size}") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == AppTab.LIBRARY) Icons.Filled.LocalLibrary else Icons.Outlined.LocalLibrary,
                                    contentDescription = "Kuusaa"
                                )
                            }
                        },
                        label = { Text("Kuusaa") },
                        modifier = Modifier.testTag("nav_tab_library")
                    )

                    // Sell / Earn Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.SELLER,
                        onClick = { viewModel.setTab(AppTab.SELLER) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.SELLER) Icons.Filled.MonetizationOn else Icons.Outlined.MonetizationOn,
                                contentDescription = "Gurgurii"
                            )
                        },
                        label = { Text("Gurgurii") },
                        modifier = Modifier.testTag("nav_tab_seller")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    AppTab.EXPLORE -> {
                        ExploreScreen(
                            books = filteredBooks,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            downloadingBookId = downloadingBookId,
                            onSearchChange = { viewModel.updateSearchQuery(it) },
                            onCategorySelected = { viewModel.selectCategory(it) },
                            onBookClick = { viewModel.openBookDetail(it) }
                        )
                    }
                    AppTab.LIBRARY -> {
                        LibraryScreen(
                            libraryBooks = libraryBooks,
                            downloadedBooks = downloadedBooks,
                            onBookClick = { viewModel.openBookDetail(it) },
                            onContinueReading = { viewModel.openReader(it) },
                            onRemoveDownload = { viewModel.removeDownload(it) }
                        )
                    }
                    AppTab.SELLER -> {
                        SellerDashboardScreen(
                            userListedBooks = userListedBooks,
                            transactions = transactions,
                            totalRevenue = totalRevenue,
                            onOpenSellDialog = { showSellDialog = true },
                            onBookClick = { viewModel.openBookDetail(it) },
                            onDeleteBook = {
                                viewModel.deleteBook(it)
                                Toast.makeText(context, "Kitaabni haqameera", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Book Detail Sheet
    selectedBookForDetail?.let { book ->
        BookDetailSheet(
            book = book,
            isDownloading = downloadingBookId == book.id,
            onDismiss = { viewModel.closeBookDetail() },
            onRead = {
                viewModel.closeBookDetail()
                viewModel.openReader(book)
            },
            onBuy = {
                viewModel.closeBookDetail()
                viewModel.startBuyFlow(book)
            },
            onDownload = {
                viewModel.downloadBook(book.id)
                Toast.makeText(context, "Kitaabni buufamaa jira...", Toast.LENGTH_SHORT).show()
            },
            onRemoveDownload = {
                viewModel.removeDownload(book.id)
                Toast.makeText(context, "Buufatni haqameera", Toast.LENGTH_SHORT).show()
            },
            onToggleFavorite = {
                viewModel.toggleFavorite(book)
            }
        )
    }

    // Buy Checkout Dialog
    bookToBuy?.let { book ->
        BuyCheckoutDialog(
            book = book,
            isProcessing = isProcessingPurchase,
            successTransaction = purchaseSuccessTransaction,
            onDismiss = { viewModel.dismissBuyFlow() },
            onConfirmPurchase = { method, buyerName ->
                viewModel.executePurchase(method, buyerName)
            },
            onReadNow = {
                viewModel.dismissBuyFlow()
                viewModel.openReader(book)
            }
        )
    }

    // Sell / Publish Book Dialog
    if (showSellDialog) {
        SellBookDialog(
            isPublishing = isPublishing,
            onDismiss = { showSellDialog = false },
            onPublish = { title, author, category, desc, price, pages, color, content ->
                viewModel.publishBook(
                    title = title,
                    author = author,
                    category = category,
                    description = desc,
                    priceEtb = price,
                    pages = pages,
                    colorHex = color,
                    content = content,
                    onSuccess = {
                        showSellDialog = false
                        Toast.makeText(context, "Kitaabni milkaa'inaan gabaaf dhihaateera!", Toast.LENGTH_LONG).show()
                    }
                )
            }
        )
    }
}
