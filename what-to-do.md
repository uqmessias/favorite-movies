# What do I have to do for the 2nd part of Favorite Movies app?

## User Interface - Layout

- [ ] Movie Details layout contains a section for displaying trailer videos and user reviews.

## User Interface - Function

- [ ] When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
- [ ] When a trailer is selected, app uses an Intent to launch the trailer.
- [ ] In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite.

## Network API Implementation

- [ ] App requests for related videos for a selected movie via the `/movie/{id}/videos` endpoint in a background thread and displays those details when the user selects a movie.
- [ ] App requests for user reviews for a selected movie via the `/movie/{id}/reviews` endpoint in a background thread and displays those details when the user selects a movie.

## Data Persistence

- [ ] The titles and ids of the user's favorite movies are stored in a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever the user favorites or unfavorites a movie.
- [ ] When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the ContentProvider.
