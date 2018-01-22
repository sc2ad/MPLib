# Trajectory Library

This repository can be used to construct MotionPaths for autonomous movement of robots. You can make any path that has defined linear derivatives (in other words, you can go as deep or as shallow as you want, as long as you end with a line of some sort).

## Getting Started

If you want to use this Library, simply visit the [Releases](https://github.com/sc2ad/MPLib/releases) page and download the latest version. Then add it to your .classpath file.

### Prerequisites for Visualizing Results

If you want to graph your results in Java, follow the steps below:

First, you will need to download the following:
* [JMathIO](https://github.com/yannrichet/jmathio/blob/master/dist/jmathio.jar) - The graphing library used
* [JMathPlot](https://github.com/yannrichet/jmathplot) - Also needed for the graphing library
Then, replace the jmathplot.jar file with this jar file:
* [JMathPlot Correct Jar](http://www.mediafire.com/file/wa3g3jfs6hv6aif/jmathplot.jar) - For the previous library
Make sure BOTH jar files are correctly in your .classpath!

If you want to graph your results in Python, follow the steps below:

First, download [Anaconda](https://www.anaconda.com/download/) which will allow you to use the dispCsv.py script included in the repository for most graphing.

NOTE: It is recommended to use Java graphing because there are many graphs that do not work with dispCsv.py and the PathTest.java script would have to be modified for Python only graphing.

### Examples

Here is a quick start for an easy ```MotionPath```:

First, define your parameters:

```
// These parameters are unit independent
double startPosition = 0;
double distanceToTravel = 10;
double maxVelocity = 5; // Units / second
double maxAcceleration = 10; // Units / second / second
```

Next, construct your path:

```
MotionPath path = new CombinedPath.LongitudalTrapezoid(startPosition, distanceToTravel, maxVelocity, maxAcceleration);
```

That's it! Then, all that needs to be done is use it. Here are some frequently used methods:

```
path.getPosition(time); // Returns the position along the path at the given time
path.getSpeed(time); // Returns the speed of the path at any given time
path.getAccel(time); // Returns the acceleration of the path at any given time (may be buggy after endpoints)
path.getTotalTime(); // Returns total time of the path
path.getTotalDistance(); // Returns the total distance of the path
```

You can use this in conjungtion with a PID algorithm as such:

```
double output = p * (path.getPosition(time) - currentPosition) + i * (totalError) + d * ((error - lastError) / (time - lastTime));
```

Remember to view the JavaDoc for more information, it contains a lot of information for each method and class!

## Advanced uses

You can integrate paths perpetually by wrapping it with an ```IntegralPath```:

```
MotionPath integratedPath = new IntegralPath(startPoint, path);
```

You can use ```integratedPath``` the same way as any other ```MotionPath```:

```
integratedPath.getPosition(time); // Returns the position along the path at the given time
integratedPath.getSpeed(time); // Returns the speed of the path at any given time
integratedPath.getAccel(time); // Returns the acceleration of the path at any given time (may be buggy after endpoints)
integratedPath.getTotalTime(); // Returns total time of the path
integratedPath.getTotalDistance(); // Returns the total distance of the path
```

For 2D Motion Trajectory generation, construct a ```Point``` array for waypoints of motion:

```
Point[] frcPath = new Point[]{
				new Point(-23.6, -6.6, 3.5, 1.2, 3, -8.2),
				new Point(-8.65, 5.5, 23.4, 12.8, 2.1, 4.72),
				new Point(28.24, 4.3, -6.9, -3.3, 12.5, 13)
		};
```

The parameters are as follows:

```
new Point(xPosition, yPosition, xVelocity, yVelocity, xAcceleration, yAcceleration);
```

Next, interpolate Splines for the Waypoints:

```
Spline[] xyspl = Spline.interpolateQuintic(frcPath);
```

Next, construct the ```Trajectory``` (which is the combination of the parametric splines for the x and y axes)

```
Trajectory traj = new Trajectory(xyspl);
```

Next, construct a ```RobotPath```

```
RobotPath robotPath = new RobotPath(traj, _, _);
```

Finally, create a follower and use it:

```
double maxVelocity = 10, maxAcceleration = 10, maxOmega = 50;
double width = 22, wheelDiameter = 6, ticksPerRev = 1024;

RobotPathFollower follower = new RobotPathFollower(width, wheelDiameter, ticksPerRev, new double[]{leftP,leftI,leftD,leftF}, new double[]{rightP,rightI,rightD,rightF});
follower.setPath(path);
follower.start();
```

## Acknowledgments

* The World of Sc2ad (Will be included within ScadLib)

Pathing

This repository can be used to construct MotionPaths for autonomous movement of robots. You can make any path that has defined linear derivatives (in other words, you can go as deep or as shallow as you want, as long as you end with a line of some sort).

To visualize results, you can download JMathIO and JMathPlot for Java graphs.
Found here:
https://github.com/yannrichet/jmathio/blob/master/dist/jmathio.jar
https://github.com/yannrichet/jmathplot

TO INSTALL PROPERLY, USE THE JMATHIO.JAR, DOWNLOAD OR CLONE THE REPOSITORY OF JMATHPLOT, THEN REPLACE THE jmathplot.jar FILE WITH THIS .JAR FILE:
http://www.mediafire.com/file/wa3g3jfs6hv6aif/jmathplot.jar

OR you can download Anaconda and visualize results using the dispCsv.py script which is part of the repository.
Found here:
https://www.anaconda.com/download/

OR you can see some example resultant images in the examples folder.
