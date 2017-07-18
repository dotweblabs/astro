# Astro - a RESTful key-value datastore based on Atomix (https://github.com/atomix/atomix) that runs on a Servlet container

## Goal

The goal of the Astro project is to build a distributed key-value datastore that runs on a Servlet container (node).
Each node having a RESTful interface to the distributed datastore that persists data on disk of each node.

Astro is intended to be a database / datastore thus it does not need any third-party datastore / database for persistence.

## Typical use-case

```
            ------------------
            |  Load Balancer |
            ------------------
                     |
        -------------------------
        |            |           |
   ----------   ----------  ----------
   |  Astro |   |  Astro |  |  Astro |
   ----------   ----------  ----------
```


## To Run:
---------

$mvn jetty:run
