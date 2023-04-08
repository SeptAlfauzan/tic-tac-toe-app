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
    private val _filledXArea = mutableListOf<Int>()
    private val _filledOArea = mutableListOf<Int>()
    private val rowNumbers = 3
    private val columnNumbers = 3
    private var xDrawable: Drawable?
    private var oDrawable: Drawable?
    private var xPos = -1f
    private var yPos = -1f

    private var WIDTH: Int = 0
    private var isResetTrigger = false

    var isXturn = true
    var isAllBoarAreasdUsed = false
    val filledXArea: List<Int> get() = _filledXArea
    val filledOArea: List<Int> get() = _filledOArea

    private val bluePrimary = Color.parseColor("#7286D3")
    private val bluePastel = Color.parseColor("#7286D3")
    private val lightBluePastel = Color.parseColor("#E5E0FF")
    private val accent = Color.parseColor("#FFF2F2")

    companion object {
        val TAG = BoardView::class.java.simpleName
    }

    init {
        xDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_close_62)
        oDrawable = ContextCompat.getDrawable(context, R.drawable.outline_circle_62)

        paint.apply {
            color = lightBluePastel
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
    }

    private fun initAreas(screenWidth: Float){
        val SQUARE_BASE_SIZE = screenWidth / 3

        for (row in 0..rowNumbers){
            for (column in 0..columnNumbers) {
                val left = column * SQUARE_BASE_SIZE
                val top = row * SQUARE_BASE_SIZE
                val right = left + SQUARE_BASE_SIZE
                val bottom = top + SQUARE_BASE_SIZE

                listArea.add(Rectangle(top, bottom, left, right))
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        WIDTH = width

        setMeasuredDimension(width, height)
        initAreas(WIDTH.toFloat())
    }

    private fun drawOnBoardPos(canvas: Canvas, drawable: Drawable?, position: Rectangle){

        val xCenter = (position.right + position.left) / 2
        val yCenter = (position.bottom + position.top) / 2

        drawable?.let {
            canvas.drawBitmap(it.toBitmap(), xCenter - it.intrinsicWidth/2, yCenter - it.intrinsicHeight/2, paint)
        }
    }

    fun resetBoard(){
        isXturn = true
        isResetTrigger = true
        _filledXArea.clear()
        _filledOArea.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val BASE_SIZE = WIDTH.toFloat()
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

        if(isResetTrigger){
            isResetTrigger = false
            return
        }

        listArea.forEachIndexed { index, square ->
            if(xPos > square.left && xPos < square.right && yPos > square.top && yPos < square.bottom){//fill the filled area with current area index pressed

                if(_filledOArea.size + _filledXArea.size == listArea.size) isAllBoarAreasdUsed = true

                if(!isAllBoarAreasdUsed && !_filledXArea.contains(index) && !_filledOArea.contains(index)){//add current index, if it's not already added
                    if(isXturn) _filledXArea.add(index)
                    else _filledOArea.add(index)
                }
            }
        }

        _filledXArea.forEach{ xFilled ->
            drawOnBoardPos(canvas, xDrawable, listArea[xFilled])
        }
        _filledOArea.forEach{ oFilled ->
            drawOnBoardPos(canvas, oDrawable, listArea[oFilled])
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN){
            xPos = event.x
            yPos = event.y

            isXturn = !isXturn//change player turn
            invalidate()
        }
        return super.onTouchEvent(event)
    }

}