package com.example.saveup.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.MainViewModel
import com.example.saveup.databinding.FragmentLimitsBinding
import com.example.saveup.model.Account
import java.util.Locale

class LimitsFragment : Fragment() {
    private var _binding: FragmentLimitsBinding? = null
    private val binding get() = _binding!!

    private var account: Account? = null

    private var viewModel: MainViewModel? = null

    private val ACCOUNT = "Account"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLimitsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if (!viewModel!!.monthlyLimit.isInitialized) {
            viewModel!!.getLimit()
        }

        initializeVariables()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.monthlyLimit?.observe(viewLifecycleOwner) {
            setLimit(it)
        }
    }

    private fun initializeVariables() {
        binding.saveFab.setOnClickListener {
            // TODO: validar límite
            val newLimit = binding.etLimit.text.toString()
            if (newLimit.isNotBlank()) {
                val limit = newLimit.toDouble()
                if (limit > 0.01) {
                    viewModel?.updateLimit(limit)
                }
            }
        }
    }

    private fun setLimit(limit: Double?) {
        if (limit == null) {
            binding.etLimit.setText("")
        } else {
            binding.etLimit.setText(String.format(Locale.getDefault(), "%.2f", limit))
        }
    }

    companion object {
        private const val ACCOUNT = "Account"

        @JvmStatic
        fun newInstance(account: Account?): LimitsFragment {
            val fragment = LimitsFragment()
            val args = Bundle()
            args.putParcelable(ACCOUNT, account)
            fragment.arguments = args
            return fragment
        }
    }
}