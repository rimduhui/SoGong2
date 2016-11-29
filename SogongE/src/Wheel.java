import lejos.hardware.motor.Motor;
import lejos.robotics.RegulatedMotor;

class FixedMotor
{
	private boolean mIsReverse;
	private RegulatedMotor mMotor;
	
	public FixedMotor(RegulatedMotor motor, boolean isReverse)
	{
		mMotor = motor;
		mIsReverse = isReverse;
		
		motor.setSpeed((int)motor.getMaxSpeed());
	}
	
	public void stop()
	{
		mMotor.stop();
	}
	
	public void forward()
	{
		if(mIsReverse) mMotor.backward();
		else mMotor.forward();
	}
	
	public void backward()
	{
		if(mIsReverse) mMotor.forward();
		else mMotor.backward();
	}
	
	public void finish()
	{
		stop();
		mMotor.close();
	}
}

public class Wheel
{
	private static int STOP = 0;
	private static int FRONT = 1;
	private static int BACK = 2;
	
	private int state = STOP;
	
	private FixedMotor mFrontLeft;
	private FixedMotor mFrontRight;
	private FixedMotor mBackLeft;
	private FixedMotor mBackRight;

	public void finish()
	{
		stop();
		mFrontLeft.finish();
		mFrontRight.finish();
		mBackLeft.finish();
		mBackRight.finish();
	}
	
	public Wheel()
	{
		mFrontLeft = new FixedMotor(Motor.C, false);
		mFrontRight = new FixedMotor(Motor.B, false);
		mBackLeft = new FixedMotor(Motor.D, true);
		mBackRight = new FixedMotor(Motor.A, true);
		
		stop();
	}
	
	public void stop()
	{
		if(state == FRONT)
		{
			mFrontLeft.backward();
			mFrontRight.backward();
			mBackLeft.stop();
			mBackRight.stop();
		}
		else if(state == BACK)
		{
			mFrontLeft.forward();
			mFrontRight.forward();
			mBackLeft.stop();
			mBackRight.stop();
		}
		
		try
		{
			Thread.sleep(30);
		}
		catch(Exception e)
		{
		}
		
		state = STOP;
		mFrontLeft.stop();
		mFrontRight.stop();
		mBackLeft.stop();
		mBackRight.stop();
	}
	
	public void goForward()
	{
		mFrontLeft.forward();
		mFrontRight.forward();
		mBackLeft.forward();
		mBackRight.forward();
		
		state = FRONT;
	}
	public void goBackward()
	{
		mFrontLeft.backward();
		mFrontRight.backward();
		mBackLeft.backward();
		mBackRight.backward();
		
		state = BACK;
	}
	
	public void goLeftward()
	{
		mFrontLeft.forward();
		mBackLeft.forward();
		
		mFrontRight.stop();
		mBackRight.forward();
		
		state = FRONT;
	}
	
	public void goRightward()
	{
		mFrontLeft.stop();
		mBackLeft.forward();
		
		mFrontRight.forward();
		mBackRight.forward();
		
		state = FRONT;
	}

	public void rotateLeft()
	{
		mFrontLeft.forward();
		mBackLeft.forward();
		mFrontRight.backward();
		mBackRight.backward();
		
		state = STOP;
	}
	
	public void rotateRight()
	{
		mFrontLeft.backward();
		mBackLeft.backward();
		mFrontRight.forward();
		mBackRight.forward();
		
		state = STOP;
	}
}
