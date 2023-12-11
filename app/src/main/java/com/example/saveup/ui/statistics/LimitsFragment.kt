package com.example.saveup.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.MainViewModel
import com.example.saveup.R
import com.example.saveup.databinding.FragmentGraphsBinding
import com.example.saveup.databinding.FragmentLimitsBinding
import com.example.saveup.model.Account

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

        //if (!viewModel)
        initializeVariables()

        return binding.root
    }

    private fun initializeVariables() {
        // TODO: obtener el texto de el monthlylimit actual
        binding.etLimit.setText("0.0")

        binding.saveFab.setOnClickListener {
            // TODO: guardar o actualizar el monthlylimit
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