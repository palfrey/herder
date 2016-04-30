Herder
======

Herder is a tool for automagically generate schedules for your convention, given such information as "when", "who's coming" and "what events do they want to attend". It leverages [OptaPlanner](http://www.optaplanner.org/) for constraint satisfaction solving to be able to figure out the best schedule it can given your constraints, or at least the one that violates the smallest number of them.

It's a Clojure(Script) Boot app, with a tiny little bit of Java for helping OptaPlanner along.

Getting started
---------------
1. [Get Boot](http://boot-clj.com/)
2. `boot dev`
3. Goto http://localhost:3000

This will get you a running dev setup using h2. Further instructions for other databases (e.g. PostgreSQL), and more production-type environments to come.
