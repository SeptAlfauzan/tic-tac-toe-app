package com.example.tictactoe.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.tictactoe.R
import com.example.tictactoe.data.Rectangle

class BoardView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val paint = Paint()
    private val listArea = mutableListOf<Rectangle>()
    private val rowNumbers = 3
    private val columnNumbers = 3
    private var xDrawable: Drawable?
    private var xPos = -1f
    private var yPos = -1f

    companion object {
        val TAG = BoardView::class.java.simpleName
    }

    init {
        xDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_close_24)

        paint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        initAreas()
    }

    private fun initAreas(){
        val screenWidth = resources.displayMetrics.widthPixels
        val SQUARE_BASE_SIZE = screenWidth / 3

        for (row in 0..rowNumbers){
            for (column in 0..columnNumbers) {
                val left = column * SQUARE_BASE_SIZE
                val top = row * SQUARE_BASE_SIZE
                val right = left + SQUARE_BASE_SIZE
                val bottom = top + SQUARE_BASE_SIZE

                listArea.add(Rectangle(top.toFloat(), bottom.toFloat(), left.toFloat(), right.toFloat()))
            }
        }
    }

    private fun drawOnBoardPos(canvas: Canvas, position: Rectangle){

        val xCenter = (position.right + position.left) / 2
        val yCenter = (position.bottom + position.top) / 2

        xDrawable?.let {
            canvas.drawBitmap(it.toBitmap(), xCenter - it.intrinsicWidth/2, yCenter - it.intrinsicHeight/2, paint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val BASE_SIZE = width.toFloat()
        val SQUARE_BASE_SIZE = BASE_SIZE / 3

//      draw outer rectangle
        canvas.drawRect(0f, 0f, BASE_SIZE, BASE_SIZE, paint)
//      draw every mini squares in it
        for (row in 0..rowNumbers){
            for (column in 0..columnNumbers){
                val left = column * SQUARE_BASE_SIZE
                val top = row * SQUARE_BASE_SIZE

                canvas.drawRect(left, top, SQUARE_BASE_SIZE, SQUARE_BASE_SIZE, paint)
            }
        }

        listArea.forEachIndexed { index, square ->
            if(xPos > square.left && xPos < square.right && yPos > square.top && yPos < square.bottom){
                Log.d(TAG, "onTouchEvent: $index")
                drawOnBoardPos(canvas, square)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN){
            xPos = event.x
            yPos = event.y
            invalidate()
        }
        return super.onTouchEvent(event)
    }
}