# Cronkot - cron parser app using kotlin

### What you need to build
+ Gradle 7.4
+ OpenJDK 17

### How to build
```gradle clean build``` 

in root directory

### How to run

After building the app the distribution should be in 

```/build/distirbutions/``` 

folder from there unpack the tar archive.

Inside you should find ```bin``` folder inside which there is executable file

#### Example run command:

```cronkot "*/15 0 1,15 * 1-5 /usr/bin/find"``` 

from inside of the bin directory