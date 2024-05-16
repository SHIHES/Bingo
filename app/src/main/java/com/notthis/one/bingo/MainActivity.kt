package com.notthis.one.bingo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.notthis.one.bingo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  lateinit var binding: ActivityMainBinding
  private val numberAdapter = NumberAdapter()

  private val randomNumber = 25
  private val matrixSize = 3
  private val winCondition = 2

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setView()
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.CREATED) {
        if (checkWinOrNot(numberAdapter.selectedPosition, matrixSize, winCondition)){
          binding.tvWin.visibility = View.VISIBLE
        } else {
          binding.tvWin.visibility = View.GONE
        }
      }
    }
  }

  fun setView() {
    binding.apply {
      with(recyclerView) {
        adapter = numberAdapter
        numberAdapter.submitList(generateRandomNumber(randomNumber, matrixSize))
        (layoutManager as GridLayoutManager).spanCount = matrixSize
      }
      with(btn) {
        setOnClickListener{
          numberAdapter.submitList(generateRandomNumber(randomNumber, matrixSize))
          numberAdapter.restore()
        }
      }
    }
  }

  fun generateRandomNumber(range: Int, size: Int): List<Int> {
    return (1..range).shuffled().take(size * size)
  }

  fun checkWinOrNot(selectedCoordinate: List<Int>, size: Int, winCondition: Int): Boolean {
    val bingoNumbers = selectedCoordinate.toSet() // 將已選擇的數字轉換為集合，方便查找
    var totalMatchedLine = 0
    // 檢查每一行
    for (row in 0 until size * size) {
      var rowMatchCount = 0 // 計算符合的數字數量
      for (col in 0 until size) {
        val number = row * size + col // 計算矩陣中的索引
        if (number in bingoNumbers) { // 如果該數字已經選中
          rowMatchCount++ // 符合的數字數量加一
        }
      }
      if (rowMatchCount == size) { // 如果某一行的所有數字都已選中
        totalMatchedLine++
      }
    }

    // 檢查每一列
    for (col in 0 until size) {
      var colMatchCount = 0 // 計算符合的數字數量
      for (row in 0 until size) {
        val number = row * size + col // 計算矩陣中的索引
        if (number in bingoNumbers) { // 如果該數字已經選中
          colMatchCount++ // 符合的數字數量加一
        }
      }
      if (colMatchCount == size) { // 如果某一列的所有數字都已選中
        totalMatchedLine ++
      }
    }

    // 檢查主對角線
    var mainDiagonalMatchCount = 0 // 計算符合的數字數量
    for (i in 0 until size) {
      val number = i * size + i // 計算主對角線上的索引
      if (number in bingoNumbers) { // 如果該數字已經選中
        mainDiagonalMatchCount++ // 符合的數字數量加一
      }
    }
    if (mainDiagonalMatchCount == size) { // 如果主對角線的所有數字都已選中
      totalMatchedLine ++
    }

    // 檢查次對角線
    var subDiagonalMatchCount = 0 // 計算符合的數字數量
    for (i in 0 until size) {
      val number = (i + 1) * (size - 1) // 計算次對角線上的索引
      if (number in bingoNumbers) { // 如果該數字已經選中
        subDiagonalMatchCount++ // 符合的數字數量加一
      }
    }
    if (subDiagonalMatchCount == size) { // 如果次對角線的所有數字都已選中
      totalMatchedLine ++
    }

    if (totalMatchedLine >= winCondition) {
      return true
    }

    return false // 預設情況下，沒有達成勝利條件
  }
}
