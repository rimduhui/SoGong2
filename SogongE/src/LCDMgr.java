import lejos.hardware.lcd.LCD;

abstract public class LCDMgr
{
	public static void setText(String str)
	{
		LCD.clear();
		LCD.drawString(str, 0, 0);
	}
}
