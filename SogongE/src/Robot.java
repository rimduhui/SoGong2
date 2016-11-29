import lejos.hardware.Button;

public class Robot
{
	private static int FRONT = 0;
	private static int BACK = 1;
	
	private int lastMove = FRONT;
	
	private static int TICK = 30;	
	
	private FloorSensor mFloorSensorFront;
	private FloorSensor mFloorSensorBack;
	private SonarSensor mSonarSensorFront;
	private SonarSensor mSonarSensorBack;
	private Wheel mMotor;
	
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
		LCDMgr.setText("Pushing Front");
		mMotor.goForward();
		lastMove = BACK;
	}
	
	public void pushBackEnemy()
	{
		LCDMgr.setText("Pushing Back");
		mMotor.goBackward();
		lastMove = FRONT;
	}
	
	public void returnToArena()
	{
		LCDMgr.setText("Returning");
		
		if(mFloorSensorFront.isInArena() && !mFloorSensorBack.isInArena())
		{
			mMotor.goForward();
			lastMove = FRONT;
			sleep(300);
		}
		else if(!mFloorSensorFront.isInArena() && mFloorSensorBack.isInArena())
		{
			mMotor.goBackward();
			lastMove = BACK;
			sleep(300);
		}
		else
		{
			if(lastMove == FRONT) mMotor.goBackward();
			else mMotor.goForward();
		}
	}
	
	public void avoidEnemy()
	{
		LCDMgr.setText("Avoiding");
		mMotor.rotateLeft();
		sleep(300);
		mMotor.goLeftward();
		lastMove = FRONT;
	}
	
	public void traceEnemy()
	{
		LCDMgr.setText("Tracing");
		mMotor.rotateRight();
		sleep(300);
		mMotor.goRightward();
		lastMove = FRONT;
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
			if(mSonarSensorFront.isFaced())
			{
				if(!isInArena()) mMotor.stop();
				else pushFrontEnemy();
			}
			else if(mSonarSensorBack.isFaced())
			{
				if(!isInArena()) mMotor.stop();
				else pushBackEnemy();
			}
			else if(!isInArena()) returnToArena();
			else if(mSonarSensorFront.isDetected()) pushFrontEnemy();
			else if(mSonarSensorBack.isDetected()) pushBackEnemy();
			else traceEnemy();
			
			if(!sleep(TICK)) break;
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
			if(!isInArena()) returnToArena();
			else if(mSonarSensorFront.isDetected()) pushFrontEnemy();
			else if(mSonarSensorBack.isDetected()) pushBackEnemy();
			else avoidEnemy();
			
			if(!sleep(TICK)) break;
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
