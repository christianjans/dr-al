# dr-al
An Android app that uses neural networks to assess your risk of illnesses (currently, only Autism is supported).

Find it on the Google Play Store here: https://play.google.com/store/apps/details?id=com.cjkj.dral

## some project details

One of the major hurdles in completing this app was ensuring that the Python and Java code worked well together. The way it works is that all the data formatting, data cleaning, and data fitting is done in Python, whereas the app and its behind the scene code are done in Java.

Essentially, once an appropriate dataset has been found, a custom fully-connected neural network written in Python fits itself to this data until it is able to catgorize each patient into their correct category (in this case, the category would be if the patient has Autism or not). Other implementations of this categorization used the k-nearest neighbours algorithm to classify patients. However, in this project I wanted to attempt this method of using a dense neural network to perform the classification and prediction. In the future, I hope to eventually compare the accuracy of the two methods.

Once the classification is done, the weights and biases of the Python neural network (and any other meta data about the dataset or input methods) are transferred to the app via a JSON file. The app then reads this file and instantiates its own neural network with the same 'connections' between 'neurons' by using the same weights and biases in the same place in the network. Finally, by using a simple custom matrix object and matrix math functions, the Java neural network on the app can predict in the exact same way as the Python network did.

NOTE: The files included with this README are the main files used in the app, none of the Python files are hosted here.
