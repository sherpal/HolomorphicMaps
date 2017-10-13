# HolomorphicMaps

The goal of this app is to get a better intuition of how holomorphic maps in general and conformal maps in particular work.
The tool is meant to be interactive. Create lines and shapes on the plane, and see how these are transformed by maps.


The app uses [electron](https://electron.atom.io/) with [scala.js](https://www.scala-js.org/).

A [release](https://github.com/sherpal/HolomorphicMaps/releases) is available for Windows, Linux and Mac.


## In the tool window

Select all the settings like
- color of the shapes and lines you draw
- select what kind of shape or line you draw
- chose the map from the list

## In a plot

Click to start drawing, then move the mouse to make to shape or line grow, and click again to create it.

## Compile and try it yourself

In [sbt](http://www.scala-sbt.org/), use the command
- fastOptCompileCopy for fast optimization, or
- fullOptCompileCopy for full optimization.

/!\ Currently, you will need to publish locally on your machine the master branch of [boopickle](https://github.com/suzaku-io/boopickle) since we're using scala.js 1.0.0-M1.
