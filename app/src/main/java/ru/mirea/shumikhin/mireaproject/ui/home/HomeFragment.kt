package ru.mirea.shumikhin.mireaproject.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mirea.shumikhin.mireaproject.WorkManager.WorkerRandomizer
import ru.mirea.shumikhin.mireaproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        initWorker()

        return root
    }

    private fun initWorker() {
        binding.btnStartWorker.setOnClickListener {
            val workRequest = OneTimeWorkRequest.Builder(WorkerRandomizer::class.java).build()
            WorkManager
                .getInstance(requireContext())
                .enqueue(workRequest)
        }
        lifecycleScope.launch {
            repeat(Int.MAX_VALUE) {
                binding.tvRandom.text = "${getWorkerMessage()}"
                delay(1000)
            }
        }
    }

    private fun getWorkerMessage(): String? {
        val sharedPreferences =
            requireContext().getSharedPreferences("my_randomizer_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(
            WorkerRandomizer.WORKER_MSG,
            "It's only after we've lost everything that we're free to do anything."
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}