package com.gathering.android.event.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class EndlessScrollListener(
    private val onLastItemReached: () -> Unit,
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 5
    private var firstVisibleItem = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
        firstVisibleItem = (recyclerView.layoutManager as? LinearLayoutManager)
            ?.findFirstVisibleItemPosition()
            ?: 0

        if (loading) {
            // If the total item count is 0, that means we currently do not have any items in the recycler view (they have been cleared).
            // This means we should set the previousTotal to 0 as well to "reset" this scroll listener
            if (totalItemCount == 0) {
                previousTotal = 0
            }

            // We finish "loading" when the totalItemCount is not equal to the previousTotal
            if (totalItemCount != previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
            <= (firstVisibleItem + visibleThreshold)
        ) {
            onLastItemReached()
            loading = true
        }
    }
}