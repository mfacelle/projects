using UnityEngine;
using System.Collections;

// singleton to handle high scores and saving them when the application is quit
public class ScoreManager : MonoBehaviour
{
	public static string EASYHIGHSCORE_KEY = "Easy High Score";
	public static string HARDHIGHSCORE_KEY = "Hard High Score";
	public static string GAMEMODE_KEY = "Game Mode";
	
	public const int EASYMODE = 0;
	public const int HARDMODE = 1;
	
	public static ScoreManager instance;
	
	// made public for ease of access. not too worried about memory leaks
	private bool musicOn = true;
	
	// =================================================
	
	private ScoreManager() { }
	
	public static ScoreManager getInstance()	
	{ 	
		return instance; 
	}
	
	// ---
	
	// singleton solution from:
	// http://answers.unity3d.com/questions/576969/create-a-persistent-gameobject-using-singleton.html
	void Awake() 
	{
		// remove object if it already exists and isn't this object
		if (instance && instance != this) {
			Destroy(this);
			return;
		}
		// else, create it
		instance = this;
		DontDestroyOnLoad(this.gameObject);
		

		// create highscore playerpref if it doesn't exist already
		if (!PlayerPrefs.HasKey(EASYHIGHSCORE_KEY))
			PlayerPrefs.SetInt(EASYHIGHSCORE_KEY, 0);
		if (!PlayerPrefs.HasKey(HARDHIGHSCORE_KEY))
			PlayerPrefs.SetInt(HARDHIGHSCORE_KEY, 0);

		audio.Play();		// start playing audio only when object is created (turn OFF "play on awake")
		musicOn = true;
	}
	
	// =================================================
	
	// set high score if player is currently beating it
	void Update()
	{
		if (Application.loadedLevelName == "maingame")
			updateScore((int)Player.points);
	}
	
	// =================================================
	
	// save high score when application is quit
	void OnApplicationQuit()
	{
		updateScore((int)Player.points);
		
		PlayerPrefs.DeleteKey(GAMEMODE_KEY);
	}
	
	// =================================================
	
	void updateScore(int points)
	{
		switch(PlayerPrefs.GetInt(GAMEMODE_KEY)) {
		case EASYMODE: 
			if (points > PlayerPrefs.GetInt(EASYHIGHSCORE_KEY))
				PlayerPrefs.SetInt(EASYHIGHSCORE_KEY, points);
			break;
		default:
		case HARDMODE: 
			if (points > PlayerPrefs.GetInt(HARDHIGHSCORE_KEY))
				PlayerPrefs.SetInt(HARDHIGHSCORE_KEY, points);
			break;
		}
	}
	
	// =================================================
	
	public bool isMusicOn()	{ return musicOn; }
	public void setMusicOn(bool isOn)	{ musicOn = isOn; }
}
