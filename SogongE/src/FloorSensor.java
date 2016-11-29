import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;

public class FloorSensor
{
	private EV3ColorSensor mSensor;
	
	public FloorSensor(Port port)
	{
		mSensor = new EV3ColorSensor(port);
	}
	
	public void finish()
	{
		mSensor.close();
	}
	
	public boolean isInArena()
	{		
		SensorMode mode = mSensor.getRedMode();
		float color[] = new float[mode.sampleSize()];
		mode.fetchSample(color, 0);
		
		return color[0] < Setting.BLACK_LIMIT;
	}
}
