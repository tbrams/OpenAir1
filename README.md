# OpenAir™ Command Parser
This is an OpenAir™ Language Parser for Google Maps implemented on the Android platform.

## Background
After making simple Airspace polygons in my Flight Planning app for a while, I discovered that many airspaces are not just a list of corners in a polygon and therefore hard, if not impossible, to include using the standard Google Maps Polygon.
<p/>
Take for example this example from skyvector.com. This is the Reno, Nevada (KRNO) airspace. Good luck trying to digitize this and manually add all this as coordinates into PolygonOptions. Needless to say, that will be very cumbersome, if not impossible. 
<p align="center">
<img width="745" alt="screenshot 2017-02-14 12 24 44" src="https://cloud.githubusercontent.com/assets/3058746/22986895/941b1d72-f3df-11e6-82b5-f49e13608e8b.png">
</p>


Realizing this is not a new challenge, I figured some smart people might already have done something on this, somewhere. And 
and soon stumbled upon the wonderful website for glider pilots [soaringweb.org](http://soaringweb.org). 
Actually this site was listed on the splash screen of the X-Plane 11 beta ... if Laminar Research are happy with the quality 
of the many airspace definitions listed there, I will certainly be using that source as well. 


## OpenAir™
All the airspace definitions on this site is using the OpenAir™ format. This is obviously not something I am very familiar 
with, but fortunately there are some documentation available as well.

It is a very clever command format, that makes “complex” polygons easily digestible - provided, of course, you have a system 
that understands OpenAir™ commands.

Using this simple language, the topology of the KRNO TMA C airspace can be described as:

```openair
  V X=39:29.9 N 119:46.1W
  DA 10,270,290
  DA 7,290,320
  DA 10,320,200
  V D=-
  DA 5,200,270
```

That is all it takes for the TMA part of the airspace. Each line in the listing above is a command with some arguments, for example:
* "V" is a Variable definition. This is used for center of a circle with "X=coord" and direction of angle with a "D" command.
* "DA" might be short for "Draw Arc", takes three arguments, a center, a from heading and a to heading.

In other words the KRNO TMA is constructed by connected arc segments with a center in the position of the X variable. 
First arc is 10 nm from ceneter and goes clockwise from 270 degrees to 290 degrees, the second is 7nm from center between 290 and 320 and the innermost is only 5 nm from center but goes all the way back from 200 to 270 counter clockwise.


## Summary
This simple language is a very appealing approach to compensate for some of the very basic limitations you are bound to run into anyway when you start looking at practical applications of the Google Maps Polygon tool for Android.

<p align="center">
<img width="603" alt="screenshot 2017-02-16 14 36 56" src="https://cloud.githubusercontent.com/assets/3058746/23011831/a5b644f0-f455-11e6-93f7-900f49919adf.png">
</p>

And it works nicele. Note that this OpenAir™ parser will accept most of the commands you need for plotting airspaces, but obviously not all of them - please feel free to take this project further if you like.

## Usage
Just make sure you have the OpenAirParser class in your project and you can do all you need from the onMapReady callback function like below. I have my OpenAir™ commands in a separate class in this example, but feel free to read from file or use your own provider for this.

```java
...
   @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        OpenAirParser openAirParser = new OpenAirParser(getApplicationContext(), mMap);

        // Focus the map in Reno, Nevada using the parse coordinate utility from OpenAirParser
        LatLng locKRNO = openAirParser.parseCoordinateString("39:29.9 N 119:46.1W");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locKRNO, 8));

        // Get the sample input file, and display the processed content on the map
        OpenAirFile openAirFile = new OpenAirFile();
        openAirParser.parseCommands(openAirFile.getCommands());
    }
    ... 
```
