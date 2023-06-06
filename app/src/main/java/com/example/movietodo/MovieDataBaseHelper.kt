package com.example.movietodo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movie_database"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "movies"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_WATCHED = "watched"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_WATCHED INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun addMovie(movieTitle: String, movieWatched: Int) {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, movieTitle)
            put(COLUMN_WATCHED, movieWatched)
        }
        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllMovies(): List<Movie> {
        val movieList = mutableListOf<Movie>()
        val db = readableDatabase
        val projection = arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_WATCHED)
        val sortOrder = "$COLUMN_TITLE ASC"
        val cursor = db.query(TABLE_NAME, projection, null, null, null, null, sortOrder)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val watched = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WATCHED))
            val movie = Movie(id, title, watched)
            movieList.add(movie)
        }
        cursor.close()
        db.close()
        return movieList
    }

    data class Movie(val id: Int, val title: String, val watched: Int)
}
