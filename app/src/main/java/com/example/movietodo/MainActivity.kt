package com.example.movietodo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var addMovieButton: Button
    private lateinit var movieTitleEditText: EditText

    private lateinit var sharedPreferences: SharedPreferences
    private val movieList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        addMovieButton = findViewById(R.id.addMovieButton)
        movieTitleEditText = findViewById(R.id.movieTitleEditText)

        movieListAdapter = MovieListAdapter()
        listView.adapter = movieListAdapter

        sharedPreferences = getSharedPreferences("MoviePrefs", Context.MODE_PRIVATE)

        addMovieButton.setOnClickListener {
            val movieTitle = movieTitleEditText.text.toString()
            if (movieTitle.isNotEmpty()) {
                val movie = Movie(movieTitle)
                movieList.add(movie)
                movieListAdapter.notifyDataSetChanged()
                movieTitleEditText.text.clear()
                saveMovieList()
            }
        }

        retrieveMovieList()
    }

    inner class MovieListAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return movieList.size
        }

        override fun getItem(position: Int): Any {
            return movieList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(applicationContext)
                    .inflate(R.layout.movie_item, parent, false)
                viewHolder = ViewHolder(view)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val movie = getItem(position) as Movie

            viewHolder.titleTextView.text = movie.title

            if (movie.isWatched) {
                viewHolder.titleTextView.paintFlags =
                    viewHolder.titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                viewHolder.titleTextView.paintFlags =
                    viewHolder.titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            viewHolder.titleTextView.setOnClickListener {
                movie.isWatched = !movie.isWatched
                notifyDataSetChanged()
                saveMovieList()
            }

            return view
        }
    }

    inner class ViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val posterImageView: ImageView = view.findViewById(R.id.posterImageView)
    }

    private fun saveMovieList() {
        val editor = sharedPreferences.edit()
        editor.putInt("movieCount", movieList.size)

        for (i in 0 until movieList.size) {
            editor.putString("movieTitle$i", movieList[i].title)
            editor.putBoolean("movieStatus$i", movieList[i].isWatched)
        }

        editor.apply()
    }

    private fun retrieveMovieList() {
        val movieCount = sharedPreferences.getInt("movieCount", 0)
        movieList.clear()

        for (i in 0 until movieCount) {
            val title = sharedPreferences.getString("movieTitle$i", "") ?: ""
            val isWatched = sharedPreferences.getBoolean("movieStatus$i", false)
            val movie = Movie(title, isWatched)
            movieList.add(movie)
        }

        movieListAdapter.notifyDataSetChanged()
    }
}

data class Movie(val title: String, var isWatched: Boolean = false)
