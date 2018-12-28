Herder
======

[![Travis](https://img.shields.io/travis/palfrey/herder.svg?maxAge=2592000)](https://travis-ci.org/palfrey/herder)
[![Docker Pulls](https://img.shields.io/docker/pulls/palfrey/herder.svg?maxAge=2592000)](https://hub.docker.com/r/palfrey/herder/)

Herder is a tool for automagically generate schedules for your convention, given such information as "when", "who's coming" and "what events do they want to attend". It leverages [OptaPlanner](http://www.optaplanner.org/) for constraint satisfaction solving to be able to figure out the best schedule it can given your constraints, or at least the one that violates the smallest number of them.

It's a Clojure(Script) Boot app, with a tiny little bit of Java for helping OptaPlanner along.

Getting started
---------------
1. [Get Boot](http://boot-clj.com/)
2. `boot dev`
3. Goto http://localhost:3000

This will get you a running dev setup using h2.

Production build
----------------
`dev` boot target builds, tests and runs the system. There's also `prod-build` which makes a `target` directory with all the parts in and `prod-run` which assumes you've done `prod-build` and runs that.

Production setup
----------------
This uses Docker, PostgreSQL and [Let's Encrypt](https://letsencrypt.org/), but if you set DATABASE_URL and HOST you can run this outside of Docker. See `docker-compose.yml` for information. We're using prebuilt images from [Docker Hub](https://hub.docker.com/r/palfrey/herder/) using `prod-build`/`prod-run`

1. Find a suitable machine to run all of this on (I suggest [DigitalOcean](https://m.do.co/c/25c8fd53e5bd), because then I get referrer credit). Main tested environment is a Debian 8.3 x64 machine.
2. `apt-get install vim curl`
3. Follow https://docs.docker.com/engine/installation/linux/debian/#debian-jessie-80-64-bit
4. Check `docker version` and then follow https://github.com/docker/compose/releases instructions for latest release
5. `git clone https://github.com/palfrey/herder.git`
6. `cd herder`
7. Edit `docker-compose-public.yml`. Set `VIRTUAL_HOST` and `LETSENCRYPT_HOST` to your host (hereafter assumed to be "herder.example.com"). Set `LETSENCRYPT_EMAIL` to your email.
8. `docker-compose -f docker-compose-public.yml up -d`
9. Goto http://herder.example.com:3000, and make sure you've got the main "Conventions" page. If not, check `docker logs -f herder_herder_1` for "Starting #'herder.systems/prod-system" at the end. This takes a bit to occur after the "Postgres is up" line
10. Goto https://herder.example.com. If that doesn't work, check `docker logs -f letsencrypt-nginx-proxy-companion` for no ERROR lines
11. If this all works, replace "ports:" section of "herder" in "docker-compose-public.yml" with an "expose:" section
```
  ports:
    - "3000:3000"
```
becomes
```
  expose:
  - "3000"
```
and `docker-compose -f docker-compose-public.yml up -d`, which will stop the port 3000 version being exposed to the outside world.

Backup
------
If you've done the Production setup, backing up your data is good. Here's an example way to backup the Postgres data

1. `apt-get install postgresql-client ssmtp uudeview`
2. Create backup.sh as follows (replacing the "From" and "To" addresses)
```
#!/bin/bash
PGPASSWORD=mysecretpassword pg_dump postgres --host localhost --username=postgres | gzip -c - > backup.sql.gz
echo -e "From: backup@herder.example.com\nTo: you@somehost.com\nSubject: Herder backup\n\n"|uuenview -a -bo backup.sql.gz |sendmail -t
```
and doing a `chmod +x backup.sh`
3. To then backup every night at 2am, `crontab -e` and add the following `0 2 * * * /root/herder/backup.sh` (assuming that's the path to `backup.sh`)
