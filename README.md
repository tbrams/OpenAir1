# OpenAir Command Parser
OpenAir Airspace and Terrain Description Language

<p align="center">
<img src="https://cloud.githubusercontent.com/assets/3058746/22985928/969548aa-f3dc-11e6-8e8f-9b37c9cb92ed.png" width="400px" alt="Screenshot" />
</p>
</center>

## Background
After making simple Airspace polygons in my Flight Planning app for a while, I discovered that many airspaces are not just a list of corners in a polygon and therefore not hard to include using the standard Google Maps Polygon.
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
* V is a Variable definition. This is used for center of a circle with "X=coord" and direction of arcs with a "D=+/D=-".
* DA is short for Draw Arc and takes three arguments, a center, a from angle and a to angle number.

In other words the KRNO TMA is constructed by connected ARC segments with a center in the X coordinate. 
First arc is 10 nm and goes clockwise from 270 degrees to 290 degrees, the second is 7nm from center between 290 and 320 and the innermost is 5 nm from center and goes all the way from 200 to 270 counter clockwise.

This is a very appealing approach to compensate for some of the very basic limitations you are bound to run into, 
if you start looking at practical applications of the Google Maps Polygon tool.

And it works nicely - this is an OpenAir™ parser that will accept most of the commands you need for plotting airspaces, but not all.
Please feel free to take this project further if you like.

