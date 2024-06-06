package com.example.batteryusagetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.TooltipPositionMode

class MainActivity : AppCompatActivity() {

    private lateinit var anyChartView: AnyChartView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppUsageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        anyChartView = findViewById(R.id.any_chart_view)
        recyclerView = findViewById(R.id.recycler_view)

        val batteryUsageData = fetchBatteryUsageData()

        setupChart(batteryUsageData)
        setupRecyclerView(batteryUsageData.last().appUsages)
    }

    private fun fetchBatteryUsageData(): List<BatteryUsage> {
        // Replace with actual data fetching logic
        return listOf(
            BatteryUsage(1622505600000, 100, listOf(AppUsage("App1", 20), AppUsage("App2", 30))),
            // Add more sample data here
        )
    }

    private fun setupChart(batteryUsageData: List<BatteryUsage>) {
        val cartesian: Cartesian = AnyChart.line()

        val data = batteryUsageData.map { CustomDataEntry(it.timestamp, it.totalUsage) }

        val set: Set = Set.instantiate()
        set.data(data)

        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Battery Usage")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(com.anychart.enums.MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        cartesian.animation(true)
        cartesian.title("Battery Usage Over Time")

        cartesian.yAxis(0).title("Usage")
        cartesian.xAxis(0).labels().format("{%Value}{type:datetime}")

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
        cartesian.interactivity().hoverMode(com.anychart.enums.HoverMode.BY_X)

        anyChartView.setChart(cartesian)
    }

    private fun setupRecyclerView(appUsages: List<AppUsage>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppUsageAdapter(appUsages)
        recyclerView.adapter = adapter
    }
}

data class BatteryUsage(
    val timestamp: Long,
    val totalUsage: Int,
    val appUsages: List<AppUsage>
)

data class AppUsage(
    val appName: String,
    val usage: Int
)

class CustomDataEntry(x: Long, value: Int) : DataEntry() {
    init {
        setValue("x", x)
        setValue("value", value)
    }
}