const mongoose = require('mongoose');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const Multer  = require('multer')
const {Storage} = require('@google-cloud/storage');


const storage = new Storage({
  keyFilename: 'Group19.json'
});

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

const multer = Multer({
  storage: Multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024, // no larger than 5mb, you can change as needed.
  }
});

const bucket = storage.bucket("group19-stories");

app.use(express.static('files'));
app.use(express.static('uploads'));

mongoose.connect("", {useNewUrlParser: true});

//Mongoose testing

const Schema = mongoose.Schema;
const ObjectId = Schema.ObjectId;

const Subscription = new Schema({
  id: ObjectId,
  userId: ObjectId,
  socId: ObjectId,
  sub: Boolean
});

const User = new Schema({
  userId: ObjectId,
  username: String,
  password: String,
  userType: String,
  eventsAttended: { type: Number, min: 0, default:0},
});

const Society = new Schema({
  id: ObjectId,
  name: String,
  userId: ObjectId,
  subscribers: {type: Number, default: 0}
});

const Event = new Schema({
  id: ObjectId,
  title: String,
  society: ObjectId,
  description: String,
  thumbnail: String,
  date: Date,
  loc: {
    latitude: Number,
    longitude: Number
  }
});

const Story = new Schema({
  id: ObjectId,
  eventId: ObjectId,
  post_time: Date,
  segments: [{url: String, duration: Number}]
});

const EventStats = new Schema({
  id: ObjectId,
  eventId: ObjectId,
  favourites: Number,
  views: Number
});

const UserEventStats = new Schema({
  id: ObjectId,
  eventId: ObjectId,
  userId: ObjectId,
  favourite: {
    type: Boolean,
    default: false
  },
  view:{
    type: Boolean,
    default: false,
  }
});

const UserModel = mongoose.model('User', User);
const EventModel = mongoose.model('Event', Event);
const StoryModel = mongoose.model('Story', Story);
const EventStatsModel = mongoose.model('EventStats', EventStats);
const UserEventStatsModel = mongoose.model('UserEventStats', UserEventStats);
const SocietyModel = mongoose.model('Society', Society);
const SubscriptionModel = mongoose.model('Subscription', Subscription);

//Mongoose testing

app.use(bodyParser.json());
app.use(bodyParser.text());
app.use(bodyParser.raw());
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/hello', function(req, res) {
  res.send("Hello");
});

//User routes
app.get('/users', function(req, res) {
  var myArray = new Array();
  console.log("getting users...");
  console.log(typeof myArray);
  UserModel.find({}, function (err, docs) {
    docs.forEach(function(user) {
      myArray.push(user);
    });
    res.json(myArray);
  });
}); 

app.get('/users/:userId', function(req, res) {
  UserModel.findById(req.params.userId, function (err, user) {
    console.log(req.params.userId);
    res.json(user);
  });
}); 


//Event routes
app.get('/events', function(req, res) {
  var resArray = new Array();
  console.log("getting events...");

  EventModel.find({}, function (err, docs) {
    docs.forEach(function(eventObj) {
      resArray.push(eventObj);
    });
    res.json(resArray);
  });
}); 

app.get('/events/:eventId', function(req, res) {
  EventModel.findById(req.params.eventId, function (err, eventObj) {
    res.json(eventObj);
  });
}); 

app.post('/events/create', function (req, res, next) {
  console.log("We got an event!");
  console.log(req.body);
  var requestBody = req.body.eventObj;
  var user = req.body.userId;
  SocietyModel.findOne({userId: user}, function(err, soc) {
    if(err) {
      console.log("Error creating");
    } else {

      console.log("Society found");
      console.log(soc);
      requestBody.society = soc;

      var eventInstance = new EventModel(requestBody).save(function(err, ev){
          console.log("Event added");
        new StoryModel({eventId:ev._id, post_time:ev.date, segments:[]}).save(function(err, story) {
          console.log("Story added");
        });
        new EventStatsModel({eventId: ev._id, favourites:0, views:0}).save(function(err, story) {
          console.log("Stats added");
        });
      });
      res.send("success");
    }
  });
  
});

app.put('/events/view/:eventId', function(req, res) {
  UserEventStatsModel.findOne({userId:req.body}, function(err, response) {
    if(err) {
      console.log("rip");
    } else {
      if(response.view == true) {
        console.log("already viewed");
      } else {
        console.log("Viewing ");
        UserEventStatsModel.findOneAndUpdate({userId:req.body}, {view: true}, {setDefaultsOnInsert:true, upsert:true}, function(err, response){
          console.log("Updated user view");
        });
        EventStatsModel.findOneAndUpdate({ eventId: req.params.eventId }, { $inc: { views: 1 } }, function(err, response) {
          if (err) {
            console.log("Could not increment views");
          } else {
            console.log("Incremented view");
            res.send('this is an update');
          }
        }); 
      }
    }
  });
});

app.get('/events/favourite/:eventId/:userId', function(req, res) {
  UserEventStatsModel.findOne({userId: req.params.userId}, function(err, response) {
    if(err) {
      console.log("error fetching if favourite");
    } else {
      if(response === null) {
        res.send(false);
      } else {
        res.send(response.favourite);
      }
    }
  });

});

app.put('/events/favourite/:eventId', function(req, res) {
  UserEventStatsModel.findOneAndUpdate({userId:req.body.userId}, {$set:{favourite: req.body.favourite}}, {setDefaultsOnInsert:true, upsert:true, returnOriginal: true}, function(err, response){
    console.log("Updated user favourite");
    if(req.body.favourite == true) {
      if(!response.favourite == req.body.favourite) {
        EventStatsModel.findOneAndUpdate({ eventId: req.params.eventId }, { $inc: { favourites: 1 } }, function(err, response) {
          if (err) {
            console.log("Could not increment favourites");
          } else {
            console.log("Incremented favourites");
            res.send(true);
          }
        }); 
      }
    } else {
      if(response.favourite == !req.body.favourite) {
        EventStatsModel.findOneAndUpdate({ eventId: req.params.eventId }, { $inc: { favourites: -1 } }, function(err, response) {
          if (err) {
            console.log("Could not decrement favourites");
          } else {
            console.log("Decrement favourites");
            res.send(false);
          }
        }); 
      }
    }
  });
});

//Story routes
app.get('/stories', function(req, res) {
  var resArray = new Array();
  console.log("getting stories...");

  StoryModel.find({}, function (err, docs) {
    docs.forEach(function(story) {
      resArray.push(story);
    });
    res.json(resArray);
  });
}); 

app.get('/stories/:storyId', function(req, res) {
  StoryModel.findById(req.params.storyId, function (err, story) {
    res.json(story);
  });
}); 

app.get('/stories/event/:eventId', function(req, res) {
  StoryModel.findOne({eventId: new mongoose.Types.ObjectId(req.params.eventId)}, function (err, story) {
    res.json(story);
  });
}); 

app.get('/societies', function(req, res) {
  SocietyModel.find({}, function(err, socs) {
    if(err) {
      console.log("error fetching socs");
    } else {
      res.json(socs);
    }
  });
}); 

app.post('/upload/', multer.single('photo'), function (req, res, next) {
  const filename = req.file.fieldname + '-' + Date.now() + '.jpg';
  console.log("filename " + filename);

  const blob = bucket.file(filename);
  const blobStream = blob.createWriteStream({
    resumable: false,
  });

  blobStream.on('error', err => {
    res.send("error");
  });

  blobStream.on('finish', () => {
    // The public URL can be used to directly access the file via HTTP.
    const publicUrl = `https://storage.googleapis.com/${bucket.name}/${blob.name}`;
    res.status(200).send(publicUrl);
    insertDocuments(publicUrl, req.body.name);
  });
  blobStream.end(req.file.buffer);
});

var insertDocuments = function(filePath, eventId) {
  StoryModel.findOneAndUpdate({eventId: new mongoose.Types.ObjectId(eventId)}, {$push:{"segments":{"url":filePath,"duration":3000}}}, {upsert:true}, function(err, story) {});
}

//Registration and login
app.put('/login', function(req, res) {
  UserModel.findOne({username: req.body.username}, function(err, user) {
    if(err) {
      console.log("Error getting user");
    } else {
      if(user === null) {
        //Register user
        res.json({result: "register"});
      } else {
        console.log(user.password);
        console.log(req.body.password);
        if(user.password === req.body.password) {
          res.json({result: "login", id: user._id});
          //Login
        } else {
          res.json({result: "wrong"});
          //Wrong password
        }
      }
    }
  });

}); 


app.put('/register', function(req, res) {
  UserModel.findOne({username: req.body.username}, function(err, user) {
    if(err) {
      console.log("Error getting user");
    } else {
      if(user === null) {
        reg(req.body.username, req.body.password, req.body.type);
        console.log("registering user");
        res.json({result: "register"});
      } else {
        console.log("User already exists");
        res.json({result: "exists"});
      }
    }
  });
});


app.get('/hot', function(req, res) {
  EventStatsModel.find({},null, {
    skip:0,
    limit:5,
    sort:{
        favourites: -1
    }
  }, 
  function(err, events) {
    if(err) {
      console.log("Error fetching hot events");
    } else {
      var ids = new Array();
      events.forEach(function(e) {
        ids.push(e.eventId);
      });

      var query = [
             {$match: {_id: {$in: ids}}},
             {$addFields: {"__order": {$indexOfArray: [ids, "$_id" ]}}},
             {$sort: {"__order": 1}}
      ];

      EventModel.aggregate(query, function(err, docs) {
        res.json(docs);
      });
    }
  })
  
});

app.get('/events/subs/:userId', function(req, res) {
  SubscriptionModel.find({userId: new mongoose.Types.ObjectId(req.params.userId), sub: true}, null, function(err, docs) {
    if(err) {
      console.log('err fetching sub events');
    } else {
      console.log(docs);
      var ids = new Array();
      docs.forEach(function(e) {
        ids.push(e.socId);
      });
      console.log(ids);
      EventModel.find({society: ids}, function(err, events) {
        if(err){ 
          console.log('err err fetching sub events');
        } else {
          res.send(events);
        }

      });
    }

  });

});

app.get('/societies/:socId/:userId', function(req, res) {
  SubscriptionModel.findOne({userId:req.params.userId, socId: req.params.socId}, function(err, sub) {
    if(err) {
      console.log("error fetching sub");
    } else {
      if(sub === null) {
        res.send(false);
      } else {
        res.send(sub.sub);
      }
    }

  });
});

app.put('/sub/:socId', function(req, res) {
  console.log(req.body.sub);
  SubscriptionModel.findOneAndUpdate({userId: req.body.user, socId: req.params.socId}, {$set: {sub:req.body.sub}}, {upsert: true}, function(err, sub) {
    if(err) {
      console.log("err");
      res.send("error");
    } else {
      console.log("subbed");
      res.send("success");
    }
  }); 
});

var reg = function(username, password, type) {
  new UserModel({username: username, password: password, userType: type}).save(function(err, user) {
    if(err) {
      console.log("Something went wrong with user registration");
    } else {
      console.log("User registered");
      if(type === "society") {
        var name = username.split('.')[1].split('@')[0];
        name = capitalizeFirstLetter(name);
        new SocietyModel({userId: user._id, name: name }).save(function(err, soc) {
          if(err) {
            console.log("Error registering soc");
            console.log(err);
          } else {
            console.log("Registered soc");
          }
        });
      }
    }
  });

};

app.listen(8080, function() {
  console.log('Port 8080 is open');
})
