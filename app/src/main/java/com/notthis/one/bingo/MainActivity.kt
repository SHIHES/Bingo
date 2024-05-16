package com.notthis.one.bingo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        if (checkWinOrNot(numberAdapter.selectedPositions, matrixSize, winCondition)) {
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
        setOnClickListener {
          numberAdapter.submitList(generateRandomNumber(randomNumber, matrixSize))
          numberAdapter.restore()
        }
      }
    }
  }

  fun generateRandomNumber(
    range: Int,
    size: Int
  ): List<Int> {
    return (1..range).shuffled()
      .take(size * size)
  }

  fun checkWinOrNot(
    selectedCoordinate: List<Int>,
    size: Int,
    winCondition: Int
  ): Boolean {
    val sortedSelectedCoordinate = selectedCoordinate.sorted()
    var totalMatchedLine = 0
    // 0 - 8 (3 * 3)
    // 0 1 2
    // 3 4 5
    // 6 7 8

    // selected 0,1,2,3,7,8, 要是 sorted 的
    // 一個 for loop 跑 整個 matrix size
    // 1. 橫的連續數字有 matrix size 個 = 一條
    // 2. 直的為 matrix size 的等差且有 matrix size 個 = 一條
    // 3. 對角線以最左上角的元素開始數(0)，為 matrix size + 1 的等差且有 matrix size 個 = 一條
    // 4. 對角線以最右上的元素開始數(matrix size - 1)，為 matrix size - 1 的等差且有 matrix size 個 = 一條


    // 檢查行
    for (row in sortedSelectedCoordinate) {
      // 當有第一列的元素時，啟動行的檢查 EX: 有 3 檢查有無 4, 5
      if (row % size == 0) {
        for (i in 0 until size) {
          var rowMatchCount = 0
          val number = row + i

          if (sortedSelectedCoordinate.contains(number)) {
            rowMatchCount++
          }
          if (rowMatchCount == size) {
            totalMatchedLine++
          }
        }
      } else {
        continue
      }
    }

    // 檢查列
    for (col in sortedSelectedCoordinate) {
      // 當有第一行的元素時，啟動列的檢查 EX: 有 1 檢查有無 4, 7
      if (col < size) {
        for (i in 0 until size) {
          var rowMatchCount = 0 // 計算符合的數字數量
          val number = col + size * i

          if (sortedSelectedCoordinate.contains(number)) {
            rowMatchCount++
          }
          if (rowMatchCount == size) {
            totalMatchedLine++
          }
        }
      } else {
        continue
      }
    }

    // 檢查主對角線(左上 - 右下)，找左上元素(0)
    if (sortedSelectedCoordinate.contains(0)) {
      for (i in 0 until size) {
        var diagonalMatchCount = 0
        val number = (1 + size) * i

        if (sortedSelectedCoordinate.contains(number)) {
          diagonalMatchCount++
        }
        if (diagonalMatchCount == size) {
          totalMatchedLine++
        }
      }
    }
    // 檢查次對角線(右上 - 左下)，找右上元素(2)
    if (sortedSelectedCoordinate.contains(size - 1)) {
      for (i in 0 until size) {
        var subDiagonalMatchCount = 0
        val number = (size - 1) * i

        if (sortedSelectedCoordinate.contains(number)) {
          subDiagonalMatchCount++
        }
        if (subDiagonalMatchCount == size) {
          totalMatchedLine++
        }
      }
    }

    if (totalMatchedLine >= winCondition) {
      return true
    }

    return false // 預設情況下，沒有達成勝利條件
  }
}
