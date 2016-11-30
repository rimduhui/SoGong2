import lejos.hardware.Button;

public class Robot
{
	private static int FRONT = 0;
	private static int BACK = 1;
	
	private static int TICK = 30;
	private static int OUT_LIMIT = 2000;
	
	private boolean mIsEntirelyOut = false;
	private long mOutTime;
	
	private boolean mNoDelay = false;
	private int mLastMove = FRONT;
	private long mTraceRotationDelay = System.currentTimeMillis();	
	
	private FloorSensor mFloorSensorFront;
	private FloorSensor mFloorSensorBack;
	private SonarSensor mSonarSensorFront;
	private SonarSensor mSonarSensorBack;
	private Wheel mMotor;
	
	private long random(long a, long b)
	{
		return (long)(Math.random() * (b - a)) + a;
	}
	
	public boolean sleep(long timeMillis)
	{
		long finish = timeMillis + System.currentTimeMillis();
		
		while(System.currentTimeMillis() < finish)
		{
			int id = Button.readButtons();
			if(id == Button.ID_ENTER) return false;
		}
		
		return true;
	}
	
	public void init()
	{
		mFloorSensorFront = new FloorSensor(Setting.FRONT_FLOOR_SENSOR_PORT);
		mFloorSensorBack = new FloorSensor(Setting.BACK_FLOOR_SENSOR_PORT);
		mSonarSensorFront = new SonarSensor(Setting.FRONT_SONAR_SENSOR_PORT, false);
		mSonarSensorBack = new SonarSensor(Setting.BACK_SONAR_SENSOR_PORT, true);
		mMotor = new Wheel();
	}
	
	public void pushFrontEnemy()
	{
		mIsEntirelyOut = false;
		
		LCDMgr.setText("Pushing Front");
		mMotor.goForward();
		mLastMove = BACK;
	}
	
	public void pushBackEnemy()
	{
		mIsEntirelyOut = false;
		
		LCDMgr.setText("Pushing Back");
		mMotor.goBackward();
		mLastMove = FRONT;
	}
	
	public void returnToArena()
	{
		LCDMgr.setText("Returning");
		
		if(mFloorSensorFront.isInArena() && !mFloorSensorBack.isInArena())
		{
			mIsEntirelyOut = false;
			mMotor.goForward();
			mLastMove = FRONT;
			sleep(300);
		}
		else if(!mFloorSensorFront.isInArena() && mFloorSensorBack.isInArena())
		{
			mIsEntirelyOut = false;
			mMotor.goBackward();
			mLastMove = BACK;
			sleep(300);
		}
		else
		{
			if(mIsEntirelyOut)
			{
				if(mOutTime + OUT_LIMIT < System.currentTimeMillis())
				{
					traceEnemy();
					return;
				}
			}
			else
			{
				mIsEntirelyOut = true;
				mOutTime = System.currentTimeMillis();
			}
			
			if(mLastMove == FRONT) mMotor.goBackward();
			else mMotor.goForward();
		}
	}
	
	public void avoidEnemy()
	{
		LCDMgr.setText("Avoiding");
		mMotor.rotateLeft();
		sleep(300);
		mMotor.goLeftward();
		mLastMove = FRONT;
	}
	
	public void traceEnemy()
	{
		LCDMgr.setText("Tracing");
		
		if(mTraceRotationDelay < System.currentTimeMillis())
		{
			mMotor.rotateRight();
			sleep(random(10, 150));
		}
		
		mMotor.goForward();
		mLastMove = FRONT;
		mTraceRotationDelay = System.currentTimeMillis() + random(10, 100);
		
		mNoDelay = true;
	}
	
	public boolean isInArena()
	{
		return mFloorSensorFront.isInArena() && mFloorSensorBack.isInArena();
	}
	
	public void attack()
	{
		LCDMgr.setText("Attack");
		init();
				
		while(true)
		{
			mNoDelay = false;
			
			if(mSonarSensorFront.isFaced())
			{	
				mIsEntirelyOut = false;
				
				if(!isInArena()) mMotor.stop();
				else pushFrontEnemy();
			}
			else if(mSonarSensorBack.isFaced())
			{
				mIsEntirelyOut = false;
				
				if(!isInArena()) mMotor.stop();
				else pushBackEnemy();
			}
			else if(!isInArena()) returnToArena();
			else if(mSonarSensorFront.isDetected()) pushFrontEnemy();
			else if(mSonarSensorBack.isDetected()) pushBackEnemy();
			else
			{
				mIsEntirelyOut = false;
				traceEnemy();
			}
			
			if(!mNoDelay && !sleep(TICK)) break;
			int id = Button.readButtons();
			if(id == Button.ID_ENTER) break;
		}
		
		finish();
	}
	
	public void defence()
	{
		LCDMgr.setText("Defence");		
		init();
		
		while(true)
		{
			mNoDelay = false;
			
			if(!isInArena()) returnToArena();
			else if(mSonarSensorFront.isDetected()) pushFrontEnemy();
			else if(mSonarSensorBack.isDetected()) pushBackEnemy();
			else
			{
				mIsEntirelyOut = false;
				avoidEnemy();
			}
			
			if(!mNoDelay && !sleep(TICK)) break;
			int id = Button.readButtons();
			if(id == Button.ID_ENTER) break;
		}
		
		finish();
	}
	
	public void finish()
	{
		mSonarSensorFront.finish();
		mSonarSensorBack.finish();
		mFloorSensorFront.finish();
		mFloorSensorBack.finish();
		mMotor.finish();
		
		LCDMgr.setText("Exit");
		Button.waitForAnyPress();
	}
	
	public static void main(String args[])
	{
		Robot robot = new Robot();
		int key = Button.waitForAnyPress();
		
		if(key == Button.ID_LEFT) robot.attack();
		else robot.defence();
	}
}
