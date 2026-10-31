package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookEntity
import com.example.data.BookmarkEntity
import com.example.data.BookRepository
import com.example.data.InitialBookData
import com.example.data.ReviewEntity
import com.example.data.SaleTransactionEntity
import com.example.data.ShelfStatus
import com.example.data.UserProfileEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppTab(val title: String, val afaanOromo: String) {
    EXPLORE("Store", "Gabaa"),
    LIBRARY("Library", "Kuusaa"),
    SELLER("Sell & Earn", "Gurgurii"),
    PROFILE("Profile", "Eenyummaa")
}

enum class SearchSortOption(val titleAfaanOromo: String, val titleEnglish: String) {
    POPULAR("Beekamummaa", "Popular"),
    RATING("Sadarkaa (Rating)", "Highest Rated"),
    NEWEST("Haaraa", "Newest"),
    PRICE_LOW("Gatii (Gadi Aanaa)", "Price: Low to High")
}

data class RecommendedBook(
    val book: BookEntity,
    val reasonAfaanOromo: String,
    val reasonEnglish: String
)

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
            textToSpeech?.setLanguage(Locale.US)
            _isTtsReady.value = true
        }
    }

    // Navigation & Tab State
    private val _currentTab = MutableStateFlow(AppTab.EXPLORE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun registerOrLogin(username: String, fullName: String, email: String, avatarColor: String, favGenre: String) {
        viewModelScope.launch {
            repository.registerOrLoginUser(username, fullName, email, avatarColor, favGenre)
        }
    }

    fun updateProfile(fullName: String, bio: String, favGenre: String, avatarColor: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            repository.updateUserProfile(
                current.copy(
                    fullName = fullName,
                    bio = bio,
                    favoriteGenre = favGenre,
                    avatarColorHex = avatarColor
                )
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    // Search & Filter & Sort
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Hunda") // "All"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SearchSortOption.POPULAR)
    val sortOption: StateFlow<SearchSortOption> = _sortOption.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSortOption(option: SearchSortOption) {
        _sortOption.value = option
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

    // Virtual Bookshelves
    val wantToReadBooks: StateFlow<List<BookEntity>> = repository.wantToReadBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentlyReadingBooks: StateFlow<List<BookEntity>> = repository.currentlyReadingBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readBooks: StateFlow<List<BookEntity>> = repository.readBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateShelfStatus(bookId: String, shelf: ShelfStatus) {
        viewModelScope.launch {
            repository.updateShelfStatus(bookId, shelf)
            if (_selectedBookForDetail.value?.id == bookId) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(shelfStatus = shelf.name)
            }
        }
    }

    val transactions: StateFlow<List<SaleTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double?> = repository.totalRevenue
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Filtered and Sorted books for Store / Explore screen
    val filteredExploreBooks: StateFlow<List<BookEntity>> = combine(
        allBooks,
        searchQuery,
        selectedCategory,
        sortOption
    ) { books, query, category, sort ->
        val filtered = books.filter { book ->
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.category.contains(query, ignoreCase = true) ||
                    book.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "Hunda" || book.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        when (sort) {
            SearchSortOption.POPULAR -> filtered.sortedByDescending { it.reviewsCount }
            SearchSortOption.RATING -> filtered.sortedByDescending { it.rating }
            SearchSortOption.NEWEST -> filtered.reversed()
            SearchSortOption.PRICE_LOW -> filtered.sortedBy { it.priceEtb }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recommendations Engine based on user reading history and popular trends
    val recommendedBooks: StateFlow<List<RecommendedBook>> = combine(
        allBooks,
        readBooks,
        currentlyReadingBooks,
        userProfile
    ) { all, read, current, profile ->
        if (all.isEmpty()) return@combine emptyList()

        val readIds = (read + current).map { it.id }.toSet()
        val readGenres = (read + current).map { it.category }.distinct()
        val favGenre = profile?.favoriteGenre ?: "Asoosama"

        val recommendations = mutableListOf<RecommendedBook>()

        // 1. Recommend unread books matching genres the user has read or loves
        all.filter { !readIds.contains(it.id) && (readGenres.contains(it.category) || it.category == favGenre) }
            .take(3)
            .forEach { book ->
                recommendations.add(
                    RecommendedBook(
                        book = book,
                        reasonAfaanOromo = "Gosa '${book.category}' jaallattuuf gorfame",
                        reasonEnglish = "Because you enjoy ${book.category}"
                    )
                )
            }

        // 2. Recommend trending / highest rated books
        all.sortedByDescending { it.rating * it.reviewsCount }
            .filter { b -> !readIds.contains(b.id) && recommendations.none { it.book.id == b.id } }
            .take(3)
            .forEach { book ->
                recommendations.add(
                    RecommendedBook(
                        book = book,
                        reasonAfaanOromo = "Biyya Keessatti Jaalatamaa (${book.rating} ★)",
                        reasonEnglish = "Top Rated & Trending (${book.rating} ★)"
                    )
                )
            }

        recommendations.ifEmpty {
            all.take(4).map {
                RecommendedBook(
                    book = it,
                    reasonAfaanOromo = "Filatamaa Mana Kitaabaa",
                    reasonEnglish = "Staff Pick"
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Book Detail Modal
    private val _selectedBookForDetail = MutableStateFlow<BookEntity?>(null)
    val selectedBookForDetail: StateFlow<BookEntity?> = _selectedBookForDetail.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewsForSelectedBook: StateFlow<List<ReviewEntity>> = _selectedBookForDetail
        .flatMapLatest { book ->
            if (book != null) {
                repository.getReviewsForBook(book.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openBookDetail(book: BookEntity) {
        _selectedBookForDetail.value = book
    }

    fun closeBookDetail() {
        _selectedBookForDetail.value = null
    }

    // Submit Review and Rating
    fun submitReview(book: BookEntity, rating: Int, reviewText: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val user = userProfile.value ?: InitialBookData.getDefaultProfile()
            val newReview = repository.submitReview(
                bookId = book.id,
                userId = user.id,
                userName = user.fullName.ifBlank { user.username },
                rating = rating,
                reviewText = reviewText
            )
            // Refresh active book detail
            val current = _selectedBookForDetail.value
            if (current != null && current.id == book.id) {
                val newCount = current.reviewsCount + 1
                val newAvg = ((current.rating * current.reviewsCount + rating) / newCount)
                val roundedAvg = (Math.round(newAvg * 10.0) / 10.0).toFloat()
                _selectedBookForDetail.value = current.copy(
                    rating = roundedAvg,
                    reviewsCount = newCount,
                    userRating = rating,
                    shelfStatus = ShelfStatus.READ.name
                )
            }
            onComplete()
        }
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
            // Also ensure it is marked on the Currently Reading shelf if not already on Read
            if (book.shelfStatus != ShelfStatus.READ.name) {
                repository.updateShelfStatus(book.id, ShelfStatus.CURRENTLY_READING)
            }
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
            delay(1200)
            repository.downloadBook(bookId)
            _downloadingBookId.value = null
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
            delay(1500)
            val txn = repository.buyBook(book, buyerName, paymentMethod)
            repository.downloadBook(book.id)
            repository.updateShelfStatus(book.id, ShelfStatus.WANT_TO_READ)
            _isProcessingPurchase.value = false
            _purchaseSuccessTransaction.value = txn
            if (_selectedBookForDetail.value?.id == book.id) {
                _selectedBookForDetail.value = _selectedBookForDetail.value?.copy(
                    isPurchased = true,
                    isDownloaded = true,
                    shelfStatus = ShelfStatus.WANT_TO_READ.name
                )
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
