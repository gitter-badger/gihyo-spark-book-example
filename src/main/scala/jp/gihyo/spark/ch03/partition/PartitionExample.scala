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

package jp.gihyo.spark.ch03.partition

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

// scalastyle:off println

object PartitionExample {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf = new SparkConf().setAppName("Partition")
    val sc = new SparkContext(conf)

    run(sc)
    sc.stop()
  }

  def run(sc: SparkContext) {
    val nums = sc.parallelize(Array(3, 2, 4, 1, 2, 1), 1)
    println(s"""nums:\n  ${nums.collect().mkString(", ")}""")
    println()

    println("original:")
    nums.glom().mapPartitionsWithIndex((p, it) =>
      it.map(n => s"""  Par$p: ${n.mkString(",")}""")
    ).foreach(println)
    println()

    val numsPar3 = nums.repartition(3)
    println("repartition to 3:")
    numsPar3.glom().mapPartitionsWithIndex((p, it) =>
      it.map(n => s"""  Par$p: ${n.mkString(",")}""")
    ).foreach(println)
    println()

    val numsPar2 = numsPar3.coalesce(2)
    println("coalesce to 2:")
    numsPar2.glom().mapPartitionsWithIndex((p, it) =>
      it.map(n => s"""  Par$p: ${n.mkString(",")}""")
    ).foreach(println)
  }
}

// scalastyle:on println
