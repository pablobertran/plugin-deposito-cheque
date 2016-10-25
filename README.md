Cordova CameraPreview Plugin
====================

Cordova plugin that allows camera interaction from HTML code.<br/>
Show camera preview popup on top of the HTML.<br/>

<p><b>Features:</b></p>
<ul>
  <li>Take a Picture</li>
  <li>Activate the flash in mode Torch when is supported by the phone</li>
  <li>Crop percentage of original picture by the params defined in Javascript </li>
</ul>

<p><b>Installation:</b></p>

```
Download zip file
Uncompress the file
Run on terminal in the path of the Cordova's project folder the next command:
cordova plugin add [Path_of_plugin_folder]

```

<b>Phonegap Build:</b><br/>

```
```

<p><b>Methods:</b></p>


  <b>startCamera(rect, defaultCamera, tapEnabled, dragEnabled, toBack)</b><br/>
  <info>
  	Starts the camera preview instance.
  	<br/>
	<br/>
	When setting the toBack to TRUE, remember to add the style bellow on your app's HTML body element:
```
style="background-color='transparent'"

```
</info>

Javascript:

```
var tapEnabled = true; //enable tap take picture
var dragEnabled = true; //enable preview box drag across the screen
var toBack = true; //send preview box to the back of the webview
var rect = {x: 100, y: 100, width: 200, height:200};
cordova.plugins.camerapreview.startCamera(rect, "front", tapEnabled, dragEnabled, toBack)

```

<b>stopCamera()</b><br/>
<info>Stops the camera preview instance.</info><br/>

```
cordova.plugins.camerapreview.stopCamera();
```

<b>takePicture(size)</b><br/>
<info>Take the picture, the parameter size is optional</info><br/>

```
cordova.plugins.camerapreview.takePicture({maxWidth:640, maxHeight:640, imageWidthPercentage: 0.75});
```


<b>setOnPictureTakenHandler(callback)</b><br/>
<info>Register a callback function that receives the cropped picture.</info><br/>

```
cordova.plugins.camerapreview.setOnPictureTakenHandler(function(result){
	document.getElementById('originalPicture').src = result[0];//croppedPicturePath;
});
```


<b>switchCamera()</b><br/>
<info>Switch from the rear camera and front camera, if available.</info><br/>

```
cordova.plugins.camerapreview.switchCamera();
```

<b>show()</b><br/>
<info>Show the camera preview box.</info><br/>

```
cordova.plugins.camerapreview.show();
```

<b>hide()</b><br/>
<info>Hide the camera preview box.</info><br/>

```
cordova.plugins.camerapreview.hide();
```

<b>Base64 image:</b><br/>
Use the cordova-file in order to read the picture file and them get the base64.<br/>
Please, refer to this documentation: http://docs.phonegap.com/en/edge/cordova_file_file.md.html<br/>
Method <i>readAsDataURL</i>: Read file and return data as a base64-encoded data URL.

