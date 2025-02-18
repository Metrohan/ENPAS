package com.haeydra.enpas.project.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haeydra.enpas.R
import com.haeydra.enpas.project.Adapter.TrendsAdapter
import com.haeydra.enpas.project.Domain.TrendSDomain

class HomeActivity : AppCompatActivity() {
    private lateinit var adapterTrendsList: RecyclerView.Adapter<*> // Adaptör tanımlaması
    private lateinit var recyclerViewTrends: RecyclerView // RecyclerView değişkeni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        BottomNavigation()
    }

    private fun BottomNavigation() {
        val profileBtn: LinearLayout = findViewById(R.id.profileBtn)

        profileBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

    private fun initRecyclerView() {
        val items = ArrayList<TrendSDomain>()
        items.add(TrendSDomain("Future in AI, what will tomorrow be like", "The National", "trends"))
        items.add(TrendSDomain("Important points in work contracts", "Reuters", "trends2"))
        items.add(TrendSDomain("CocOynları yıkılıyor, ortalık kavruluyor", "The Önemli", "trends3"))

        recyclerViewTrends = findViewById(R.id.viewOngoing)
        recyclerViewTrends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterTrendsList = TrendsAdapter(items)
        recyclerViewTrends.adapter = adapterTrendsList
    }
}
