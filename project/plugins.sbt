logLevel := Level.Warn

// create a fat JAR of your project with all of its dependencies
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")
// sbt-sonatype plugin used to publish artifact to maven central via sonatype nexus
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.5")
// sbt-pgp plugin used to sign the artifcat with pgp keys
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.1.2")
// generates Scala source from your build definitions
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")