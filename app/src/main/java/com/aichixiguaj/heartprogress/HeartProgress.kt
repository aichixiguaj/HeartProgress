package com.aichixiguaj.heartprogress

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 *    @author   ： AiChiXiGuaJ
 *    @date      ： 2022/10/13 16:09
 *    @email    ： aichixiguaj@qq.com
 *    @desc     :    心率进度条
 */
class HeartProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 视图宽高
    private var viewWith = 0f
    private var viewHeight = 0f

    // 当前的进度和即将变化的进度
    private var currentHeartRate = 0f

    // 心可以移动的X坐标值
    private var heartMinX = 0f
    private var heartMaxX = 0f

    // 最大值
    private var maxHeartRate = 300f

    // 心的路径
    private val heartLeftPath = Path()
    private val heartRightPath = Path()

    // 视图高度
    private var viewMaxHeight = 60

    // 心左右边距(单边心的宽度)
    private val heartHorizontalPadding = 30

    // 心顶部向下浮动的值
    private val heartCenterYFloatValue = 0.25f

    // 线的高度
    private var lineWidth = 5f

    // 心下边的分割距离
    private val heartBottomDistance = 6

    // 圆比线高一丢丢 除以二得到半径
    private var pointRadius = (lineWidth + 12f) / 2f

    // 基础画笔
    private val basePaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    // 心的画笔
    private val heartPaint = Paint(basePaint).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
    }

    // 文本画笔
    private val textPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        textSize = 12f
        textAlign = Paint.Align.CENTER
        strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWith = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        viewHeight = min(viewMaxHeight, MeasureSpec.getSize(heightMeasureSpec)).toFloat()
        resetHeartPositionRange()
        setMeasuredDimension(viewWith.toInt(), viewHeight.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWith = w.toFloat()
        viewHeight = min(viewMaxHeight, h).toFloat()
        setMeasuredDimension(viewWith.toInt(), viewHeight.toInt())
        resetHeartPositionRange()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            // 绘制点
            drawPoint(it, true)
            drawPoint(it, false)
            // 绘制线
            drawLine(it)
            // 绘制心
            drawHeart(it)
            // 绘制
            drawHeartValue(it)
        }
    }

    /**
     *  绘制心跳的数值
     */
    private fun drawHeartValue(canvas: Canvas) {
        val heartTopCenterXPosition = getHeartTopCenterXPosition()
        val showValue = currentHeartRate.toInt().toString()
        val textMaxWidth = heartHorizontalPadding * 2 - lineWidth * 2
        val textLength = max(showValue.length,2)
        val textSize = textMaxWidth / textLength
        textPaint.textSize = textSize
        val textPositionY=if (textLength>2){
            viewHeight / 2+ (viewHeight / 2*0.1f)
        }else{
            viewHeight / 2 + (viewHeight / 2*0.25f)
        }
        canvas.drawText(showValue, heartTopCenterXPosition,textPositionY , textPaint)

    }

    /**
     *  重置心可移动的区间
     */
    private fun resetHeartPositionRange() {
        // 可移动的X轴最大最小值
        heartMinX = heartHorizontalPadding.toFloat()
        heartMaxX = viewWith - heartHorizontalPadding
    }

    /**
     *  绘制爱心
     */
    private fun drawHeart(it: Canvas) {
        // 心顶部中间下凹的点
        val heartTopCenterXPosition = getHeartTopCenterXPosition()
        val heartTopCenterYPosition = viewHeight * heartCenterYFloatValue

        // 重置路径
        heartLeftPath.reset()
        heartRightPath.reset()

        // 设置心的开始点(顶部中间凹陷处)
        heartLeftPath.moveTo(heartTopCenterXPosition, heartTopCenterYPosition)
        heartRightPath.moveTo(heartTopCenterXPosition, heartTopCenterYPosition)

        heartLeftPath.cubicTo(
            heartTopCenterXPosition + 0, 0f,
            heartTopCenterXPosition - heartHorizontalPadding, 0f,
            heartTopCenterXPosition - heartHorizontalPadding, heartTopCenterYPosition + 0
        )

        heartLeftPath.cubicTo(
            heartTopCenterXPosition - heartHorizontalPadding, viewHeight / 2,
            heartTopCenterXPosition - heartBottomDistance, viewHeight - pointRadius,
            heartTopCenterXPosition - heartBottomDistance, viewHeight - pointRadius
        )

        heartRightPath.cubicTo(
            heartTopCenterXPosition, 0f,
            heartTopCenterXPosition + heartHorizontalPadding, 0f,
            heartTopCenterXPosition + heartHorizontalPadding, heartTopCenterYPosition
        )

        heartRightPath.cubicTo(
            heartTopCenterXPosition + heartHorizontalPadding, viewHeight / 2,
            heartTopCenterXPosition + heartBottomDistance, viewHeight - pointRadius,
            heartTopCenterXPosition + heartBottomDistance, viewHeight - pointRadius
        )

        it.drawPath(heartLeftPath, heartPaint)
        it.drawPath(heartRightPath, heartPaint)
    }

    /**
     *  获取心顶部中间凹陷的X点
     */
    private fun getHeartTopCenterXPosition(): Float {
        // 计算点的百分比
        val progressPercent = currentHeartRate / maxHeartRate
        return max(progressPercent * (viewWith - heartHorizontalPadding),heartHorizontalPadding.toFloat())
    }

    /**
     *  绘制下面的线
     */
    private fun drawLine(it: Canvas) {
        basePaint.strokeWidth = lineWidth
        // 心的正中点
        val heartTopCenterXPosition = getHeartTopCenterXPosition()

        // 心左边的线
        it.drawLine(
            pointRadius, viewHeight - pointRadius,
            heartTopCenterXPosition - heartBottomDistance, viewHeight - pointRadius,
            basePaint
        )

        // 心右边的线
        it.drawLine(
            heartTopCenterXPosition + heartBottomDistance, viewHeight - pointRadius,
            viewWith - pointRadius, viewHeight - pointRadius,
            basePaint
        )
    }

    /**
     *  绘制点
     */
    private fun drawPoint(canvas: Canvas, isStart: Boolean) {
        val x = if (isStart) pointRadius else viewWith - pointRadius
        val y = if (isStart) viewHeight - pointRadius else viewHeight - pointRadius
        canvas.drawCircle(x, y, pointRadius, basePaint)
    }

    /**
     *  设置当前心率
     */
    fun setCurrentHeartRate(heartRate: Int) {
        this.currentHeartRate = if (heartRate < 0) {
            0f
        } else {
            max(0f, heartRate.toFloat())
        }
        invalidate()
    }
}