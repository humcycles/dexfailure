package com.ridemission.dexfail

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget.TextView

import scala.collection.mutable._

class MainActivity extends Activity {

  // The following link kills the DEX installer
  class ObservableTest extends ArrayBuffer[Int]

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(new TextView(this) {
      setText("hello, world")
    })
  }
}
