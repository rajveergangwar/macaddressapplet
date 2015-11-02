# Introduction #

Since Java 6 update 22, which closed the native access hole the Mac Address Applet now needs to be signed with a valid certificate to work properly. Versions prior to updated 22 will still work whether the jar is signed or not.

## Applet Signing ##

If you are planning to use this applet or a derivative for a commercial application you will  want to sign your jar with an official code signing certificate, otherwise you can use a self signed certificate. You can official obtain code signing certificates from many sources like [VeriSign](https://www.verisign.com/) or [Go Daddy](http://godaddy.com/).

There is a default self signed certificate included in the source distribution.