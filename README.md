# gaugeview
A configurable gauge widget

![sample configuration](https://github.com/daverix/gaugeview/raw/master/assets/screenshot.png)

## Configuration

 Attribute               | Type        | Default value       | Description                           
 ----------------------  | ----------- | ------------------: | -------------------------------------
 pointerColor            | color       | #FF0000 (red)       | color of the pointer that indicates the current value
 arc                     | integer     | 270                 | the number of degrees the lines should take up, half of the degrees points upwards unless you set the rotation to something else
 rotation                | float       | -90f                | in Android the value 0 starts at the right. The default in the widget is to set the default value to be upwards by setting -90f. Alternative you can set the degrees to 270. Positive values spins clock wise.
 startValue              | integer     | -30                 | what the first value should start with on the left hand side. If value is negative indicators for minus and plus will be shown at the bottom.
 endValue                | integer     | 50                  | what the last value should be on the right hand side.
 value                   | float       | 0f                  | sets the initial value that the pointer should point to.
 showNumberEvery         | integer     | 10                  | decides if the number should be shown by checking if the value for a line is divisable with this attribute value
 smallLineEvery          | integer     | 1                   | decides when a small line should be drawn by checking if the value for the line is divisable with this attribute value
 mediumLineEvery         | integer     | 5                   | decides when a medium line should be drawn by checking if the value for the line is divisable with this attribute value
 bigLineEvery            | integer     | 10                  | decides when a big line should be drawn by checking if the value for the line is divisable with this attribute value
 smallLineLength         | dimension   | 32f                 | the length of the small line
 mediumLineLength        | dimension   | 48f                 | the length of the medium line
 bigLineLength           | dimension   | 64f                 | the length of the biggest line
 smallLineStrokeWidth    | dimension   | 4f                  | the stroke width of the small line
 mediumLineStrokeWidth   | dimension   | 6f                  | the stroke width of the medium line
 bigLineStrokeWidth      | dimension   | 8f                  | the stroke width of the big line
 numberSize              | dimension   | 48f                 | the text size of the numbers
 numberColor             | color       | #000000 (black)     | the color of the positive numbers
 negativeNumberColor     | color       | #0000FF (blue)      | the color of the numbers which are 0 or negative
 smallLineColor          | color       | #444444 (dark gray) | the color of the small line
 mediumLineColor         | color       | #444444 (dark gray) | the color of the medium line
 bigLineColor            | color       | #444444 (dark gray) | the color of the big line
 negativeSmallLineColor  | color       | #0000FF (blue)      | the color of the small lines with 0 or negative values
 negativeMediumLineColor | color       | #0000FF (blue)      | the color of the medium lines with 0 or negative values
 negativeBigLineColor    | color       | #0000FF (blue)      | the color of the big line withs 0 or negative values

## Usage

Will upload to mavenCentral if someone is interested.

Meanwhile, add the following to your build.gradle file:
```
dependencies {
    implementation 'net.daverix.gaugeview:view:0.1'
}

repositories {
    mavenLocal()
}
```

And then checkout and run the following to publish the library to your local maven:
```
./gradlew publishToMavenLocal
```

Check the sample for how to apply the attributes above. All attributes above can be changed during 
runtime with the corresponding properties on the view object.

## License

    Copyright 2020 David Laurell
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
