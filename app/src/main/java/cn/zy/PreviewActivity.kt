package cn.zy

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.zy.R.id.viewPager

import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_preview.*

/**
 * @author ZyElite
 * @date 2018-06-25
 * @depot https://github.com/ZyElite/DragPhotoView
 *
 */
class PreviewActivity : AppCompatActivity() {

    private var mCurrentHeight = 0
    private var mCurrentWidth = 0
    private var mAdapter: ImageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val position = intent.getIntExtra("position", 0)
        val location = intent.getIntArrayExtra("location")
        mCurrentHeight = intent.getIntExtra("height", 0)
        mCurrentWidth = intent.getIntExtra("width", 0)
        val datas = ArrayList<Drawable>()
        datas.add(resources.getDrawable(R.mipmap.ic_1))
        datas.add(resources.getDrawable(R.mipmap.ic_2))
        datas.add(resources.getDrawable(R.mipmap.ic_3))
        datas.add(resources.getDrawable(R.mipmap.ic_4))
        datas.add(resources.getDrawable(R.mipmap.ic_5))
        mAdapter = ImageAdapter(datas)
        viewPager.adapter = mAdapter
        mAdapter!!.setLocation(location, position)
        mAdapter!!.setTarget(mCurrentHeight, mCurrentWidth)
        viewPager.currentItem = position
        mAdapter!!.setActivity(this)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    class ImageAdapter(private var datas: List<Drawable>) : PagerAdapter() {
        private var location: IntArray? = null
        private var currentPosition: Int = 0
        private var mCurrentHeight = 0
        private var mCurrentWidth = 0
        private var mActivity: AppCompatActivity? = null


        fun setTarget(targetHeight: Int, targerWidth: Int) {
            this.mCurrentHeight = targetHeight
            this.mCurrentWidth = targerWidth
        }

        fun setActivity(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun setLocation(location: IntArray, position: Int) {
            this.location = location
            this.currentPosition = position
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val dragPhotoView = LayoutInflater.from(container.context).inflate(R.layout.item_preview_layout, null) as DragPhotoView
            Glide.with(container.context).load(datas[position]).into(dragPhotoView)
            dragPhotoView.setOnExitListener(object : DragPhotoView.OnExitClickListener {
                override fun onExit(view: DragPhotoView, translateX: Float, translateY: Float, width: Int, height: Int) {
                    if (position == currentPosition) finish(width, translateX, height, translateY, view) else view.finishAnimator(mActivity!!)
                }

            })
            container.addView(dragPhotoView)
            return dragPhotoView
        }

        private fun finish(width: Int, translateX: Float, height: Int, translateY: Float, view: DragPhotoView) {
            val targetX = location!![0] + mCurrentWidth / 2
            val targetY = location!![1] + mCurrentHeight / 2
            val mTranslateX = if (targetX > width / 2) {
                -translateX + (targetX - width / 2)
            } else {
                -translateX - Math.abs(targetX - width / 2)
            }
            val mTranslateY = if (targetY > height / 2) {
                -translateY + (targetY - height / 2)
            } else {
                -translateY - Math.abs(targetY - height / 2)
            }

            val animatorX = ValueAnimator.ofFloat(0F, mTranslateX)
            val animatorY = ValueAnimator.ofFloat(0F, mTranslateY)
            animatorX.duration = 500
            animatorY.duration = 500

            animatorX.addUpdateListener {
                view.x = it.animatedValue as Float
            }
            animatorY.addUpdateListener {
                view.y = it.animatedValue as Float
            }
            animatorX.start()
            animatorY.start()

            animatorY.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    mActivity!!.finish()
                    mActivity!!.overridePendingTransition(0, 0)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return datas.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View?)
        }
    }

}
