package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookById(id: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookByIdSync(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE isPurchased = 1 OR isDownloaded = 1 OR priceEtb = 0.0 OR isUserListed = 1 OR shelfStatus != 'NONE' ORDER BY lastReadTimestamp DESC, title ASC")
    fun getLibraryBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isDownloaded = 1 ORDER BY title ASC")
    fun getDownloadedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isUserListed = 1 ORDER BY title ASC")
    fun getUserListedBooks(): Flow<List<BookEntity>>

    // Virtual Bookshelves
    @Query("SELECT * FROM books WHERE shelfStatus = :shelf ORDER BY title ASC")
    fun getBooksByShelf(shelf: String): Flow<List<BookEntity>>

    @Query("UPDATE books SET shelfStatus = :shelf WHERE id = :bookId")
    suspend fun updateShelfStatus(bookId: String, shelf: String)

    @Query("UPDATE books SET userRating = :rating WHERE id = :bookId")
    suspend fun updateUserRating(bookId: String, rating: Int)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBooksCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET currentPage = :page, lastReadTimestamp = :timestamp WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: String, page: Int, timestamp: Long)

    @Query("UPDATE books SET isDownloaded = :isDownloaded WHERE id = :bookId")
    suspend fun updateDownloadStatus(bookId: String, isDownloaded: Boolean)

    @Query("UPDATE books SET isPurchased = :isPurchased WHERE id = :bookId")
    suspend fun updatePurchaseStatus(bookId: String, isPurchased: Boolean)

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    suspend fun updateFavoriteStatus(bookId: String, isFavorite: Boolean)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: String)

    // Bookmarks & Notes
    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC, createdAt DESC")
    fun getBookmarksForBook(bookId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<SaleTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: SaleTransactionEntity)

    @Query("SELECT SUM(amountEtb) FROM transactions")
    fun getTotalSalesRevenue(): Flow<Double?>

    // Reviews & Ratings
    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getReviewsForBook(bookId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("SELECT COUNT(*) FROM reviews WHERE bookId = :bookId")
    suspend fun getReviewsCountForBook(bookId: String): Int

    @Query("SELECT AVG(rating) FROM reviews WHERE bookId = :bookId")
    suspend fun getAverageRatingForBook(bookId: String): Double?

    @Query("UPDATE books SET rating = :newRating, reviewsCount = :newCount WHERE id = :bookId")
    suspend fun updateBookRatingStats(bookId: String, newRating: Float, newCount: Int)

    // User Profile
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)
}
