# dr-al
An Android app that uses neural networks to assess your risk of illnesses (currently, only Autism is supported).

Find it on the Google Play Store here: https://play.google.com/store/apps/details?id=com.cjkj.dral

## some project details

One of the major hurdles in completing this app was ensuring that the Python and Java code worked well together. The way it works is that all the data formatting, data cleaning, and data fitting is done in Python, whereas the app and its behind the scene code are done in Java.

Essentially, once an appropriate dataset has been found, a custom fully-connected neural network written in Python fits itself to this data until it is able to catgorize each patient into their correct category.

This README will be continued...
