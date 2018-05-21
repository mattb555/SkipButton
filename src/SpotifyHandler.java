import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;

public class SpotifyHandler {
	
	private final SpotifyApi SPOTIFY_API;
	
	public SpotifyHandler() {
		SPOTIFY_API = new SpotifyApi.Builder()
				.setClientId("7766a81849654ce2997a5d0fab65f2cd").setClientSecret("b622510771ad4fffa61f817ae5521483")
				.setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8000")).build();
	}
	
	public void connectApi() {
		URI uri = SPOTIFY_API.authorizationCodeUri().state("x4xkmn9pu3j6ukrs8n").scope("user-read-playback-state,user-read-currently-playing,user-modify-playback-state,streaming").build().execute();
		String line = "";
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
				final ServerSocket server = new ServerSocket(8000);
				while (true) {
					final Socket client = server.accept();
					InputStreamReader isr = new InputStreamReader(client.getInputStream());
					BufferedReader reader = new BufferedReader(isr);
					line = reader.readLine();
					reader.close();
					isr.close();
					client.close();
					server.close();
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String code = line.substring(line.indexOf("code=") + 5, line.indexOf("&state="));
		try {
			final AuthorizationCodeCredentials authorizationCodeCredentials = SPOTIFY_API.authorizationCode(code).build()
					.execute();

			// Set access and refresh token for further "spotifyApi" object
			// usage
			SPOTIFY_API.setAccessToken(authorizationCodeCredentials.getAccessToken());
			SPOTIFY_API.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

			System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
		} catch (IOException | SpotifyWebApiException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public CurrentlyPlaying getCurrentlyPlaying() {
		CurrentlyPlaying currentlyPlaying;
		try {
			currentlyPlaying = SPOTIFY_API.getUsersCurrentlyPlayingTrack().build().execute();
		} catch (SpotifyWebApiException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return currentlyPlaying;
	}
	
	public boolean skipTrack() {
		try {
			SPOTIFY_API.skipUsersPlaybackToNextTrack().build().execute();
		} catch (SpotifyWebApiException | IOException e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	public void refreshCredentials() {
		try {
			SPOTIFY_API.setAccessToken(SPOTIFY_API.authorizationCodeRefresh().build().execute().getAccessToken());		
		} catch (SpotifyWebApiException | IOException e) {
			System.out.println("Authorization Refresh failed");
		}
	}
}
