// SPDX-License-Identifier: Apache-2.0

name := "layered-firrtl"

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

val defaultVersions = Map(
//  "chisel3" -> "3.5-SNAPSHOT"
  "chisel3" -> "3.4.3"
)

organization := "org.easysoc"
organizationName := "EasySoC"
organizationHomepage := Some(url("https://github.com/easysoc/"))
//version := "1.1-SNAPSHOT"
version := "1.1.1"
autoAPIMappings := true
// should match chisel's dependencies https://search.maven.org/artifact/edu.berkeley.cs/chisel3-core_2.12
//scalaVersion := "2.12.13"
scalaVersion := "2.12.12"
crossScalaVersions := Seq("2.13.5", "2.12.13")
scalacOptions := Seq("-deprecation", "-feature")

//crossPaths := false
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoKeys ++= Seq[BuildInfoKey]("chiselVersion" -> defaultVersions("chisel3")),
    buildInfoPackage := "layered",
    buildInfoUsePackageAsPath := true
  )

publishMavenStyle := true
Test / publishArtifact := false
pomIncludeRepository := { _ => false }
// Don't add 'scm' elements if we have a git.remoteRepo definition,
//  but since we don't (with the removal of ghpages), add them in below.
pomExtra := <url>https://github.com/easysoc/</url>
  <licenses>
    <license>
      <name>Apache-2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/easysoc/layered-firrtl</url>
    <connection>scm:git:github.com/easysoc/layered-firrtl.git</connection>
  </scm>
  <developers>
    <developer>
      <id>itviewer</id>
      <name>XinJun Ma</name>
      <url>https://github.com/easysoc</url>
    </developer>
  </developers>

// publishSigned and sonatypeBundleRelease
sonatypeBundleDirectory := baseDirectory.value / target.value.getName / "sonatype-staging" / version.value
publishTo := sonatypePublishToBundle.value

libraryDependencies ++= Seq("chisel3").map { dep: String =>
  "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep))
}

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.3" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.3" % "test"
)

// Assembly

assembly / assemblyJarName := "layered-firrtl.jar"

assembly / test := {} // Should there be tests?

assembly / assemblyOutputPath := file("./utils/bin/layered-firrtl.jar")

import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

// skip dependency elements with a scope
pomPostProcess := { (node: XmlNode) =>
  new RuleTransformer(new RewriteRule {
    override def transform(node: XmlNode): XmlNodeSeq = node match {
      case e: Elem if e.label == "dependency"
        && e.child.exists(child => child.label == "scope") =>
        def txt(label: String): String = "\"" + e.child.filter(_.label == label).flatMap(_.text).mkString + "\""
        Comment(s""" scoped dependency ${txt("groupId")} % ${txt("artifactId")} % ${txt("version")} % ${txt("scope")} has been omitted """)
      case _ => node
    }
  }).transform(node).head
}