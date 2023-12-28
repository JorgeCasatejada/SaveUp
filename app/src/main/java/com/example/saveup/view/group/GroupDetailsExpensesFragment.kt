package com.example.saveup.view.group

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saveup.databinding.FragmentGroupDetailsExpensesBinding
import com.example.saveup.model.Transaction
import com.example.saveup.view.adapter.TransactionsListAdapter
import com.example.saveup.view.mainScreen.AddTransaction
import com.example.saveup.view.mainScreen.MainScreenFragment
import com.example.saveup.viewModel.MainViewModel

class GroupDetailsExpensesFragment : Fragment() {
    val ACTIVITY_MODE = "activity_mode"
    val MODE_ADD: Int = 1
    val MODE_DETAILS: Int = 2
    val INTENT_ADD_TRANSACTION: Int = 1
    private var _binding: FragmentGroupDetailsExpensesBinding? = null
    private val binding get() = _binding!!
    private var viewModel: MainViewModel? = null
    private lateinit var transactionsListAdapter: TransactionsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGroupDetailsExpensesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.tvTitleGroup.text = viewModel!!.currentGroup.value?.title ?: ""
        updateBalance()

        binding.btAdd.setOnClickListener {
            startAddGroupTransaction()
        }

        binding.recyclerTransactionsGroup.layoutManager = LinearLayoutManager(context)
        binding.recyclerTransactionsGroup.setHasFixedSize(true)
        transactionsListAdapter = TransactionsListAdapter(requireContext()) { clickedItem ->
            showTransaction(clickedItem)
        }
        binding.recyclerTransactionsGroup.adapter = transactionsListAdapter

        viewModel!!.currentGroupTransactions.observe(viewLifecycleOwner,  Observer {
            transactionsListAdapter.update(it)
        })

        binding.btClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_ADD_TRANSACTION && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                handleTransactionResult(data)
            }
        }
    }

    private fun handleTransactionResult(data: Intent) {
        val mode = data.getIntExtra(AddTransaction.MODE, 0)
        when (mode) {
            AddTransaction.MODE_ADD -> {
                val createdTransaction = data.getParcelableExtra<Transaction>(AddTransaction.CREATED_TRANSACTION)
                if (createdTransaction != null) {
                    viewModel?.addTransactionToGroup(createdTransaction,
                        viewModel!!.currentGroup.value!!
                    )
                }
            }
            AddTransaction.MODE_DELETE -> {
                val detailsTransaction = data.getParcelableExtra<Transaction>(AddTransaction.DETAILS_TRANSACTION)
                if (detailsTransaction != null) {
                    viewModel?.removeTransactionFromGroup(detailsTransaction,
                        viewModel!!.currentGroup.value!!
                    )
                }
            }
            AddTransaction.MODE_MODIFY -> {
                val transactionOld = data.getParcelableExtra<Transaction>(AddTransaction.OLD_MODIFIED_TRANSACTION)
                val transactionNew = data.getParcelableExtra<Transaction>(AddTransaction.NEW_MODIFIED_TRANSACTION)
                if (transactionOld != null && transactionNew != null) {
                    viewModel?.modifyTransactionFromGroup(transactionOld, transactionNew,
                        viewModel!!.currentGroup.value!!
                    )
                }
            }
        }
        updateBalance()
    }

    private fun updateBalance() {
        // TODO: hacer funcion como en mainscreenfragment que actualiza solo el balance
        binding.tvInitialBudgetGroup.text = viewModel!!.currentGroup.value?.initialBudget.toString() + "€"
        binding.tvActualBudgetGroup.text = viewModel!!.currentGroup.value?.currentBudget.toString() + "€"
    }


    private fun showTransaction(transaction: Transaction?) {
        val intentAddTransaction = Intent(activity, AddTransaction::class.java)
        intentAddTransaction.putExtra(
            ACTIVITY_MODE,
            MODE_DETAILS
        )
        intentAddTransaction.putExtra(MainScreenFragment.TRANSACTION_DETAILS, transaction)
        startActivityForResult(intentAddTransaction, MainScreenFragment.INTENT_ADD_TRANSACTION)
    }

    private fun startAddGroupTransaction() {
        val intentAddTransaction = Intent(activity, AddTransaction::class.java)
        intentAddTransaction.putExtra(
            ACTIVITY_MODE,
            MODE_ADD
        )
        startActivityForResult(intentAddTransaction, MainScreenFragment.INTENT_ADD_TRANSACTION)
    }
}