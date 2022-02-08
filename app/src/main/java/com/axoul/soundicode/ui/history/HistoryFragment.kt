package com.axoul.soundicode.ui.history

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axoul.soundicode.R
import com.axoul.soundicode.communication.JsonResponse
import com.axoul.soundicode.databinding.FragmentHistoryBinding
import com.axoul.soundicode.history.History
import com.axoul.soundicode.history.HistoryAdapter
import com.axoul.soundicode.history.HistoryDialog

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var rvHistory: RecyclerView
    private var rspList: ArrayList<JsonResponse?>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root = binding.root
        setHasOptionsMenu(true)

        rvHistory = binding.recyclerHistory
        rspList = History.getHistory(activity) as ArrayList<JsonResponse?>?
        if (rspList != null) {
            val adapter = HistoryAdapter(rspList!!)
            rvHistory.adapter = adapter
            rvHistory.layoutManager = LinearLayoutManager(context)
            rvHistory.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

            adapter.setOnAdapterItemClickListener(object :
                HistoryAdapter.OnAdapterItemClickListener {
                override fun onAdapterViewClick(view: View) {
                    val childAdapterPosition = rvHistory.getChildAdapterPosition(view)
                    val itemAtPosition = adapter.getItemAtPosition(childAdapterPosition)
                    if (itemAtPosition != null) {
                        onAdapterClick(itemAtPosition)
                    }
                }
            })
        }
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_history -> {
                History.clearHistory(activity)
                Toast.makeText(activity, getString(R.string.cleared_history), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onAdapterClick(rspResult: JsonResponse) {
        val dialog = HistoryDialog(this.requireContext(), rspResult)
        dialog.show()
    }
}