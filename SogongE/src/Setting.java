import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

abstract public class Setting
{
	public static float BLACK_LIMIT = 0.3f;
	public static float PUSH_LIMIT = 0.1f;
	public static float DIST_LIMIT = 0.4f;
	public static Port FRONT_SONAR_SENSOR_PORT = SensorPort.S2;
	public static Port BACK_SONAR_SENSOR_PORT = SensorPort.S3;
	public static Port FRONT_FLOOR_SENSOR_PORT = SensorPort.S4;
	public static Port BACK_FLOOR_SENSOR_PORT = SensorPort.S1;
}
