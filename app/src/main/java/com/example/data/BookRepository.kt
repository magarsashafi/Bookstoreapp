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

    suspend fun ensureInitialData() = withContext(Dispatchers.IO) {
        val count = bookDao.getBooksCount()
        if (count == 0) {
            bookDao.insertBooks(InitialBookData.getSampleBooks())
        }
    }

    fun getBook(id: String): Flow<BookEntity?> = bookDao.getBookById(id)

    suspend fun updateReadingProgress(bookId: String, page: Int) = withContext(Dispatchers.IO) {
        bookDao.updateReadingProgress(bookId, page, System.currentTimeMillis())
    }

    suspend fun toggleFavorite(bookId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        bookDao.updateFavoriteStatus(bookId, !currentFavorite)
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
            content = content
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
}
