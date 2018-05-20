# SkipButton
A button that logs info about songs skipped on Spotify to a local database

This repository contains a Java Swing button that when pressed will skip forward the presently playing song on Spotify. It then will log information on a local MySQL database regarding the song skipped and at what point in the song the skip button was pressed.

The purpose of this application is to log data for the use in analysis of song skipping. I have a theory that I skip songs at certain points, ie, I like the first 30 seconds and none of the rest of the song.

Because the database is local on my work machine, this program will not work when downloaded by another user. In the future where I have a dedicated database machine, I would like to release something like this as a downloadable application to be able to log the habits of the general population.

Stay tuned for some D3 on my website related to the data farmed from this in the near future.
