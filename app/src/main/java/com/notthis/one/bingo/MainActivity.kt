package com.notthis.one.bingo

import android.os.Bundle
import android.util.Log
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
  lateinit var numberAdapter: NumberAdapter

  private val randomNumber = 25
  private val matrixSize = 3
  private val winCondition = 2
  private val numberListener: NumberAdapter.NumberListener = object : NumberAdapter.NumberListener {
    override fun onNumberSelected() {
      if (checkWinOrNot(numberAdapter.selectedPositions, matrixSize, winCondition)) {
        binding.tvWin.visibility = View.VISIBLE
      } else {
        binding.tvWin.visibility = View.GONE
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    binding = ActivityMainBinding.inflate(layoutInflater)
    numberAdapter = NumberAdapter(numberListener)
    setContentView(binding.root)
    setView()
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
          numberAdapter.restore()
          numberAdapter.submitList(generateRandomNumber(randomNumber, matrixSize))
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
        var rowMatchCount = 0
        for (i in 0 until size) {
          val number = row + i

          if (sortedSelectedCoordinate.contains(number)) {
            rowMatchCount++
          }
          if (rowMatchCount == size) {
            Log.d("Steven", "from row")
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
        var rowMatchCount = 0 // 計算符合的數字數量
        for (i in 0 until size) {
          val number = col + size * i

          if (sortedSelectedCoordinate.contains(number)) {
            rowMatchCount++
          }
          if (rowMatchCount == size) {
            Log.d("Steven", "from col")
            totalMatchedLine++
          }
        }
      } else {
        continue
      }
    }

    // 檢查主對角線(左上 - 右下)，找左上元素(0)
    if (sortedSelectedCoordinate.contains(0)) {
      var diagonalMatchCount = 0
      for (i in 0 until size) {
        val number = (1 + size) * i

        if (sortedSelectedCoordinate.contains(number)) {
          diagonalMatchCount++
        }
        if (diagonalMatchCount == size) {
          Log.d("Steven", "from dia")
          totalMatchedLine++
        }
      }
    }
    // 檢查次對角線(右上 - 左下)，找右上元素(2)
    if (sortedSelectedCoordinate.contains(size - 1)) {
      var subDiagonalMatchCount = 0
      for (i in 1 until size + 1) {
        val number = (size - 1) * i

        if (sortedSelectedCoordinate.contains(number)) {
          subDiagonalMatchCount++
        }
        if (subDiagonalMatchCount == size) {
          Log.d("Steven", "from subdia")
          totalMatchedLine++
        }
      }
    }
    Log.d("Steven", "totalMatchedLine: $totalMatchedLine \n selectedCoordinate: $sortedSelectedCoordinate")
    if (totalMatchedLine >= winCondition) {
      return true
    }

    return false // 預設情況下，沒有達成勝利條件
  }
}
