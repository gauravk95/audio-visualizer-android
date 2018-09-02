# Audio Visualizer
A light-weight and easy-to-use Audio Visualizer for Android using the Android Canvas. 

## Demos
| Blob          | Blast           |
| ------------- |:-------------:| 
| ![Blob](https://raw.githubusercontent.com/gauravk95/audio-visualizer-android/master/samplegif/blob_sample.gif) |![Blast](https://raw.githubusercontent.com/gauravk95/audio-visualizer-android/master/samplegif/blast_sample.gif)|

| Wave          | Bar           |
| ------------- |:-------------:| 
| ![Wave](https://raw.githubusercontent.com/gauravk95/audio-visualizer-android/master/samplegif/wave_sample.gif) |![Bar](https://raw.githubusercontent.com/gauravk95/audio-visualizer-android/master/samplegif/bar_sample.gif)|

## Available Visualizers:
* **BlobVisualizer** - Gives blob like effect, good for low bpm audio
* **BlastVisualizer** - Gives a blast like effect, very random, good for high bpm audio
* **WaveVisualizer** - Gives a nice wave like effect, good for all kinds of audio
* **BarVisualizer** - Gives the contemporary bar effect, good for all kinds of audio

## Usage
* Include the library
* Add the `com.gauravk.musicvisualizer.visualizer.BlastVisualizer` to your XML Layout file:
```xml
<com.gauravk.musicvisualizer.visualizer.BlastVisualizer
            xmlns:custom="http://schemas.android.com/apk/res-auto"
            android:id="@+id/blast"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            custom:avDensity="0.8"
            custom:avType="fill"
            custom:avColor="@color/av_dark_blue"
            custom:avSpeed="normal"/>
```
* Get the reference to this view in you Java Class
```java
        //get reference to visualizer
        mVisualizer = findViewById(R.id.blast);

        //TODO: init MediaPlayer and play the audio
        
        //get the AudioSessionId from your MediaPlayer and pass it to the visualizer
        int audioSessionId = mAudioPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId);
        
```
*Alternatively*, you can pass the raw audio bytes to the visualizer
```java
        //get reference to visualizer
        mVisualizer = findViewById(R.id.blast);

        //TODO: get the raw audio bytes
        
        //pass the bytes to visualizer
        mVisualizer.setRawAudioBytes(bytes);
```
**Now**, release the visualizer in your `onDestroy() or onStop()`
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVisualizer != null)
            mVisualizer.release();
    }
```
If you want to hide the view upon completion of the audio, use
```java
            //TODO: check for completion of audio eg. using MediaPlayer.OnCompletionListener()
            if (mVisualizer != null)
                mVisualizer.hide();
```

### Similarly, include other visualizer

## Attributes
| **attr**      | **Description**         |
| ------------- | ------------- | 
| avType     | Changes the Visualization type - **outline** or **fill**. (N/A for Bar Visualizer) | 
| avColor     | Defines the color that is used in the visualizer | 
| avDensity     | Sets the density of the visualization between `(0,1)` | 
| avSpeed     | Defines the speed of the animation - **slow**, **medium** and **fast** | 
| avGravity     | Updates position of the visualizers - **top** and **bottom** (N/A for Blob and Blast Visualizers) | 
| avWidth     | Describes the width of the line if `avType is outine`, in case of Bar Visualizer, defines width of the bar  | 

## License:
```
    Copyright 2018 Gaurav Kumar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
