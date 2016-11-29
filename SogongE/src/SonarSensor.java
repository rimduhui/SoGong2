import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;

class Sonar
{
	private EV3UltrasonicSensor mEV3;
	private NXTUltrasonicSensor mNXT;
	
	public Sonar(Port port, boolean isNxt)
	{
		if(isNxt)
		{
			mNXT = new NXTUltrasonicSensor(port);
			mNXT.enable();
		}
		else
		{
			mEV3 = new EV3UltrasonicSensor(port);
			mEV3.enable();
		}
	}
	
	public SampleProvider getDistanceMode()
	{
		if(mNXT != null) return mNXT.getDistanceMode();
		else return mEV3.getDistanceMode();
	}
	
	public void finish()
	{
		if(mNXT != null)
		{
			mNXT.disable();
			mNXT.close();
		}
		else
		{
			mEV3.disable();
			mEV3.close();
		}
	}
}

public class SonarSensor
{
	private Sonar mSonarSensor;

	public SonarSensor(Port port, boolean isNxt)
	{
		mSonarSensor = new Sonar(port, isNxt);
	}

	public void finish()
	{
		mSonarSensor.finish();
	}
	
	public boolean isFaced()
	{
		return getDistance() < Setting.PUSH_LIMIT;
	}
	
	public boolean isDetected()
	{
		return getDistance() < Setting.DIST_LIMIT;
	}
	
	public float getDistance()
	{
		SampleProvider distance = mSonarSensor.getDistanceMode();
		
		float distSample[] = new float[distance.sampleSize()];
		distance.fetchSample(distSample, 0);
		
		return distSample[0];
	}
}
