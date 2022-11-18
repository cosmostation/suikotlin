package io.cosmostation.sui.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import io.cosmostation.sui.sample.databinding.ActivityMainBinding
import io.cosmostation.suikotlin.SuiClient

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SampleViewModel by viewModels()
    private val preloadMnemonic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        binding.newMnemonic.setOnClickListener {
            viewModel.loadMnemonic(SuiClient.instance.generateMnemonic())
        }

        binding.loadMnemonic.setOnClickListener {
            viewModel.loadMnemonic(preloadMnemonic)
        }

        binding.faucet.setOnClickListener {
            viewModel.address.value?.let { address -> viewModel.faucet(address) };
        }

        binding.getObjectsByOwner.setOnClickListener {
            viewModel.address.value?.let { address -> viewModel.getObjectsByOwner(address) }
        }

        binding.getObjectDetails.setOnClickListener {
            viewModel.getObjectsDetails()
        }

        binding.getTransactions.setOnClickListener {
            viewModel.address.value?.let { address -> viewModel.getTransactions(address) }
        }

        binding.getTransactionDetails.setOnClickListener {
            viewModel.getTransactionDetails()
        }

        binding.transfer.setOnClickListener {
            viewModel.address.value?.let { address ->
                viewModel.objectInfos.value?.let {
                    viewModel.transferObject(it.first(), address, address)
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel.mnemonic.observe(this) {
            binding.mnemonic.text = it
        }

        viewModel.keyPair.observe(this) {
            Toast.makeText(this@MainActivity, "Key loaded !", Toast.LENGTH_SHORT).show()
        }

        viewModel.address.observe(this) {
            binding.address.text = it
        }

        viewModel.objectInfos.observe(this) {
            Toast.makeText(this@MainActivity, "${it.size} Objects loaded !", Toast.LENGTH_SHORT)
                .show()
        }

        viewModel.objectDetails.observe(this) {
            Toast.makeText(
                this@MainActivity, "${it.size} Object Details loaded !", Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.transactions.observe(this) {
            Toast.makeText(
                this@MainActivity, "${it.size} Transactions loaded !", Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.transactionDetails.observe(this) {
            Toast.makeText(
                this@MainActivity, "${it.size} Transaction Details loaded !", Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.toastMessage.observe(this) {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
        }
    }
}