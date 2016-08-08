Spark Singularity
=================

Use [Spark](https://spark.apache.org) on Heroku in a single dyno. Experiment inexpensively with Spark in the [Common Runtime](https://devcenter.heroku.com/articles/dyno-runtime#common-runtime).

Production-quality Spark clusters may be deployed into [Private Spaces](https://devcenter.heroku.com/articles/dyno-runtime#private-spaces-runtime) using [spark-in-space](https://github.com/heroku/spark-in-space).

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/heroku/spark-singularity)

This buildpack provides the following three processes, children of the main web process:
  1. Nginx proxy for
    * basic password authentication set via environment variable
      * `SPACE_PROXY_BASIC_AUTH`
      * format `username:{PLAIN}password`
  2. Spark master
    * web UI `https://your-spark-app.herokuapp.com/`
    * [REST API](http://arturmkrtchyan.com/apache-spark-hidden-rest-api) `https://your-spark-app.herokuapp.com/rest`
  3. one [Spark worker](https://spark.apache.org/docs/latest/monitoring.html)
    * `https://your-spark-app.herokuapp.com/worker`

🚨 **This app should not be scaled beyond a single dyno.** (There is no coordination mechanism between multiple instances; implicitly use `127.0.0.1:7077` as Spark Master.)


Submitting & controlling jobs
------------------------------

Because Spark Singularity is contained in a single dyno with only port 80 exposed, there are two options for submitting jobs:

1. [Spark's REST API](http://arturmkrtchyan.com/apache-spark-hidden-rest-api), proxied at `https://your-spark-app.herokuapp.com/rest`
2. TODO Commit Scala job source to the app to run automatically
