package eu.tutorials.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


// A short summary of this whole page in given in myNotes with the name DrawingVIew.kt , please refer if needed

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

//set up drawing is a function when u do not use init then u have to manually call the setUpDrawing function whenever u want to initialize the all
// the value but by using init function it  automatically executes the initialization logic immediately after an instance of the class is created
    init {
        setUpDrawing()
    }
    fun onClickUndo(){
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint() // Initialize the mDrawPaint object to control how things are drawn on the screen (color, thickness, style, etc.).
        mDrawPath = CustomPath(color, mBrushSize) //Create a new mDrawPath object to store the path information of the user's drawing, including color and brush size.
        mDrawPaint!!.color = color //Set the color of the brush (for drawing) to the color variable (which you likely defined elsewhere).
        mDrawPaint!!.style=Paint.Style.STROKE //Set the painting style of the mDrawPaint object to STROKE. This means it will draw outlines (lines) instead of filling shapes.
        mDrawPaint!!.strokeJoin= Paint.Join.ROUND // Make the corners of the lines (where two lines meet) round using the strokeJoin property.
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND //Make the ends of the lines (where a line starts or stops) round using the strokeCap property.
        mCanvasPaint = Paint(Paint.DITHER_FLAG) //Create a new mCanvasPaint object (likely for the background). The Paint.DITHER_FLAG might help smooth out color blending, but its usage depends on your specific needs.
//      mBrushSize = setSizeForBrush(20.toFloat())
    }


    /**
     * Manages the canvas bitmap used for drawing within this view.
     * This function ensures the bitmap's dimensions match the view's size for efficient drawing.
     * Bitmaps are pixel buffers used to store image data and can be memory intensive, especially for larger sizes with high color depth.
     * By recreating the bitmap only when the view resizes, we avoid holding onto unnecessary bitmaps and optimize memory usage.
     * This approach is particularly beneficial for views that frequently change their size or content.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Call the superclass's onSizeChanged method first
        super.onSizeChanged(w, h, oldw, oldh)

        // Check if the view's size has actually changed
        if (w != oldw || h != oldh) {
            // If the size changed, create a new Bitmap object
            mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            // Create a Canvas object to draw on the Bitmap
            canvas = Canvas(mCanvasBitmap!!)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)


        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path,mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchY != null) {
                    if (touchX != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
               mDrawPath= CustomPath(color,mBrushSize)
            }
            else -> return false
        }

        invalidate()

        return true
    }

    /**
     * Sets the brush size for drawing. This method converts the provided size
     * to device-independent pixels (dp) for consistent behavior across devices.
     *
     * @param newSize The desired brush size (thickness) in any unit (likely pixels).
     */
    fun setSizeForBrush(newSize:Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize,resources.displayMetrics
        )
        // here TypedValue.applyDimension(inbuild function) is used for making the the new size compatible with screen in which u are making it , it take the reference from the screen resolution
        mDrawPaint!!.strokeWidth=mBrushSize
    }


    fun setColor(newColor : String){
        color = Color.parseColor(newColor)
        mDrawPath!!.color = color
    }

    // An inner class for custom path with two parameters as color and stroke size
    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path() {


    }
}