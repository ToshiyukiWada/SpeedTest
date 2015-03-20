package jp.futuresoftware.android.sakura.core;

import java.util.ArrayList;
import java.util.List;

import jp.futuresoftware.android.sakura.SakuraActivity;
import jp.futuresoftware.android.sakura.SakuraManager;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author toshiyuki
 *
 */
public class SakuraTouchManager implements OnTouchListener
{
	// メンバ変数定義
	private SakuraManager sakuraManager;
	private static final int MAX_TOUCHPOINTS		= 10;
	public boolean[] isTouched						= new boolean[MAX_TOUCHPOINTS];
	private int[] touchX							= new int[MAX_TOUCHPOINTS];
	private int[] touchY							= new int[MAX_TOUCHPOINTS];
	private int[] touchPointerID					= new int[MAX_TOUCHPOINTS];
	private Pool<TouchEvent> touchEventPool;
	private List<TouchEvent> touchEvents			= new ArrayList<TouchEvent>();
	private List<TouchEvent> touchEventsBuffer		= new ArrayList<TouchEvent>();
	private float scaleX;
	private float scaleY;

	private int length;
	private int count;

	/**
	 * @param sakuraView
	 * @param scaleX
	 * @param scaleY
	 */
	@SuppressLint("ClickableViewAccessibility")
	public SakuraTouchManager(SakuraActivity SakuraActivity)
	{
		this.sakuraManager			= SakuraActivity.getSakuraManager();
	
		Pool.PoolObjectFactory<TouchEvent> factory = new Pool.PoolObjectFactory<SakuraTouchManager.TouchEvent>()
		{
			@Override
			public TouchEvent createObject()
			{
				return new TouchEvent();
			}
		};
		this.touchEventPool = new Pool<TouchEvent>(factory ,100);
		sakuraManager.getSakuraView().setOnTouchListener(this);
	}

	/**
	 * @param offsetX
	 * @param offsetY
	 * @param scaleX
	 * @param scaleY
	 */
	public void setScale(float scaleX, float scaleY)
	{
		this.scaleX				= scaleX;
		this.scaleY				= scaleY;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		synchronized(this)
		{
			int pointerIndex		= (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int pointerCount		= event.getPointerCount();
			TouchEvent touchEvent;
			for (int count = 0 ; count < MAX_TOUCHPOINTS ; count++)
			{
				if (count >= pointerCount)
				{
					this.isTouched[count]		= false;
					this.touchPointerID[count]	= -1;
					continue;
				}
				
				int pointerID = event.getPointerId(count);
				if (event.getAction() != MotionEvent.ACTION_MOVE && count != pointerIndex){ continue; }
				
				switch(event.getAction() & MotionEvent.ACTION_MASK)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						touchEvent					= touchEventPool.newObject();
						touchEvent.type				= TouchEvent.TOUCH_DOWN;
						touchEvent.pointer			= pointerID;
						touchEvent.x				= touchX[count] = (int)(event.getX(count) * scaleX);
						touchEvent.y				= touchY[count] = (int)(event.getY(count) * scaleY);
						this.isTouched[count]		= true;
						this.touchPointerID[count]	= pointerID;
						this.touchEventsBuffer.add(touchEvent);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL:
						touchEvent					= touchEventPool.newObject();
	                    touchEvent.type				= TouchEvent.TOUCH_UP;
	                    touchEvent.pointer			= pointerID;
	                    touchEvent.x				= touchX[count] = (int)(event.getX(count) * scaleX);
	                    touchEvent.y				= touchY[count] = (int)(event.getY(count) * scaleY);
	                    this.isTouched[count]		= false;
	                    this.touchPointerID[count]	= -1;
	                    this.touchEventsBuffer.add(touchEvent);
	                    break;
					case MotionEvent.ACTION_MOVE:
	                    touchEvent					= touchEventPool.newObject();
	                    touchEvent.type				= TouchEvent.TOUCH_DRAGGED;
	                    touchEvent.pointer			= pointerID;
	                    touchEvent.x				= touchX[count] = (int)(event.getX(count) * scaleX);
	                    touchEvent.y				= touchY[count] = (int)(event.getY(count) * scaleY);
	                    this.isTouched[count]		= true;
	                    this.touchPointerID[count]	= pointerID;
	                    this.touchEventsBuffer.add(touchEvent);
	                    break;
				}
				return true;
			}
		}
		return true;
	}
	
	/**
	 * @param pointerID
	 * @return
	 */
	private int getIndexOfTouchPointerID(int pointerID) {
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            if (touchPointerID[i] == pointerID) {
                return i;
            }
        }
        return -1;
    }
 
    /**
     * @param pointerID
     * @return
     */
    public boolean isTouchDown(int pointerID) {
        synchronized (this) {
            int index = getIndexOfTouchPointerID(pointerID);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return false;
            } else {
                return isTouched[index];
            }
        }
    }
 
    /**
     * @param pointerID
     * @return
     */
    public int getTouchX(int pointerID) {
        synchronized (this) {
            int index = getIndexOfTouchPointerID(pointerID);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return 0;
            } else {
                return touchX[index];
            }
        }
    }
 
    /**
     * @param pointerID
     * @return
     */
    public int getTouchY(int pointerID) {
        synchronized (this) {
            int index = getIndexOfTouchPointerID(pointerID);
            if (index < 0 || index >= MAX_TOUCHPOINTS) {
                return 0;
            } else {
                return touchY[index];
            }
        }
    }
 
    /**
     * @return
     */
    public List<TouchEvent> getTouchEvents()
    {
        synchronized (this) {
            length = touchEvents.size();
            for (count = 0; count < length; count++)
            {
                touchEventPool.free(touchEvents.get(count));
            }
            touchEvents.clear();
            length = touchEventsBuffer.size();
            for (count = 0; count < length; count++)
            {
            	touchEvents.add(touchEventsBuffer.get(count));
            }
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
	
    /**
     * 
     */
    public void init()
    {
    	touchEvents.clear();
    	touchEventsBuffer.clear();
    }
    
	/**
	 * @author toshiyuki
	 *
	 */
	public static class TouchEvent
	{
		public static final int TOUCH_DOWN		= 0;
		public static final int TOUCH_UP		= 1;
		public static final int TOUCH_DRAGGED	= 2;
		
		public int type;								// 
		public int x, y;								// 
		public int pointer;								// 
	}
	
	/**
	 * @author toshiyuki
	 *
	 * @param <T>
	 */
	private static class Pool<T>
	{
		public interface PoolObjectFactory<T>
		{
			public T createObject();
		}
		
		private final PoolObjectFactory<T> factory;
		private final int maxSize;
		private final List<T> freeObjects;
		
		public Pool(PoolObjectFactory<T> factory, int maxSize)
		{
			this.factory			= factory;
			this.maxSize			= maxSize;
			this.freeObjects		= new ArrayList<T>(maxSize);
		}
		
		public T newObject()
		{
			T object = null;
			if (this.freeObjects.isEmpty())
			{
				object			= factory.createObject();
			}
			else
			{
				object			= freeObjects.remove(freeObjects.size() - 1);
			}
			return object;
		}
		
		public void free(T object)
		{
			if (this.freeObjects.size() < this.maxSize)
			{
				freeObjects.add(object);
			}
		}
	}
}
