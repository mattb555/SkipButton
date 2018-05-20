import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlaying;

public class ButtonClient {
	
	static SpotifyHandler spotHandler;

	public static void main(String[] args) {
		spotHandler = new SpotifyHandler();
		SQLHandler sqlHandler = new SQLHandler();
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		JButton b = new JButton(":(");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Font myFont = new Font(Font.SANS_SERIF, Font.PLAIN, (screenSize.width + screenSize.height) / 100);
		b.setSize(400, 200);
		b.setFont(myFont);
		b.setFocusPainted(false);
		p.setSize(400, 200);
		p.add(b);
		f.add(p);
		f.setSize(400, 200);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CurrentlyPlaying currentlyPlaying = spotHandler.getCurrentlyPlaying();
				spotHandler.skipTrack();
				System.out.println("Elapsed:\t" + currentlyPlaying.getProgress_ms());
				System.out.println("SongName:\t" + currentlyPlaying.getItem().getName());
				System.out.println("SongID:\t" + currentlyPlaying.getItem().getId());
				System.out.println("PrimaryArtist:\t" + currentlyPlaying.getItem().getArtists()[0].getName());
				System.out.println("Total Length:\t" + currentlyPlaying.getItem().getDurationMs());
				sqlHandler.songSkipped(currentlyPlaying);
			}

		});
		spotHandler.connectApi();
		Thread t = new UpdateThread();
		t.start();
		f.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {
				t.interrupt();
				sqlHandler.close();
			}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
	
	private static class UpdateThread extends Thread {
		private boolean interrupted = false;
		@Override
		public void run() {
			while (!interrupted) {
				try {
					Thread.sleep(300000);
				} catch (InterruptedException e1) {
					interrupted = true;
				}
				spotHandler.refreshCredentials();
			}
		}
	}
}
