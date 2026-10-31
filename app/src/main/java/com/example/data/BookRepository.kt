package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val libraryBooks: Flow<List<BookEntity>> = bookDao.getLibraryBooks()
    val downloadedBooks: Flow<List<BookEntity>> = bookDao.getDownloadedBooks()
    val favoriteBooks: Flow<List<BookEntity>> = bookDao.getFavoriteBooks()
    val userListedBooks: Flow<List<BookEntity>> = bookDao.getUserListedBooks()
    val allTransactions: Flow<List<SaleTransactionEntity>> = bookDao.getAllTransactions()
    val totalRevenue: Flow<Double?> = bookDao.getTotalSalesRevenue()

    // Virtual Bookshelves
    val wantToReadBooks: Flow<List<BookEntity>> = bookDao.getBooksByShelf(ShelfStatus.WANT_TO_READ.name)
    val currentlyReadingBooks: Flow<List<BookEntity>> = bookDao.getBooksByShelf(ShelfStatus.CURRENTLY_READING.name)
    val readBooks: Flow<List<BookEntity>> = bookDao.getBooksByShelf(ShelfStatus.READ.name)

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = bookDao.getUserProfile()

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        val count = bookDao.getBooksCount()
        if (count == 0) {
            bookDao.insertBooks(InitialBookData.getSampleBooks())
            bookDao.insertReviews(InitialBookData.getInitialReviews())
            bookDao.insertUserProfile(InitialBookData.getDefaultProfile())
        } else {
            val profile = bookDao.getUserProfileSync()
            if (profile == null) {
                bookDao.insertUserProfile(InitialBookData.getDefaultProfile())
            }
        }
    }

    fun getBook(id: String): Flow<BookEntity?> = bookDao.getBookById(id)

    suspend fun updateReadingProgress(bookId: String, page: Int) = withContext(Dispatchers.IO) {
        bookDao.updateReadingProgress(bookId, page, System.currentTimeMillis())
    }

    suspend fun toggleFavorite(bookId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        bookDao.updateFavoriteStatus(bookId, !currentFavorite)
    }

    suspend fun updateShelfStatus(bookId: String, shelf: ShelfStatus) = withContext(Dispatchers.IO) {
        bookDao.updateShelfStatus(bookId, shelf.name)
    }

    suspend fun downloadBook(bookId: String) = withContext(Dispatchers.IO) {
        bookDao.updateDownloadStatus(bookId, true)
    }

    suspend fun removeDownload(bookId: String) = withContext(Dispatchers.IO) {
        bookDao.updateDownloadStatus(bookId, false)
    }

    suspend fun buyBook(
        book: BookEntity,
        buyerName: String,
        paymentMethod: String
    ): SaleTransactionEntity = withContext(Dispatchers.IO) {
        val transaction = SaleTransactionEntity(
            id = UUID.randomUUID().toString(),
            bookId = book.id,
            bookTitle = book.title,
            amountEtb = book.priceEtb,
            buyerName = buyerName.ifBlank { "Maamila" },
            paymentMethod = paymentMethod,
            timestamp = System.currentTimeMillis(),
            transactionRef = "TXN-${System.currentTimeMillis().toString().takeLast(6)}"
        )
        bookDao.insertTransaction(transaction)
        bookDao.updatePurchaseStatus(book.id, true)
        transaction
    }

    suspend fun publishBookForSale(
        title: String,
        author: String,
        category: String,
        description: String,
        priceEtb: Double,
        pages: Int,
        colorHex: String,
        content: String
    ): BookEntity = withContext(Dispatchers.IO) {
        val newBook = BookEntity(
            id = "user_book_${UUID.randomUUID()}",
            title = title,
            author = author.ifBlank { "Barreessaa Haaraa" },
            category = category,
            description = description,
            coverColorHex = colorHex,
            pagesCount = if (pages > 0) pages else 65,
            priceEtb = priceEtb,
            rating = 5.0f,
            reviewsCount = 1,
            isDownloaded = true,
            fileSizeMb = 1.8,
            isPurchased = true,
            isFavorite = false,
            isUserListed = true,
            currentPage = 1,
            lastReadTimestamp = System.currentTimeMillis(),
            content = content,
            shelfStatus = ShelfStatus.READ.name,
            userRating = 5
        )
        bookDao.insertBook(newBook)
        newBook
    }

    suspend fun deleteBook(bookId: String) = withContext(Dispatchers.IO) {
        bookDao.deleteBookById(bookId)
    }

    fun getBookmarks(bookId: String): Flow<List<BookmarkEntity>> = bookDao.getBookmarksForBook(bookId)

    suspend fun addBookmark(
        bookId: String,
        chapterTitle: String,
        pageNumber: Int,
        noteText: String,
        quoteText: String
    ) = withContext(Dispatchers.IO) {
        val bookmark = BookmarkEntity(
            bookId = bookId,
            chapterTitle = chapterTitle,
            pageNumber = pageNumber,
            noteText = noteText,
            quoteText = quoteText
        )
        bookDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) = withContext(Dispatchers.IO) {
        bookDao.deleteBookmark(bookmark)
    }

    // Reviews & Ratings
    fun getReviewsForBook(bookId: String): Flow<List<ReviewEntity>> = bookDao.getReviewsForBook(bookId)

    suspend fun submitReview(
        bookId: String,
        userId: String,
        userName: String,
        rating: Int,
        reviewText: String
    ): ReviewEntity = withContext(Dispatchers.IO) {
        val review = ReviewEntity(
            id = "review_${UUID.randomUUID()}",
            bookId = bookId,
            userId = userId,
            userName = userName,
            rating = rating,
            reviewText = reviewText,
            timestamp = System.currentTimeMillis()
        )
        bookDao.insertReview(review)
        bookDao.updateUserRating(bookId, rating)

        // Recalculate average rating & reviews count
        val count = bookDao.getReviewsCountForBook(bookId)
        val avg = bookDao.getAverageRatingForBook(bookId) ?: rating.toDouble()
        val roundedAvg = (Math.round(avg * 10.0) / 10.0).toFloat()
        bookDao.updateBookRatingStats(bookId, roundedAvg, count)

        // Ensure book is on the Read shelf
        bookDao.updateShelfStatus(bookId, ShelfStatus.READ.name)

        review
    }

    // User Profile
    suspend fun updateUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        bookDao.updateUserProfile(profile)
    }

    suspend fun registerOrLoginUser(
        username: String,
        fullName: String,
        email: String,
        avatarColor: String = "#8C2D19",
        favGenre: String = "Asoosama"
    ): UserProfileEntity = withContext(Dispatchers.IO) {
        val existing = bookDao.getUserProfileSync()
        val profile = (existing ?: InitialBookData.getDefaultProfile()).copy(
            username = username,
            fullName = fullName.ifBlank { username },
            email = email,
            avatarColorHex = avatarColor,
            favoriteGenre = favGenre,
            isLoggedIn = true
        )
        bookDao.insertUserProfile(profile)
        profile
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        val existing = bookDao.getUserProfileSync()
        if (existing != null) {
            bookDao.updateUserProfile(existing.copy(isLoggedIn = false))
        }
    }
}
