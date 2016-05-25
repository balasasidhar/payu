# payu

<h4> PayU Money SDK & UI integration (beta) </h4>

<img src="https://dl.dropbox.com/s/prj2gv928sgjh8u/device-2016-05-25-032533.png" alt="PayU Money" width="33%">

<h5>Step by step guide to integrate PayU Money SDK with your Android Application </h5>
<ol>

<li> Add Maven repository and compile dependency in <b> build.gradle </b> file.
<pre>
apply plugin: 'com.android.application'

android {
    repositories {
        maven {
            url  "http://dl.bintray.com/sasidhar-678/maven"
        }
    }
}

dependencies {
    compile 'com.sasidhar.smaps.payu:payu:0.0.3'
}
</pre>
</li>
</ol>

