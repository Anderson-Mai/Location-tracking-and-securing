package anderson.android.retrievelocation;

//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import androidOne.google.apps.video_msg.Global;

public final class MySingleton_Client
{
	private static MySingleton_Client instance = null;

	public String customVar;

	public static void initInstance()
	{
		if (MySingleton_Client.instance == null)
		{
			// Create the instance
			MySingleton_Client.instance = new MySingleton_Client();
		}
		else {
			instance = null;
			
		}
	}

	public static MySingleton_Client getInstance()
	{
		// Return the instance
		return MySingleton_Client.instance;
	}

	private MySingleton_Client()
	{
		// Constructor hidden because this is a singleton
	}

	public static void customSingletonMethod()
	{
		// Custom method
	}
}
