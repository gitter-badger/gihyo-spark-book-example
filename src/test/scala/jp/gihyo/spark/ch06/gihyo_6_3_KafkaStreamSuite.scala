/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.gihyo.spark.ch06

import scala.collection.mutable
import java.nio.file.Files

import jp.gihyo.spark.{SparkFunSuite, TestStreamingContext}

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContextWrapper

class gihyo_6_3_KafkaStreamSuite extends SparkFunSuite with TestStreamingContext {

  test("run") {
    val lines = mutable.Queue[RDD[(String, String)]]()
    val ds = ssc.queueStream(lines)
    val clock = new StreamingContextWrapper(ssc).manualClock
    gihyo_6_3_KafkaStream.run(ds, Files.createTempDirectory("KafkaStreamSuite").toString, 2, 1)
    val checkpointDir = Files.createTempDirectory("StreamingUnitTest").toString
    ssc.checkpoint(checkpointDir)
    ssc.start()
    (1 to 2).foreach { case i =>
      lines += sc.makeRDD(Seq(("", "userid:userid001,action:view,pageid:value1"),
        ("", "userid:userid002,action:click,pageid:value2"),
        ("", "userid:userid003,action:view,pageid:value3"),
        ("", "userid:userid001,action:view,pageid:value4"))) // test data
      clock.advance(1000)
      Thread.sleep(1000)
    }
  }
}
