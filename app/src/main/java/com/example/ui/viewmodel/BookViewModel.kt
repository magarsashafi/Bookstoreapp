package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookEntity
import com.example.data.BookmarkEntity
import com.example.data.BookRepository
import com.example.data.SaleTransactionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppTab(val title: String, val afaanOromo: String) {
    EXPLORE("Store", "Gabaa"),
    LIBRARY("Library", "Kuusaa"),
    SELLER("Sell & Earn", "Gurgurii")
}

enum class ReaderTheme(val displayName: String) {
    LIGHT("Ifaa"),
    SEPIA("Waraqaa (Sepia)"),
    DARK("Dukkana")
}

enum class ReaderFont(val displayName: String) {
    SERIF("Serif (Aadaa)"),
    SANS("Sans-Serif"),
    MONO("Monospace")
}

class BookViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: BookRepository
    private var textToSpeech: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _isTtsPlaying = MutableStateFlow(false)
    val isTtsPlaying: StateFlow<Boolean> = _isTtsPlaying.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = BookRepository(database.bookDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
        textToSpeech = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            _isTtsReady.value = true
        }
    }

    // Navigation & Tab State
    private val _currentTab = MutableStateFlow(AppTab.EXPLORE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Search & Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Hunda") // "All"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // Books Streams
    val allBooks: StateFlow<List<BookEntity>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val libraryBooks: StateFlow<List<BookEntity>> = repository.libraryBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedBooks: StateFlow<List<BookEntity>> = repository.downloadedBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userListedBooks: StateFlow<List<BookEntity>> = repository.userListedBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<SaleTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double?> = repository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Filtered books for Store / Explore screen
    val filteredExploreBooks: StateFlow<List<BookEntity>> = combine(
        allBooks,
        searchQuery,
        selectedCategory
    ) { books, query, category ->
        books.filter { book ->
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "Hunda" || book.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Book Detail Modal
    private val _selectedBookForDetail = MutableStateFlow<BookEntity?>(null)
    val selectedBookForDetail: StateFlow<BookEntity?> = _selectedBookForDetail.asStateFlow()

    fun openBookDetail(book: BookEntity) {
        _selectedBookForDetail.value = book
    }

    fun closeBookDetail() {
        _selectedBookForDetail.value = null
    }

    // Active Reader State
    private val _activeReadingBook = MutableStateFlow<BookEntity?>(null)
    val activeReadingBook: StateFlow<BookEntity?> = _activeReadingBook.asStateFlow()

    private val _readerTheme = MutableStateFlow(ReaderTheme.SEPIA)
    val readerTheme: StateFlow<ReaderTheme> = _readerTheme.asStateFlow()

    private val _readerFontSize = MutableStateFlow(18) // Sp
    val readerFontSize: StateFlow<Int> = _readerFontSize.asStateFlow()

    private val _readerFont = MutableStateFlow(ReaderFont.SERIF)
    val readerFont: StateFlow<ReaderFont> = _readerFont.asStateFlow()

    private val _activeChapterIndex = MutableStateFlow(0)
    val activeChapterIndex: StateFlow<Int> = _activeChapterIndex.asStateFlow()

    fun openReader(book: BookEntity) {
        stopTts()
        _activeReadingBook.value = book
        _activeChapterIndex.value = 0
        viewModelScope.launch {
            repository.updateReadingProgress(book.id, book.currentPage)
        }
    }

    fun closeReader() {
        stopTts()
        _activeReadingBook.value = null
    }

    fun setReaderTheme(theme: ReaderTheme) {
        _readerTheme.value = theme
    }

    fun adjustFontSize(delta: Int) {
        val newSize = (_readerFontSize.value + delta).coerceIn(12, 32)
        _readerFontSize.value = newSize
    }

    fun setReaderFont(font: ReaderFont) {
        _readerFont.value = font
    }

    fun selectChapter(index: Int) {
        stopTts()
        _activeChapterIndex.value = index
    }

    fun updatePage(page: Int) {
        val currentBook = _activeReadingBook.value ?: return
        _activeReadingBook.value = currentBook.copy(currentPage = page)
        viewModelScope.launch {
            repository.updateReadingProgress(currentBook.id, page)
        }
    }

    // TTS Operations
    fun toggleTts(textToRead: String) {
        if (_isTtsPlaying.value) {
            stopTts()
        } else {
            speakText(textToRead)
        }
    }

    private fun speakText(text: String) {
        if (_isTtsReady.value) {
            val cleanText = text.replace("#", "")
                .replace("---", "")
                .take(3000)
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "BOOK_SPEECH")
            _isTtsPlaying.value = true
        }
    }

    fun stopTts() {
        textToSpeech?.stop()
        _isTtsPlaying.value = false
    }

    // Download Simulation & Execution
    private val _downloadingBookId = MutableStateFlow<String?>(null)
    val downloadingBookId: StateFlow<String?> = _downloadingBookId.asStateFlow()

    fun downloadBook(bookId: String) {
        viewModelScope.launch {
            _downloadingBookId.value = bookId
            delay(1200) // realistic download feedback
            repository.downloadBook(bookId)
            _downloadingBookId.value = null
            // update active detail if open
            if (_selectedBookForDetail.value?.id == bookId) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(isDownloaded = true)
            }
        }
    }

    fun removeDownload(bookId: String) {
        viewModelScope.launch {
            repository.removeDownload(bookId)
            if (_selectedBookForDetail.value?.id == bookId) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(isDownloaded = false)
            }
        }
    }

    // Buying & Payment Checkout Modal
    private val _bookToBuy = MutableStateFlow<BookEntity?>(null)
    val bookToBuy: StateFlow<BookEntity?> = _bookToBuy.asStateFlow()

    private val _purchaseSuccessTransaction = MutableStateFlow<SaleTransactionEntity?>(null)
    val purchaseSuccessTransaction: StateFlow<SaleTransactionEntity?> = _purchaseSuccessTransaction.asStateFlow()

    private val _isProcessingPurchase = MutableStateFlow(false)
    val isProcessingPurchase: StateFlow<Boolean> = _isProcessingPurchase.asStateFlow()

    fun startBuyFlow(book: BookEntity) {
        _bookToBuy.value = book
    }

    fun dismissBuyFlow() {
        _bookToBuy.value = null
        _purchaseSuccessTransaction.value = null
        _isProcessingPurchase.value = false
    }

    fun executePurchase(paymentMethod: String, buyerName: String) {
        val book = _bookToBuy.value ?: return
        viewModelScope.launch {
            _isProcessingPurchase.value = true
            delay(1500) // realistic gateway verification
            val txn = repository.buyBook(book, buyerName, paymentMethod)
            // automatically mark downloaded as well for convenience
            repository.downloadBook(book.id)
            _isProcessingPurchase.value = false
            _purchaseSuccessTransaction.value = txn
            // refresh selected book detail
            if (_selectedBookForDetail.value?.id == book.id) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(isPurchased = true, isDownloaded = true)
            }
        }
    }

    // Publish Book for Sale State
    private val _isPublishing = MutableStateFlow(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing.asStateFlow()

    fun publishBook(
        title: String,
        author: String,
        category: String,
        description: String,
        priceEtb: Double,
        pages: Int,
        colorHex: String,
        content: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isPublishing.value = true
            val newBook = repository.publishBookForSale(
                title = title,
                author = author,
                category = category,
                description = description,
                priceEtb = priceEtb,
                pages = pages,
                colorHex = colorHex,
                content = content
            )
            _isPublishing.value = false
            onSuccess()
        }
    }

    fun toggleFavorite(book: BookEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(book.id, book.isFavorite)
            if (_selectedBookForDetail.value?.id == book.id) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(isFavorite = !book.isFavorite)
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
            if (_selectedBookForDetail.value?.id == bookId) {
                _selectedBookForDetail.value = null
            }
        }
    }

    // Bookmarks for current book in reader
    val currentBookBookmarks: StateFlow<List<BookmarkEntity>> = combine(
        _activeReadingBook,
        repository.allBooks
    ) { activeBook, _ ->
        activeBook?.id
    }.combine(_activeReadingBook) { _, activeBook ->
        activeBook?.id
    }.let {
        MutableStateFlow(emptyList()) // loaded on demand
    }

    fun addBookmark(noteText: String, quoteText: String) {
        val book = _activeReadingBook.value ?: return
        viewModelScope.launch {
            repository.addBookmark(
                bookId = book.id,
                chapterTitle = "Boqonnaa ${_activeChapterIndex.value + 1}",
                pageNumber = book.currentPage,
                noteText = noteText,
                quoteText = quoteText
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
