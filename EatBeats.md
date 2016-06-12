EatBeats Documentation 

General Description: 
EatBeats is an app that generates playlists based on what you're eating.  
Users will provide EatBeats with a meal with several properties, like region, season, 
and type (e.g. lunch, pastry, side).  EatBeats will then generate a randomized playlist
based on those properties, by choosing from the highest rated (rated by user) songs
returned from those properties.  For example, a root vegetable stew might be a french, 
wintery entree.  This will return all songs tagged with these tags (winter, french, 
entree) and weight them based on user ratings.  It will then randomize this weighted 
selection and return a playlist (what happens if there aren't enough songs?).  

User stories:
S: as a user, I want a playlist for my hearty winter stew. 
A: 
 - return a reasonable-sized playlist (10-20 songs) based on meal parameters
 - allow users to upvote/downvote songs in playlist
 - if less than 10 songs are available, fill with random selection
 - allow users to "reroll" playlist if they don't like the first one
 - allow users to save playlist as a favorite (which saves meal it's associated with 
 as well)
 - have "user rating" as property on songs
 
S: as a user, I want to add a recipe to my collection
A:
 - user can create meal and enter season, region, and type (ingredients to be added later)
 - saves recipe to user database, which can be browsed
 
S: as a user, I want to change a recipe 
A: 
	- user can edit recipe with spring, this will change/delete playlists associated with
	that recipe
	
S: as a user, I want to share my recipe with others
A: 
	- stretch goal, page will have buttons for sharing via social media
	
Unit tests:
1. test creating a user/logging in via hashed password
2. test creating a playlist
3. test saving/recalling a favorite playlist
4. test what happens when less than 10 songs are available for a specific metric 
5. test rerolling playlists
6. test creating a recipe
7. test rating a song in a playlist (do we rate songs or whole playlists?)
8. test editing a recipe

MVP: 
- create recipe, 
- create random playlist, 
- save recipe, 
- upvote/downvote user ratings
 