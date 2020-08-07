# SurreyEvents

An android app for the Software Engineering module (Year 2) - by Group 19

### Description

An app that tracks events around campus.

Societies can post events (date, time, description) and place a pin on the map, where the event will take place.

Societies can also post pictures before, during or after the event.

Users/students can like events and mark that they're attending.

They can also subscribe to events to get notifications.

Users can see a list of all the local events, the events they have subscribed to, as well as the top n events (based on views).


### The technical stuff

Most of the functionality happens on the express.js server that is hosted on Google Clouds App Engine.

User and event data are stored in a MongoDB Atlas Cluster.

Images and pictures of the events are stored in a Google Cloud storage bucket.
