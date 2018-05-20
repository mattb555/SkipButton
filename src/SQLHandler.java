import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;

public class SQLHandler {
	
	private Statement statement;
	private Connection con;

	public SQLHandler() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/SpotifySkip?useSSL=false",
					"spotifyaccess", "cadenza");
			statement = con.createStatement();
		} catch (Exception f) {
			f.printStackTrace();
		}
	}
	
	public void songSkipped(CurrentlyPlaying currentlyPlaying) {
		try {
		ResultSet rs = statement.executeQuery(
				"SELECT SongID FROM Songs WHERE SongID = '" + currentlyPlaying.getItem().getId() + "'");
		if (!rs.next()) {
			statement.executeUpdate("INSERT INTO Songs(SongID, Artist, Length, SongName) "
					+ "VALUES('" + currentlyPlaying.getItem().getId() + "', '"
					+ currentlyPlaying.getItem().getArtists()[0].getName() + "', "
					+ currentlyPlaying.getItem().getDurationMs() + ", '"
					+ currentlyPlaying.getItem().getName() + "')");
		}
		statement.executeUpdate("INSERT INTO Skips(SongID, SkipPoint) "
				+ "VALUES('" + currentlyPlaying.getItem().getId() + "', '" 
				+ currentlyPlaying.getProgress_ms() + "')");
		} catch (SQLException e) {
			System.out.println("Query failed for unknown reason");
		}
	}
	
	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Close failed for unknown reason");
			e.printStackTrace();
		}
	}
}
